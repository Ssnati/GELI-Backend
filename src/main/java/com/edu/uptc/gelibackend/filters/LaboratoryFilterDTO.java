package com.edu.uptc.gelibackend.filters;

import com.edu.uptc.gelibackend.filters.BaseFilterDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LaboratoryFilterDTO implements BaseFilterDTO {
    private String laboratoryName;
    private Boolean laboratoryAvailability;
    private Long locationId;
    private String laboratoryDescription;
    private String laboratoryObservations;
}
