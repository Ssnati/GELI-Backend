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
        return LaboratoryEntity.builder()
                .id(laboratoryDTO.getId())
                .laboratoryName(laboratoryDTO.getLaboratoryName())
                .laboratoryDescription(laboratoryDTO.getLaboratoryDescription())
                .laboratoryLocation(locationMapper.toEntity(laboratoryDTO.getLocation()))
                .laboratoryAvailability(laboratoryDTO.getLaboratoryAvailability())
                .laboratoryObservations(laboratoryDTO.getLaboratoryObservations())
                .build();
    }

    public LaboratoryDTO mapEntityToDTO(LaboratoryEntity laboratoryEntity) {
        return LaboratoryDTO.builder()
                .id(laboratoryEntity.getId())
                .laboratoryName(laboratoryEntity.getLaboratoryName())
                .laboratoryDescription(laboratoryEntity.getLaboratoryDescription())
                .location(locationMapper.toDTO(laboratoryEntity.getLaboratoryLocation()))
                .laboratoryAvailability(laboratoryEntity.getLaboratoryAvailability())
                .laboratoryObservations(laboratoryEntity.getLaboratoryObservations())
                .build();
    }

}