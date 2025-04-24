package com.edu.uptc.gelibackend.services;

import com.edu.uptc.gelibackend.dtos.UserCreationDTO;
import com.edu.uptc.gelibackend.dtos.UserResponseDTO;
import com.edu.uptc.gelibackend.dtos.UserUpdateDTO;
import com.edu.uptc.gelibackend.entities.UserEntity;
import com.edu.uptc.gelibackend.entities.UserStatusHistoryEntity;
import com.edu.uptc.gelibackend.mappers.UserMapper;
import com.edu.uptc.gelibackend.repositories.UserRepository;
import com.edu.uptc.gelibackend.repositories.UserStatusHistoryRepository;
import com.edu.uptc.gelibackend.specifications.UserFilterDTO;
import com.edu.uptc.gelibackend.specifications.UserSpecification;
import lombok.RequiredArgsConstructor;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.ws.rs.core.Response;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserStatusHistoryRepository historyRepo;
    private final UserRepository userRepo;
    private final KeyCloakUserService keyCloakUserService;
    private final UserMapper mapper;
    private final UserSpecification userSpecification;

    public List<UserResponseDTO> findAll() {
        List<UserEntity> userEntities = userRepo.findAll(); // -> api -> consultan todos los usuarios de la base de datos
        List<UserStatusHistoryEntity> historyEntities = historyRepo.findAll();

        List<UserResponseDTO> userResponseDTOs = userEntities.stream()
                .map(entity -> mapper.completeDTOWithEntity(new UserResponseDTO(), entity))
                .toList();

        userResponseDTOs.forEach(dto -> historyEntities.stream()
                .filter(history -> history.getUser().getId().equals(dto.getId()))
                .max(Comparator.comparing(UserStatusHistoryEntity::getModificationStatusDate))
                .ifPresent(history -> dto.setModificationStatusDate(history.getModificationStatusDate())));
        return userResponseDTOs;
    }

    public Optional<UserResponseDTO> findById(Long id) {
        Optional<UserEntity> optional = userRepo.findById(id);
        if (optional.isPresent()) {
            UserEntity entity = optional.get();
            UserResponseDTO dto = mapper.completeDTOWithEntity(new UserResponseDTO(), entity);
            UserStatusHistoryEntity statusDateDesc = historyRepo.findFirstByUserIdOrderByModificationStatusDateDesc(entity.getId());
            if (statusDateDesc != null) {
                dto.setModificationStatusDate(statusDateDesc.getModificationStatusDate());
            }
            return Optional.of(dto);
        }
        return Optional.empty();
    }

    @Transactional
    public UserResponseDTO createUser(UserCreationDTO userCreationDTO) {
        validateUniqueEmail(userCreationDTO);
        validateUniqueIdentificationNumber(userCreationDTO);
        UserResponseDTO userResponseDTO = mapper.mapCreationDTOToResponseDTO(userCreationDTO);

        String userId = null;
        try {
            // Crear el usuario en Keycloak
            Response response = createUserInKeycloak(userCreationDTO);
            userId = extractUserIdFromResponse(response);

            // Assign role to user
            keyCloakUserService.assignRealmRoleToUser(userId, userCreationDTO.getRole());

            // Build the UserResponseDTO
            UserRepresentation userRepresentation = keyCloakUserService.getById(userId);

            // Map the Keycloak user representation to the UserResponseDTO
            userResponseDTO.setRole(userCreationDTO.getRole());
            UserEntity entity = saveUserInDatabase(mapper.completeDTOWithRepresentation(userResponseDTO, userRepresentation));
            userResponseDTO.setId(entity.getId());

            // Save the user status history
            UserStatusHistoryEntity statusHistory = saveUserRoleHistoryInDatabase(entity);
            userResponseDTO.setModificationStatusDate(statusHistory.getModificationStatusDate());

            return userResponseDTO;

        } catch (Exception e) {
            // Si ocurre un error, eliminar el usuario de Keycloak si ya fue creado
            if (userId != null) {
                keyCloakUserService.deleteUser(userId);
            }
            throw new RuntimeException("Error creating user: " + e.getMessage(), e);
        }
    }

    private UserResponseDTO mergeEntityWithRepresentationInDTO(UserEntity userEntity, UserRepresentation keycloakUser) {
        UserResponseDTO dto = mapper.completeDTOWithRepresentation(new UserResponseDTO(), keycloakUser);
        return mapper.completeDTOWithEntity(dto, userEntity);
    }

    private Response createUserInKeycloak(UserCreationDTO userCreationDTO) {
        try {
            UserRepresentation userRepresentation = mapper.mapCreationDTOToRepresentation(userCreationDTO);
            userRepresentation.setEnabled(true);
            userRepresentation.setUsername(userCreationDTO.getEmail().split("@")[0]);
            return keyCloakUserService.createUser(userRepresentation);
        } catch (Exception e) {
            throw new RuntimeException("Error creating user in Keycloak", e);
        }
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
    public UserResponseDTO updateUser(Long id, UserUpdateDTO updateDTO) {
        validateEmptyFields(updateDTO);


        Optional<UserEntity> optional = userRepo.findById(id);
        if (optional.isEmpty()) {
            throw new RuntimeException("User with ID " + id + " not found");
        }

        UserEntity userEntity = optional.get();

        // Obtener usuario de Keycloak
        UserRepresentation keycloakUser = keyCloakUserService.getById(userEntity.getKeycloakId());
        if (keycloakUser == null) {
            throw new RuntimeException("Keycloak user not found");
        }

        // Manejar cambio de estado
        UserStatusHistoryEntity statusHistory = handleChangeStatus(updateDTO, keycloakUser, userEntity);

        if (statusHistory == null) {
            throw new RuntimeException("No changes detected in user status");
        }

        try {
            // Actualizar en local
            userRepo.save(userEntity);

            // Crear el nuevo registro de estado
            historyRepo.save(statusHistory);

            // Actualizar Keycloak
            keyCloakUserService.updateUser(keycloakUser);

            UserResponseDTO userResponseDTO = mapper.completeDTOWithRepresentation(new UserResponseDTO(), keycloakUser);
            userResponseDTO.setModificationStatusDate(statusHistory.getModificationStatusDate());
            return mapper.completeDTOWithEntity(userResponseDTO, userEntity);
        } catch (Exception e) {
            throw new RuntimeException("Error updating user: " + e.getMessage(), e);
        }

    }

    private UserStatusHistoryEntity handleChangeStatus(UserUpdateDTO updateDTO, UserRepresentation keycloakUser, UserEntity existingEntity) {
        if (updateDTO.getIsActive().equals(keycloakUser.isEnabled())) {
            return null; // No hay cambios en el estado
        }
        keycloakUser.setEnabled(updateDTO.getIsActive());
        existingEntity.setState(updateDTO.getIsActive());

        UserStatusHistoryEntity entity = new UserStatusHistoryEntity();
        entity.setUser(existingEntity);
        entity.setStatusToDate(updateDTO.getIsActive());
        entity.setModificationStatusDate(LocalDate.now());

        return entity;
    }

    private void validateEmptyFields(UserUpdateDTO updateDTO) {
        if (updateDTO.getIsActive() == null) {
            throw new RuntimeException("The field enabledStatus cannot be empty");
        }
    }

    public List<UserResponseDTO> filter(UserFilterDTO filter) {
        Specification<UserEntity> spec = userSpecification.build(filter);
        List<UserEntity> userEntities = userRepo.findAll(spec);
        List<UserRepresentation> keycloakUsers = keyCloakUserService.getAllUsers();

        Map<String, UserRepresentation> usersMap = keycloakUsers.stream()
                .collect(Collectors.toMap(UserRepresentation::getId, user -> user));

        return userEntities.stream()
                .map(userEntity -> {
                    UserRepresentation userRepresentation = usersMap.get(userEntity.getKeycloakId());
                    return mergeEntityWithRepresentationInDTO(userEntity, userRepresentation);
                })
                .collect(Collectors.toList());
    }
}