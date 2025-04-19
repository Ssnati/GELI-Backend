package com.edu.uptc.gelibackend.services;

import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.ws.rs.core.Response;
import java.util.*;

@Service
public class KeyCloakUserService {

    @Autowired
    private Keycloak keyCloakProvider;

    @Value("${keycloak.realm}")
    private String REALM;

    public List<UserRepresentation> getAllUsers() {
        List<UserRepresentation> users = keyCloakProvider.realm(REALM).users().list();
        getRolesForAllUsers(users);
        return users;
    }

    private void getRolesForAllUsers(List<UserRepresentation> users) {
        for (UserRepresentation user : users) {
            List<RoleRepresentation> roles = keyCloakProvider.realm(REALM)
                    .users()
                    .get(user.getId())
                    .roles()
                    .realmLevel()
                    .listAll();
            user.setRealmRoles(roles.stream().map(RoleRepresentation::getName).toList());
        }
    }


    public Response createUser(UserRepresentation userRepresentation) {
        userRepresentation.setEnabled(true);
        userRepresentation.setEmailVerified(true);
//        userRepresentation.setCredentials(List.of(
//                new CredentialRepresentation() {{
//                    setType(CredentialRepresentation.PASSWORD);
//                    setValue(userRepresentation.getCredentials().get(0).getValue());
//                    setTemporary(true);
//                }}
//        ));
        return keyCloakProvider.realm(REALM).users().create(userRepresentation);
    }

    public void assignRealmRoleToUser(String userId, String roleName) {
        RoleRepresentation role = keyCloakProvider.realm(REALM)
                .roles()
                .get(roleName)
                .toRepresentation();

        keyCloakProvider.realm(REALM)
                .users()
                .get(userId)
                .roles()
                .realmLevel()
                .add(Collections.singletonList(role));
    }

    public UserRepresentation getById(String id) {
        UserRepresentation representation = keyCloakProvider.realm(REALM).users().get(id).toRepresentation();
        getRolesForAllUsers(List.of(representation));
        return representation;
    }
}