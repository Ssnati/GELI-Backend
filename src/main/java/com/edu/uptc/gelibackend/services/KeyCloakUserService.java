package com.edu.uptc.gelibackend.services;

import com.edu.uptc.gelibackend.utils.KeyCloakProvider;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class KeyCloakUserService {

    public List<UserRepresentation> getAllUsers() {
        return KeyCloakProvider.getUserResource().list();
    }
}