package com.edu.uptc.gelibackend.mappers;

import com.edu.uptc.gelibackend.dtos.PositionDTO;
import com.edu.uptc.gelibackend.entities.Position;
import org.springframework.stereotype.Component;

@Component
public class PositionMapper {

    public PositionDTO toDto(Position entity) {
        if (entity == null) {
            return null;
        }
        return new PositionDTO(entity.getId(), entity.getName());
    }

    public Position toEntity(PositionDTO dto) {
        if (dto == null) {
            return null;
        }
        Position entity = new Position();
        entity.setId(dto.getId());
        entity.setName(dto.getName());
        return entity;
    }
}
