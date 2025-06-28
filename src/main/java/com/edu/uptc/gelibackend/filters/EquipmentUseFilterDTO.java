package com.edu.uptc.gelibackend.filters;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EquipmentUseFilterDTO implements BaseFilterDTO {
    private Boolean isInUse;
    private Boolean isVerified;
    private Boolean isAvailable;
    private Long equipmentId;
    private Long userId;
    private Long laboratoryId;
    private int samplesNumberFrom;
    private int samplesNumberTo;
    private List<Long> usedFunctionsIds;
    private LocalDate useDateFrom;
    private LocalDate useDateTo;
    private LocalTime startTimeFrom;
    private LocalTime endTimeTo;
    private String equipmentName;
    private String equipmentInventoryCode;
}
