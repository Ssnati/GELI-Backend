package com.edu.uptc.gelibackend.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {


    @PostMapping("/authenticate")
    public ResponseEntity<?> authenticate() {
        // TODO: Implementar lógica de autenticación
        return ResponseEntity.ok(("fake-jwt-token"));
    }

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword() {
        // TODO: Implementar lógica para cambiar contraseña
        return ResponseEntity.ok("Contraseña actualizada");
    }

    @PostMapping("/recover-password")
    public ResponseEntity<?> recoverPassword() {
        // TODO: Lógica para recuperar contraseña (envío de email)
        return ResponseEntity.ok("Correo de recuperación enviado");
    }


}