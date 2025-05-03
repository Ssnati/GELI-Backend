package com.edu.uptc.gelibackend.dtos;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserAuthorizedEquipmentsUpdateDTO {

    private List<Long> equipmentIds;
}
