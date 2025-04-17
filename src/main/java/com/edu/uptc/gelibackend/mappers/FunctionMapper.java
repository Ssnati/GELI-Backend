package com.edu.uptc.gelibackend.mappers;

import com.edu.uptc.gelibackend.dtos.FunctionDTO;
import com.edu.uptc.gelibackend.entities.FunctionEntity;
import org.springframework.stereotype.Component;

@Component
public class FunctionMapper {

    public FunctionEntity mapDTOToEntity(FunctionDTO functionDTO) {
        return new FunctionEntity(
                functionDTO.getId(),
                functionDTO.getFunctionName());
    }

    public FunctionDTO mapEntityToDTO(FunctionEntity functionEntity) {
        return new FunctionDTO(
                functionEntity.getId(),
                functionEntity.getFunctionName());
    }
}
