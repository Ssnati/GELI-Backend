package com.edu.uptc.gelibackend.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "equipment_use")
@Builder
public class EquipmentUseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "equipment_id", nullable = false)
    private EquipmentEntity equipment;

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @NotNull
    @Column(name = "use_date", nullable = false)
    private LocalDate useDate;

    @NotNull
    @Column(name = "start_use_time", nullable = false)
    private LocalTime startUseTime;

    @Column(name = "end_use_time")
    private LocalTime endUseTime;

    @NotNull // Indica si el equipo está actualmente en uso (true) o si ya finalizó su uso (false)
    @Column(name = "status_in_use", nullable = false)
    private Boolean isInUse;

    @NotNull
    @Column(name = "is_verified", nullable = false)
    private Boolean isVerified;

    @NotNull // indica si el equipo está disponible para su uso o no
    @Column(name = "is_available", nullable = false)
    private Boolean isAvailable;

    @NotNull
    @Column(name = "samples_number", nullable = false)
    private int samplesNumber;

    @OneToMany(mappedBy = "equipment", cascade = CascadeType.ALL)
    @ToString.Exclude
    private List<EquipmentFunctionsUsedEntity> equipmentFunctionsUsedList;

    @Column(name = "observations")
    private String observations;
}