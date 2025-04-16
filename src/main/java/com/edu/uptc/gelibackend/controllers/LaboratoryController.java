package com.edu.uptc.gelibackend.controllers;

import com.edu.uptc.gelibackend.entities.LaboratoryEntity;
import com.edu.uptc.gelibackend.services.LaboratoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/laboratory")
public class LaboratoryController {

    @Autowired
    private LaboratoryService laboratoryService;

    @GetMapping
    public ResponseEntity<List<LaboratoryEntity>> getAll() {
        return ResponseEntity.ok(laboratoryService.findAll());
    }
}
