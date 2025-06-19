package com.edu.uptc.gelibackend.dtos.user;

import com.edu.uptc.gelibackend.dtos.PositionDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserFilterResponseDTO {
    private Long id; //registro del cambio de estado -> inactivo o activo
    private String keycloakId; // id de keycloak -> user, correo, los roles, permiso
    private String firstName;
    private String lastName;
    private String identification;
    private Boolean enabledStatus;
    private String role;
    private LocalDate creationDate;
    private PositionDTO position;
}
