package com.edu.uptc.gelibackend.controllers;

import com.edu.uptc.gelibackend.dtos.AuthRequestDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * Provides an endpoint for user login.
 *
 * <p>Requirements:</p>
 * <ul>
 *   <li>JWT authentication is not required for this endpoint.</li>
 *   <li>Valid Keycloak credentials must be provided.</li>
 * </ul>
 */
@RestController
@RequestMapping("/api/auth")
@Tag(
        name = "Authentication Management",
        description = """
                Management of user authentication.
                This API provides an endpoint for user login.
                """
)
@RequiredArgsConstructor
public class LoginController {

    private final RestTemplate restTemplate;

    @Value("${keycloak.auth-server-url}")
    private String keycloakUrl;

    @Value("${keycloak.realm}")
    private String realm;

    @Value("${keycloak.client-id}")
    private String clientId;

    private final String clientSecret = System.getenv("CLIENT_SECRET");

    /**
     * Authenticate a user and retrieve an access token.
     *
     * @param authRequest The {@link AuthRequestDTO} containing the username and password.
     * @return A map containing the access token and other authentication details, or an error message.
     */
    @Operation(
            summary = "User login",
            description = """
                    Authenticate a user using their credentials and retrieve an access token.
                    Requirements:
                    - Valid Keycloak credentials must be provided.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully authenticated the user.",
                    content = @Content(schema = @Schema(implementation = Map.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request. Username and password must be provided."
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized. Invalid credentials."
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Unexpected error occurred during login."
            )
    })
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequestDTO authRequest) {
        if (authRequest.getUsername() == null || authRequest.getPassword() == null) {
            return ResponseEntity.badRequest().body("Username and password must be provided.");
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "password");
        params.add("client_id", clientId);
        params.add("client_secret", clientSecret);
        params.add("username", authRequest.getUsername());
        params.add("password", authRequest.getPassword());
        params.add("scope", "openid profile email");

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(params, headers);

        String tokenUrl = String.format("%s/realms/%s/protocol/openid-connect/token", keycloakUrl, realm);

        try {
            ResponseEntity<Map> response = restTemplate.exchange(
                    tokenUrl,
                    HttpMethod.POST,
                    requestEntity,
                    Map.class
            );
            return ResponseEntity.ok(response.getBody());
        } catch (HttpClientErrorException.Unauthorized e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials.");
        } catch (HttpClientErrorException e) {
            return ResponseEntity.status(e.getStatusCode()).body("Login error: " + e.getResponseBodyAsString());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected error: " + e.getMessage());
        }
    }
}