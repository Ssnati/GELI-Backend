package com.edu.uptc.gelibackend.utils;

import org.keycloak.representations.idm.CredentialRepresentation;

public class KeyCloakUtils {

    public static CredentialRepresentation createPasswordCredential(String password) {
        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setTemporary(false); // true si quieres forzar que cambien la clave al primer login
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(password);
        return credential;
    }
}
