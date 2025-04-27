package com.edu.uptc.gelibackend.entities;

import jakarta.persistence.*;
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

    @ManyToOne
    private EquipmentEntity equipment;

    @ManyToOne
    private UserEntity user;
    private LocalDate useDate;
    private LocalTime startUseTime;
    private LocalTime endUseTime;
    private Boolean status;
    private int samplesNumber;
    
    @OneToMany(mappedBy = "equipment", cascade = CascadeType.ALL)
    @ToString.Exclude
    private List<EquipmentFunctionsUsedEntity> equipmentFunctionsUsedList;
    
    @ManyToOne
    @JoinColumn(name = "applicant_id", nullable = false)
    private ApplicantEntity applicant;
    private String observations;
}