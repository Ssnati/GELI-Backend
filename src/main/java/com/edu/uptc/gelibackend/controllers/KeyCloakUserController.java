package com.edu.uptc.gelibackend.controllers;

import com.edu.uptc.gelibackend.services.KeyCloakUserService;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/keycloak")
public class KeyCloakUserController {

    @Autowired
    private KeyCloakUserService keycloakUserService;

    @GetMapping("/users")
    public List<UserRepresentation> getUsers() {
        return keycloakUserService.getAllUsers();
    }
}
