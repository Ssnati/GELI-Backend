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
    @Column(name = "id_equipment")
    private Long equipmentId;

    @Column(name = "id_function")
    private Long functionId;
}
