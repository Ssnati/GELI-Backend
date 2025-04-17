package com.edu.uptc.gelibackend.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FunctionDTO {
    private Long id;
    private String functionName;
    private EquipmentDTO equipment;
}
