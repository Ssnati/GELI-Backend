package com.edu.uptc.gelibackend.mappers;

import com.edu.uptc.gelibackend.dtos.FunctionDTO;
import com.edu.uptc.gelibackend.entities.FunctionEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class FunctionMapper {

    private final EquipmentMapper equipmentMapper;

    public FunctionMapper(EquipmentMapper equipmentMapper) {
        this.equipmentMapper = equipmentMapper;
    }

    public FunctionEntity mapDTOToEntity(FunctionDTO functionDTO) {
        FunctionEntity entity = new FunctionEntity();
        entity.setId(functionDTO.getId());
        entity.setFunctionName(functionDTO.getFunctionName());
        return entity;
    }

    public FunctionDTO mapEntityToDTO(FunctionEntity functionEntity) {
        FunctionDTO functionDTO = new FunctionDTO();
        functionDTO.setId(functionEntity.getId());
        functionDTO.setFunctionName(functionEntity.getFunctionName());
        return functionDTO;
    }
}
