package com.edu.uptc.gelibackend.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    private Long id;
    private String keycloakId;
    private String firstName;
    private String lastName;
    private String email;
    private String identification;
    private Boolean enabledStatus;
    private String role;
    private String modificationRoleDate;
    private String creationDate;
}
