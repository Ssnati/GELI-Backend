package com.edu.uptc.gelibackend.dtos.equipment;

import com.edu.uptc.gelibackend.dtos.BrandDTO;
import com.edu.uptc.gelibackend.dtos.LaboratoryDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EquipmentByUserResponseDTO {
    private Long id;
    private String equipmentName;
    private String inventoryNumber;
}
