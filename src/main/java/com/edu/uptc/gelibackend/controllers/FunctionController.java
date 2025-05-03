package com.edu.uptc.gelibackend.controllers;

import com.edu.uptc.gelibackend.dtos.FunctionDTO;
import com.edu.uptc.gelibackend.services.FunctionService;
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
 * Controller for managing equipment functions.
 * Provides endpoints for retrieving and creating equipment functions.
 *
 * <p>Requirements:</p>
 * <ul>
 *   <li>JWT authentication is mandatory.</li>
 *   <li>Specific permissions are required for each operation:</li>
 *   <ul>
 *     <li>'FUNCTION_READ' for read operations.</li>
 *     <li>'FUNCTION_WRITE' for write operations.</li>
 *   </ul>
 *   <li>The user must have the role 'QUALITY-ADMIN-USER'.</li>
 * </ul>
 */
@RestController
@RequestMapping("/api/v1/functions")
@RequiredArgsConstructor
@Tag(
        name = "Functions Management",
        description = """
                Management of equipment functions.
                This API provides endpoints for creating and retrieving equipment functions.
                """
)
@PreAuthorize("hasRole('QUALITY-ADMIN-USER')")
public class FunctionController {

    private final FunctionService service;

    /**
     * Retrieve all equipment functions.
     *
     * <p>Note:</p>
     * To retrieve the functions associated with a specific equipment, 
     * use the endpoint in the EquipmentController to fetch the equipment by its ID.
     * The equipment details will include its associated functions.
     *
     * @return A list of {@link FunctionDTO} or a 204 status if no functions are found.
     */
    @Operation(
            summary = "Retrieve all functions",
            description = """
                    Fetch a list of all equipment functions.
                    
                    Note:
                    To retrieve the functions associated with a specific equipment,
                    use the endpoint in the EquipmentController to fetch the equipment by its ID.
                    The equipment details will include its associated functions.
                    
                    Requirements:
                    - The user must have the 'FUNCTION_READ' authority.
                    - The user must have the role 'QUALITY-ADMIN-USER'.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved the list of functions.",
                    content = @Content(
                            array = @ArraySchema(
                                    schema = @Schema(implementation = FunctionDTO.class)
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "204",
                    description = "No functions found."
            )
    })
    @GetMapping
    @PreAuthorize("hasAuthority('FUNCTION_READ')")
    public ResponseEntity<List<FunctionDTO>> getAllFunctions() {
        List<FunctionDTO> list = service.getAllFunctions();
        return list.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(list);
    }

    /**
     * Retrieve a specific function by its ID.
     *
     * @param id The ID of the function.
     * @return The {@link FunctionDTO} if found, or a 404 status if not found.
     */
    @Operation(
            summary = "Retrieve function by ID",
            description = """
                    Fetch a specific function by its unique ID.
                    
                    Note:
                    To retrieve the functions associated with a specific equipment,
                    use the endpoint in the EquipmentController to fetch the equipment by its ID.
                    The equipment details will include its associated functions.
                    
                    Requirements:
                    - The user must have the 'FUNCTION_READ' authority.
                    - The user must have the role 'QUALITY-ADMIN-USER'.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved the function.",
                    content = @Content(schema = @Schema(implementation = FunctionDTO.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Function not found."
            )
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('FUNCTION_READ')")
    public ResponseEntity<FunctionDTO> getFunctionById(@PathVariable Long id) {
        Optional<FunctionDTO> response = service.getFunctionById(id);
        return response.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Create a new function.
     *
     * @param function The {@link FunctionDTO} containing the function details.
     * @return The created {@link FunctionDTO}.
     */
    @Operation(
            summary = "Create a new function",
            description = """
                    Add a new function to the system.
                    
                    Requirements:
                    - The user must have the 'FUNCTION_WRITE' authority.
                    - The user must have the role 'QUALITY-ADMIN-USER'.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Successfully created the function.",
                    content = @Content(schema = @Schema(implementation = FunctionDTO.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request. The function could not be created."
            )
    })
    @PostMapping
    @PreAuthorize("hasAuthority('FUNCTION_WRITE')")
    public ResponseEntity<FunctionDTO> createFunction(@RequestBody FunctionDTO function) {
        FunctionDTO createdFunction = service.createFunction(function);
        return ResponseEntity.status(201).body(createdFunction);
    }
}