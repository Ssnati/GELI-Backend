package com.edu.uptc.gelibackend.dtos;

import com.edu.uptc.gelibackend.filers.BaseFilterDTO;
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
}
