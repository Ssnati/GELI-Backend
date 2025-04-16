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
@Table(name = "location")
public class LocationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "location_name", nullable = false, length = 100)
    private String locationName; // Nombre del lugar

    @NotNull
    @ManyToOne
    @JoinColumn(name = "location_type_id", nullable = false)
    private LocationTypeEntity locationType; // Relación con la tabla LocationType

    @ManyToOne
    @JoinColumn(name = "parent_location_id")
    private LocationEntity parentLocation; // Relación con la misma tabla (lugar padre, si aplica)
}