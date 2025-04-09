package com.edu.uptc.gelibackend.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/geli")
public class MainController {

    @GetMapping("/v1")
    public ResponseEntity<String> getVersion() {
        return ResponseEntity.ok("Versión 1.0.0");
    }

}
