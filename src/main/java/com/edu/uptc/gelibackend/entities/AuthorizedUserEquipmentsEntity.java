package com.edu.uptc.gelibackend.entities;

import com.edu.uptc.gelibackend.entities.ids.AuthorizedUserEquipmentsId;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "authorized_user_equipments")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthorizedUserEquipmentsEntity {

    // PK compuesta (userId + equipmentId)
    @EmbeddedId
    private AuthorizedUserEquipmentsId id;

    // Relación ManyToOne con UserEntity:
    // - @MapsId("userId") liga este campo a la parte userId de la PK
    // - FK en columna "user_id"
    @MapsId("userId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    // Relación ManyToOne con EquipmentEntity:
    // - @MapsId("equipmentId") liga este campo a la parte equipmentId de la PK
    // - FK en columna "equipment_id"
    @MapsId("equipmentId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "equipment_id", nullable = false)
    private EquipmentEntity equipment;

    // Estado de la asignación en el momento (p. ej. "ASIGNADO", "DEVUELTO", etc.)
    @NotNull
    @Column(name = "actual_status", nullable = false)
    private Boolean actualStatus;
}