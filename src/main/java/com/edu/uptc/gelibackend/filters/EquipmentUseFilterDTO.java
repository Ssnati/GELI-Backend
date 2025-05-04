package com.edu.uptc.gelibackend.filters;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
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
    private LocalDateTime useDateFrom;
    private LocalDateTime useDateTo;
    private LocalDateTime startUseTimeFrom;
    private LocalDateTime endUseTimeTo;
}
