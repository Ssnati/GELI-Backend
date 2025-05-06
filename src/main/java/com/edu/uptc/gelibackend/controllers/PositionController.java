package com.edu.uptc.gelibackend.controllers;

import com.edu.uptc.gelibackend.dtos.PositionDTO;
import com.edu.uptc.gelibackend.services.PositionService;
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
 * Controller for managing positions.
 * Provides endpoints for retrieving and creating positions.
 *
 * <p>Requirements:</p>
 * <ul>
 *   <li>JWT authentication is mandatory.</li>
 *   <li>Specific permissions are required for each operation:</li>
 *   <ul>
 *     <li>'POSITION_READ' for read operations.</li>
 *     <li>'POSITION_WRITE' for write operations.</li>
 *   </ul>
 *   <li>The user must have the role 'QUALITY-ADMIN-USER'.</li>
 * </ul>
 */
@RestController
@RequestMapping("/api/v1/positions")
@RequiredArgsConstructor
@Tag(
        name = "Positions Management",
        description = """
                Management of positions.
                This API provides endpoints for retrieving and creating positions.
                """
)
public class PositionController {

    private final PositionService positionService;

    /**
     * Retrieve all positions.
     *
     * @return A list of {@link PositionDTO} or a 204 status if no positions are found.
     */
    @Operation(
            summary = "Retrieve all positions",
            description = """
                    Fetch a list of all registered positions.
                    Requirements:
                    - The user must have the 'POSITION_READ' authority.
                    - The user must have the role 'QUALITY-ADMIN-USER'.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved the list of positions.",
                    content = @Content(
                            array = @ArraySchema(
                                    schema = @Schema(implementation = PositionDTO.class)
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "204",
                    description = "No positions found."
            )
    })
    @GetMapping
    @PreAuthorize("hasRole('QUALITY-ADMIN-USER') and hasAuthority('POSITION_READ')")
    public ResponseEntity<List<PositionDTO>> getAll() {
        List<PositionDTO> list = positionService.getAll();
        return list.isEmpty()
                ? ResponseEntity.noContent().build()
                : ResponseEntity.ok(list);
    }

    /**
     * Create a new position.
     *
     * @param dto The {@link PositionDTO} containing the position details.
     * @return The created {@link PositionDTO}.
     */
    @Operation(
            summary = "Create a new position",
            description = """
                    Add a new position to the system.
                    Requirements:
                    - The user must have the 'POSITION_WRITE' authority.
                    - The user must have the role 'QUALITY-ADMIN-USER'.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Successfully created the position.",
                    content = @Content(schema = @Schema(implementation = PositionDTO.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request. The position could not be created."
            )
    })
    @PostMapping
    @PreAuthorize("hasRole('QUALITY-ADMIN-USER') and hasAuthority('POSITION_WRITE')")
    public ResponseEntity<PositionDTO> create(@RequestBody PositionDTO dto) {
        PositionDTO created = positionService.create(dto);
        return ResponseEntity.status(201).body(created);
    }
}
