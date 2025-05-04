package com.edu.uptc.gelibackend.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO usado para iniciar una nueva sesión de uso de equipo. Solo contiene los
 * campos requeridos para crear la entidad inicialmente.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EquipmentUseStartDTO {

    private Long userId;
    private Long equipmentId;
}
