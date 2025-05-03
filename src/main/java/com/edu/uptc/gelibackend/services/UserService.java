package com.edu.uptc.gelibackend.services;

import com.edu.uptc.gelibackend.dtos.*;
import com.edu.uptc.gelibackend.entities.*;
import com.edu.uptc.gelibackend.entities.ids.AuthorizedUserEquipmentsId;
import com.edu.uptc.gelibackend.filters.UserFilterDTO;
import com.edu.uptc.gelibackend.mappers.UserMapper;
import com.edu.uptc.gelibackend.repositories.*;
import com.edu.uptc.gelibackend.specifications.UserSpecification;
import com.edu.uptc.gelibackend.utils.KeyCloakUtils;
import lombok.RequiredArgsConstructor;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.ws.rs.core.Response;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final PositionRepository positionRepo;      // ← inject this
    private final UserStatusHistoryRepository historyRepo;
    private final EquipmentRepository equipmentRepo;
    private final UserRepository userRepo;
    private final KeyCloakUserService keyCloakUserService;
    private final UserMapper mapper;
    private final UserSpecification userSpecification;
    private final UserPositionHistoryRepository positionHistoryRepo;
    private final AuthorizedUserEquipmentsRepo authorizedUserEquipmentsRepo;
    private final JavaMailSender mailSender; // Para enviar correos


    public List<UserResponseDTO> findAll() {
        List<UserEntity> userEntities = userRepo.findAll(); // asegúrate que esté con @EntityGraph para traer position
        List<UserStatusHistoryEntity> historyEntities = historyRepo.findAll();

        return userEntities.stream()
                .map(entity -> {
                    UserResponseDTO dto = mapper.completeDTOWithEntity(new UserResponseDTO(), entity);

                    // Agregar posición
                    if (entity.getPosition() != null) {
                        dto.setPosition(new PositionDTO(
                                entity.getPosition().getId(),
                                entity.getPosition().getName()
                        ));
                    }

                    // Agregar modificación de estado
                    historyEntities.stream()
                            .filter(h -> h.getUser().getId().equals(dto.getId()))
                            .max(Comparator.comparing(UserStatusHistoryEntity::getModificationStatusDate))
                            .ifPresent(h -> dto.setModificationStatusDate(h.getModificationStatusDate()));

                    return dto;
                })
                .toList();
    }

    @Transactional(readOnly = true)
    public Optional<UserResponseDTO> findById(Long id) {
        return userRepo.findById(id) // o findByIdWithPosition
                .map(entity -> {
                    UserResponseDTO dto = mapper.completeDTOWithEntity(new UserResponseDTO(), entity);

                    // mapeo manual de Position
                    PositionEntity pos = entity.getPosition();
                    dto.setPosition(new PositionDTO(pos.getId(), pos.getName()));

                    // el resto de tu lógica de statusHistory…
                    UserStatusHistoryEntity statusDateDesc
                            = historyRepo.findFirstByUserIdOrderByModificationStatusDateDesc(entity.getId());
                    if (statusDateDesc != null) {
                        dto.setModificationStatusDate(statusDateDesc.getModificationStatusDate());
                    }
                    return dto;
                });
    }

    public boolean existsByEmail(String email) {
        return userRepo.findByEmail(email) != null;
    }

    @Transactional
    public UserResponseDTO createUser(UserCreationDTO dto) {
        validateUniqueEmail(dto);
        validateUniqueIdentificationNumber(dto);

        String generatedPassword = generateRandomPassword();
        String keycloakUserId = null;

        try {
            PositionEntity position = resolvePosition(dto);

            keycloakUserId = createUserInKeycloakAndAssignRole(dto, generatedPassword);

            UserEntity savedUser = saveUserInDatabase(dto, keycloakUserId, position);

            saveInitialStatusHistory(savedUser);

            sendWelcomeEmail(dto.getEmail(), generatedPassword);

            return buildResponseDTO(savedUser);

        } catch (Exception e) {
            handleKeycloakRollback(keycloakUserId);
            throw new RuntimeException("Error creating user: " + e.getMessage(), e);
        }
    }

    private PositionEntity resolvePosition(UserCreationDTO dto) {
        if (dto.getPositionId() != null) {
            return positionRepo.findById(dto.getPositionId())
                    .orElseThrow(() -> new RuntimeException("Position not found: " + dto.getPositionId()));
        } else if (dto.getPositionName() != null && !dto.getPositionName().isBlank()) {
            String name = dto.getPositionName().trim().toUpperCase();
            return positionRepo.findByNameIgnoreCase(name)
                    .orElseGet(() -> {
                        PositionEntity newPos = new PositionEntity();
                        newPos.setName(name);
                        return positionRepo.save(newPos);
                    });
        } else {
            throw new RuntimeException("Either positionId or positionName must be provided");
        }
    }

    private String createUserInKeycloakAndAssignRole(UserCreationDTO dto, String password) {
        Response kcResp = createUserInKeycloak(dto, password);
        String keycloakUserId = extractUserIdFromResponse(kcResp);
        keyCloakUserService.assignRealmRoleToUser(keycloakUserId, dto.getRole());
        return keycloakUserId;
    }

    private UserEntity saveUserInDatabase(UserCreationDTO dto, String keycloakUserId, PositionEntity position) {
        UserEntity entity = mapper.mapCreationDTOToEntity(dto);
        entity.setKeycloakId(keycloakUserId);
        entity.setState(true);
        entity.setCreateDateUser(LocalDate.now());
        entity.setPosition(position);
        List<AuthorizedUserEquipmentsEntity> authorizedEquipments = handleAuthorizedEquipments(entity, dto.getAuthorizedEquipmentsIds());
        entity.setAuthorizedUserEquipments(authorizedEquipments);
        return userRepo.save(entity);
    }

    private List<AuthorizedUserEquipmentsEntity> handleAuthorizedEquipments(UserEntity entity, List<Long> authorizedEquipmentsIds) {
        if (authorizedEquipmentsIds == null || authorizedEquipmentsIds.isEmpty()) {
            return Collections.emptyList();
        }

        List<EquipmentEntity> authorizedUserEquipmentsEntities = equipmentRepo.findAllById(authorizedEquipmentsIds);

        return authorizedEquipmentsIds.stream()
                .map(id -> {
                    AuthorizedUserEquipmentsEntity authorizedUserEquipments = new AuthorizedUserEquipmentsEntity();
                    authorizedUserEquipments.setId(new AuthorizedUserEquipmentsId());
                    authorizedUserEquipments.setUser(entity);
                    authorizedUserEquipments.setEquipment(authorizedUserEquipmentsEntities.stream()
                            .filter(e -> e.getId().equals(id))
                            .findFirst()
                            .orElseThrow(() -> new RuntimeException("Equipment not found: " + id)));
                    authorizedUserEquipments.setActualStatus(true);
                    return authorizedUserEquipments;
                })
                .collect(Collectors.toList());
    }

    private void saveInitialStatusHistory(UserEntity user) {
        UserStatusHistoryEntity hist = new UserStatusHistoryEntity();
        hist.setUser(user);
        hist.setStatusToDate(user.getState());
        hist.setModificationStatusDate(LocalDate.now());
        historyRepo.save(hist);
    }

    private UserResponseDTO buildResponseDTO(UserEntity user) {
        UserResponseDTO responseDto = mapper.completeDTOWithEntity(new UserResponseDTO(), user);
        responseDto.setModificationStatusDate(LocalDate.now());
        return responseDto;
    }

    private void handleKeycloakRollback(String keycloakUserId) {
        if (keycloakUserId != null) {
            keyCloakUserService.deleteUser(keycloakUserId);
        }
    }

    private String generateRandomPassword() {
        return UUID.randomUUID().toString()
                .replace("-", "")
                .substring(0, 10)
                .toUpperCase(); // Ej: "AB12CD34EF"
    }

    private Response createUserInKeycloak(UserCreationDTO userCreationDTO, String password) {
        try {
            UserRepresentation userRepresentation = mapper.mapCreationDTOToRepresentation(userCreationDTO);
            userRepresentation.setEnabled(true);
            userRepresentation.setUsername(userCreationDTO.getEmail().split("@")[0]);
            userRepresentation.setCredentials(List.of(KeyCloakUtils.createPasswordCredential(password)));
            return keyCloakUserService.createUser(userRepresentation);
        } catch (Exception e) {
            throw new RuntimeException("Error creating user in Keycloak", e);
        }
    }

    private void sendWelcomeEmail(String toEmail, String password) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Bienvenido a GELI - Credenciales de acceso");

        message.setText(
                "¡Bienvenido a GELI!\n\n"
                + "Tu cuenta ha sido creada exitosamente.\n"
                + "Tu contraseña temporal para ingresar es: " + password + "\n\n"
                + "Por razones de seguridad, cambia tu contraseña en tu primer inicio de sesión.\n\n"
                + "Saludos,\n"
                + "Equipo GELI"
        );

        mailSender.send(message);
    }

    private UserResponseDTO mergeEntityWithRepresentationInDTO(UserEntity userEntity, UserRepresentation keycloakUser) {
        UserResponseDTO dto = mapper.completeDTOWithRepresentation(new UserResponseDTO(), keycloakUser);
        return mapper.completeDTOWithEntity(dto, userEntity);
    }

    private String extractUserIdFromResponse(Response response) {
        int status = response.getStatus();
        if (status != 201) {
            throw new RuntimeException("Failed to create user in Keycloak. Status code: " + status);
        }

        return Optional.ofNullable(response.getLocation())
                .map(java.net.URI::getPath)
                .map(path -> path.substring(path.lastIndexOf("/") + 1))
                .orElseThrow(() -> new RuntimeException("Invalid response location from Keycloak"));
    }

    private UserStatusHistoryEntity saveUserRoleHistoryInDatabase(UserEntity entity) {
        UserStatusHistoryEntity historyEntity = new UserStatusHistoryEntity();
        historyEntity.setUser(entity);
        historyEntity.setStatusToDate(entity.getState());
        historyEntity.setModificationStatusDate(LocalDate.now());
        try {
            return historyRepo.save(historyEntity);
        } catch (Exception e) {
            throw new RuntimeException("Error saving user status history in the database: " + e.getMessage(), e);
        }
    }

    private UserEntity saveUserInDatabase(UserResponseDTO userResponseDTO) {
        try {
            return userRepo.save(mapper.mapResponseDTOToEntity(userResponseDTO));
        } catch (Exception e) {
            throw new RuntimeException("Error saving user in the database: " + e.getMessage(), e);
        }
    }

    private void validateUniqueIdentificationNumber(UserCreationDTO userCreationDTO) {
        if (userRepo.findByIdentification(userCreationDTO.getIdentification()) != null) {
            throw new RuntimeException("Identification number already exists");
        }
    }

    private void validateUniqueEmail(UserCreationDTO userCreationDTO) {
        if (userRepo.findByEmail(userCreationDTO.getEmail()) != null) {
            throw new RuntimeException("Email already exists");
        }
    }

    @Transactional
    public UserResponseDTO updateUser(Long id, UserUpdateDTO dto) {
        UserEntity user = userRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // ——— 1) Manejar cambio de estado ———
        UserStatusHistoryEntity statusHistory = handleChangeStatus(dto, user);
        if (statusHistory != null) {
            historyRepo.save(statusHistory);
        }

        // ——— 2) Manejar cambio de cargo con historial ———
        UserPositionHistoryEntity posHistory;
        if (dto.getPositionId() != null || dto.getPositionName() != null) {
            // guarda el cargo anterior
            PositionEntity oldPos = user.getPosition();

            // obtiene o crea el nuevo cargo
            PositionEntity newPos;
            if (dto.getPositionId() != null) {
                newPos = positionRepo.findById(dto.getPositionId())
                        .orElseThrow(() -> new RuntimeException("Cargo no encontrado"));
            } else {
                newPos = new PositionEntity();
                newPos.setName(dto.getPositionName());
                newPos = positionRepo.save(newPos);
            }

            // si cambió, crea el registro de historial
            if (oldPos == null || !oldPos.getId().equals(newPos.getId())) {
                posHistory = new UserPositionHistoryEntity();
                posHistory.setUser(user);
                posHistory.setOldPosition(oldPos);
                posHistory.setNewPosition(newPos);
                posHistory.setChangeDate(LocalDate.now());
                positionHistoryRepo.save(posHistory);
            }

            // actualiza la posición en el usuario
            user.setPosition(newPos);
        }

        // ——— 3) Persistir usuario ———
        userRepo.save(user);

        // ——— 4) Actualizar en Keycloak sólo el isActive ———
        UserRepresentation kc = keyCloakUserService.getById(user.getKeycloakId());
        kc.setEnabled(dto.getIsActive());
        keyCloakUserService.updateUser(kc);

        // ——— 5) Construir y devolver el DTO completo ———
        UserResponseDTO out = mapper.completeDTOWithEntity(new UserResponseDTO(), user);

        if (statusHistory != null) {
            out.setModificationStatusDate(statusHistory.getModificationStatusDate());
        }

        return out;
    }

    private UserStatusHistoryEntity handleChangeStatus(UserUpdateDTO dto, UserEntity user) {
        if (dto.getIsActive().equals(user.getState())) {
            return null;
        }
        UserStatusHistoryEntity h = new UserStatusHistoryEntity();
        h.setUser(user);
        h.setStatusToDate(dto.getIsActive());
        h.setModificationStatusDate(LocalDate.now());
        user.setState(dto.getIsActive());
        return h;
    }

    private void validateEmptyFields(UserUpdateDTO updateDTO) {
        if (updateDTO.getIsActive() == null) {
            throw new RuntimeException("The field enabledStatus cannot be empty");
        }
    }

    @Transactional(readOnly = true)
    public List<UserResponseDTO> filter(UserFilterDTO filter) {
        // 1. Aplica la especificación y trae las entidades de usuario
        Specification<UserEntity> spec = userSpecification.build(filter);
        List<UserEntity> userEntities = userRepo.findAll(spec);

        // 2. Obtén también todos los usuarios de Keycloak para mapear atributos comunes
        List<UserRepresentation> keycloakUsers = keyCloakUserService.getAllUsers();
        Map<String, UserRepresentation> usersMap = keycloakUsers.stream()
                .collect(Collectors.toMap(UserRepresentation::getId, u -> u));

        // 3. Por cada usuario filtrado, arma el DTO y añade la fecha de modificación más reciente
        return userEntities.stream()
                .map(userEntity -> {
                    UserRepresentation rep = usersMap.get(userEntity.getKeycloakId());
                    UserResponseDTO dto = mergeEntityWithRepresentationInDTO(userEntity, rep);

                    // Agregar posición
                    if (userEntity.getPosition() != null) {
                        dto.setPosition(new PositionDTO(
                                userEntity.getPosition().getId(),
                                userEntity.getPosition().getName()
                        ));
                    }

                    // Aquí viene el cambio clave:
                    // Busca el registro de historial más reciente para este user
                    UserStatusHistoryEntity latest = historyRepo
                            .findFirstByUserIdOrderByModificationStatusDateDesc(userEntity.getId());
                    if (latest != null) {
                        dto.setModificationStatusDate(latest.getModificationStatusDate());
                    }
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<UserResponseDTO> findUserByEmail(String email) {
        UserEntity entity = userRepo.findByEmail(email);
        if (entity == null) {
            return Optional.empty();
        }

        UserResponseDTO dto = mapper.completeDTOWithEntity(new UserResponseDTO(), entity);

        // Mapeo correcto del campo position
        PositionEntity position = entity.getPosition();
        if (position != null) {
            dto.setPosition(new PositionDTO(position.getId(), position.getName()));
        }

        // Obtener el historial completo de cambios de posición
        List<UserPositionHistoryEntity> positionHistories
                = positionHistoryRepo.findByUserIdOrderByChangeDateDesc(entity.getId());

        if (positionHistories != null) {
            positionHistories.forEach(ph -> {
                PositionHistoryDTO historyDTO = new PositionHistoryDTO(
                        ph.getOldPosition() != null ? ph.getOldPosition().getName() : null,
                        ph.getNewPosition().getName(),
                        ph.getChangeDate()
                );
                dto.getPositionHistory().add(historyDTO);
            });
        }

        // Cargar el historial de estado más reciente
        UserStatusHistoryEntity latest = historyRepo
                .findFirstByUserIdOrderByModificationStatusDateDesc(entity.getId());
        if (latest != null) {
            dto.setModificationStatusDate(latest.getModificationStatusDate());
        }

        // Traer datos adicionales desde Keycloak
        UserRepresentation rep = keyCloakUserService.getById(entity.getKeycloakId());
        mapper.completeDTOWithRepresentation(dto, rep);

        return Optional.of(dto);
    }

    @Transactional
    public void updateAuthorizedEquipments(Long userId, List<Long> equipmentIds) {
        UserEntity user = userRepo.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Borrar relaciones actuales
        authorizedUserEquipmentsRepo.deleteByUserId(userId);

        // Crear nuevas relaciones
        List<AuthorizedUserEquipmentsEntity> newAssignments = equipmentIds.stream()
                .map(equipmentId -> {
                    EquipmentEntity equipment = equipmentRepo.findById(equipmentId)
                            .orElseThrow(() -> new IllegalArgumentException("Equipment not found"));

                    AuthorizedUserEquipmentsEntity entity = new AuthorizedUserEquipmentsEntity();
                    entity.setId(new AuthorizedUserEquipmentsId(userId, equipmentId));
                    entity.setUser(user);
                    entity.setEquipment(equipment);
                    entity.setActualStatus(true);
                    return entity;
                })
                .toList();

        authorizedUserEquipmentsRepo.saveAll(newAssignments);
    }

}
