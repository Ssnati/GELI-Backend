package com.edu.uptc.gelibackend.controllers;

import com.edu.uptc.gelibackend.dtos.EquipmentUseDTO;
import com.edu.uptc.gelibackend.dtos.EquipmentUseResponseDTO;
import com.edu.uptc.gelibackend.services.EquipmentUseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1/equipment-use")
@RequiredArgsConstructor
@PreAuthorize("hasRole('QUALITY-ADMIN-USER') or hasRole('AUTHORIZED-USER')")
public class EquipmentUseController {

    private final EquipmentUseService service;

    @PreAuthorize("hasAuthority('EQUIPMENT_USE_WRITE')")
    @PostMapping("/start")
    public ResponseEntity<EquipmentUseResponseDTO> startEquipmentUse(@RequestBody EquipmentUseDTO equipmentUseDTO) {
        Optional<EquipmentUseResponseDTO> response = service.startEquipmentUse(equipmentUseDTO);
        return response.map(useDTO -> ResponseEntity.status(201).body(useDTO)).orElseGet(() -> ResponseEntity.badRequest().build());
    }

    @PreAuthorize("hasAuthority('EQUIPMENT_USE_WRITE')")
    @PutMapping("/{id}/end")
    public ResponseEntity<EquipmentUseResponseDTO> endEquipmentUse(@PathVariable Long id) {
        Optional<EquipmentUseResponseDTO> response = service.endEquipmentUse(id);
        return response.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }
}
