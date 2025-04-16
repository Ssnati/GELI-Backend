package com.edu.uptc.gelibackend.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LocationDTO {

    private Long id;
    private String locationName;
    private LocationTypeDTO locationType;
    private LocationDTO parentLocation;
}
