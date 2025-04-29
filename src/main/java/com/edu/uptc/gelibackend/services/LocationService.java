package com.edu.uptc.gelibackend.services;

import com.edu.uptc.gelibackend.dtos.LocationDTO;
import com.edu.uptc.gelibackend.entities.LocationEntity;
import com.edu.uptc.gelibackend.entities.LocationTypeEntity;
import com.edu.uptc.gelibackend.mappers.LocationMapper;
import com.edu.uptc.gelibackend.repositories.LocationRepository;
import com.edu.uptc.gelibackend.repositories.LocationTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.ws.rs.NotFoundException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LocationService {

    private final LocationRepository locationRepository;
    private final LocationTypeRepository locationTypeRepository;
    private final LocationMapper locationMapper;

    public List<LocationDTO> findAll() {
        return locationRepository.findAll().stream()
                .map(locationMapper::mapEntityToDTO)
                .collect(Collectors.toList());
    }

    public Optional<LocationDTO> findById(Long id) {
        return locationRepository.findById(id)
                .map(locationMapper::mapEntityToDTO);
    }

    public LocationDTO create(LocationDTO dto) {
        LocationEntity entity = locationMapper.mapDTOToEntity(dto);

        // Validar tipo de lugar
        Long locationTypeId = dto.getLocationType().getId();
        LocationTypeEntity type = locationTypeRepository.findById(locationTypeId)
                .orElseThrow(() -> new NotFoundException("LocationType not found"));
        entity.setLocationType(type);

        // Validar lugar padre si existe
        if (dto.getParentLocation() != null && dto.getParentLocation().getId() != null) {
            LocationEntity parent = locationRepository.findById(dto.getParentLocation().getId())
                    .orElseThrow(() -> new NotFoundException("Parent location not found"));
            entity.setParentLocation(parent);
        }

        return locationMapper.mapEntityToDTO(locationRepository.save(entity));
    }
}
