package com.edu.uptc.gelibackend.services;

import com.edu.uptc.gelibackend.dtos.UserCreationDTO;
import com.edu.uptc.gelibackend.dtos.UserResponseDTO;
import com.edu.uptc.gelibackend.dtos.UserUpdateDTO;
import com.edu.uptc.gelibackend.entities.UserEntity;
import com.edu.uptc.gelibackend.mappers.UserMapper;
import com.edu.uptc.gelibackend.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.ws.rs.core.Response;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepo;
    private final KeyCloakUserService keyCloakUserService;
    private final UserMapper mapper;

    public List<UserResponseDTO> findAll() {
        List<UserResponseDTO> userResponseDTOList = new ArrayList<>();
        Map<String, UserRepresentation> usersMap = fetchAllKeycloakUsers();
        List<UserEntity> userEntities = userRepo.findAll();

        for (UserEntity userEntity : userEntities) {
            UserRepresentation userRepresentation = usersMap.get(userEntity.getKeycloakId());
            if (userRepresentation != null) {
                userResponseDTOList.add(mergeEntityWithRepresentationInDTO(userEntity, userRepresentation));
            }
        }
        return userResponseDTOList;
    }

    public Optional<UserResponseDTO> findById(Long id) {
        Optional<UserEntity> userEntityOptional = userRepo.findById(id);
        if (userEntityOptional.isEmpty()) {
            return Optional.empty();
        }

        UserEntity userEntity = userEntityOptional.get();
        UserRepresentation keycloakUser = keyCloakUserService.getById(userEntity.getKeycloakId());

        if (keycloakUser == null) {
            return Optional.empty();
        }

        return Optional.of(mergeEntityWithRepresentationInDTO(userEntity, keycloakUser));
    }


    private UserResponseDTO mergeEntityWithRepresentationInDTO(UserEntity userEntity, UserRepresentation keycloakUser) {
        UserResponseDTO dto = mapper.completeDTOWithRepresentation(new UserResponseDTO(), keycloakUser);
        return mapper.completeDTOWithEntity(dto, userEntity);
    }

    public UserResponseDTO createUser(UserCreationDTO userCreationDTO) {
        validateUniqueIdentificationNumber(userCreationDTO);
        validateUniqueEmail(userCreationDTO);
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

            return saveUserInDatabase(mapper.completeDTOWithRepresentation(userResponseDTO, userRepresentation));

        } catch (Exception e) {
            // Si ocurre un error, eliminar el usuario de Keycloak si ya fue creado
            if (userId != null) {
                keyCloakUserService.deleteUser(userId);
            }
            throw new RuntimeException("Error creating user: " + e.getMessage(), e);
        }
    }

    private Map<String, UserRepresentation> fetchAllKeycloakUsers() {
        try {
            List<UserRepresentation> keycloakUsers = keyCloakUserService.getAllUsers();
            return keycloakUsers.stream()
                    .collect(Collectors.toMap(UserRepresentation::getId, user -> user));
        } catch (Exception e) {
            throw new RuntimeException("Error fetching users from Keycloak", e);
        }
    }

    private Response createUserInKeycloak(UserCreationDTO userCreationDTO) {
        try {
            return keyCloakUserService.createUser(mapper.mapCreationDTOToRepresentation(userCreationDTO));
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

    private UserResponseDTO saveUserInDatabase(UserResponseDTO userResponseDTO) {
        try {
            UserEntity userEntity = mapper.mapResponseDTOToEntity(userResponseDTO);
            userEntity.setModificationStatusDate(userResponseDTO.getCreationDate());
            userRepo.save(userEntity);
            return mapper.completeDTOWithEntity(userResponseDTO, userEntity);
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
        String keycloakId = fetchAllKeycloakUsers().values().stream()
                .filter(user -> user.getEmail().equals(userCreationDTO.getEmail()))
                .map(UserRepresentation::getId)
                .findFirst()
                .orElse(null);
        if (keycloakId != null) {
            throw new RuntimeException("Email already exists in Keycloak");
        }

    }

    @Transactional
    public UserResponseDTO updateUser(String username, UserUpdateDTO updateDTO) {
        validateEmptyFields(updateDTO);

        UserResponseDTO userResponseDTO = new UserResponseDTO();
        // Obtener usuario de Keycloak
        UserRepresentation keycloakUser = keyCloakUserService.getAllUsers()
                .stream()
                .filter(user -> user.getUsername().equals(username))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("User not found by username: " + username));

        // Obtener usuario existente
        UserEntity existingEntity = userRepo.findAll().stream()
                .filter(user -> user.getKeycloakId().equals(keycloakUser.getId()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("User not found in database"));

        userResponseDTO = mapper.completeDTOWithRepresentation(userResponseDTO, keycloakUser);
        userResponseDTO = mapper.completeDTOWithEntity(userResponseDTO, existingEntity);

        // Validar identificación
        validateIdentificationUpdate(updateDTO, existingEntity);

        // Manejar cambio de estado
        handleChangeStatus(updateDTO, keycloakUser, existingEntity);

        // buscar en los roles del realm, si el rol nuevo no se encuentra,lanzar una excepción
        keyCloakUserService.getAllRoles()
                .stream()
                .filter(role -> role.getName().equals(updateDTO.getRole()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Role not found in Keycloak"));


        try {
//             Actualizar base de datos primero
            UserEntity updatedEntity = userRepo.save(existingEntity);

            // Actualizar Keycloak (roles y datos básicos)
            List<String> newRoles = Collections.singletonList(updateDTO.getRole());
            keyCloakUserService.updateUser(keycloakUser, newRoles);

            userResponseDTO = mapper.completeDTOWithRepresentation(userResponseDTO, keycloakUser);
            return mapper.completeDTOWithEntity(userResponseDTO, updatedEntity);
        } catch (Exception e) {
            throw new RuntimeException("Error actualizando usuario: " + e.getMessage(), e);
        }

    }

    private void handleChangeStatus(UserUpdateDTO updateDTO, UserRepresentation keycloakUser, UserEntity existingEntity) {
        if (updateDTO.getEnabledStatus().equals(keycloakUser.isEnabled())) {
            return;
        }
        keycloakUser.setEnabled(updateDTO.getEnabledStatus());
        existingEntity.setModificationStatusDate(LocalDate.now());
    }

    private void validateIdentificationUpdate(UserUpdateDTO updateDTO, UserEntity existingEntity) {
        if (updateDTO.getIdentification().equals(existingEntity.getIdentification())) {
            return;
        }
        UserCreationDTO userCreationDTO = new UserCreationDTO();
        userCreationDTO.setIdentification(updateDTO.getIdentification());
        validateUniqueIdentificationNumber(userCreationDTO);
        existingEntity.setIdentification(updateDTO.getIdentification());
    }

    private void validateEmptyFields(UserUpdateDTO updateDTO) {
        if (updateDTO.getFirstName() == null || updateDTO.getFirstName().isEmpty()) {
            throw new RuntimeException("The field firstName cannot be empty");
        }
        if (updateDTO.getLastName() == null || updateDTO.getLastName().isEmpty()) {
            throw new RuntimeException("The field lastName cannot be empty");
        }
        if (updateDTO.getIdentification() == null || updateDTO.getIdentification().isEmpty()) {
            throw new RuntimeException("The field identification cannot be empty");
        }
        if (updateDTO.getEnabledStatus() == null) {
            throw new RuntimeException("The field enabledStatus cannot be empty");
        }
    }
}