package com.edu.uptc.gelibackend.services;

import lombok.RequiredArgsConstructor;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Response;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class KeyCloakUserService {

    private final Keycloak keyCloakProvider;

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

    public void updateUser(UserRepresentation keycloakUser) {
        String userId = keycloakUser.getId();
        UsersResource usersResource = keyCloakProvider.realm(REALM).users();

        // 1. Actualizar atributos básicos del usuario
        usersResource.get(userId).update(keycloakUser);
    }

    public void updateUserRoles(String userId, List<String> newRoles) {
        RealmResource realmResource = keyCloakProvider.realm(REALM);
        UsersResource usersResource = realmResource.users();

        // Obtener roles por defecto del realm
        List<RoleRepresentation> defaultRoles = List.of(realmResource.roles().get("default-roles-".concat(REALM)).toRepresentation());

        // Combinar roles por defecto con los nuevos roles
        List<RoleRepresentation> roles = new java.util.ArrayList<>(realmResource.roles()
                .list()
                .stream()
                .filter(role -> newRoles.stream()
                .anyMatch(roleName -> roleName.equalsIgnoreCase(role.getName())))
                .toList());
        roles.addAll(defaultRoles);

        // Actualizar roles del usuario
        usersResource.get(userId).roles().realmLevel().remove(usersResource.get(userId).roles().realmLevel().listAll());
        usersResource.get(userId).roles().realmLevel().add(roles);
    }

    public List<RoleRepresentation> getAllRoles() {
        return keyCloakProvider.realm(REALM).roles().list();
    }
}
