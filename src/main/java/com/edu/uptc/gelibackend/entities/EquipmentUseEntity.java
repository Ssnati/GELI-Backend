package com.edu.uptc.gelibackend.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "equipment_usage")
@Builder
public class EquipmentUseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "equipment_usage_id")
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "eu_equipment_id", nullable = false)
    private EquipmentEntity equipment;

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "eu_user_id", nullable = false)
    private UserEntity user;

    @NotNull
    @Column(name = "eu_start_date", nullable = false)
    private LocalDateTime startUseTime;

    @Column(name = "eu_end_date")
    private LocalDateTime endUseTime;

    @NotNull // Indica si el equipo está actualmente en uso (true) o si ya finalizó su uso (false)
    @Column(name = "eu_is_in_use", nullable = false)
    private Boolean isInUse;

    @NotNull
    @Column(name = "eu_is_verified", nullable = false)
    private Boolean isVerified;

    @NotNull // indica si el equipo está disponible para su uso o no
    @Column(name = "eu_is_available", nullable = false)
    private Boolean isAvailable;

    @NotNull
    @Column(name = "eu_samples_number", nullable = false)
    private int samplesNumber;

    @OneToMany(mappedBy = "equipment", cascade = CascadeType.ALL)
    @ToString.Exclude
    private List<EquipmentFunctionsUsedEntity> equipmentFunctionsUsedList;

    @Column(name = "equipment_use_observations", length = 500)
    private String observations;
}