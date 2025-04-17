package com.edu.uptc.gelibackend.mappers;

import com.edu.uptc.gelibackend.dtos.LocationDTO;
import com.edu.uptc.gelibackend.entities.LocationEntity;
import org.springframework.stereotype.Component;

@Component
public class LocationMapper {

    private final LocationTypeMapper locationTypeMapper;

    public LocationMapper(LocationTypeMapper locationTypeMapper) {
        this.locationTypeMapper = locationTypeMapper;
    }

    public LocationDTO mapEntityToDTO(LocationEntity locationEntity) {
        return new LocationDTO(
                locationEntity.getId(),
                locationEntity.getLocationName(),
                locationTypeMapper.mapEntityToDTO(locationEntity.getLocationType()),
                locationEntity.getParentLocation() != null ? mapEntityToDTO(locationEntity.getParentLocation()) : null
        );
    }

    public LocationEntity mapDTOToEntity(LocationDTO locationDTO) {
        return new LocationEntity(
                locationDTO.getId(),
                locationDTO.getLocationName(),
                locationTypeMapper.mapDTOToEntity(locationDTO.getLocationType()),
                locationDTO.getParentLocation() != null ? mapDTOToEntity(locationDTO.getParentLocation()) : null
        );
    }
}
