package com.edu.uptc.gelibackend.mappers;

import com.edu.uptc.gelibackend.dtos.EquipmentDTO;
import com.edu.uptc.gelibackend.entities.EquipmentEntity;
import org.springframework.stereotype.Component;

@Component
public class EquipmentMapper {

    private final LaboratoryMapper laboratoryMapper;

    public EquipmentMapper(LaboratoryMapper laboratoryMapper) {
        this.laboratoryMapper = laboratoryMapper;
    }

    public EquipmentEntity mapDTOToEntity(EquipmentDTO equipmentDTO) {
        EquipmentEntity entity = new EquipmentEntity();
        entity.setId(equipmentDTO.getId());
        entity.setEquipmentName(equipmentDTO.getEquipmentName());
        entity.setBrand(equipmentDTO.getBrand());
        entity.setInventoryNumber(equipmentDTO.getInventoryNumber());
        entity.setLaboratory(laboratoryMapper.mapDTOToEntity(equipmentDTO.getLaboratory()));
        entity.setAvailability(equipmentDTO.getAvailability());
        return entity;
    }

    public EquipmentDTO mapEntityToDTO(EquipmentEntity equipmentEntity) {
        return new EquipmentDTO(
                equipmentEntity.getId(),
                equipmentEntity.getEquipmentName(),
                equipmentEntity.getBrand(),
                equipmentEntity.getInventoryNumber(),
                laboratoryMapper.mapEntityToDTO(equipmentEntity.getLaboratory()),
                equipmentEntity.getAvailability()
        );
    }
}
