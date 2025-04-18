package com.edu.uptc.gelibackend.services;

import com.edu.uptc.gelibackend.utils.KeyCloakProvider;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Service;

import javax.ws.rs.core.Response;
import java.util.List;

@Service
public class KeyCloakUserService {

    public List<UserRepresentation> getAllUsers() {
        List<UserRepresentation> users = KeyCloakProvider.getUserResource().list();
        for (UserRepresentation user : users) {
            user.setRealmRoles(KeyCloakProvider.getUserRole(user.getId()).stream()
                    .map(RoleRepresentation::getName)
                    .toList());
        }
        return users;
    }

    public Response createUser(UserRepresentation userRepresentation) {
        userRepresentation.setEnabled(true);
        userRepresentation.setEmailVerified(true);
        userRepresentation.setRealmRoles(
                KeyCloakProvider.getRealmResource().roles().list().stream()
                        .map(RoleRepresentation::getName)
                        .filter(role -> userRepresentation.getRealmRoles().contains(role))
                        .toList()
        );
//        userRepresentation.setCredentials(List.of(
//                new CredentialRepresentation() {{
//                    setType(CredentialRepresentation.PASSWORD);
//                    setValue(userRepresentation.getCredentials().get(0).getValue());
//                    setTemporary(true);
//                }}
//        ));
        return KeyCloakProvider.getUserResource().create(userRepresentation);
    }
}