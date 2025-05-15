package com.edu.uptc.gelibackend.controllers;

import com.edu.uptc.gelibackend.services.KeycloakAdminService;
import com.edu.uptc.gelibackend.services.RecoveryCodeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Controller for managing password recovery.
 * Provides endpoints for sending, verifying, and resending recovery codes.
 *
 * <p>Requirements:</p>
 * <ul>
 *   <li>Valid user credentials must be provided for recovery operations.</li>
 *   <li>Email configuration must be set up for sending recovery codes.</li>
 * </ul>
 */
@RestController
@RequestMapping("/api/recovery")
@RequiredArgsConstructor
@Tag(
        name = "Password Recovery Management",
        description = """
                Management of password recovery.
                This API provides endpoints for sending, verifying, and resending recovery codes.
                """
)
public class RecoveryController {

    private final RestTemplate restTemplate;
    private final JavaMailSender mailSender;
    private final RecoveryCodeService recoveryCodeService;
    private final KeycloakAdminService keycloakAdminService;

    /**
     * Send a recovery code to the user's registered email.
     *
     * @param body A map containing the username of the user.
     * @return A response indicating whether the recovery code was successfully sent.
     */
    @Operation(
            summary = "Send recovery code",
            description = """
                    Send a recovery code to the user's registered email.
                    Requirements:
                    - The user must provide a valid username.
                    - Email configuration must be set up.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Recovery code successfully sent.",
                    content = @Content(schema = @Schema(implementation = Map.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request. Username is required."
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Unexpected error occurred while sending the recovery code."
            )
    })
    @PostMapping("/send-code")
    public ResponseEntity<?> sendRecoveryCode(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        if (username == null || username.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of(
                    "usuarioEncontrado", false,
                    "codigoEnviado", false,
                    "message", "El nombre de usuario es obligatorio"
            ));
        }

        try {
            String accessToken = keycloakAdminService.getAdminToken();

            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(accessToken);

            ResponseEntity<List> userResponse = restTemplate.exchange(
                    keycloakAdminService.getKeycloakUrl() + "/admin/realms/" + keycloakAdminService.getRealm()
                            + "/users?username=" + username,
                    HttpMethod.GET,
                    new HttpEntity<>(headers),
                    List.class
            );

            if (userResponse.getBody() == null || userResponse.getBody().isEmpty()) {
                return ResponseEntity.ok(Map.of(
                        "usuarioEncontrado", false,
                        "codigoEnviado", false,
                        "message", "Usuario no encontrado"
                ));
            }

            Map user = (Map) userResponse.getBody().get(0);
            String email = (String) user.get("email");

            if (email == null || email.isBlank()) {
                return ResponseEntity.ok(Map.of(
                        "usuarioEncontrado", true,
                        "codigoEnviado", false,
                        "message", "El usuario no tiene un correo registrado"
                ));
            }

            String code = recoveryCodeService.generateCode(username);

            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(email);
            message.setSubject("Recuperación de contraseña - Código de verificación");
            message.setText("Tu código de recuperación es: " + code + "\nEste código expira en 5 minutos.");
            mailSender.send(message);

            String tempToken = UUID.randomUUID().toString();
            recoveryCodeService.saveTempToken(username, tempToken);

            return ResponseEntity.ok(Map.of(
                    "usuarioEncontrado", true,
                    "codigoEnviado", true,
                    "tempToken", tempToken,
                    "message", "Código enviado al correo de recuperación"
            ));

        } catch (HttpClientErrorException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "usuarioEncontrado", false,
                    "codigoEnviado", false,
                    "message", "Error al procesar la solicitud: " + e.getMessage()
            ));
        }
    }

    /**
     * Verify the recovery code provided by the user.
     *
     * @param body A map containing the username and recovery code.
     * @return A response indicating whether the recovery code is valid.
     */
    @Operation(
            summary = "Verify recovery code",
            description = """
                    Verify the recovery code provided by the user.
                    Requirements:
                    - The user must provide a valid username and recovery code.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Recovery code successfully verified.",
                    content = @Content(schema = @Schema(implementation = Map.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request. Username and recovery code are required."
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Invalid or expired recovery code."
            )
    })
    @PostMapping("/verify-code")
    public ResponseEntity<?> verifyRecoveryCode(@RequestBody Map<String, String> body) {
        String tempToken = body.get("tempToken");
        String code = body.get("code");

        if (tempToken == null || code == null) {
            return ResponseEntity.badRequest().body(Map.of(
                    "valid", false,
                    "message", "Faltan datos para validar el código"
            ));
        }

        return recoveryCodeService.findByTempToken(tempToken)
                .filter(rc -> rc.getCode().equals(code))
                .filter(rc -> rc.getExpiresAt().isAfter(LocalDateTime.now(ZoneId.of("America/Bogota"))))
                .map(rc -> ResponseEntity.ok(Map.of(
                        "valid", true,
                        "message", "Código válido. Puedes continuar.",
                        "tempToken", tempToken
                )))
                .orElse(ResponseEntity.badRequest().body(Map.of(
                        "valid", false,
                        "message", "Código inválido o expirado"
                )));
    }

    /**
     * Resend a new recovery code to the user's registered email.
     *
     * @param body A map containing the username of the user.
     * @return A response indicating whether the new recovery code was successfully sent.
     */
    @Operation(
            summary = "Resend recovery code",
            description = """
                    Resend a new recovery code to the user's registered email.
                    Requirements:
                    - The user must provide a valid username.
                    - Email configuration must be set up.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "New recovery code successfully sent.",
                    content = @Content(schema = @Schema(implementation = Map.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request. Username is required."
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Unexpected error occurred while resending the recovery code."
            )
    })
    @PostMapping("/resend-code")
    public ResponseEntity<?> resendRecoveryCode(@RequestBody Map<String, String> body) {
        String tempToken = body.get("tempToken");

        if (tempToken == null || tempToken.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of(
                    "usuarioEncontrado", false,
                    "codigoEnviado", false,
                    "message", "Token temporal no proporcionado"
            ));
        }

        String username = recoveryCodeService.getUsernameByTempToken(tempToken);
        if (username == null || username.isBlank()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                    "usuarioEncontrado", false,
                    "codigoEnviado", false,
                    "message", "Token inválido o expirado"
            ));
        }

        try {
            String accessToken = keycloakAdminService.getAdminToken();

            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(accessToken);

            ResponseEntity<List> userResponse = restTemplate.exchange(
                    keycloakAdminService.getKeycloakUrl() + "/admin/realms/" + keycloakAdminService.getRealm()
                            + "/users?username=" + username,
                    HttpMethod.GET,
                    new HttpEntity<>(headers),
                    List.class
            );

            if (userResponse.getBody() == null || userResponse.getBody().isEmpty()) {
                return ResponseEntity.ok(Map.of(
                        "usuarioEncontrado", false,
                        "codigoEnviado", false,
                        "message", "Usuario no encontrado"
                ));
            }

            Map user = (Map) userResponse.getBody().get(0);
            String email = (String) user.get("email");

            if (email == null || email.isBlank()) {
                return ResponseEntity.ok(Map.of(
                        "usuarioEncontrado", true,
                        "codigoEnviado", false,
                        "message", "El usuario no tiene un correo registrado"
                ));
            }

            // ⚠️ Generar nuevo código y tempToken
            String code = recoveryCodeService.generateCode(username);
            String newTempToken = UUID.randomUUID().toString();
            recoveryCodeService.saveTempToken(username, newTempToken);

            // Enviar email
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(email);
            message.setSubject("Reenvío del código de verificación");
            message.setText("Tu nuevo código de recuperación es: " + code + "\nEste código expira en 5 minutos.");
            mailSender.send(message);

            return ResponseEntity.ok(Map.of(
                    "usuarioEncontrado", true,
                    "codigoEnviado", true,
                    "tempToken", newTempToken,
                    "message", "Nuevo código enviado al correo de recuperación"
            ));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "usuarioEncontrado", false,
                    "codigoEnviado", false,
                    "message", "Error al procesar la solicitud: " + e.getMessage()
            ));
        }
    }

}
