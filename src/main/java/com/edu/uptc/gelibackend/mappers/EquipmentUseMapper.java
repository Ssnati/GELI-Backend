package com.edu.uptc.gelibackend.mappers;

import com.edu.uptc.gelibackend.dtos.EquipmentUseDTO;
import com.edu.uptc.gelibackend.dtos.EquipmentUseResponseDTO;
import com.edu.uptc.gelibackend.dtos.UserResponseDTO;
import com.edu.uptc.gelibackend.entities.EquipmentUseEntity;
import com.edu.uptc.gelibackend.mappers.ApplicantMapper;
import com.edu.uptc.gelibackend.mappers.EquipmentMapper;
import com.edu.uptc.gelibackend.mappers.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EquipmentUseMapper {

    private final EquipmentMapper equipmentMapper;
    private final UserMapper userMapper;
    private final ApplicantMapper applicantMapper;

    public EquipmentUseResponseDTO toResponseDTO(EquipmentUseDTO equipmentUseDTO) {
        return EquipmentUseResponseDTO.builder()
                .status(equipmentUseDTO.getStatus())
                .samplesNumber(equipmentUseDTO.getSamplesNumber())
                .observations(equipmentUseDTO.getObservations())
                .build();
    }

    public EquipmentUseResponseDTO toResponseDTO(EquipmentUseEntity equipmentUseEntity) {
        return EquipmentUseResponseDTO.builder()
                .id(equipmentUseEntity.getId())
                .status(equipmentUseEntity.getStatus())
                .equipment(equipmentMapper.toResponseDTO(equipmentUseEntity.getEquipment()))
                .user(userMapper.completeDTOWithEntity(new UserResponseDTO(), equipmentUseEntity.getUser()))
                .applicant(applicantMapper.toDTO(equipmentUseEntity.getApplicant()))
                .samplesNumber(equipmentUseEntity.getSamplesNumber())
                .observations(equipmentUseEntity.getObservations())
                .useDate(equipmentUseEntity.getUseDate())
                .startTime(equipmentUseEntity.getStartTime())
                .duration(equipmentUseEntity.getDuration())
                .build();
    }

    public EquipmentUseEntity toEntity(EquipmentUseDTO equipmentUseDTO) {
        return EquipmentUseEntity.builder()
                .status(equipmentUseDTO.getStatus())
                .samplesNumber(equipmentUseDTO.getSamplesNumber())
                .observations(equipmentUseDTO.getObservations())
                .build();
    }


}
