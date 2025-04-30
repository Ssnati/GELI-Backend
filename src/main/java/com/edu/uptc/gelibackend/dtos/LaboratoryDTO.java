package com.edu.uptc.gelibackend.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LaboratoryDTO {
    private Long id;
    private String laboratoryName;
    private String laboratoryDescription;
    private LocationDTO location;
    private Boolean laboratoryAvailability;
    private String laboratoryObservations;
}
