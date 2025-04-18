package com.edu.uptc.gelibackend.services;

import com.edu.uptc.gelibackend.dtos.UserDTO;
import com.edu.uptc.gelibackend.entities.UserEntity;
import com.edu.uptc.gelibackend.mappers.UserMapper;
import com.edu.uptc.gelibackend.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Service;

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

    public List<UserDTO> findAll() {
        // Asegurarse de que el mapa en memoria esté actualizado
        updateInMemoryUsersMapIfNeeded();

        // Mapear usuarios locales a DTOs y combinar datos con Keycloak
        return userRepo.findAll().stream()
                .map(userEntity -> mergeEntityWithRepresentation(userEntity, inMemmoryUsersMap))
                .collect(Collectors.toList());
    }

    public Optional<UserDTO> findById(Long id) {
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
        UserDTO userDTO = mergeEntityWithRepresentation(userEntity, inMemmoryUsersMap);
        return Optional.of(userDTO);
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

    private UserDTO mergeEntityWithRepresentation(UserEntity userEntity, Map<String, UserRepresentation> keycloakUserMap) {
        UserDTO dto = new UserDTO();
        // Completar el DTO con datos de Keycloak si existe
        UserRepresentation keycloakUser = keycloakUserMap.get(userEntity.getKeycloakId());
        if (keycloakUser != null) {
            dto = mapper.completeDTOWithRepresentation(dto, keycloakUser);
        }

        // Completar el DTO con datos de la entidad local
        return mapper.completeDTOWithEntity(dto, userEntity);
    }

    public UserDTO createUser(UserDTO user) {
        return user;
    }

    public UserDTO updateUser(Long id, UserDTO updatedUser) {
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