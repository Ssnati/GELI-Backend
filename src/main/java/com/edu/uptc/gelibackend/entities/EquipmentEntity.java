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
@Table(name = "equipments")
public class EquipmentEntity {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "equipment_id")
    private Long id;

    @NotNull
    @Column(name = "equipment_name", nullable = false, length = 100)
    private String equipmentName;

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "equipment_brand_id", nullable = false)
    private BrandEntity brand;

    @NotNull
    @Column(name = "equipment_inventory_number", nullable = false, length = 100)
    private String inventoryNumber;

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "equipment_laboratory_id", nullable = false)
    private LaboratoryEntity laboratory;

    @NotNull
    @Column(name = "equipment_availability", nullable = false)
    private Boolean availability;

    @Column(name = "equipment_observations", length = 500)
    private String equipmentObservations;

    @OneToMany(mappedBy = "equipment", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private List<EquipmentFunctionsEntity> equipmentFunctions;

    @OneToMany(mappedBy = "equipment", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private List<AuthorizedUserEquipmentsEntity> authorizedUsersEquipments;
}
