package com.edu.uptc.gelibackend.controllers;

import com.edu.uptc.gelibackend.dtos.BrandDTO;
import com.edu.uptc.gelibackend.services.BrandService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/brands")
@RequiredArgsConstructor
@PreAuthorize("hasRole('QUALITY-ADMIN-USER')")
public class BrandController {
    
    private final BrandService service;

    @PreAuthorize("hasAuthority('BRAND_READ')")
    @GetMapping()
    public ResponseEntity<List<BrandDTO>> getAllBrands() {
        List<BrandDTO> list = service.getAllBrands();
        return list.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(list);
    }

    @PreAuthorize("hasAuthority('BRAND_READ')")
    @GetMapping("/{id}")
    public ResponseEntity<BrandDTO> getBrandById(@PathVariable Long id) {
        Optional<BrandDTO> response = service.getBrandById(id);
        return response
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasAuthority('BRAND_WRITE')")
    @PostMapping
    public ResponseEntity<BrandDTO> createBrand(@RequestBody BrandDTO brand) {
        BrandDTO createdBrand = service.createBrand(brand);
        return ResponseEntity.status(201).body(createdBrand);
    }
}
