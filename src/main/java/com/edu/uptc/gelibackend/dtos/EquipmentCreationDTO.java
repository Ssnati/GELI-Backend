package com.edu.uptc.gelibackend.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EquipmentCreationDTO {
    private String equipmentName;
    private BrandDTO brand;
    private String inventoryNumber;
    private Long laboratoryId;
    private Boolean availability;
    private String equipmentObservations;
    private List<Long> authorizedUsersIds;
    private List<Long> functions;
}
