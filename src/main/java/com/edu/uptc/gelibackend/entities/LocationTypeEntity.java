package com.edu.uptc.gelibackend.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "location_type")
public class LocationTypeEntity {
    @Id
    private Long id;

    @NotNull
    private String name; // Nombre del tipo de lugar
}