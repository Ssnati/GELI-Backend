package com.edu.uptc.gelibackend.controllers;

import com.edu.uptc.gelibackend.dtos.LaboratoryDTO;
import com.edu.uptc.gelibackend.dtos.PageResponse;
import com.edu.uptc.gelibackend.filters.LaboratoryFilterDTO;
import com.edu.uptc.gelibackend.services.LaboratoryService;
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
 * Controller for managing laboratories.
 * Provides endpoints for CRUD operations and filtering laboratories.
 *
 * <p>Requirements:</p>
 * <ul>
 *   <li>JWT authentication is mandatory.</li>
 *   <li>Specific permissions are required for each operation:</li>
 *   <ul>
 *     <li>'LABORATORY_READ' for read operations.</li>
 *     <li>'LABORATORY_WRITE' for write operations.</li>
 *   </ul>
 *   <li>The user must have the role 'QUALITY-ADMIN-USER' or 'AUTHORIZED-USER'.</li>
 * </ul>
 */
@RestController
@RequestMapping("/api/v1/laboratories")
@RequiredArgsConstructor
@Tag(
        name = "Laboratories Management",
        description = """
                Complete management of laboratories.
                This API provides CRUD operations and advanced filtering for laboratories.
                """
)
@PreAuthorize("hasRole('QUALITY-ADMIN-USER') or hasRole('AUTHORIZED-USER')")
public class LaboratoryController {

    private final LaboratoryService service;

    /**
     * Retrieve all laboratories.
     *
     * @return A list of {@link LaboratoryDTO} or a 204 status if no laboratories are found.
     */
    @Operation(
            summary = "Retrieve all laboratories",
            description = """
                    Fetch a list of all registered laboratories.
                    Requirements:
                    - The user must have the 'LABORATORY_READ' authority.
                    - The user must have the role 'QUALITY-ADMIN-USER' or 'AUTHORIZED-USER'.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved the list of laboratories.",
                    content = @Content(
                            array = @ArraySchema(
                                    schema = @Schema(implementation = LaboratoryDTO.class)
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "204",
                    description = "No laboratories found."
            )
    })
    @GetMapping
    @PreAuthorize("hasAuthority('LABORATORY_READ')")
    public ResponseEntity<List<LaboratoryDTO>> getAll() {
        List<LaboratoryDTO> list = service.findAll();
        return list.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(list);
    }

    /**
     * Retrieve a specific laboratory by its ID.
     *
     * @param id The ID of the laboratory.
     * @return The {@link LaboratoryDTO} if found, or a 404 status if not found.
     */
    @Operation(
            summary = "Retrieve laboratory by ID",
            description = """
                    Fetch a specific laboratory by its unique ID.
                    Requirements:
                    - The user must have the 'LABORATORY_READ' authority.
                    - The user must have the role 'QUALITY-ADMIN-USER' or 'AUTHORIZED-USER'.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved the laboratory.",
                    content = @Content(schema = @Schema(implementation = LaboratoryDTO.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Laboratory not found."
            )
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('LABORATORY_READ')")
    public ResponseEntity<LaboratoryDTO> getById(@PathVariable Long id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Check if a laboratory exists by its name.
     *
     * @param laboratoryName The name of the laboratory to check.
     * @return True if the laboratory exists, false otherwise.
     */
    @Operation(
            summary = "Check laboratory existence by name",
            description = """
                    Verify if a laboratory exists using its name.
                    Requirements:
                    - The user must have the 'LABORATORY_READ' authority.
                    - The user must have the role 'QUALITY-ADMIN-USER' or 'AUTHORIZED-USER'.
                    """
    )
    @ApiResponse(
            responseCode = "200",
            description = "Successfully checked the existence of the laboratory."
    )
    @GetMapping("/exists-by-name")
    @PreAuthorize("hasAuthority('LABORATORY_READ')")
    public ResponseEntity<Boolean> existsByName(@RequestParam String laboratoryName) {
        boolean exists = service.existsByName(laboratoryName);
        return ResponseEntity.ok(exists);
    }

    /**
     * Create a new laboratory.
     *
     * @param dto The {@link LaboratoryDTO} containing the laboratory details.
     * @return The created {@link LaboratoryDTO}.
     */
    @Operation(
            summary = "Create a new laboratory",
            description = """
                    Add a new laboratory to the system.
                    Requirements:
                    - The user must have the 'LABORATORY_WRITE' authority.
                    - The user must have the role 'QUALITY-ADMIN-USER'.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Successfully created the laboratory.",
                    content = @Content(schema = @Schema(implementation = LaboratoryDTO.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request. The laboratory could not be created."
            )
    })
    @PostMapping
    @PreAuthorize("hasAuthority('LABORATORY_WRITE')")
    public ResponseEntity<LaboratoryDTO> create(@RequestBody LaboratoryDTO dto) {
        LaboratoryDTO created = service.create(dto);
        return ResponseEntity.status(201).body(created);
    }

    /**
     * Update an existing laboratory.
     *
     * @param id  The ID of the laboratory to update.
     * @param dto The {@link LaboratoryDTO} containing the updated details.
     * @return The updated {@link LaboratoryDTO}.
     */
    @Operation(
            summary = "Update an existing laboratory",
            description = """
                    Modify the details of an existing laboratory.
                    Requirements:
                    - The user must have the 'LABORATORY_WRITE' authority.
                    - The user must have the role 'QUALITY-ADMIN-USER'.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully updated the laboratory.",
                    content = @Content(schema = @Schema(implementation = LaboratoryDTO.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Laboratory not found."
            )
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('LABORATORY_WRITE')")
    public ResponseEntity<LaboratoryDTO> update(@PathVariable Long id, @RequestBody LaboratoryDTO dto) {
        LaboratoryDTO updated = service.update(id, dto);
        return ResponseEntity.ok(updated);
    }

    /**
     * Filter laboratories based on specific criteria.
     *
     * @param filters The {@link LaboratoryFilterDTO} containing the filter criteria.
     * @return A list of {@link LaboratoryDTO} matching the criteria, or a 204 status if no laboratories are found.
     */
    @Operation(
            summary = "Filter laboratories",
            description = """
                    Fetch a list of laboratories based on specific filter criteria.
                    The filter can include the following attributes:
                    - `laboratoryName` (String): The name of the laboratory.
                    - `laboratoryAvailability` (Boolean): Indicates whether the laboratory is available.
                    - `location` (Long): The ID of the location where the laboratory is located.
                    - `laboratoryDescription` (String): A description of the laboratory.
                    - `laboratoryObservations` (String): Any observations related to the laboratory.
                    - `equipmentCountFrom` (Integer): The minimum number of equipment in the laboratory.
                    - `equipmentCountTo` (Integer): The maximum number of equipment in the laboratory.
                    
                    The filter is optional, and you can provide any combination of the above attributes.
                    ---
                    Requirements:
                    - The user must have the 'LABORATORY_READ' authority.
                    - The user must have the role 'QUALITY-ADMIN-USER' or 'AUTHORIZED-USER'.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved the filtered list of laboratories.",
                    content = @Content(
                            array = @ArraySchema(
                                    schema = @Schema(implementation = LaboratoryDTO.class)
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "204",
                    description = "No laboratories found."
            )
    })
    @PostMapping("/filter")
    @PreAuthorize("hasAuthority('LABORATORY_READ')")
    public ResponseEntity<PageResponse<LaboratoryDTO>> filterLaboratories(
            @RequestBody LaboratoryFilterDTO filters,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        PageResponse<LaboratoryDTO> response = service.filterLaboratories(filters, page, size);
        return response.getContent().isEmpty()
                ? ResponseEntity.noContent().build()
                : ResponseEntity.ok(response);
    }

}