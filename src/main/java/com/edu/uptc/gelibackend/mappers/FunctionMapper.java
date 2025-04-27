package com.edu.uptc.gelibackend.mappers;

import com.edu.uptc.gelibackend.dtos.FunctionDTO;
import com.edu.uptc.gelibackend.entities.EquipmentFunctionsEntity;
import com.edu.uptc.gelibackend.entities.EquipmentFunctionsUsedEntity;
import com.edu.uptc.gelibackend.entities.FunctionEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class FunctionMapper {

    public FunctionEntity toEntity(FunctionDTO functionDTO) {
        FunctionEntity entity = new FunctionEntity();
        entity.setId(functionDTO.getId());
        entity.setFunctionName(functionDTO.getFunctionName());
        return entity;
    }

    public FunctionDTO toDTO(FunctionEntity functionEntity) {
        FunctionDTO functionDTO = new FunctionDTO();
        functionDTO.setId(functionEntity.getId());
        functionDTO.setFunctionName(functionEntity.getFunctionName());
        return functionDTO;
    }
    
    public List<FunctionDTO> equipmentFunctionsToDTOs(List<EquipmentFunctionsEntity> equipmentFunctions) {
        return equipmentFunctions.stream()
                .map(EquipmentFunctionsEntity::getFunction)
                .map(this::toDTO)
                .toList();
    }

    public List<FunctionEntity> toEntities(List<EquipmentFunctionsEntity> equipmentFunctions) {
        return equipmentFunctions.stream()
                .map(EquipmentFunctionsEntity::getFunction)
                .toList();
    }

    public List<FunctionDTO> equipmentFunctionsUsedToDTOs(List<EquipmentFunctionsUsedEntity> equipmentFunctionsUsedList) {
        return equipmentFunctionsUsedList.stream()
                .map(EquipmentFunctionsUsedEntity::getFunction)
                .map(this::toDTO)
                .toList();
    }
}