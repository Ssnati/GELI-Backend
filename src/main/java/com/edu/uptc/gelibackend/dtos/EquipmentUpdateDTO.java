package com.edu.uptc.gelibackend.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EquipmentUpdateDTO {
    private Long laboratoryId;
    private Long brandId;
    private Boolean availability;
    private String equipmentObservations;
}
