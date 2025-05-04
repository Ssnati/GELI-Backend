package com.edu.uptc.gelibackend.mappers;

import com.edu.uptc.gelibackend.dtos.EquipmentUseDTO;
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

    public EquipmentUseResponseDTO toResponseDTO(EquipmentUseDTO equipmentUseDTO) {
        return EquipmentUseResponseDTO.builder()
                .isInUse(equipmentUseDTO.getIsInUse())
                .isVerified(equipmentUseDTO.getIsVerified())
                .isAvailable(equipmentUseDTO.getIsAvailable())
                .samplesNumber(equipmentUseDTO.getSamplesNumber())
                .observations(equipmentUseDTO.getObservations())
                .build();
    }

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
                .useDate(equipmentUseEntity.getUseDate())
                .startUseTime(equipmentUseEntity.getStartUseTime())
                .endUseTime(equipmentUseEntity.getEndUseTime())
                .build();
    }

    public EquipmentUseEntity toEntity(EquipmentUseDTO equipmentUseDTO) {
        return EquipmentUseEntity.builder()
                .isInUse(equipmentUseDTO.getIsInUse())
                .isVerified(equipmentUseDTO.getIsVerified())
                .isAvailable(equipmentUseDTO.getIsAvailable())
                .samplesNumber(equipmentUseDTO.getSamplesNumber())
                .observations(equipmentUseDTO.getObservations())
                .build();
    }


}
