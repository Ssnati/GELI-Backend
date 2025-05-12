package com.edu.uptc.gelibackend.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "equipment_authorization_history")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EquipmentAuthorizationHistoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "equipment_authorization_history_id")
    private Long idAuthorizationHistory;
    // PK propia para el historial

    // FK compuesta hacia la entidad intermedia:
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumns({
            @JoinColumn(name = "eah_user_id", referencedColumnName = "aue_user_id"),
            @JoinColumn(name = "eah_equipment_id", referencedColumnName = "aue_equipment_id")
    })
    private AuthorizedUserEquipmentsEntity authorizedUserEquipments;

    @NotNull
    @Column(name = "eah_modified_at", nullable = false)
    private LocalDate modificationAuthorizationStatusDate;  // fecha de la modificación

    @NotNull
    @Column(name = "eah_status_to_date", nullable = false)
    private Boolean authorizationStatusToDate;  // estado de la autorización en la fecha de modificación

}
