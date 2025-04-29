package com.edu.uptc.gelibackend.dtos;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PositionHistoryDTO {

    private String oldPositionName;
    private String newPositionName;
    private LocalDate changeDate;
}
