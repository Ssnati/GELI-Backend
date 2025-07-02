package com.edu.uptc.gelibackend.mappers;

import com.edu.uptc.gelibackend.dtos.EquipmentCreationDTO;
import com.edu.uptc.gelibackend.dtos.EquipmentResponseDTO;
import com.edu.uptc.gelibackend.dtos.equipment.EquipmentFilterResponseDTO;
import com.edu.uptc.gelibackend.dtos.equipment.EquipmentFunctionsResponseDTO;
import com.edu.uptc.gelibackend.entities.EquipmentEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EquipmentMapper {

    private final LaboratoryMapper laboratoryMapper;
    private final FunctionMapper functionMapper;
    private final UserMapper userMapper;
    private final BrandMapper brandMapper;


    public EquipmentEntity toEntity(EquipmentResponseDTO equipmentDTO) {
        EquipmentEntity entity = new EquipmentEntity();
        entity.setId(equipmentDTO.getId());
        entity.setEquipmentName(equipmentDTO.getEquipmentName());
        entity.setBrand(brandMapper.toEntity(equipmentDTO.getBrand()));
        entity.setInventoryNumber(equipmentDTO.getInventoryNumber());
        entity.setLaboratory(laboratoryMapper.mapDTOToEntity(equipmentDTO.getLaboratory()));
        entity.setAvailability(equipmentDTO.getAvailability());
        entity.setEquipmentObservations(equipmentDTO.getEquipmentObservations());
        return entity;
    }

    public EquipmentResponseDTO toResponseDTO(EquipmentEntity equipmentEntity) {
        EquipmentResponseDTO equipmentResponseDTO = new EquipmentResponseDTO();
        equipmentResponseDTO.setId(equipmentEntity.getId());
        equipmentResponseDTO.setEquipmentName(equipmentEntity.getEquipmentName());
        equipmentResponseDTO.setBrand(brandMapper.toDTO(equipmentEntity.getBrand()));
        equipmentResponseDTO.setInventoryNumber(equipmentEntity.getInventoryNumber());
        equipmentResponseDTO.setLaboratory(laboratoryMapper.mapEntityToDTO(equipmentEntity.getLaboratory()));
        equipmentResponseDTO.setFunctions(functionMapper.equipmentFunctionsToDTOs(equipmentEntity.getEquipmentFunctions()));
        equipmentResponseDTO.setAuthorizedUsers(userMapper.toResponseDTOs(equipmentEntity.getAuthorizedUsersEquipments()));
        equipmentResponseDTO.setAvailability(equipmentEntity.getAvailability());
        equipmentResponseDTO.setEquipmentObservations(equipmentEntity.getEquipmentObservations());
        return equipmentResponseDTO;
    }

    public EquipmentFunctionsResponseDTO toFunctionsResponseDTO(EquipmentEntity equipmentEntity) {
        EquipmentFunctionsResponseDTO equipmentResponseDTO = new EquipmentFunctionsResponseDTO();
        equipmentResponseDTO.setId(equipmentEntity.getId());
        equipmentResponseDTO.setEquipmentName(equipmentEntity.getEquipmentName());
        equipmentResponseDTO.setInventoryNumber(equipmentEntity.getInventoryNumber());
        equipmentResponseDTO.setFunctions(functionMapper.equipmentFunctionsToDTOs(equipmentEntity.getEquipmentFunctions()));
        equipmentResponseDTO.setAvailability(equipmentEntity.getAvailability());
        return equipmentResponseDTO;
    }

    public EquipmentFilterResponseDTO toFilterResponseDTO(EquipmentEntity equipmentEntity) {
        EquipmentFilterResponseDTO equipmentResponseDTO = new EquipmentFilterResponseDTO();
        equipmentResponseDTO.setId(equipmentEntity.getId());
        equipmentResponseDTO.setEquipmentName(equipmentEntity.getEquipmentName());
        equipmentResponseDTO.setBrand(brandMapper.toDTO(equipmentEntity.getBrand()));
        equipmentResponseDTO.setInventoryNumber(equipmentEntity.getInventoryNumber());
        equipmentResponseDTO.setLaboratory(laboratoryMapper.mapEntityToDTO(equipmentEntity.getLaboratory()));
        equipmentResponseDTO.setAvailability(equipmentEntity.getAvailability());
        return equipmentResponseDTO;
    }

    public EquipmentEntity toEntity(EquipmentCreationDTO dto) {
        EquipmentEntity equipmentEntity = new EquipmentEntity();
        equipmentEntity.setEquipmentName(dto.getEquipmentName());
        equipmentEntity.setBrand(brandMapper.toEntity(dto.getBrand()));
        equipmentEntity.setInventoryNumber(dto.getInventoryNumber());
        equipmentEntity.setAvailability(dto.getAvailability());
        equipmentEntity.setEquipmentObservations(dto.getEquipmentObservations());
        return equipmentEntity;
    }
}
