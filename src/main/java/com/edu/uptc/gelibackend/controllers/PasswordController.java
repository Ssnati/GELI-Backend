package com.edu.uptc.gelibackend.controllers;

import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.edu.uptc.gelibackend.services.KeycloakAdminService;
import com.edu.uptc.gelibackend.services.RecoveryCodeService;

import java.util.Map;

@RestController
@RequestMapping("/api/password")
public class PasswordController {

    private final RestTemplate restTemplate;
    private final KeycloakAdminService keycloakAdminService;
    private final RecoveryCodeService recoveryCodeService;

    public PasswordController(RestTemplate restTemplate,
            KeycloakAdminService keycloakAdminService,
            RecoveryCodeService recoveryCodeService) {
        this.restTemplate = restTemplate;
        this.keycloakAdminService = keycloakAdminService;
        this.recoveryCodeService = recoveryCodeService;
    }

    @PostMapping("/validate")
    public ResponseEntity<?> validateCurrentPassword(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody Map<String, String> body) {

        String currentPassword = body.get("currentPassword");
        if (currentPassword == null || currentPassword.isBlank()) {
            return ResponseEntity.badRequest().body("La contraseña actual es requerida");
        }

        // 1. Obtener username desde el token del usuario actual
        HttpHeaders userHeaders = new HttpHeaders();
        userHeaders.setBearerAuth(authHeader.replace("Bearer ", ""));

        ResponseEntity<Map> userInfoResponse;
        try {
            userInfoResponse = restTemplate.exchange(
                    keycloakAdminService.getKeycloakUrl() + "/realms/" + keycloakAdminService.getRealm()
                    + "/protocol/openid-connect/userinfo",
                    HttpMethod.GET,
                    new HttpEntity<>(userHeaders),
                    Map.class
            );
        } catch (HttpClientErrorException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token inválido");
        }

        String username = (String) userInfoResponse.getBody().get("preferred_username");

        // 2. Intentar login con user + current password (sin importar si ya está autenticado)
        LinkedMultiValueMap<String, String> formParams = new LinkedMultiValueMap<>();
        formParams.add("client_id", "geli-backend"); // tu client_id configurado en Keycloak
        formParams.add("grant_type", "password");
        formParams.add("username", username);
        formParams.add("password", currentPassword);
        formParams.add("client_secret", keycloakAdminService.getClientSecret());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<LinkedMultiValueMap<String, String>> request = new HttpEntity<>(formParams, headers);

        try {
            restTemplate.postForEntity(
                    keycloakAdminService.getKeycloakUrl() + "/realms/" + keycloakAdminService.getRealm()
                    + "/protocol/openid-connect/token",
                    request,
                    String.class
            );
            // Si no lanza error, la contraseña es correcta
            return ResponseEntity.ok(Map.of("valid", true, "message", "Contraseña actual válida"));
        } catch (HttpClientErrorException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("valid", false, "message", "Contraseña incorrecta"));
        }
    }

    @PostMapping("/change")
    public ResponseEntity<?> changePassword(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody Map<String, String> body) {

        String newPassword = body.get("newPassword");
        if (newPassword == null || newPassword.isEmpty()) {
            return ResponseEntity.badRequest().body("Nueva contraseña requerida");
        }

        HttpHeaders userHeaders = new HttpHeaders();
        userHeaders.setBearerAuth(authHeader.replace("Bearer ", ""));

        ResponseEntity<Map> userInfoResponse;
        try {
            userInfoResponse = restTemplate.exchange(
                    keycloakAdminService.getKeycloakUrl() + "/realms/" + keycloakAdminService.getRealm()
                    + "/protocol/openid-connect/userinfo",
                    HttpMethod.GET,
                    new HttpEntity<>(userHeaders),
                    Map.class
            );
        } catch (HttpClientErrorException e) {
            return ResponseEntity.status(e.getStatusCode()).body("Token inválido");
        }

        String userId = (String) userInfoResponse.getBody().get("sub");
        String adminAccessToken = keycloakAdminService.getAdminToken();

        HttpHeaders passwordHeaders = new HttpHeaders();
        passwordHeaders.setBearerAuth(adminAccessToken);
        passwordHeaders.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> passwordPayload = Map.of(
                "type", "password",
                "value", newPassword,
                "temporary", false
        );

        try {
            restTemplate.exchange(
                    keycloakAdminService.getKeycloakUrl() + "/admin/realms/" + keycloakAdminService.getRealm()
                    + "/users/" + userId + "/reset-password",
                    HttpMethod.PUT,
                    new HttpEntity<>(passwordPayload, passwordHeaders),
                    Void.class
            );
            // Invalida todas las sesiones del usuario (logout global)
            restTemplate.postForEntity(
                    keycloakAdminService.getKeycloakUrl() + "/admin/realms/" + keycloakAdminService.getRealm()
                    + "/users/" + userId + "/logout",
                    new HttpEntity<>(passwordHeaders),
                    Void.class
            );
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Contraseña actualizada exitosamente."
            ));
        } catch (HttpClientErrorException e) {
            return ResponseEntity.status(e.getStatusCode()).body(Map.of(
                    "success", false,
                    "message", "Error al cambiar contraseña: " + e.getMessage()
            ));
        }
    }

    @PostMapping("/reset")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> body) {
        String tempToken = body.get("tempToken");
        String newPassword = body.get("newPassword");

        if (tempToken == null || newPassword == null) {
            return ResponseEntity.badRequest().body("Datos incompletos");
        }

        return recoveryCodeService.findByTempToken(tempToken)
                .map(recoveryCode -> {
                    try {
                        String adminToken = keycloakAdminService.getAdminToken();
                        String userId = keycloakAdminService.getUserIdByUsername(recoveryCode.getUsername(), adminToken);

                        HttpHeaders headers = new HttpHeaders();
                        headers.setBearerAuth(adminToken);
                        headers.setContentType(MediaType.APPLICATION_JSON);

                        Map<String, Object> payload = Map.of(
                                "type", "password",
                                "value", newPassword,
                                "temporary", false
                        );

                        restTemplate.exchange(
                                keycloakAdminService.getKeycloakUrl() + "/admin/realms/"
                                + keycloakAdminService.getRealm() + "/users/" + userId + "/reset-password",
                                HttpMethod.PUT,
                                new HttpEntity<>(payload, headers),
                                Void.class
                        );

                        recoveryCodeService.invalidateCode(recoveryCode.getUsername());

                        return ResponseEntity.ok(Map.of(
                                "success", true,
                                "message", "Contraseña cambiada exitosamente"
                        ));

                    } catch (Exception e) {
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body("Error al cambiar contraseña: " + e.getMessage());
                    }
                })
                .orElse(ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Token inválido o expirado"));
    }
}
