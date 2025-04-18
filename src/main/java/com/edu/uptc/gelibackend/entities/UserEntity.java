package com.edu.uptc.gelibackend.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users")
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "keycloak_id", unique = true, nullable = false)
    private String keycloakId;

    @NotNull
    @Column(name = "identification", unique = true, nullable = false)
    private String identification;

    @NotNull
    @Column(name = "modification_role_date", nullable = false)
    private LocalDate modificationRoleDate;

}
