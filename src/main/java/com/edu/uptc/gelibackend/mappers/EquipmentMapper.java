package com.edu.uptc.gelibackend.mappers;

import com.edu.uptc.gelibackend.dtos.EquipmentDTO;
import com.edu.uptc.gelibackend.entities.EquipmentEntity;
import org.springframework.stereotype.Component;

@Component
public class EquipmentMapper {

    private final FunctionMapper functionMapper;
    private final LaboratoryMapper laboratoryMapper;

    public EquipmentMapper(FunctionMapper functionMapper, LaboratoryMapper laboratoryMapper) {
        this.functionMapper = functionMapper;
        this.laboratoryMapper = laboratoryMapper;
    }

    public EquipmentEntity mapDTOToEntity(EquipmentDTO equipmentDTO) {
        return new EquipmentEntity(
                equipmentDTO.getId(),
                equipmentDTO.getEquipmentName(),
                equipmentDTO.getBrand(),
                equipmentDTO.getInventoryNumber(),
                laboratoryMapper.mapDTOToEntity(equipmentDTO.getLaboratory()),
                equipmentDTO.getAvailability(),
                functionMapper.mapDTOToEntity(equipmentDTO.getFunction())
        );
    }

    public EquipmentDTO mapEntityToDTO(EquipmentEntity equipmentEntity) {
        return new EquipmentDTO(
                equipmentEntity.getId(),
                equipmentEntity.getEquipmentName(),
                equipmentEntity.getBrand(),
                equipmentEntity.getInventoryNumber(),
                laboratoryMapper.mapEntityToDTO(equipmentEntity.getLaboratory()),
                equipmentEntity.getAvailability(),
                functionMapper.mapEntityToDTO(equipmentEntity.getFunction())
        );
    }
}
