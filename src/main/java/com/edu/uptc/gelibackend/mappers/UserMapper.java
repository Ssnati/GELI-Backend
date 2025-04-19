package com.edu.uptc.gelibackend.mappers;

import com.edu.uptc.gelibackend.dtos.UserCreationDTO;
import com.edu.uptc.gelibackend.dtos.UserResponseDTO;
import com.edu.uptc.gelibackend.entities.UserEntity;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.ZoneId;
import java.util.List;

@Component
public class UserMapper {

    public UserEntity mapResponseDTOToEntity(UserResponseDTO dto) {
        return new UserEntity(
                dto.getId(),
                dto.getKeycloakId(),
                dto.getIdentification(),
                dto.getModificationRoleDate()
        );
    }

    public UserRepresentation mapResponseDTOToRepresentation(UserResponseDTO dto) {
        UserRepresentation userRepresentation = new UserRepresentation();
        userRepresentation.setId(dto.getKeycloakId());
        userRepresentation.setFirstName(dto.getFirstName());
        userRepresentation.setLastName(dto.getLastName());
        userRepresentation.setEmail(dto.getEmail());
        userRepresentation.setUsername(dto.getEmail().split("@")[0]);
        userRepresentation.setEnabled(dto.getEnabledStatus());
        userRepresentation.setRealmRoles(dto.getRoles());
        userRepresentation.setCreatedTimestamp(dto.getCreationDate().atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli());
        return userRepresentation;
    }

    public UserResponseDTO completeDTOWithEntity(UserResponseDTO dto, UserEntity entity) {
        dto.setId(entity.getId());
        dto.setKeycloakId(entity.getKeycloakId());
        dto.setIdentification(entity.getIdentification());
        dto.setModificationRoleDate(entity.getModificationRoleDate());
        return dto;
    }

    public UserResponseDTO completeDTOWithRepresentation(UserResponseDTO dto, UserRepresentation representation) {
        dto.setFirstName(representation.getFirstName());
        dto.setLastName(representation.getLastName());
        dto.setEmail(representation.getEmail());
        dto.setEnabledStatus(representation.isEnabled());
        dto.setCreationDate(Instant.ofEpochMilli(representation.getCreatedTimestamp())
                .atZone(ZoneId.systemDefault())
                .toLocalDate());
        dto.setRoles(representation.getRealmRoles());
        return dto;
    }

    public UserEntity mapCreationDTOToEntity(UserCreationDTO dto) {
        UserEntity userEntity = new UserEntity();
        userEntity.setIdentification(dto.getIdentification());
        return userEntity;
    }

    public UserRepresentation mapCreationDTOToRepresentation(UserCreationDTO dto) {
        UserRepresentation userRepresentation = new UserRepresentation();
        userRepresentation.setFirstName(dto.getFirstName());
        userRepresentation.setLastName(dto.getLastName());
        userRepresentation.setEmail(dto.getEmail());
        userRepresentation.setUsername(dto.getEmail().split("@")[0]);
        userRepresentation.setEnabled(true);
        if (dto.getRole().equals("QUALITY-ADMIN-USER") || dto.getRole().equals("AUTHORIZED-USER")) {
            userRepresentation.setRealmRoles(List.of(dto.getRole()));
        } else {
            throw new RuntimeException("Invalid role");
        }
        return userRepresentation;
    }

    public UserResponseDTO mapCreationDTOToResponseDTO(UserCreationDTO dto) {
        UserResponseDTO userResponseDTO = new UserResponseDTO();
        userResponseDTO.setFirstName(dto.getFirstName());
        userResponseDTO.setLastName(dto.getLastName());
        userResponseDTO.setEmail(dto.getEmail());
        userResponseDTO.setIdentification(dto.getIdentification());
        if (dto.getRole().equals("QUALITY-ADMIN-USER") || dto.getRole().equals("AUTHORIZED-USER")) {
            userResponseDTO.setRoles(List.of(dto.getRole()));
        } else {
            throw new RuntimeException("Invalid role");
        }
        return userResponseDTO;
    }
}
