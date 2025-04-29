package com.edu.uptc.gelibackend.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserResponseDTO {

    private Long id; //registro del cambio de estado -> inactivo o activo
    private String keycloakId; // id de keycloak -> user, correo, los roles, permiso
    private String firstName;
    private String lastName;
    private String email;
    private String identification;
    private Boolean enabledStatus;
    private String role;
    private LocalDate modificationStatusDate;
    private LocalDate creationDate;
    private PositionDTO position;
}
