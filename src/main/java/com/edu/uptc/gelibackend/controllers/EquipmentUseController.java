package com.edu.uptc.gelibackend.controllers;

import com.edu.uptc.gelibackend.dtos.EquipmentUseDTO;
import com.edu.uptc.gelibackend.dtos.EquipmentUseResponseDTO;
import com.edu.uptc.gelibackend.services.EquipmentUseService;
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
import java.util.Optional;

/**
 * Controller for managing the use of laboratory equipment.
 * Provides endpoints for starting, ending, and retrieving equipment usage records.
 *
 * <p>Requirements:</p>
 * <ul>
 *   <li>JWT authentication is mandatory.</li>
 *   <li>Specific permissions are required for each operation:</li>
 *   <ul>
 *     <li>'EQUIPMENT_USE_READ' for read operations.</li>
 *     <li>'EQUIPMENT_USE_WRITE' for write operations.</li>
 *   </ul>
 *   <li>The user must have the role 'QUALITY-ADMIN-USER' or 'AUTHORIZED-USER'.</li>
 * </ul>
 */
@RestController
@RequestMapping("/api/v1/equipment-use")
@RequiredArgsConstructor
@Tag(
        name = "Equipment Use Management",
        description = """
                Management of laboratory equipment usage.
                This API provides endpoints for starting, ending, and retrieving equipment usage records.
                """
)
@PreAuthorize("hasRole('QUALITY-ADMIN-USER') or hasRole('AUTHORIZED-USER')")
public class EquipmentUseController {

    private final EquipmentUseService service;

    /**
     * Start the use of a specific equipment.
     *
     * @param equipmentUseDTO The {@link EquipmentUseDTO} containing the details of the equipment usage.
     * @return The created {@link EquipmentUseResponseDTO} if successful, or a 400 status if the request is invalid.
     */
    @Operation(
            summary = "Start equipment use",
            description = """
                    Start the usage of a specific equipment.
                    Requirements:
                    - The user must have the 'EQUIPMENT_USE_WRITE' authority.
                    - The user must have the role 'AUTHORIZED-USER'.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Successfully started the equipment use.",
                    content = @Content(schema = @Schema(implementation = EquipmentUseResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request. The equipment use could not be started."
            )
    })
    @PostMapping("/start") //start
    @PreAuthorize("hasAuthority('EQUIPMENT_USE_WRITE')")
    public ResponseEntity<EquipmentUseResponseDTO> startEquipmentUse(@RequestBody EquipmentUseDTO equipmentUseDTO) {
        Optional<EquipmentUseResponseDTO> response = service.startEquipmentUse(equipmentUseDTO);
        return response.map(useDTO -> ResponseEntity.status(201).body(useDTO)).orElseGet(() -> ResponseEntity.badRequest().build());
    }

    /**
     * End the use of a specific equipment.
     *
     * @param id The ID of the equipment usage record to end.
     * @return The updated {@link EquipmentUseResponseDTO} if successful, or a 404 status if the record is not found.
     */
    @Operation(
            summary = "End equipment use",
            description = """
                    End the usage of a specific equipment.
                    Requirements:
                    - The user must have the 'EQUIPMENT_USE_WRITE' authority.
                    - The user must have the role 'AUTHORIZED-USER'.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully ended the equipment use.",
                    content = @Content(schema = @Schema(implementation = EquipmentUseResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Equipment use record not found."
            )
    })
    @PutMapping("/{id}/end")
    @PreAuthorize("hasAuthority('EQUIPMENT_USE_WRITE')")
    public ResponseEntity<EquipmentUseResponseDTO> endEquipmentUse(@PathVariable Long id) {
        Optional<EquipmentUseResponseDTO> response = service.endEquipmentUse(id);
        return response.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Retrieve all equipment usage records.
     *
     * @return A list of {@link EquipmentUseResponseDTO} or a 204 status if no records are found.
     */
    @Operation(
            summary = "Retrieve all equipment usage records",
            description = """
                    Fetch a list of all equipment usage records.
                    Requirements:
                    - The user must have the 'EQUIPMENT_USE_READ' authority.
                    - The user must have the role 'QUALITY-ADMIN-USER' or 'AUTHORIZED-USER'.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved the list of equipment usage records.",
                    content = @Content(
                            array = @ArraySchema(
                                    schema = @Schema(implementation = EquipmentUseResponseDTO.class)
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "204",
                    description = "No equipment usage records found."
            )
    })
    @GetMapping
    @PreAuthorize("hasAuthority('EQUIPMENT_USE_READ')")
    public ResponseEntity<List<EquipmentUseResponseDTO>> getAllEquipmentUses() {
        List<EquipmentUseResponseDTO> list = service.getAllEquipmentUses();
        return list.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(list);
    }

    /**
     * Retrieve a specific equipment usage record by its ID.
     *
     * @param id The ID of the equipment usage record.
     * @return The {@link EquipmentUseResponseDTO} if found, or a 404 status if not found.
     */
    @Operation(
            summary = "Retrieve equipment usage record by ID",
            description = """
                    Fetch a specific equipment usage record by its unique ID.
                    Requirements:
                    - The user must have the 'EQUIPMENT_USE_READ' authority.
                    - The user must have the role 'AUTHORIZED-USER'.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved the equipment usage record.",
                    content = @Content(schema = @Schema(implementation = EquipmentUseResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Equipment usage record not found."
            )
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('EQUIPMENT_USE_READ')")
    public ResponseEntity<EquipmentUseResponseDTO> getEquipmentUse(@PathVariable Long id) {
        Optional<EquipmentUseResponseDTO> response = service.getEquipmentUse(id);
        return response.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }
}