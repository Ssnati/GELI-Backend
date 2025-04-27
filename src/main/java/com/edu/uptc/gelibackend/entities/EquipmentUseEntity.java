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

    @NotNull
    @Column(name = "status", nullable = false)
    private Boolean status;

    @NotNull
    @Column(name = "samples_number", nullable = false)
    private int samplesNumber;

    @OneToMany(mappedBy = "equipment", cascade = CascadeType.ALL)
    @ToString.Exclude
    private List<EquipmentFunctionsUsedEntity> equipmentFunctionsUsedList;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "applicant_id", nullable = false)
    private ApplicantEntity applicant;

    @Column(name = "observations")
    private String observations;
}