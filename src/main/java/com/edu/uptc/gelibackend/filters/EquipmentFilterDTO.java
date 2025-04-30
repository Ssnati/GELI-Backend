package com.edu.uptc.gelibackend.filters;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EquipmentFilterDTO implements BaseFilterDTO {
    private String equipmentName;
    private String brand;
    private String inventoryNumber;
    private Long laboratoryId;
    private Boolean availability;
    private String equipmentObservations;
}
