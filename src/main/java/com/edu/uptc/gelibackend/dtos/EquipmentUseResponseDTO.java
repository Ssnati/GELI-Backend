package com.edu.uptc.gelibackend.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EquipmentUseResponseDTO {
    private Long id; // Se pasa el id porque cuando se vaya a termiar el uso se tiene que registrar a que equipo se le va a colocar la fecha de finalización
    private Boolean isInUse;
    private Boolean isVerified;
    private Boolean isAvailable;
    private EquipmentResponseDTO equipment;
    private UserResponseDTO user;
    private int samplesNumber;
    private List<FunctionDTO> usedFunctions;
    private String observations;
    private LocalDateTime startUseTime;
    private LocalDateTime endUseTime;
}
