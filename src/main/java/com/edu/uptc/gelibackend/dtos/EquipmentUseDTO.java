package com.edu.uptc.gelibackend.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EquipmentUseDTO {
    private Boolean status;
    private Long equipmentId;
    private Long userId;
    private int samplesNumber;
    private List<Long> usedFunctions;
    private String observations;
}
