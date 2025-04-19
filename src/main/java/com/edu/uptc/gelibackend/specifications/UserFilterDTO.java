package com.edu.uptc.gelibackend.specifications;

import com.edu.uptc.gelibackend.filters.BaseFilterDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserFilterDTO implements BaseFilterDTO {
    private String firstName;
    private String lastName;
    private String username;
    private String email;
    private String identification;
    private Boolean enabledStatus;
    private String role;
    private LocalDate modificationStatusDateFrom;
    private LocalDate modificationStatusDateTo;
    private LocalDate creationDateFrom;
    private LocalDate creationDateTo;
}
