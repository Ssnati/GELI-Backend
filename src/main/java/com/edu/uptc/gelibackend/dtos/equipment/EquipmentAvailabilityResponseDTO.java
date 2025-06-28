package com.edu.uptc.gelibackend.dtos.equipment;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EquipmentAvailabilityResponseDTO {
    private boolean active;
    private String message;
}
