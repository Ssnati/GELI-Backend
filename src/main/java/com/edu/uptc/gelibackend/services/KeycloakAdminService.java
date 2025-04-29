package com.edu.uptc.gelibackend.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
public class KeycloakAdminService {

    private final RestTemplate restTemplate;

    @Value("${keycloak.auth-server-url}")
    private String keycloakUrl;

    @Value("${keycloak.realm}")
    private String realm;

    @Value("${keycloak.client-id}")
    private String clientId;

    @Value("${keycloak.client-secret}")
    private String clientSecret;

    public KeycloakAdminService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String getAdminToken() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "client_credentials");
        params.add("client_id", clientId);
        params.add("client_secret", clientSecret);

        HttpEntity<?> request = new HttpEntity<>(params, headers);

        ResponseEntity<Map> response = restTemplate.exchange(
                keycloakUrl + "/realms/" + realm + "/protocol/openid-connect/token",
                HttpMethod.POST,
                request,
                Map.class
        );

        return (String) response.getBody().get("access_token");
    }

    public String getUserIdByUsername(String username, String adminToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(adminToken);

        HttpEntity<?> request = new HttpEntity<>(headers);

        ResponseEntity<List> response = restTemplate.exchange(
                keycloakUrl + "/admin/realms/" + realm + "/users?username=" + username,
                HttpMethod.GET,
                request,
                List.class
        );

        List<?> users = response.getBody();
        if (users == null || users.isEmpty()) {
            throw new RuntimeException("Usuario no encontrado en Keycloak");
        }

        Map<?, ?> user = (Map<?, ?>) users.get(0);
        return (String) user.get("id");
    }

    public String getKeycloakUrl() {
        return keycloakUrl;
    }

    public String getRealm() {
        return realm;
    }

    public String getClientSecret() {
        return clientSecret;
    }

}
