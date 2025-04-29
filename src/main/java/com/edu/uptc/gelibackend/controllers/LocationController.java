package com.edu.uptc.gelibackend.controllers;

import com.edu.uptc.gelibackend.dtos.LocationDTO;
import com.edu.uptc.gelibackend.services.LocationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/locations")
@RequiredArgsConstructor
@PreAuthorize("hasRole('QUALITY-ADMIN-USER')")
public class LocationController {

    private final LocationService locationService;

    @GetMapping
    @PreAuthorize("hasAuthority('LOCATION_READ')")
    public ResponseEntity<List<LocationDTO>> getAllLocations() {
        List<LocationDTO> locations = locationService.findAll();
        return locations.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(locations);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('LOCATION_READ')")
    public ResponseEntity<LocationDTO> getLocationById(@PathVariable Long id) {
        return locationService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasAuthority('LOCATION_WRITE')")
    public ResponseEntity<LocationDTO> createLocation(@RequestBody LocationDTO dto) {
        LocationDTO created = locationService.create(dto);
        return ResponseEntity.status(201).body(created);
    }
}
