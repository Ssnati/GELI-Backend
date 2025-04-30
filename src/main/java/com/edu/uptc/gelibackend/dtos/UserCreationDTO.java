package com.edu.uptc.gelibackend.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserCreationDTO {

    private String email;
    private String firstName;
    private String lastName;
    private String identification;
    private String role;
    private Long positionId;
    private String positionName;
    private List<Long> authorizedEquipmentsIds;
}
