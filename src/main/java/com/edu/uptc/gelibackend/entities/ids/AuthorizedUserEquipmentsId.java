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
public class AuthorizedUserEquipmentsId implements Serializable {

    // Mapeamos el campo userId a la columna user_id
    @Column(name = "user_id")
    private Long userId;

    // Mapeamos el campo equipmentId a la columna equipment_id
    @Column(name = "equipment_id")
    private Long equipmentId;
}