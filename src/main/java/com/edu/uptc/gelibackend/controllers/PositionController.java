package com.edu.uptc.gelibackend.controllers;

import com.edu.uptc.gelibackend.dtos.PositionDTO;
import com.edu.uptc.gelibackend.services.PositionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/positions")
@RequiredArgsConstructor
public class PositionController {

    private final PositionService positionService;

    /**
     * GET /api/v1/positions → all positions
     */
    @GetMapping
    @PreAuthorize("hasRole('QUALITY-ADMIN-USER') and hasAuthority('POSITION_READ')")
    public ResponseEntity<List<PositionDTO>> getAll() {
        List<PositionDTO> list = positionService.getAll();
        return list.isEmpty()
                ? ResponseEntity.noContent().build()
                : ResponseEntity.ok(list);
    }

    /**
     * POST /api/v1/positions → create new position
     */
    @PostMapping
    @PreAuthorize("hasRole('QUALITY-ADMIN-USER') and hasAuthority('POSITION_WRITE')")
    public ResponseEntity<PositionDTO> create(@RequestBody PositionDTO dto) {
        PositionDTO created = positionService.create(dto);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.getId())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }
}
