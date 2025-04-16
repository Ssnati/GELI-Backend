package com.edu.uptc.gelibackend.mappers;

import com.edu.uptc.gelibackend.dtos.LaboratoryDTO;
import com.edu.uptc.gelibackend.entities.LaboratoryEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class LaboratoryMapper {

    @Autowired
    private LocationMapper locationMapper;

    public LaboratoryEntity mapLaboratoryDTOToLaboratory(LaboratoryDTO laboratoryDTO) {
        return new LaboratoryEntity(
                laboratoryDTO.getId(),
                laboratoryDTO.getLaboratoryName(),
                laboratoryDTO.getLaboratoryDescription(),
                locationMapper.toLocationEntity(laboratoryDTO.getLocation()),
                laboratoryDTO.getLaboratoryAvailability()
        );
    }

    public LaboratoryDTO mapLaboratoryToLaboratoryDTO(LaboratoryEntity laboratoryEntity) {
        return new LaboratoryDTO(
                laboratoryEntity.getId(),
                laboratoryEntity.getLaboratoryName(),
                laboratoryEntity.getLaboratoryDescription(),
                locationMapper.toLocationDTO(laboratoryEntity.getLaboratoryLocation()),
                laboratoryEntity.getLaboratoryAvailability()
        );
    }

}