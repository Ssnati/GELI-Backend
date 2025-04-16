package com.edu.uptc.gelibackend.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "laboratory")
public class LaboratoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "laboratory_name", nullable = false)
    private String laboratoryName; // Nombre del laboratorio

    @Column(name = "laboratory_description")
    private String laboratoryDescription; // Descripción del laboratorio

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "location_id", nullable = false)
    private LocationEntity laboratoryLocation; // Relación con la tabla Location

    @NotNull
    @Column(name = "laboratory_availability", nullable = false)
    private Boolean laboratoryAvailability; // Disponibilidad del laboratorio

}