package com.edu.uptc.gelibackend.services;

import com.edu.uptc.gelibackend.dtos.UserDTO;
import com.edu.uptc.gelibackend.entities.UserEntity;
import com.edu.uptc.gelibackend.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepo;
    private final KeyCloakUserService keyCloakUserService;

    public List<UserDTO> findAll() {
        List<UserDTO> users = new ArrayList<>();
        Map<Long, UserEntity> entityList = userRepo.findAll().stream()
                .collect(Collectors.toMap(UserEntity::getId, userEntity -> userEntity));
        Map<String, UserRepresentation> keycloakUserMap = keyCloakUserService.getAllUsers().stream()
                .collect(Collectors.toMap(UserRepresentation::getId, user -> user));

        for (UserEntity userEntity : entityList.values()) {
            UserRepresentation keycloakUser = keycloakUserMap.get(userEntity.getKeycloakId());
            if (keycloakUser != null) {
                UserDTO userDTO = new UserDTO();
                userDTO.setId(userEntity.getId());
                userDTO.setKeycloakId(userEntity.getKeycloakId());
                userDTO.setFirstName(keycloakUser.getFirstName());
                userDTO.setLastName(keycloakUser.getLastName());
                userDTO.setEmail(keycloakUser.getEmail());
                userDTO.setIdentification(userEntity.getIdentification());
                userDTO.setEnabledStatus(keycloakUser.isEnabled());
                List<String> roles = keycloakUser.getRealmRoles().stream()
                        .filter(role -> role.equals(role.toUpperCase()))
                        .toList();
                userDTO.setRole(roles.toString());
                userDTO.setModificationRoleDate(userEntity.getModificationRoleDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
                userDTO.setCreationDate(DateTimeFormatter.ofPattern("yyyy-MM-dd")
                        .format(Instant.ofEpochMilli(keycloakUser.getCreatedTimestamp()).atZone(ZoneId.systemDefault())));
                users.add(userDTO);
            }
        }
        return users;
    }

    public Optional<UserDTO> findById(Long id) {
        return userRepo.findById(id)
                .map(userEntity -> {
                    UserDTO dto = new UserDTO();
                    dto.setId(userEntity.getId());
                    dto.setKeycloakId(userEntity.getKeycloakId());
                    dto.setIdentification(userEntity.getIdentification());
                    return dto;
                });
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