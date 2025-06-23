package com.edu.uptc.gelibackend.controllers;

import com.edu.uptc.gelibackend.dtos.*;
import com.edu.uptc.gelibackend.dtos.user.UserFilterResponseDTO;
import com.edu.uptc.gelibackend.filters.UserFilterDTO;
import com.edu.uptc.gelibackend.services.UserService;
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
 * Controller for managing users.
 * Provides endpoints for retrieving, creating, updating, and deleting users.
 *
 * <p>Requirements:</p>
 * <ul>
 *   <li>JWT authentication is mandatory.</li>
 *   <li>Specific permissions are required for each operation:</li>
 *   <ul>
 *     <li>'USER_READ' for read operations.</li>
 *     <li>'USER_WRITE' for write operations.</li>
 *   </ul>
 *   <li>The user must have the role 'QUALITY-ADMIN-USER'.</li>
 * </ul>
 */
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(
        name = "Users Management",
        description = """
                Management of users.
                This API provides endpoints for retrieving, creating, updating, and deleting users.
                """
)
@PreAuthorize("hasRole('QUALITY-ADMIN-USER')")
public class UsersController {

    private final UserService userService;

    /**
     * Retrieve all users.
     *
     * @return A list of {@link UserResponseDTO} or a 204 status if no users are found.
     */
    @Operation(
            summary = "Retrieve all users",
            description = """
                    Fetch a list of all registered users.
                    Requirements:
                    - The user must have the 'USER_READ' authority.
                    - The user must have the role 'QUALITY-ADMIN-USER'.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved the list of users.",
                    content = @Content(
                            array = @ArraySchema(
                                    schema = @Schema(implementation = UserResponseDTO.class)
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "204",
                    description = "No users found."
            )
    })
    @GetMapping
    @PreAuthorize("hasAuthority('USER_READ')")
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
        List<UserResponseDTO> users = userService.findAll();
        return users.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(users);
    }

    /**
     * Retrieve a specific user by their ID.
     *
     * @param id The ID of the user.
     * @return The {@link UserResponseDTO} if found, or a 404 status if not found.
     */
    @Operation(
            summary = "Retrieve user by ID",
            description = """
                    Fetch a specific user by their unique ID.
                    Requirements:
                    - The user must have the 'USER_READ' authority.
                    - The user must have the role 'QUALITY-ADMIN-USER'.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved the user.",
                    content = @Content(schema = @Schema(implementation = UserResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found."
            )
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('USER_READ')")
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable Long id) {
        return userService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Create a new user.
     *
     * @param user The {@link UserCreationDTO} containing the user details.
     * @return The created {@link UserResponseDTO}.
     */
    @Operation(
            summary = "Create a new user",
            description = """
                    Add a new user to the system.
                    Requirements:
                    - The user must have the 'USER_WRITE' authority.
                    - The user must have the role 'QUALITY-ADMIN-USER'.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Successfully created the user.",
                    content = @Content(schema = @Schema(implementation = UserCreationDTO.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request. The user could not be created."
            )
    })
    @PostMapping
    @PreAuthorize("hasAuthority('USER_WRITE')")
    public ResponseEntity<UserResponseDTO> createUser(@RequestBody UserCreationDTO user) {
        UserResponseDTO createdUser = userService.createUser(user);
        return ResponseEntity.status(201).body(createdUser);
    }

    /**
     * Update an existing user.
     *
     * @param id            The ID of the user to update.
     * @param userUpdateDTO The {@link UserUpdateDTO} containing the updated details.
     * @return The updated {@link UserResponseDTO}.
     */
    @Operation(
            summary = "Update an existing user",
            description = """
                    Modify the details of an existing user.
                    Requirements:
                    - The user must have the 'USER_WRITE' authority.
                    - The user must have the role 'QUALITY-ADMIN-USER'.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully updated the user.",
                    content = @Content(schema = @Schema(implementation = UserUpdateDTO.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found."
            )
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('USER_WRITE')")
    public ResponseEntity<UserResponseDTO> updateUser(@PathVariable Long id, @RequestBody UserUpdateDTO userUpdateDTO) {
        UserResponseDTO updated = userService.updateUser(id, userUpdateDTO);
        return ResponseEntity.ok(updated);
    }

    /**
     * Filter users based on specific criteria.
     *
     * @param filter The {@link UserFilterDTO} containing the filter criteria.
     * @return A list of {@link UserResponseDTO} matching the criteria, or a 204 status if no users are found.
     */
    @Operation(
            summary = "Filter users",
            description = """
                    Fetch a list of users based on specific filter criteria.
                    The filter can include attributes such as:
                    - `firstName` (String): The first name of the user.
                    - `lastName` (String): The last name of the user.
                    - `username` (String): The username of the user.
                    - `email` (String): The email of the user.
                    - `identification` (String): The identification of the user.
                    - `enabledStatus` (Boolean): The active/inactive status of the user.
                    - `role` (String): The role of the user.
                    - `modificationStatusDateFrom` (LocalDate): The start date for status modification.
                    - `modificationStatusDateTo` (LocalDate): The end date for status modification.
                    - `creationDateFrom` (LocalDate): The start date for user creation.
                    - `creationDateTo` (LocalDate): The end date for user creation.
                    - `positionId` (Long): The ID of the user's position.
                    ---
                    The filter is optional, and you can provide any combination of the above attributes.
                                    
                    Requirements:
                    - The user must have the 'USER_READ' authority.
                    - The user must have the role 'QUALITY-ADMIN-USER'.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved the filtered list of users.",
                    content = @Content(
                            array = @ArraySchema(
                                    schema = @Schema(implementation = UserResponseDTO.class)
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "204",
                    description = "No users found matching the filter criteria."
            )
    })
    @PostMapping("/filter")
    @PreAuthorize("hasRole('QUALITY-ADMIN-USER') and hasAuthority('USER_READ')")
    public ResponseEntity<PageResponse<UserFilterResponseDTO>> filterUsers(
            @RequestBody UserFilterDTO filter,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        PageResponse<UserFilterResponseDTO> response = userService.filter(filter, page, size);
        return response.getContent().isEmpty()
                ? ResponseEntity.noContent().build()
                : ResponseEntity.ok(response);
    }


    /**
     * Update the list of authorized equipment for a specific user.
     *
     * @param id  The ID of the user whose authorized equipment list is to be updated.
     * @param dto The {@link UserAuthorizedEquipmentsUpdateDTO} containing the list of equipment IDs.
     * @return A 200 status if the update is successful.
     */
    @Operation(
            summary = "Update authorized equipment for a user",
            description = """
                    Update the list of equipment that a specific user is authorized to use.
                    Requirements:
                    - The user must have the 'USER_WRITE' authority.
                    - The user must have the role 'QUALITY-ADMIN-USER'.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully updated the authorized equipment list."
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request. The equipment list could not be updated."
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found."
            )
    })
    @PutMapping("/{id}/authorized-equipments")
    @PreAuthorize("hasRole('QUALITY-ADMIN-USER') and hasAuthority('USER_WRITE')")
    public ResponseEntity<Void> updateAuthorizedEquipments(@PathVariable Long id, @RequestBody UserAuthorizedEquipmentsUpdateDTO dto) {
        userService.updateAuthorizedEquipments(id, dto.getEquipmentIds());
        return ResponseEntity.ok().build();
    }

    /**
     * Check if user exists by email.
     *
     * @param email The email of the user to check.
     * @return True if the user exists, false otherwise.
     */
    @Operation(
            summary = "Check user existence by email",
            description = "Verify if an email is already associated with a user in the system."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Successfully checked the existence of the user."
    )
    @GetMapping("/exists-by-email")
    @PreAuthorize("hasAuthority('USER_READ')")
    public ResponseEntity<Boolean> existsByName(@RequestParam String email) {
        boolean exists = userService.existsByEmail(email);
        return ResponseEntity.ok(exists);
    }

    @GetMapping("/exists-by-identification")
    @PreAuthorize("hasAuthority('USER_READ')")
    public ResponseEntity<Boolean> existsByIdentification(@RequestParam String identification) {
        boolean exists = userService.existsByIdentification(identification);
        return ResponseEntity.ok(exists);
    }

    /**
     * Retrieve a user by email.
     *
     * @param email The email of the user to retrieve.
     * @return The {@link UserResponseDTO} if found, or a 404 status if not found.
     */
    @Operation(
            summary = "Retrieve user by email",
            description = "Fetch a specific user by their email address."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved the user.",
                    content = @Content(schema = @Schema(implementation = UserResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found."
            )
    })
    @GetMapping("/by-email")
    @PreAuthorize("hasAuthority('USER_READ')")
    public ResponseEntity<UserResponseDTO> getUserByEmail(@RequestParam String email) {
        return userService.findUserByEmail(email)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
