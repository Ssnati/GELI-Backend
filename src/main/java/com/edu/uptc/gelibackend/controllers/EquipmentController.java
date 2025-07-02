package com.edu.uptc.gelibackend.controllers;

import com.edu.uptc.gelibackend.dtos.EquipmentCreationDTO;
import com.edu.uptc.gelibackend.dtos.EquipmentResponseDTO;
import com.edu.uptc.gelibackend.dtos.EquipmentUpdateDTO;
import com.edu.uptc.gelibackend.dtos.PageResponse;
import com.edu.uptc.gelibackend.dtos.equipment.EquipmentAvailabilityResponseDTO;
import com.edu.uptc.gelibackend.dtos.equipment.EquipmentByUserResponseDTO;
import com.edu.uptc.gelibackend.dtos.equipment.EquipmentFilterResponseDTO;
import com.edu.uptc.gelibackend.dtos.equipment.EquipmentFunctionsResponseDTO;
import com.edu.uptc.gelibackend.filters.EquipmentFilterDTO;
import com.edu.uptc.gelibackend.services.EquipmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
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
     * Fetch all registered laboratory equipment with pagination.
     *
     * @return A page of {@link EquipmentResponseDTO} or a 204 status if no equipment is found.
     */
    @Operation(
            summary = "Fetch all equipments",
            description = """
                    Fetch a paged list of all registered laboratory equipments.
                    Requirements:
                    - The user must have the 'EQUIPMENT_READ' authority.
                    - The user must have the role 'QUALITY-ADMIN-USER'.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully Fetched the list of equipments.",
                    content = @Content(
                            schema = @Schema(
                                    implementation = PageResponse.class,
                                    type = "object"),
                            mediaType = "application/json")
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
    public ResponseEntity<PageResponse<EquipmentResponseDTO>> getAll(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10000") int size) {
        PageResponse<EquipmentResponseDTO> response = service.findAll(page, size);
        return response.getContent().isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(response);
    }

    /**
     * Fetch specific equipment by its ID.
     *
     * @param id The ID of the equipment.
     * @return The {@link EquipmentResponseDTO} if found, or a 404 status if not found.
     */
    @Operation(
            summary = "Fetch equipment by ID",
            description = "Fetch a specific equipment by its unique ID."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully Fetchd the equipment.",
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
            description = "Verify if an equipment exists using its inventory number."
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

    @GetMapping("/exists-by-update-inventory-number")
    @PreAuthorize("hasAuthority('EQUIPMENT_READ')")
    public ResponseEntity<Boolean> existsByInventoryNumber(
            @RequestParam String inventoryNumber,
            @RequestParam(required = false) Long excludeId
    ) {
        boolean exists;

        if (excludeId != null) {
            // Excluir el laboratorio con el ID especificado
            exists = service.existsByInventoryNumberExcludingId(inventoryNumber, excludeId);
        } else {
            // Validación normal (caso creación)
            exists = service.existsByInventoryNumber(inventoryNumber);
        }

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
            description = "Verify if an equipment exists using its name."
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
            description = "Add a new equipment to the system."
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
            description = "Modify the details of an existing equipment."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Successfully updated the equipment.",
            content = @Content(schema = @Schema(implementation = EquipmentResponseDTO.class))
    )
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('EQUIPMENT_WRITE')")
    public ResponseEntity<EquipmentResponseDTO> update(
            @PathVariable Long id,
            @RequestBody EquipmentUpdateDTO dto
    ) {
        EquipmentResponseDTO updated = service.update(id, dto);
        return ResponseEntity.ok(updated);
    }

    /**
     * Filter equipment based on specific criteria with pagination.
     *
     * @param filter The {@link EquipmentFilterDTO} containing the filter criteria.
     * @param page The page number to retrieve (zero-based index).
     * @param size The number of items per page.
     * @return A paginated list of {@link EquipmentResponseDTO} matching the criteria.
     */
    @Operation(
            summary = "Filter equipments with pagination",
            description = """
                    Fetch a paginated list of equipments based on specific filter criteria.
                    The filter can include the following attributes:
                    - `equipmentName` (String): The name of the equipment.
                    - `brandId` (Long): The ID of the brand.
                    - `laboratoryId` (Long): The ID of the laboratory.
                    - `availability` (Boolean): The availability status of the equipment.
                    - `functionId` (Long): The ID of the function.
                    The filter is optional, and you can provide any combination of the above attributes.
                    Pagination parameters:
                    - `page` (int): The page number to retrieve (zero-based index).
                    - `size` (int): The number of items per page.
                    Requirements:
                        - The user must have the 'EQUIPMENT_READ' authority.
                        - The user must have the role 'QUALITY-ADMIN-USER'.
                    """
    )
    @ApiResponse(
            responseCode = "200",
            description = "Successfully fetched the paginated and filtered list of equipments.",
            content = @Content(
                    schema = @Schema(
                            implementation = PageResponse.class,
                            type = "object"
                    ),
                    mediaType = "application/json"
            )
    )
    @PostMapping("/filter")
    @PreAuthorize("hasAuthority('EQUIPMENT_READ')")
    public ResponseEntity<PageResponse<EquipmentFilterResponseDTO>> filter(@RequestBody EquipmentFilterDTO filter,
                                                                                       @RequestParam(defaultValue = "0") int page,
                                                                                       @RequestParam(defaultValue = "10") int size) {
        PageResponse<EquipmentFilterResponseDTO> response = service.filter(filter,page, size);
        if (response.getContent().isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping("/authorized/by-lab/{labId}")
    @PreAuthorize("hasAuthority('EQUIPMENT_READ')")
    public ResponseEntity<List<EquipmentByUserResponseDTO>> getAuthorizedEquipmentsByUserAndLab(
            Authentication authentication,
            @PathVariable Long labId
    ) {
        Jwt jwt = (Jwt) authentication.getPrincipal();
        String email = jwt.getClaim("email");
        List<EquipmentByUserResponseDTO> list = service.getAuthorizedEquipmentsByUserAndLab(email, labId);
        return list.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(list);
    }

    @GetMapping("/{id}/availability")
    @PreAuthorize("hasAuthority('EQUIPMENT_READ')")
    public ResponseEntity<EquipmentAvailabilityResponseDTO> getAvailability(@PathVariable Long id) {
        return ResponseEntity.ok(service.getAvailability(id));
    }

    @GetMapping("/{id}/functions")
    @PreAuthorize("hasAuthority('EQUIPMENT_READ')")
    public ResponseEntity<EquipmentFunctionsResponseDTO> getFunctionsById(@PathVariable Long id) {
        EquipmentFunctionsResponseDTO equipment = service.findFunctionsById(id);
        return equipment != null ? ResponseEntity.ok(equipment) : ResponseEntity.notFound().build();
    }

}
