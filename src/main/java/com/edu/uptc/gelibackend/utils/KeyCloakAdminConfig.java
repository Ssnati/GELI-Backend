package com.edu.uptc.gelibackend.utils;

import lombok.RequiredArgsConstructor;
import org.jboss.resteasy.client.jaxrs.internal.ResteasyClientBuilderImpl;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class KeyCloakAdminConfig {

    @Value("${keycloak.server.url}")
    private String SERVER_URL;
    @Value("${keycloak.realm}")
    private String REALM;
    @Value("${keycloak.realm.client}")
    private String CLIENT_ID = "geli-backend";
    private final String CLIENT_SECRET = System.getenv("CLIENT_SECRET");

    @Bean
    public Keycloak KeyCloakAdminClient() {
        return KeycloakBuilder.builder()
                .serverUrl(SERVER_URL)
                .realm(REALM)
                .grantType(OAuth2Constants.CLIENT_CREDENTIALS)
                .clientId(CLIENT_ID)
                .clientSecret(CLIENT_SECRET)
                .resteasyClient(new ResteasyClientBuilderImpl()
                        .connectionPoolSize(10)
                        .build())
                .build();
    }
}