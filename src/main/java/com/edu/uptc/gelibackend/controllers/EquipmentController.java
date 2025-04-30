package com.edu.uptc.gelibackend.controllers;

import com.edu.uptc.gelibackend.dtos.EquipmentCreationDTO;
import com.edu.uptc.gelibackend.dtos.EquipmentResponseDTO;
import com.edu.uptc.gelibackend.services.EquipmentService;
import com.edu.uptc.gelibackend.filters.EquipmentFilterDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
@PreAuthorize("hasRole('QUALITY-ADMIN-USER')")
public class EquipmentController {

    private final EquipmentService service;

    /**
     * Obtiene todos los equipos registrados en el sistema.
     *
     * @return Lista de equipos con respuesta HTTP 200, o 204 si no hay contenido
     */
    @Operation(
            summary = "Obtener todos los equipos",
            description = "Recupera todos los equipos disponibles en el sistema. Requiere permiso de lectura."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Equipos encontrados",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = EquipmentResponseDTO.class)))
            ),
            @ApiResponse(responseCode = "204", description = "No hay equipos registrados")
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

    @PostMapping
    @PreAuthorize("hasAuthority('EQUIPMENT_WRITE')")
    public ResponseEntity<EquipmentResponseDTO> create(@RequestBody EquipmentCreationDTO dto) {
        EquipmentResponseDTO created = service.create(dto);
        return ResponseEntity.status(201).body(created);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('EQUIPMENT_WRITE')")
    public ResponseEntity<EquipmentResponseDTO> update(@PathVariable Long id, @RequestBody EquipmentResponseDTO dto) {
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