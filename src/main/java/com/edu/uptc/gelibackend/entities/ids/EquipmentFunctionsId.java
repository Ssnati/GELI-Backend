package com.edu.uptc.gelibackend.entities.ids;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EquipmentFunctionsId implements Serializable {
    @Column(name = "ef_equipment_id")
    private Long equipmentId;

    @Column(name = "ef_function_id")
    private Long functionId;
}
