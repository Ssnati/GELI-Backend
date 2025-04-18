package com.edu.uptc.gelibackend.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserResponseDTO {
    private Long id;
    private String keycloakId;
    private String firstName;
    private String lastName;
    private String email;
    private String identification;
    private Boolean enabledStatus;
    private String role;
    private LocalDate modificationRoleDate;
    private LocalDate creationDate;
}
