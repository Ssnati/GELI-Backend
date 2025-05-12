package com.edu.uptc.gelibackend.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @NotNull
    @Column(name = "keycloak_id", unique = true, nullable = false, length = 100)
    private String keycloakId;

    @NotNull
    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    @NotNull
    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;

    @NotNull
    @Column(name = "user_email", unique = true, nullable = false, length = 100)
    private String email;

    @NotNull
    @Column(name = "user_identification", unique = true, nullable = false, length = 100)
    private String identification;

    @NotNull
    @Column(name = "user_status", nullable = false)
    private Boolean state;

    @NotNull
    @Column(name = "user_role", nullable = false, length = 100)
    private String role;

    @NotNull
    @Column(name = "create_date_user", nullable = false)
    private LocalDate createDateUser;

    /**
     * Many users → one position
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_position_id", nullable = false)
    private PositionEntity position;

    @OneToMany(
            mappedBy = "user",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    @ToString.Exclude
    private List<UserStatusHistoryEntity> statusHistory = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @ToString.Exclude
    private List<AuthorizedUserEquipmentsEntity> authorizedUserEquipments;

    @OneToMany(
            mappedBy = "user",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    @ToString.Exclude
    private List<UserPositionHistoryEntity> positionHistory = new ArrayList<>();
}
