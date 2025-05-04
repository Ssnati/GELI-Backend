package com.edu.uptc.gelibackend.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuthorizedEquipmentDTO {
    private Long id;
    private String equipmentName;
    private BrandDTO brand;
    private String inventoryNumber;
    private LaboratoryDTO laboratory;
    private Boolean availability;
    private List<FunctionDTO> functions;
    private String observations;
}
