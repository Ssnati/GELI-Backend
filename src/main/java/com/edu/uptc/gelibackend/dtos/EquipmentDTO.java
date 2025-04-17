package com.edu.uptc.gelibackend.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EquipmentDTO {
    private Long id;
    private String equipmentName;
    private String brand;
    private String inventoryNumber;
    private LaboratoryDTO laboratory;
    private Boolean availability;
    private FunctionDTO function;
}
