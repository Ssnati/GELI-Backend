package com.edu.uptc.gelibackend.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EquipmentEndUseDTO {
    private Boolean isInUse;
    private Boolean isVerified;
    private Boolean isAvailable;
    private int samplesNumber;
    private List<Long> usedFunctions;
    private String observations;
}
