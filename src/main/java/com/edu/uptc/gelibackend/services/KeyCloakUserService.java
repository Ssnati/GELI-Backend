package com.edu.uptc.gelibackend.services;

import com.edu.uptc.gelibackend.utils.KeyCloakProvider;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Service;

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
}