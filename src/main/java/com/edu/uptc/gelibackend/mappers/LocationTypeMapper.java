package com.edu.uptc.gelibackend.mappers;

import com.edu.uptc.gelibackend.dtos.LocationTypeDTO;
import com.edu.uptc.gelibackend.entities.LocationTypeEntity;
import org.springframework.stereotype.Component;

@Component
public class LocationTypeMapper {

    public LocationTypeDTO toLocationTypeDTO(LocationTypeEntity locationTypeEntity) {
        return new LocationTypeDTO(
                locationTypeEntity.getId(),
                locationTypeEntity.getName()
        );
    }

    public LocationTypeEntity toLocationTypeEntity(LocationTypeDTO locationTypeDTO) {
        return new LocationTypeEntity(
                locationTypeDTO.getId(),
                locationTypeDTO.getLocationTypeName()
        );
    }
}
