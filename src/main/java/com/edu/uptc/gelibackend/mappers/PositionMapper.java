package com.edu.uptc.gelibackend.mappers;

import com.edu.uptc.gelibackend.dtos.PositionDTO;
import com.edu.uptc.gelibackend.entities.PositionEntity;
import org.springframework.stereotype.Component;

@Component
public class PositionMapper {

    public PositionDTO toDto(PositionEntity entity) {
        if (entity == null) {
            return null;
        }
        return new PositionDTO(entity.getId(), entity.getName());
    }

    public PositionEntity toEntity(PositionDTO dto) {
        if (dto == null) {
            return null;
        }
        PositionEntity entity = new PositionEntity();
        entity.setId(dto.getId());
        entity.setName(dto.getName());
        return entity;
    }
}
