package com.edu.uptc.gelibackend.entities;

import com.edu.uptc.gelibackend.entities.ids.EquipmentFunctionsId;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "equipment_functions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EquipmentFunctionsEntity {

    @EmbeddedId
    private EquipmentFunctionsId id;

    @ManyToOne(fetch = FetchType.EAGER)
    @MapsId("equipmentId") // mapea la parte equipmentId del EmbeddedId
    @JoinColumn(name = "id_equipment")
    private EquipmentEntity equipment;

    @ManyToOne(fetch = FetchType.EAGER)
    @MapsId("functionId") // mapea la parte functionId del EmbeddedId
    @JoinColumn(name = "id_function")
    private FunctionEntity function;
}
