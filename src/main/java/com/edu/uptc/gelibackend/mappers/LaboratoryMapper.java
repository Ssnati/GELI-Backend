package com.edu.uptc.gelibackend.mappers;

import com.edu.uptc.gelibackend.dtos.LaboratoryDTO;
import com.edu.uptc.gelibackend.entities.LaboratoryEntity;
import org.springframework.stereotype.Component;

@Component
public class LaboratoryMapper {

    private final LocationMapper locationMapper;

    public LaboratoryMapper(LocationMapper locationMapper) {
        this.locationMapper = locationMapper;
    }

    public LaboratoryEntity mapDTOToEntity(LaboratoryDTO laboratoryDTO) {
        return new LaboratoryEntity(
                laboratoryDTO.getId(),
                laboratoryDTO.getLaboratoryName(),
                laboratoryDTO.getLaboratoryDescription(),
                locationMapper.toEntity(laboratoryDTO.getLocation()),
                laboratoryDTO.getLaboratoryAvailability()
        );
    }

    public LaboratoryDTO mapEntityToDTO(LaboratoryEntity laboratoryEntity) {
        return new LaboratoryDTO(
                laboratoryEntity.getId(),
                laboratoryEntity.getLaboratoryName(),
                laboratoryEntity.getLaboratoryDescription(),
                locationMapper.toDTO(laboratoryEntity.getLaboratoryLocation()),
                laboratoryEntity.getLaboratoryAvailability()
        );
    }

}