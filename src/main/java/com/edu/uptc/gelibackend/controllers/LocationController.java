package com.edu.uptc.gelibackend.controllers;

import com.edu.uptc.gelibackend.dtos.LocationDTO;
import com.edu.uptc.gelibackend.services.LocationService;
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
 * Controller for managing locations.
 * Provides endpoints for retrieving and creating locations.
 *
 * <p>Requirements:</p>
 * <ul>
 *   <li>JWT authentication is mandatory.</li>
 *   <li>Specific permissions are required for each operation:</li>
 *   <ul>
 *     <li>'LOCATION_READ' for read operations.</li>
 *     <li>'LOCATION_WRITE' for write operations.</li>
 *   </ul>
 *   <li>The user must have the role 'QUALITY-ADMIN-USER'.</li>
 * </ul>
 */
@RestController
@RequestMapping("/api/v1/locations")
@RequiredArgsConstructor
@Tag(
        name = "Locations Management",
        description = """
                Management of locations.
                This API provides endpoints for retrieving and creating locations.
                """
)
@PreAuthorize("hasRole('QUALITY-ADMIN-USER')")
public class LocationController {

    private final LocationService locationService;

    /**
     * Retrieve all locations.
     *
     * @return A list of {@link LocationDTO} or a 204 status if no locations are found.
     */
    @Operation(
            summary = "Retrieve all locations",
            description = """
                    Fetch a list of all registered locations.
                    Requirements:
                    - The user must have the 'LOCATION_READ' authority.
                    - The user must have the role 'QUALITY-ADMIN-USER'.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved the list of locations.",
                    content = @Content(
                            array = @ArraySchema(
                                    schema = @Schema(implementation = LocationDTO.class)
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "204",
                    description = "No locations found."
            )
    })
    @GetMapping
    @PreAuthorize("hasAuthority('LOCATION_READ')")
    public ResponseEntity<List<LocationDTO>> getAllLocations() {
        List<LocationDTO> locations = locationService.findAll();
        return locations.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(locations);
    }

    /**
     * Retrieve a specific location by its ID.
     *
     * @param id The ID of the location.
     * @return The {@link LocationDTO} if found, or a 404 status if not found.
     */
    @Operation(
            summary = "Retrieve location by ID",
            description = """
                    Fetch a specific location by its unique ID.
                    Requirements:
                    - The user must have the 'LOCATION_READ' authority.
                    - The user must have the role 'QUALITY-ADMIN-USER'.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved the location.",
                    content = @Content(schema = @Schema(implementation = LocationDTO.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Location not found."
            )
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('LOCATION_READ')")
    public ResponseEntity<LocationDTO> getLocationById(@PathVariable Long id) {
        return locationService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Create a new location.
     *
     * @param dto The {@link LocationDTO} containing the location details.
     * @return The created {@link LocationDTO}.
     */
    @Operation(
            summary = "Create a new location",
            description = """
                    Add a new location to the system.
                    Requirements:
                    - The user must have the 'LOCATION_WRITE' authority.
                    - The user must have the role 'QUALITY-ADMIN-USER'.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Successfully created the location.",
                    content = @Content(schema = @Schema(implementation = LocationDTO.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request. The location could not be created."
            )
    })
    @PostMapping
    @PreAuthorize("hasAuthority('LOCATION_WRITE')")
    public ResponseEntity<LocationDTO> createLocation(@RequestBody LocationDTO dto) {
        LocationDTO created = locationService.create(dto);
        return ResponseEntity.status(201).body(created);
    }
}
