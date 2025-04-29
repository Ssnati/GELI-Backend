package com.edu.uptc.gelibackend.mappers;

import com.edu.uptc.gelibackend.dtos.LocationDTO;
import com.edu.uptc.gelibackend.entities.LocationEntity;
import org.springframework.stereotype.Component;

@Component
public class LocationMapper {

    public LocationDTO toDTO(LocationEntity locationEntity) {
        return LocationDTO.builder()
                .id(locationEntity.getId())
                .locationName(locationEntity.getLocationName())
                .build();
    }

    public LocationEntity toEntity(LocationDTO locationDTO) {
        return LocationEntity.builder()
                .id(locationDTO.getId())
                .locationName(locationDTO.getLocationName())
                .build();
    }
}