package com.edu.uptc.gelibackend.controllers;

import com.edu.uptc.gelibackend.dtos.LaboratoryDTO;
import com.edu.uptc.gelibackend.services.LaboratoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/laboratories")
@RequiredArgsConstructor
public class LaboratoryController {

    private final LaboratoryService service;

    @GetMapping
    public ResponseEntity<List<LaboratoryDTO>> getAll() {
        List<LaboratoryDTO> list = service.findAll();
        return list.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(list);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LaboratoryDTO> getById(@PathVariable Long id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<LaboratoryDTO> create(@RequestBody LaboratoryDTO dto) {
        LaboratoryDTO created = service.create(dto);
        return ResponseEntity.status(201).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<LaboratoryDTO> update(@PathVariable Long id, @RequestBody LaboratoryDTO dto) {
        LaboratoryDTO updated = service.update(id, dto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (service.delete(id)) return ResponseEntity.noContent().build();
        return ResponseEntity.notFound().build();
    }
}
