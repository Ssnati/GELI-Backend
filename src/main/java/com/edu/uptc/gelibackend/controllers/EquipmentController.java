package com.edu.uptc.gelibackend.controllers;

import com.edu.uptc.gelibackend.dtos.EquipmentCreationDTO;
import com.edu.uptc.gelibackend.dtos.EquipmentResponseDTO;
import com.edu.uptc.gelibackend.dtos.EquipmentUpdateDTO;
import com.edu.uptc.gelibackend.filters.EquipmentFilterDTO;
import com.edu.uptc.gelibackend.services.EquipmentService;
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
 * Controller for managing laboratory equipment.
 * Provides endpoints for CRUD operations, filtering, and validation.
 *
 * <p>Requirements:</p>
 * <ul>
 *   <li>JWT authentication is mandatory.</li>
 *   <li>Specific permissions are required for each operation:</li>
 *   <ul>
 *     <li>'EQUIPMENT_READ' for read operations.</li>
 *     <li>'EQUIPMENT_WRITE' for write operations.</li>
 *   </ul>
 *   <li>The user must have the role 'QUALITY-ADMIN-USER'.</li>
 * </ul>
 */
@RestController
@RequestMapping("/api/v1/equipments")
@RequiredArgsConstructor
@Tag(
        name = "Equipments Management",
        description = """
                Complete management of laboratory equipment.
                This API provides CRUD operations and advanced filtering for laboratory equipment.
                """
)
@PreAuthorize("hasRole('QUALITY-ADMIN-USER')")
public class EquipmentController {

    private final EquipmentService service;

    /**
     * Retrieve all registered laboratory equipment.
     *
     * @return A list of {@link EquipmentResponseDTO} or a 204 status if no equipment is found.
     */
    @Operation(
            summary = "Retrieve all equipments",
            description = """
                    Fetch a list of all registered laboratory equipments.
                    Requirements:
                    - The user must have the 'EQUIPMENT_READ' authority.
                    - The user must have the role 'QUALITY-ADMIN-USER'.
                    """,
            tags = {"Equipments"}
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved the list of equipments.",
                    content = @Content(
                            array = @ArraySchema(
                                    schema = @Schema(implementation = EquipmentResponseDTO.class)
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "204",
                    description = "No equipments found."
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized. The user is not authenticated.",
                    content = @Content(schema = @Schema())
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden. The user does not have the required permissions.",
                    content = @Content(schema = @Schema())
            )
    })
    @GetMapping
    @PreAuthorize("hasAuthority('EQUIPMENT_READ')")
    public ResponseEntity<List<EquipmentResponseDTO>> getAll() {
        List<EquipmentResponseDTO> list = service.findAll();
        return list.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(list);
    }

    /**
     * Retrieve specific equipment by its ID.
     *
     * @param id The ID of the equipment.
     * @return The {@link EquipmentResponseDTO} if found, or a 404 status if not found.
     */
    @Operation(
            summary = "Retrieve equipment by ID",
            description = "Fetch a specific equipment by its unique ID.",
            tags = {"Equipments"}
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved the equipment.",
                    content = @Content(schema = @Schema(implementation = EquipmentResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Equipment not found."
            )
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('EQUIPMENT_READ')")
    public ResponseEntity<EquipmentResponseDTO> getById(@PathVariable Long id) {
        EquipmentResponseDTO equipment = service.findById(id);
        return equipment != null ? ResponseEntity.ok(equipment) : ResponseEntity.notFound().build();
    }

    /**
     * Check if equipment exists by its inventory number.
     *
     * @param inventoryNumber The inventory number to check.
     * @return True if the equipment exists, false otherwise.
     */
    @Operation(
            summary = "Check equipment existence by inventory number",
            description = "Verify if an equipment exists using its inventory number.",
            tags = {"Equipments"}
    )
    @ApiResponse(
            responseCode = "200",
            description = "Successfully checked the existence of the equipment."
    )
    @GetMapping("/exists-by-inventory-number")
    @PreAuthorize("hasAuthority('EQUIPMENT_READ')")
    public ResponseEntity<Boolean> existsByInventoryNumber(@RequestParam String inventoryNumber) {
        boolean exists = service.existsByInventoryNumber(inventoryNumber);
        return ResponseEntity.ok(exists);
    }

    /**
     * Check if equipment exists by its name.
     *
     * @param equipmentName The name of the equipment to check.
     * @return True if the equipment exists, false otherwise.
     */
    @Operation(
            summary = "Check equipment existence by name",
            description = "Verify if an equipment exists using its name.",
            tags = {"Equipments"}
    )
    @ApiResponse(
            responseCode = "200",
            description = "Successfully checked the existence of the equipment."
    )
    @GetMapping("/exists-by-name")
    @PreAuthorize("hasAuthority('EQUIPMENT_READ')")
    public ResponseEntity<Boolean> existsByName(@RequestParam String equipmentName) {
        boolean exists = service.existsByName(equipmentName);
        return ResponseEntity.ok(exists);
    }

    /**
     * Create a new equipment.
     *
     * @param dto The {@link EquipmentCreationDTO} containing the equipment details.
     * @return The created {@link EquipmentResponseDTO}.
     */
    @Operation(
            summary = "Create a new equipment",
            description = "Add a new equipment to the system.",
            tags = {"Equipments"}
    )
    @ApiResponse(
            responseCode = "201",
            description = "Successfully created the equipment.",
            content = @Content(schema = @Schema(implementation = EquipmentResponseDTO.class))
    )
    @PostMapping
    @PreAuthorize("hasAuthority('EQUIPMENT_WRITE')")
    public ResponseEntity<EquipmentResponseDTO> create(@RequestBody EquipmentCreationDTO dto) {
        EquipmentResponseDTO created = service.create(dto);
        return ResponseEntity.status(201).body(created);
    }

    /**
     * Update an existing equipment.
     *
     * @param id  The ID of the equipment to update.
     * @param dto The {@link EquipmentUpdateDTO} containing the updated details.
     * @return The updated {@link EquipmentResponseDTO}.
     */
    @Operation(
            summary = "Update an existing equipment",
            description = "Modify the details of an existing equipment.",
            tags = {"Equipments"}
    )
    @ApiResponse(
            responseCode = "200",
            description = "Successfully updated the equipment.",
            content = @Content(schema = @Schema(implementation = EquipmentResponseDTO.class))
    )
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('EQUIPMENT_WRITE')")
    public ResponseEntity<EquipmentResponseDTO> update(@PathVariable Long id, @RequestBody EquipmentUpdateDTO dto) {
        EquipmentResponseDTO updated = service.update(id, dto);
        return ResponseEntity.ok(updated);
    }

    /**
     * Filter equipment based on specific criteria.
     *
     * @param filter The {@link EquipmentFilterDTO} containing the filter criteria.
     * @return A list of {@link EquipmentResponseDTO} matching the criteria.
     */
    @Operation(
            summary = "Filter equipments",
            description = """
                    Retrieve a list of equipments based on specific filter criteria.
                    The filter can include the following attributes:
                    - `equipmentName` (String): The name of the equipment.
                    - `brandId` (Long): The ID of the brand.
                    - `laboratoryId` (Long): The ID of the laboratory.
                    - `availability` (Boolean): The availability status of the equipment.
                    - `functionId` (Long): The ID of the function.
                    The filter is optional, and you can provide any combination of the above attributes.
                    Requirements:
                        - The user must have the 'EQUIPMENT_READ' authority.
                        - The user must have the role 'QUALITY-ADMIN-USER'.
                    """,
            tags = {"Equipments"}
    )
    @ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved the filtered list of equipments.",
            content = @Content(
                    array = @ArraySchema(
                            schema = @Schema(implementation = EquipmentResponseDTO.class)
                    )
            )
    )
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