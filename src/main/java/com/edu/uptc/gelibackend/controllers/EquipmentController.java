package com.edu.uptc.gelibackend.controllers;

import com.edu.uptc.gelibackend.dtos.EquipmentCreationDTO;
import com.edu.uptc.gelibackend.dtos.EquipmentResponseDTO;
import com.edu.uptc.gelibackend.dtos.EquipmentUpdateDTO;
import com.edu.uptc.gelibackend.services.EquipmentService;
import com.edu.uptc.gelibackend.filters.EquipmentFilterDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador para la gestión de equipos de laboratorio.
 * Proporciona operaciones CRUD y filtrado avanzado de equipos.
 * Requiere autenticación JWT y permisos específicos para cada operación.
 */
@RestController
@RequestMapping("/api/v1/equipments")
@RequiredArgsConstructor
@Tag(
        name = "Equipments",
        description = """
                    Equipment management API.
                    This API provides CRUD operations and advanced filtering for laboratory equipment.
                    Requirements:
                    - JWT authentication is mandatory.
                    - Specific permissions are required for each operation:
                      - 'EQUIPMENT_READ' for read operations.
                      - 'EQUIPMENT_WRITE' for write operations.
                    - The user must have the role 'QUALITY-ADMIN-USER'.
                """
)
@PreAuthorize("hasRole('QUALITY-ADMIN-USER')")
public class EquipmentController {

    private final EquipmentService service;

    @Operation(
            summary = "Get all equipments",
            description = "Retrieve a list of all registered equipments. Requires 'EQUIPMENT_READ' permission."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "List of equipments",
                    content = @Content(
                            array = @ArraySchema(
                                    schema = @Schema(
                                            implementation = EquipmentResponseDTO.class)))
            ),
            @ApiResponse(responseCode = "204", description = "No equipments found"),
    })
    @GetMapping
    @PreAuthorize("hasAuthority('EQUIPMENT_READ')")
    public ResponseEntity<List<EquipmentResponseDTO>> getAll() {
        List<EquipmentResponseDTO> list = service.findAll();
        return list.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(list);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('EQUIPMENT_READ')")
    public ResponseEntity<EquipmentResponseDTO> getById(@PathVariable Long id) {
        EquipmentResponseDTO equipment = service.findById(id);
        return equipment != null ? ResponseEntity.ok(equipment) : ResponseEntity.notFound().build();
    }

    @GetMapping("/exists-by-inventory-number")
    @PreAuthorize("hasAuthority('EQUIPMENT_READ')")
    public ResponseEntity<Boolean> existsByInventoryNumber(@RequestParam String inventoryNumber) {
        boolean exists = service.existsByInventoryNumber(inventoryNumber);
        return ResponseEntity.ok(exists);
    }

    @GetMapping("/exists-by-name")
    @PreAuthorize("hasAuthority('EQUIPMENT_READ')")
    public ResponseEntity<Boolean> existsByName(@RequestParam String equipmentName) {
        boolean exists = service.existsByName(equipmentName);
        return ResponseEntity.ok(exists);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('EQUIPMENT_WRITE')")
    public ResponseEntity<EquipmentResponseDTO> create(@RequestBody EquipmentCreationDTO dto) {
        EquipmentResponseDTO created = service.create(dto);
        return ResponseEntity.status(201).body(created);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('EQUIPMENT_WRITE')")
    public ResponseEntity<EquipmentResponseDTO> update(@PathVariable Long id, @RequestBody EquipmentUpdateDTO dto) {
        EquipmentResponseDTO updated = service.update(id, dto);
        return ResponseEntity.ok(updated);
    }


    @PostMapping("/filter")
    @PreAuthorize("hasAuthority('EQUIPMENT_READ')")
    public ResponseEntity<List<EquipmentResponseDTO>> filter(@RequestBody EquipmentFilterDTO filter) {
        List<EquipmentResponseDTO> filteredList = service.filter(filter);
        if (filteredList.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(filteredList);
    }
}