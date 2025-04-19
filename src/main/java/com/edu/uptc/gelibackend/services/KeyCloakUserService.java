package com.edu.uptc.gelibackend.services;

import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response;
import java.util.Collections;
import java.util.List;

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

    public void deleteUser(String userId) {
        try {
            UserResource userResource = keyCloakProvider.realm(REALM).users().get(userId);
            userResource.remove();
        } catch (NotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado con ID: " + userId);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al eliminar el usuario: " + e.getMessage(), e);
        }
    }

    public void updateUser(UserRepresentation keycloakUser, List<String> newRoles) {
        String userId = keycloakUser.getId();
        UserResource userResource = keyCloakProvider.realm(REALM).users().get(userId);

        // 1. Actualizar atributos básicos del usuario
        userResource.update(keycloakUser);

        // 2. Manejar actualización de roles
        List<RoleRepresentation> currentRoles = userResource.roles().realmLevel().listAll();

        // 3. Eliminar todos los roles actuales
        if (!currentRoles.isEmpty()) {
            userResource.roles().realmLevel().remove(currentRoles);
        }

        // 4. Validar y convertir nuevos roles a representación
        List<RoleRepresentation> rolesToAdd = newRoles.stream()
                .map(roleName -> {
                    RoleRepresentation role = keyCloakProvider.realm(REALM)
                            .roles()
                            .get(roleName)
                            .toRepresentation();

                    if (role == null) {
                        throw new RuntimeException("Rol no encontrado: " + roleName);
                    }
                    return role;
                })
                .toList();


        // 5. Agregar nuevos roles
        if (!rolesToAdd.isEmpty()) {
            userResource.roles().realmLevel().add(rolesToAdd);
        }
    }

    public List<RoleRepresentation> getAllRoles() {
        return keyCloakProvider.realm(REALM).roles().list();
    }
}