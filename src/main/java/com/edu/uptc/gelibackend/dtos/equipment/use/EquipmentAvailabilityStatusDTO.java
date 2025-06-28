package com.edu.uptc.gelibackend.dtos.equipment.use;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EquipmentAvailabilityStatusDTO {
    private String status;  // "AVAILABLE", "IN_USE_BY_YOU", "IN_USE_BY_ANOTHER"
    private String message; // Mensaje legible para el usuario
}