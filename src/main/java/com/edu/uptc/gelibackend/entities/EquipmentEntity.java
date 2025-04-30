package com.edu.uptc.gelibackend.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "equipment")
public class EquipmentEntity {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_equipment")
    private Long id;

    @NotNull
    @Column(name = "equipment_name", nullable = false)
    private String equipmentName;

    @NotNull
    @Column(name = "brand", nullable = false)
    private String brand;

    @NotNull
    @Column(name = "inventory_number", nullable = false)
    private String inventoryNumber;

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "laboratory_location_id", nullable = false)
    private LaboratoryEntity laboratory;

    @NotNull
    @Column(name = "availability", nullable = false)
    private Boolean availability;

    @Column(name = "equipment_observations")
    private String equipmentObservations;

    @OneToMany(mappedBy = "equipment", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private List<EquipmentFunctionsEntity> equipmentFunctions;

    @OneToMany(mappedBy = "equipment", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private List<AuthorizedUserEquipmentsEntity> authorizedUsersEquipments;
}
