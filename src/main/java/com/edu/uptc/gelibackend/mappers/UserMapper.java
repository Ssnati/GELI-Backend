package com.edu.uptc.gelibackend.mappers;

import com.edu.uptc.gelibackend.dtos.UserCreationDTO;
import com.edu.uptc.gelibackend.dtos.UserResponseDTO;
import com.edu.uptc.gelibackend.dtos.user.UserFilterResponseDTO;
import com.edu.uptc.gelibackend.entities.AuthorizedUserEquipmentsEntity;
import com.edu.uptc.gelibackend.entities.UserEntity;
import lombok.RequiredArgsConstructor;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.ZoneId;
import java.util.List;

@Component
@RequiredArgsConstructor
public class UserMapper {

    private final AuthorizedUserEquipmentsMapper userEquipmentsMapper;
    private final PositionMapper posMapper;

    public UserEntity mapResponseDTOToEntity(UserResponseDTO dto) {
        UserEntity entity = new UserEntity();
        entity.setId(dto.getId());
        entity.setKeycloakId(dto.getKeycloakId());
        entity.setFirstName(dto.getFirstName());
        entity.setLastName(dto.getLastName());
        entity.setEmail(dto.getEmail());
        entity.setIdentification(dto.getIdentification());
        entity.setState(dto.getEnabledStatus());
        entity.setRole(dto.getRole());
        entity.setCreateDateUser(dto.getCreationDate());
        return entity;
    }

    public UserRepresentation mapResponseDTOToRepresentation(UserResponseDTO dto) {
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

    public UserResponseDTO completeDTOWithEntity(UserResponseDTO dto, UserEntity entity) {
        dto.setId(entity.getId());
        dto.setKeycloakId(entity.getKeycloakId());
        dto.setFirstName(entity.getFirstName());
        dto.setLastName(entity.getLastName());
        dto.setEmail(entity.getEmail());
        dto.setIdentification(entity.getIdentification());
        dto.setEnabledStatus(entity.getState());
        dto.setRole(entity.getRole());
        dto.setCreationDate(entity.getCreateDateUser());
        dto.setAuthorizedUserEquipments(userEquipmentsMapper.toAuthorizedEquipmentDTOs(entity.getAuthorizedUserEquipments()));
        dto.setPosition(posMapper.toDto(entity.getPosition()));
        dto.setPositionHistory(posMapper.toPositionHistoryDTOs(entity.getPositionHistory()));
        return dto;
    }

    public UserFilterResponseDTO completeFilterDTOWithEntity(UserFilterResponseDTO dto, UserEntity entity) {
        dto.setId(entity.getId());
        dto.setKeycloakId(entity.getKeycloakId());
        dto.setFirstName(entity.getFirstName());
        dto.setLastName(entity.getLastName());
        dto.setIdentification(entity.getIdentification());
        dto.setEnabledStatus(entity.getState());
        dto.setRole(entity.getRole());
        dto.setCreationDate(entity.getCreateDateUser());
        dto.setPosition(posMapper.toDto(entity.getPosition()));
        return dto;
    }

    public UserResponseDTO completeDTOWithRepresentation(UserResponseDTO dto, UserRepresentation representation) {
        dto.setKeycloakId(representation.getId());
        dto.setFirstName(representation.getFirstName());
        dto.setLastName(representation.getLastName());
        dto.setEmail(representation.getEmail());
        dto.setEnabledStatus(representation.isEnabled());
        dto.setCreationDate(Instant.ofEpochMilli(representation.getCreatedTimestamp())
                .atZone(ZoneId.systemDefault())
                .toLocalDate());
        return dto;
    }

    public UserFilterResponseDTO completeFilterDTOWithRepresentation(UserFilterResponseDTO dto, UserRepresentation representation) {
        dto.setKeycloakId(representation.getId());
        dto.setFirstName(representation.getFirstName());
        dto.setLastName(representation.getLastName());
        dto.setEnabledStatus(representation.isEnabled());
        dto.setCreationDate(Instant.ofEpochMilli(representation.getCreatedTimestamp())
                .atZone(ZoneId.systemDefault())
                .toLocalDate());
        return dto;
    }

    public UserEntity mapCreationDTOToEntity(UserCreationDTO dto) {
        UserEntity userEntity = new UserEntity();
        userEntity.setIdentification(dto.getIdentification());
        userEntity.setFirstName(dto.getFirstName());
        userEntity.setLastName(dto.getLastName());
        userEntity.setEmail(dto.getEmail());
        userEntity.setRole(dto.getRole());
        return userEntity;
    }

    public UserRepresentation mapCreationDTOToRepresentation(UserCreationDTO dto) {
        UserRepresentation userRepresentation = new UserRepresentation();
        userRepresentation.setFirstName(dto.getFirstName());
        userRepresentation.setLastName(dto.getLastName());
        userRepresentation.setEmail(dto.getEmail());
        userRepresentation.setRealmRoles(List.of(dto.getRole()));
        return userRepresentation;
    }

    public UserResponseDTO mapCreationDTOToResponseDTO(UserCreationDTO dto) {
        UserResponseDTO userResponseDTO = new UserResponseDTO();
        userResponseDTO.setFirstName(dto.getFirstName());
        userResponseDTO.setLastName(dto.getLastName());
        userResponseDTO.setEmail(dto.getEmail());
        userResponseDTO.setIdentification(dto.getIdentification());
        userResponseDTO.setRole((dto.getRole()));
        return userResponseDTO;
    }

    public List<UserResponseDTO> toResponseDTOs(List<AuthorizedUserEquipmentsEntity> authorizedUsersEquipments) {
        return authorizedUsersEquipments.stream()
                .map(authorizedUserEquipmentsEntity
                        -> this.completeDTOWithEntity(new UserResponseDTO(), authorizedUserEquipmentsEntity.getUser()))
                .toList();
    }
}
