package com.edu.uptc.gelibackend.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.edu.uptc.gelibackend.services.KeycloakAdminService;
import com.edu.uptc.gelibackend.services.RecoveryCodeService;

import java.util.Map;

/**
 * <p>Requirements:</p>
 * <ul>
 *   <li>JWT authentication is mandatory for validation and change operations.</li>
 *   <li>Temporary tokens are required for password reset operations.</li>
 * </ul>
 */
@RestController
@RequestMapping("/api/password")
@RequiredArgsConstructor
@Tag(
        name = "Password Management",
        description = """
                Management of user passwords.
                This API provides endpoints for validating, changing, and resetting passwords.
                """
)
public class PasswordController {

    private final RestTemplate restTemplate;
    private final KeycloakAdminService keycloakAdminService;
    private final RecoveryCodeService recoveryCodeService;

    /**
     * Validate the current password of the authenticated user.
     *
     * @param authHeader The authorization header containing the user's JWT token.
     * @param body       A map containing the current password.
     * @return A response indicating whether the password is valid or not.
     */
    @Operation(
            summary = "Validate current password",
            description = """
                    Validate the current password of the authenticated user.
                    Requirements:
                    - The user must provide their current password.
                    - JWT authentication is mandatory.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Password is valid.",
                    content = @Content(schema = @Schema(implementation = Map.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Current password is required."
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Invalid token or incorrect password."
            )
    })
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

    /**
     * Change the password of the authenticated user.
     *
     * @param authHeader The authorization header containing the user's JWT token.
     * @param body       A map containing the new password.
     * @return A response indicating whether the password was successfully changed.
     */
    @Operation(
            summary = "Change password",
            description = """
                    Change the password of the authenticated user.
                    Requirements:
                    - The user must provide a new password.
                    - JWT authentication is mandatory.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Password successfully changed.",
                    content = @Content(schema = @Schema(implementation = Map.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "New password is required."
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Invalid token."
            )
    })
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

    /**
     * Reset the password using a temporary token.
     *
     * @param body A map containing the temporary token and the new password.
     * @return A response indicating whether the password was successfully reset.
     */
    @Operation(
            summary = "Reset password",
            description = """
                    Reset the password using a temporary token.
                    Requirements:
                    - The user must provide a valid temporary token and a new password.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Password successfully reset.",
                    content = @Content(schema = @Schema(implementation = Map.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Incomplete data provided."
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Invalid or expired token."
            )
    })
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
