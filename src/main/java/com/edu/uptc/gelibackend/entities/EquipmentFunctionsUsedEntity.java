package com.edu.uptc.gelibackend.entities;

import com.edu.uptc.gelibackend.entities.ids.EquipmentFunctionsUsedId;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "equipment_functions_used")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EquipmentFunctionsUsedEntity {

    @EmbeddedId
    private EquipmentFunctionsUsedId id;

    @ManyToOne(fetch = FetchType.EAGER)
    @MapsId("equipmentUseId")
    @JoinColumn(name = "id_equipment_use")
    private EquipmentUseEntity equipment;

    @ManyToOne(fetch = FetchType.EAGER)
    @MapsId("functionId")
    @JoinColumn(name = "id_function")
    private FunctionEntity function;
}
