package com.edu.uptc.gelibackend.entities;

import com.edu.uptc.gelibackend.entities.ids.EquipmentFunctionsUsedId;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "equipment_function_usage")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EquipmentFunctionsUsedEntity {

    @EmbeddedId
    private EquipmentFunctionsUsedId id;

    @ManyToOne(fetch = FetchType.EAGER)
    @MapsId("equipmentUseId")
    @JoinColumn(name = "efu_equipment_usage_id")
    private EquipmentUseEntity equipment;

    @ManyToOne(fetch = FetchType.EAGER)
    @MapsId("functionId")
    @JoinColumn(name = "efu_function_id")
    private FunctionEntity function;
}
