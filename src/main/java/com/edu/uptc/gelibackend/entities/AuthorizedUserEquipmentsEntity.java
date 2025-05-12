package com.edu.uptc.gelibackend.entities;

import com.edu.uptc.gelibackend.entities.ids.AuthorizedUserEquipmentsId;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "authorized_user_equipments")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthorizedUserEquipmentsEntity {

    // PK compuesta (userId + equipmentId)
    @EmbeddedId
    private AuthorizedUserEquipmentsId id;

    // Relación ManyToOne con UserEntity:
    // - @MapsId("userId") liga este campo a la parte userId de la PK
    // - FK en columna "user_id"
    @MapsId("userId")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "aue_user_id", nullable = false)
    private UserEntity user;

    // Relación ManyToOne con EquipmentEntity:
    // - @MapsId("equipmentId") liga este campo a la parte equipmentId de la PK
    // - FK en columna "equipment_id"
    @MapsId("equipmentId")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "aue_equipment_id", nullable = false)
    private EquipmentEntity equipment;

    // Estado de la asignación en el momento (p. ej. "ASIGNADO", "DEVUELTO", etc.)
    @NotNull
    @Column(name = "aue_actual_status", nullable = false)
    private Boolean actualStatus;

    @OneToMany(mappedBy = "authorizedUserEquipments", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private List<EquipmentAuthorizationHistoryEntity> equipmentAuthorizationHistory;
}