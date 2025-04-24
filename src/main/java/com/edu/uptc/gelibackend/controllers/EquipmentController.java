package com.edu.uptc.gelibackend.controllers;

import com.edu.uptc.gelibackend.dtos.EquipmentDTO;
import com.edu.uptc.gelibackend.services.EquipmentService;
import com.edu.uptc.gelibackend.filters.EquipmentFilterDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/equipments")
@RequiredArgsConstructor
@PreAuthorize("hasRole('QUALITY-ADMIN-USER')")
public class EquipmentController {

    private final EquipmentService service;

    @GetMapping
    @PreAuthorize("hasAuthority('EQUIPMENT_READ')")
    public ResponseEntity<List<EquipmentDTO>> getAll() {
        List<EquipmentDTO> list = service.findAll();
        return list.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(list);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('EQUIPMENT_READ')")
    public ResponseEntity<EquipmentDTO> getById(@PathVariable Long id) {
        EquipmentDTO equipment = service.findById(id);
        return equipment != null ? ResponseEntity.ok(equipment) : ResponseEntity.notFound().build();
    }

    @PostMapping
    @PreAuthorize("hasAuthority('EQUIPMENT_WRITE')")
    public ResponseEntity<EquipmentDTO> create(@RequestBody EquipmentDTO dto) {
        EquipmentDTO created = service.create(dto);
        return ResponseEntity.status(201).body(created);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('EQUIPMENT_WRITE')")
    public ResponseEntity<EquipmentDTO> update(@PathVariable Long id, @RequestBody EquipmentDTO dto) {
        EquipmentDTO updated = service.update(id, dto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('EQUIPMENT_DELETE')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (service.delete(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/filter")
    @PreAuthorize("hasAuthority('EQUIPMENT_READ')")
    public ResponseEntity<List<EquipmentDTO>> filter(@RequestBody EquipmentFilterDTO filter) {
        List<EquipmentDTO> filteredList = service.filter(filter);
        if (filteredList.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(filteredList);
    }
}