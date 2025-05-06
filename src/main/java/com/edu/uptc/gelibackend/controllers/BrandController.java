package com.edu.uptc.gelibackend.controllers;

import com.edu.uptc.gelibackend.dtos.BrandDTO;
import com.edu.uptc.gelibackend.services.BrandService;
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
 * Provides endpoints for retrieving and creating brands.
 *
 * <p>Requirements:</p>
 * <ul>
 *   <li>JWT authentication is mandatory.</li>
 *   <li>Specific permissions are required for each operation:</li>
 *   <ul>
 *     <li>'BRAND_READ' for read operations.</li>
 *     <li>'BRAND_WRITE' for write operations.</li>
 *   </ul>
 *   <li>The user must have the role 'QUALITY-ADMIN-USER'.</li>
 * </ul>
 */
@RestController
@RequestMapping("/api/v1/brands")
@RequiredArgsConstructor
@Tag(
        name = "Brands Management",
        description = """
                Management of brands.
                This API provides endpoints for retrieving and creating brands.
                """
)
@PreAuthorize("hasRole('QUALITY-ADMIN-USER')")
public class BrandController {

    private final BrandService service;

    /**
     * Retrieve all brands.
     *
     * @return A list of {@link BrandDTO} or a 204 status if no brands are found.
     */
    @Operation(
            summary = "Retrieve all brands",
            description = """
                    Fetch a list of all registered brands.
                    Requirements:
                    - The user must have the 'BRAND_READ' authority.
                    - The user must have the role 'QUALITY-ADMIN-USER'.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved the list of brands.",
                    content = @Content(
                            array = @ArraySchema(
                                    schema = @Schema(implementation = BrandDTO.class)
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "204",
                    description = "No brands found."
            )
    })
    @GetMapping
    @PreAuthorize("hasAuthority('BRAND_READ')")
    public ResponseEntity<List<BrandDTO>> getAllBrands() {
        List<BrandDTO> list = service.getAllBrands();
        return list.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(list);
    }

    /**
     * Retrieve a specific brand by its ID.
     *
     * @param id The ID of the brand.
     * @return The {@link BrandDTO} if found, or a 404 status if not found.
     */
    @Operation(
            summary = "Retrieve brand by ID",
            description = """
                    Fetch a specific brand by its unique ID.
                    Requirements:
                    - The user must have the 'BRAND_READ' authority.
                    - The user must have the role 'QUALITY-ADMIN-USER'.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved the brand.",
                    content = @Content(schema = @Schema(implementation = BrandDTO.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Brand not found."
            )
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('BRAND_READ')")
    public ResponseEntity<BrandDTO> getBrandById(@PathVariable Long id) {
        Optional<BrandDTO> response = service.getBrandById(id);
        return response
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Create a new brand.
     *
     * @param brand The {@link BrandDTO} containing the brand details.
     * @return The created {@link BrandDTO}.
     */
    @Operation(
            summary = "Create a new brand",
            description = """
                    Add a new brand to the system.
                    Requirements:
                    - The user must have the 'BRAND_WRITE' authority.
                    - The user must have the role 'QUALITY-ADMIN-USER'.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Successfully created the brand.",
                    content = @Content(schema = @Schema(implementation = BrandDTO.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request. The brand could not be created."
            )
    })
    @PostMapping
    @PreAuthorize("hasAuthority('BRAND_WRITE')")
    public ResponseEntity<BrandDTO> createBrand(@RequestBody BrandDTO brand) {
        BrandDTO createdBrand = service.createBrand(brand);
        return ResponseEntity.status(201).body(createdBrand);
    }
}
