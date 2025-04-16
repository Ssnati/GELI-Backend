package com.edu.uptc.gelibackend.mappers;

import com.edu.uptc.gelibackend.dtos.LocationDTO;
import com.edu.uptc.gelibackend.entities.LocationEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class LocationMapper {

    @Autowired
    private LocationTypeMapper locationTypeMapper;

    public LocationDTO toLocationDTO(LocationEntity locationEntity) {
        return new LocationDTO(
                locationEntity.getId(),
                locationEntity.getLocationName(),
                locationTypeMapper.toLocationTypeDTO(locationEntity.getLocationType()),
                locationEntity.getParentLocation() != null ? toLocationDTO(locationEntity.getParentLocation()) : null
        );
    }

    public LocationEntity toLocationEntity(LocationDTO locationDTO) {
        return new LocationEntity(
                locationDTO.getId(),
                locationDTO.getLocationName(),
                locationTypeMapper.toLocationTypeEntity(locationDTO.getLocationType()),
                locationDTO.getParentLocation() != null ? toLocationEntity(locationDTO.getParentLocation()) : null
        );
    }
}
