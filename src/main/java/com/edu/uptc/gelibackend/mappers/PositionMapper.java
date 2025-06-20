package com.edu.uptc.gelibackend.mappers;

import com.edu.uptc.gelibackend.dtos.PositionDTO;
import com.edu.uptc.gelibackend.dtos.PositionHistoryDTO;
import com.edu.uptc.gelibackend.entities.PositionEntity;
import com.edu.uptc.gelibackend.entities.UserPositionHistoryEntity;
import org.springframework.stereotype.Component;

import java.util.List;

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
        entity.setName(dto.getPositionName());
        return entity;
    }

    public List<PositionHistoryDTO> toPositionHistoryDTOs(List<UserPositionHistoryEntity> positionHistory) {
        return positionHistory.stream()
                .map(history -> new PositionHistoryDTO(
                        history.getOldPosition().getName(),
                        history.getNewPosition().getName(),
                        history.getChangeDate()))
                .toList();
    }
}
