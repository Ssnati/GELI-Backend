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
        // Obtener todos los usuarios de Keycloak y mapearlos por su ID
        List<UserRepresentation> keycloakUsers;
        try {
            keycloakUsers = keyCloakUserService.getAllUsers();
        } catch (Exception e) {
            throw new RuntimeException("Error fetching users from Keycloak", e);
        }

        Map<String, UserRepresentation> keycloakUserMap = keycloakUsers.stream()
                .collect(Collectors.toMap(UserRepresentation::getId, user -> user));
        inMemmoryUsersMap = keycloakUserMap;
        // Mapear usuarios locales a DTOs y combinar datos con Keycloak
        return userRepo.findAll().stream()
                .map(userEntity -> mergeUserEntityWithKeycloakData(userEntity, keycloakUserMap))
                .collect(Collectors.toList());
    }

    private UserDTO mergeUserEntityWithKeycloakData(UserEntity userEntity, Map<String, UserRepresentation> keycloakUserMap) {
        // Crear el DTO a partir de la entidad local
        UserDTO dto = mapper.completeDTOWithEntity(new UserDTO(), userEntity);

        // Completar el DTO con datos de Keycloak si existe
        UserRepresentation keycloakUser = keycloakUserMap.get(userEntity.getKeycloakId());
        if (keycloakUser != null) {
            dto = mapper.completeDTOWithRepresentation(dto, keycloakUser);
        }
        return dto;
    }

    public Optional<UserDTO> findById(Long id) {
        Optional<UserEntity> userEntityOptional = userRepo.findById(id);
        if (userEntityOptional.isEmpty()) {
            return Optional.empty();
        }

        UserRepresentation keycloakUser = inMemmoryUsersMap.getOrDefault(
                userEntityOptional.get().getKeycloakId(),
                updateInMemmoryUsersMap().get(userEntityOptional.get().getKeycloakId()));
        if (keycloakUser == null) {
            return Optional.empty();
        }

        UserDTO userDTO = mapper.completeDTOWithEntity(
                mapper.completeDTOWithRepresentation(
                        new UserDTO(),
                        keycloakUser
                ),
                userEntityOptional.get()
        );
        return Optional.of(userDTO);
    }

    private Map<String, UserRepresentation> updateInMemmoryUsersMap() {
        List<UserRepresentation> keycloakUsers;
        try {
            keycloakUsers = keyCloakUserService.getAllUsers();
        } catch (Exception e) {
            throw new RuntimeException("Error fetching users from Keycloak", e);
        }
        inMemmoryUsersMap = keycloakUsers.stream()
                .collect(Collectors.toMap(UserRepresentation::getId, user -> user));
        return inMemmoryUsersMap;
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