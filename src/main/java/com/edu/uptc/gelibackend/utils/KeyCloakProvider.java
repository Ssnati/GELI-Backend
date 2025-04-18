package com.edu.uptc.gelibackend.utils;

import org.jboss.resteasy.client.jaxrs.internal.ResteasyClientBuilderImpl;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.RoleRepresentation;

import javax.annotation.PreDestroy;
import java.util.List;

public class KeyCloakProvider {

    private static final String SERVER_URL = "http://localhost:9090";
    private static final String REALM = "geli-dev";
    private static final String CLIENT_ID = "geli-backend";
    private static final String CLIENT_SECRET = System.getenv("CLIENT_SECRET");

    private static final Keycloak keycloakInstance = KeycloakBuilder.builder()
            .serverUrl(SERVER_URL)
            .realm(REALM)
            .grantType(OAuth2Constants.CLIENT_CREDENTIALS)
            .clientId(CLIENT_ID)
            .clientSecret(CLIENT_SECRET)
            .resteasyClient(new ResteasyClientBuilderImpl()
                    .connectionPoolSize(10)
                    .build())
            .build();

    public static RealmResource getRealmResource() {
        return keycloakInstance.realm(REALM);
    }

    public static UsersResource getUserResource() {
        RealmResource realmResource = getRealmResource();
        return realmResource.users();
    }

    public static List<RoleRepresentation> getUserRole(String id) {
        return getUserResource().get(id).roles().realmLevel().listAll();
    }

    @PreDestroy
    public void closeKeycloak() {
        keycloakInstance.close();
    }
}