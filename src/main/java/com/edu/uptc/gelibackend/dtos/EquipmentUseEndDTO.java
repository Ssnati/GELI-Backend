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
public class EquipmentUseEndDTO {

    private Boolean isVerified;
    private Boolean isAvailable;
    private int samplesNumber;
    private List<Long> usedFunctions;
    private String observations;
}
