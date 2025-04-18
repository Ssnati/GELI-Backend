package com.edu.uptc.gelibackend.services;

import com.edu.uptc.gelibackend.dtos.UserCreationDTO;
import com.edu.uptc.gelibackend.dtos.UserResponseDTO;
import com.edu.uptc.gelibackend.entities.UserEntity;
import com.edu.uptc.gelibackend.mappers.UserMapper;
import com.edu.uptc.gelibackend.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Service;

import javax.ws.rs.core.Response;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepo;
    private final KeyCloakUserService keyCloakUserService;
    private final UserMapper mapper;
    private Map<String, UserRepresentation> inMemmoryUsersMap = new HashMap<>();

    public List<UserResponseDTO> findAll() {
        // Asegurarse de que el mapa en memoria esté actualizado
        updateInMemoryUsersMapIfNeeded();

        // Mapear usuarios locales a DTOs y combinar datos con Keycloak
        return userRepo.findAll().stream()
                .map(userEntity -> mergeEntityWithRepresentation(userEntity, inMemmoryUsersMap))
                .collect(Collectors.toList());
    }

    public Optional<UserResponseDTO> findById(Long id) {
        // Buscar el usuario local por ID
        Optional<UserEntity> userEntityOptional = userRepo.findById(id);
        if (userEntityOptional.isEmpty()) {
            return Optional.empty();
        }

        // Asegurarse de que el mapa en memoria esté actualizado
        updateInMemoryUsersMapIfNeeded();

        // Buscar el usuario en Keycloak usando el mapa en memoria
        UserEntity userEntity = userEntityOptional.get();
        UserRepresentation keycloakUser = inMemmoryUsersMap.get(userEntity.getKeycloakId());

        // Si no se encuentra en Keycloak, devolver vacío
        if (keycloakUser == null) {
            return Optional.empty();
        }

        // Combinar datos locales y de Keycloak en un DTO
        UserResponseDTO userResponseDTO = mergeEntityWithRepresentation(userEntity, inMemmoryUsersMap);
        return Optional.of(userResponseDTO);
    }

    private void updateInMemoryUsersMapIfNeeded() {
        // Si el mapa en memoria está vacío, actualizarlo
        if (inMemmoryUsersMap.isEmpty()) {
            List<UserRepresentation> keycloakUsers;
            try {
                keycloakUsers = keyCloakUserService.getAllUsers();
            } catch (Exception e) {
                throw new RuntimeException("Error fetching users from Keycloak", e);
            }
            inMemmoryUsersMap = keycloakUsers.stream()
                    .collect(Collectors.toMap(UserRepresentation::getId, user -> user));
        }
    }

    private UserResponseDTO mergeEntityWithRepresentation(UserEntity userEntity, Map<String, UserRepresentation> keycloakUserMap) {
        UserResponseDTO dto = new UserResponseDTO();
        // Completar el DTO con datos de Keycloak si existe
        UserRepresentation keycloakUser = keycloakUserMap.get(userEntity.getKeycloakId());
        if (keycloakUser != null) {
            dto = mapper.completeDTOWithRepresentation(dto, keycloakUser);
        }

        // Completar el DTO con datos de la entidad local
        return mapper.completeDTOWithEntity(dto, userEntity);
    }

    public UserResponseDTO createUser(UserCreationDTO userCreationDTO) {
        validateUniqueIdentificationNumber(userCreationDTO);
        validateUniqueEmail(userCreationDTO);

        // Crear el usuario en Keycloak
        Response response = createUserInKeycloak(userCreationDTO);
        String userId = extractUserIdFromResponse(response);

        // Crear un mapa temporal de todos los usuarios de Keycloak
        Map<String, UserRepresentation> temporaryUsersMap = fetchAllKeycloakUsers();

        // Guardar en local
        UserResponseDTO userResponseDTO = buildUserResponseDTO(userCreationDTO, userId);
        userResponseDTO.setCreationDate(convertLongToLocalDate(temporaryUsersMap.get(userId).getCreatedTimestamp()));
        userResponseDTO.setModificationRoleDate(convertLongToLocalDate(temporaryUsersMap.get(userId).getCreatedTimestamp()));
        saveUserInDatabase(userResponseDTO);

        // Actualizar el mapa en memoria
        inMemmoryUsersMap = temporaryUsersMap;

        // Retornar la respuesta
        return userResponseDTO;
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

    private UserResponseDTO buildUserResponseDTO(UserCreationDTO userCreationDTO, String userId) {
        UserResponseDTO userResponseDTO = mapper.mapCreationDTOToResponseDTO(userCreationDTO);
        userResponseDTO.setKeycloakId(userId);
        return userResponseDTO;
    }

    public LocalDate convertLongToLocalDate(long timestamp) {
        return Instant.ofEpochMilli(timestamp)
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }

    private void saveUserInDatabase(UserResponseDTO userResponseDTO) {
        UserEntity userEntity = mapper.mapResponseDTOToEntity(userResponseDTO);
        userRepo.save(userEntity);
    }

    private void validateUniqueIdentificationNumber(UserCreationDTO userCreationDTO) {
        if (userRepo.findByIdentification(userCreationDTO.getIdentification()) != null) {
            throw new RuntimeException("Identification number already exists");
        }
    }

    private void validateUniqueEmail(UserCreationDTO userCreationDTO) {
        String keycloakId = inMemmoryUsersMap.values().stream()
                .filter(user -> user.getEmail().equals(userCreationDTO.getEmail()))
                .map(UserRepresentation::getId)
                .findFirst()
                .orElse(null);
        if (keycloakId != null) {
            throw new RuntimeException("Email already exists in Keycloak");
        }

    }

    public UserResponseDTO updateUser(Long id, UserResponseDTO updatedUser) {
        return updatedUser;
    }

    public boolean deleteUser(Long id) {
        return userRepo.findById(id)
                .map(user -> {
                    userRepo.delete(user);
                    return true;
                })
                .orElse(false);
    }
}