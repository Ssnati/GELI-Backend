package com.edu.uptc.gelibackend.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EquipmentStartUseDTO {
    private Long equipmentId;
    private Long userId;
}
