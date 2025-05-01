package com.edu.uptc.gelibackend.controllers;

import com.edu.uptc.gelibackend.dtos.FunctionDTO;
import com.edu.uptc.gelibackend.services.FunctionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/functions")
@RequiredArgsConstructor
@PreAuthorize("hasRole('QUALITY-ADMIN-USER')")
public class FunctionController {

    private final FunctionService service;

    @PreAuthorize("hasAuthority('FUNCTION_READ')")
    @GetMapping()
    public ResponseEntity<List<FunctionDTO>> getAllFunctions() {
        List<FunctionDTO> list = service.getAllFunctions();
        return list.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(list);
    }

    @PreAuthorize("hasAuthority('FUNCTION_READ')")
    @GetMapping("/{id}")
    public ResponseEntity<FunctionDTO> getFunctionById(@PathVariable Long id) {
        Optional<FunctionDTO> response = service.getFunctionById(id);
        return response
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasAuthority('FUNCTION_WRITE')")
    @PostMapping
    public ResponseEntity<FunctionDTO> createFunction(@RequestBody FunctionDTO function) {
        FunctionDTO createdFunction = service.createFunction(function);
        return ResponseEntity.status(201).body(createdFunction);
    }
}
