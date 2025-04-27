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
public class EquipmentFunctionsUsedId implements Serializable {
    @Column(name = "id_function")
    private Long functionId;

    @Column(name = "id_equipment_use")
    private Long equipmentUseId;
}
