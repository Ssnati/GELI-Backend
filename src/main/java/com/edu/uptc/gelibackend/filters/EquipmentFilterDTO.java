package com.edu.uptc.gelibackend.filters;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EquipmentFilterDTO implements BaseFilterDTO {

    private String equipmentName;    
    private Long brandId;             
    private Long laboratoryId;
    private Boolean availability;
    private Long functionId;          
}