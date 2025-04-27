package com.edu.uptc.gelibackend.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

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

    @OneToOne
    private EquipmentEntity equipment;

    @ManyToOne
    private UserEntity user;
    private LocalDate useDate;
    private LocalTime startTime;
    private Long duration;
    private Boolean status;
    private int samplesNumber;
    @ManyToOne
    @JoinColumn(name = "applicant_id", nullable = false)
    private ApplicantEntity applicant;
    private String observations;
}