package com.edu.uptc.gelibackend.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "equipment_authorization_history")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EquipmentAuthorizationHistoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idAuthorizationHistory;
    // PK propia para el historial

    // FK compuesta hacia la entidad intermedia:
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({
            @JoinColumn(name = "user_id", referencedColumnName = "user_id"),
            @JoinColumn(name = "equipment_id", referencedColumnName = "equipment_id")
    })
    private AuthorizedUserEquipmentsEntity authorizedUserEquipments;

    @NotNull
    @Column(name = "modification_authorization_status_date", nullable = false)
    private LocalDate modificationAuthorizationStatusDate;  // fecha de la modificación

    @NotNull
    @Column(name = "authorization_status_to_date", nullable = false)
    private Boolean authorizationStatusToDate;  // estado de la autorización en la fecha de modificación

}
