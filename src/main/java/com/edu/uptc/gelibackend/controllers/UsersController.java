package com.edu.uptc.gelibackend.controllers;

import com.edu.uptc.gelibackend.dtos.UserCreationDTO;
import com.edu.uptc.gelibackend.dtos.UserResponseDTO;
import com.edu.uptc.gelibackend.dtos.UserUpdateDTO;
import com.edu.uptc.gelibackend.filters.UserFilterDTO;
import com.edu.uptc.gelibackend.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UsersController {

    private final UserService userService;

    // — Este endpoint LO PUEDEN LLAMAR AMBOS ROLES —
    @GetMapping("/by-email")
    @PreAuthorize("hasAnyRole('QUALITY-ADMIN-USER','AUTHORIZED-USER')")
    public ResponseEntity<UserResponseDTO> getUserByEmail(@RequestParam String email) {
        return userService.findUserByEmail(email)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // — Sólo ADMIN CALIDAD —
    @GetMapping
    @PreAuthorize("hasRole('QUALITY-ADMIN-USER') and hasAuthority('USER_READ')")
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
        List<UserResponseDTO> users = userService.findAll();
        return users.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('QUALITY-ADMIN-USER') and hasAuthority('USER_READ')")
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable Long id) {
        return userService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/exists-by-email")
    @PreAuthorize("hasRole('QUALITY-ADMIN-USER') and hasAuthority('USER_READ')")
    public ResponseEntity<Boolean> existsByEmail(@RequestParam String email) {
        boolean exists = userService.existsByEmail(email);
        return ResponseEntity.ok(exists);
    }

    @PostMapping
    @PreAuthorize("hasRole('QUALITY-ADMIN-USER') and hasAuthority('USER_WRITE')")
    public ResponseEntity<UserResponseDTO> createUser(@RequestBody UserCreationDTO user) {
        UserResponseDTO createdUser = userService.createUser(user);
        return ResponseEntity.status(201).body(createdUser);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('QUALITY-ADMIN-USER') and hasAuthority('USER_WRITE')")
    public ResponseEntity<UserResponseDTO> updateUser(
            @PathVariable Long id,
            @RequestBody UserUpdateDTO dto
    ) {
        UserResponseDTO updated = userService.updateUser(id, dto);
        return ResponseEntity.ok(updated);
    }

    @PostMapping("/filter")
    @PreAuthorize("hasRole('QUALITY-ADMIN-USER') and hasAuthority('USER_READ')")
    public ResponseEntity<List<UserResponseDTO>> filterUsers(@RequestBody UserFilterDTO filter) {
        List<UserResponseDTO> filtered = userService.filter(filter);
        return filtered.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(filtered);
    }
}
