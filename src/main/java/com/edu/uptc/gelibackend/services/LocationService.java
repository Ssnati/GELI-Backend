package com.edu.uptc.gelibackend.services;

import com.edu.uptc.gelibackend.dtos.LocationDTO;
import com.edu.uptc.gelibackend.entities.LocationEntity;
import com.edu.uptc.gelibackend.mappers.LocationMapper;
import com.edu.uptc.gelibackend.repositories.LocationRepository;
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
    private final LocationMapper locationMapper;

    public List<LocationDTO> findAll() {
        return locationRepository.findAll().stream()
                .map(locationMapper::toDTO)
                .collect(Collectors.toList());
    }

    public Optional<LocationDTO> findById(Long id) {
        return locationRepository.findById(id)
                .map(locationMapper::toDTO);
    }

    public LocationDTO create(LocationDTO dto) {
        LocationEntity entity = locationMapper.toEntity(dto);

        if (locationRepository.findAll().stream()
                .anyMatch(loc -> loc.getLocationName().equalsIgnoreCase(entity.getLocationName()))) {
            throw new IllegalArgumentException("Location's name must be unique");
        }

        return locationMapper.toDTO(locationRepository.save(entity));
    }
}
