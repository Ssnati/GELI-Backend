package com.edu.uptc.gelibackend.mappers;

import com.edu.uptc.gelibackend.dtos.UserDTO;
import com.edu.uptc.gelibackend.entities.UserEntity;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.ZoneId;
import java.util.List;

@Component
public class UserMapper {

    public UserEntity mapDTOToEntity(UserDTO dto) {
        return new UserEntity(
                dto.getId(),
                dto.getKeycloakId(),
                dto.getIdentification(),
                dto.getModificationRoleDate()
        );
    }

    public UserRepresentation mapDTOToRepresentation(UserDTO dto) {
        UserRepresentation userRepresentation = new UserRepresentation();
        userRepresentation.setId(dto.getKeycloakId());
        userRepresentation.setFirstName(dto.getFirstName());
        userRepresentation.setLastName(dto.getLastName());
        userRepresentation.setEmail(dto.getEmail());
        userRepresentation.setUsername(dto.getEmail().split("@")[0]);
        userRepresentation.setEnabled(dto.getEnabledStatus());
        userRepresentation.setRealmRoles(List.of(dto.getRole()));
        userRepresentation.setCreatedTimestamp(dto.getCreationDate().atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli());
        return userRepresentation;
    }

    public UserDTO completeDTOWithEntity(UserDTO dto, UserEntity entity) {
        dto.setId(entity.getId());
        dto.setKeycloakId(entity.getKeycloakId());
        dto.setIdentification(entity.getIdentification());
        dto.setModificationRoleDate(entity.getModificationRoleDate());
        return dto;
    }

    public UserDTO completeDTOWithRepresentation(UserDTO dto, UserRepresentation representation) {
        dto.setFirstName(representation.getFirstName());
        dto.setLastName(representation.getLastName());
        dto.setEmail(representation.getEmail());
        dto.setEnabledStatus(representation.isEnabled());
        dto.setCreationDate(Instant.ofEpochMilli(representation.getCreatedTimestamp())
                .atZone(ZoneId.systemDefault())
                .toLocalDate());
        return dto;
    }
}
