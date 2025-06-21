package com.edu.uptc.gelibackend.services;

import com.edu.uptc.gelibackend.dtos.LocationDTO;
import com.edu.uptc.gelibackend.dtos.PositionDTO;
import com.edu.uptc.gelibackend.entities.LocationEntity;
import com.edu.uptc.gelibackend.entities.PositionEntity;
import com.edu.uptc.gelibackend.mappers.LocationMapper;
import com.edu.uptc.gelibackend.repositories.LocationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional
    public LocationDTO update(Long id, LocationDTO dto) {
        LocationEntity existing = locationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Location not found with id: " + id));

        // Check uniqueness ignoring current entity
        if (locationRepository.existsByLocationNameIgnoreCaseAndIdNot(dto.getLocationName(), id)) {
            throw new RuntimeException("Another position with name '" + dto.getLocationName() + "' already exists.");
        }

        existing.setLocationName(dto.getLocationName());
        LocationEntity saved = locationRepository.save(existing);
        return locationMapper.toDTO(saved);
    }

    @Transactional(readOnly = true)
    public boolean existsByNameIgnoreCase(String locationName) {
        return locationRepository.existsByLocationNameIgnoreCase(locationName);
    }
}
