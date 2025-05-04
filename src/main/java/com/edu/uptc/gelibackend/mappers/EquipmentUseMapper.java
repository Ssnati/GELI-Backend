package com.edu.uptc.gelibackend.mappers;

import com.edu.uptc.gelibackend.dtos.EquipmentEndUseDTO;
import com.edu.uptc.gelibackend.dtos.EquipmentUseResponseDTO;
import com.edu.uptc.gelibackend.dtos.UserResponseDTO;
import com.edu.uptc.gelibackend.entities.EquipmentUseEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EquipmentUseMapper {

    private final EquipmentMapper equipmentMapper;
    private final UserMapper userMapper;
    private final FunctionMapper functionMapper;

    public EquipmentUseResponseDTO toResponseDTO(EquipmentUseEntity equipmentUseEntity) {
        return EquipmentUseResponseDTO.builder()
                .id(equipmentUseEntity.getId())
                .isInUse(equipmentUseEntity.getIsInUse())
                .isVerified(equipmentUseEntity.getIsVerified())
                .isAvailable(equipmentUseEntity.getIsAvailable())
                .equipment(equipmentMapper.toResponseDTO(equipmentUseEntity.getEquipment()))
                .user(userMapper.completeDTOWithEntity(new UserResponseDTO(), equipmentUseEntity.getUser()))
                .usedFunctions(functionMapper.equipmentFunctionsUsedToDTOs(equipmentUseEntity.getEquipmentFunctionsUsedList()))
                .samplesNumber(equipmentUseEntity.getSamplesNumber())
                .observations(equipmentUseEntity.getObservations())
                .startUseTime(equipmentUseEntity.getStartUseTime())
                .endUseTime(equipmentUseEntity.getEndUseTime())
                .startUseTime(equipmentUseEntity.getStartUseTime())
                .endUseTime(equipmentUseEntity.getEndUseTime())
                .build();
    }

    public void completeEntityWithEndDTO(EquipmentUseEntity equipmentUseEntity, EquipmentEndUseDTO equipmentEndUseDTO) {
        equipmentUseEntity.setIsInUse(equipmentEndUseDTO.getIsInUse());
        equipmentUseEntity.setIsVerified(equipmentEndUseDTO.getIsVerified());
        equipmentUseEntity.setIsAvailable(equipmentEndUseDTO.getIsAvailable());
        equipmentUseEntity.setSamplesNumber(equipmentEndUseDTO.getSamplesNumber());
        equipmentUseEntity.setObservations(equipmentEndUseDTO.getObservations());
    }
}