package com.edu.uptc.gelibackend.controllers;

import com.edu.uptc.gelibackend.dtos.LaboratoryDTO;
import com.edu.uptc.gelibackend.services.LaboratoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/laboratory")
public class LaboratoryController {

    @Autowired
    private LaboratoryService laboratoryService;

    @GetMapping
    public ResponseEntity<List<LaboratoryDTO>> getAll() {
        List<LaboratoryDTO> all = laboratoryService.findAll();
        System.out.println("Laboratories: " + all);
        return ResponseEntity.ok(all);
    }
}
