package com.edu.uptc.gelibackend.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EquipmentResponseDTO {
    private Long id;
    private String equipmentName;
    private String brand;
    private String inventoryNumber;
    private LaboratoryDTO laboratory;
    private Boolean availability;
    private List<FunctionDTO> functions;
    private List<UserResponseDTO> authorizedUsers;
}
