package com.edu.uptc.gelibackend.controllers;

import com.edu.uptc.gelibackend.services.KeycloakAdminService;
import com.edu.uptc.gelibackend.services.RecoveryCodeService;
import org.springframework.http.*;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/recovery")
public class RecoveryController {

    private final RestTemplate restTemplate;
    private final JavaMailSender mailSender;
    private final RecoveryCodeService recoveryCodeService;
    private final KeycloakAdminService keycloakAdminService;

    public RecoveryController(
            RestTemplate restTemplate,
            JavaMailSender mailSender,
            RecoveryCodeService recoveryCodeService,
            KeycloakAdminService keycloakAdminService
    ) {
        this.restTemplate = restTemplate;
        this.mailSender = mailSender;
        this.recoveryCodeService = recoveryCodeService;
        this.keycloakAdminService = keycloakAdminService;
    }

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
                .filter(rc -> rc.getExpiresAt().isAfter(LocalDateTime.now()))
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
