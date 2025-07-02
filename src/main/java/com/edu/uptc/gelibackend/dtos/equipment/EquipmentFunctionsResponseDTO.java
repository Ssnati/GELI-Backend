package com.edu.uptc.gelibackend.dtos.equipment;

import com.edu.uptc.gelibackend.dtos.BrandDTO;
import com.edu.uptc.gelibackend.dtos.FunctionDTO;
import com.edu.uptc.gelibackend.dtos.LaboratoryDTO;
import com.edu.uptc.gelibackend.dtos.UserResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EquipmentFunctionsResponseDTO {
    private Long id;
    private String equipmentName;
    private String inventoryNumber;
    private Boolean availability;
    private List<FunctionDTO> functions;
}
