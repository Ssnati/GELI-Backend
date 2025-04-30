package com.edu.uptc.gelibackend.controllers;

import com.edu.uptc.gelibackend.dtos.LaboratoryDTO;
import com.edu.uptc.gelibackend.filters.LaboratoryFilterDTO;
import com.edu.uptc.gelibackend.services.LaboratoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/laboratories")
@RequiredArgsConstructor
@PreAuthorize("hasRole('QUALITY-ADMIN-USER')")
public class LaboratoryController {

    private final LaboratoryService service;

    @GetMapping
    @PreAuthorize("hasAuthority('LABORATORY_READ')")
    public ResponseEntity<List<LaboratoryDTO>> getAll() {
        List<LaboratoryDTO> list = service.findAll();
        return list.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(list);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('LABORATORY_READ')")
    public ResponseEntity<LaboratoryDTO> getById(@PathVariable Long id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasAuthority('LABORATORY_WRITE')")
    public ResponseEntity<LaboratoryDTO> create(@RequestBody LaboratoryDTO dto) {
        LaboratoryDTO created = service.create(dto);
        return ResponseEntity.status(201).body(created);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('LABORATORY_WRITE')")
    public ResponseEntity<LaboratoryDTO> update(@PathVariable Long id, @RequestBody LaboratoryDTO dto) {
        LaboratoryDTO updated = service.update(id, dto);
        return ResponseEntity.ok(updated);
    }

    @PostMapping("/filter")
    @PreAuthorize("hasAuthority('LABORATORY_READ')")
    public ResponseEntity<List<LaboratoryDTO>> filterLaboratories(@RequestBody LaboratoryFilterDTO filters) {
        List<LaboratoryDTO> result = service.filterLaboratories(filters);
        if (result.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(result);
    }
}