package com.edu.uptc.gelibackend.mappers;

import com.edu.uptc.gelibackend.dtos.AuthorizedEquipmentDTO;
import com.edu.uptc.gelibackend.entities.AuthorizedUserEquipmentsEntity;
import com.edu.uptc.gelibackend.entities.EquipmentEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class AuthorizedUserEquipmentsMapper {


    private final LaboratoryMapper laboratoryMapper;
    private final FunctionMapper functionMapper;

    public AuthorizedEquipmentDTO toAuthorizedEquipmentDTO(EquipmentEntity equipmentEntity) {
        return AuthorizedEquipmentDTO.builder()
                .id(equipmentEntity.getId())
                .equipmentName(equipmentEntity.getEquipmentName())
                .brand(equipmentEntity.getBrand())
                .inventoryNumber(equipmentEntity.getInventoryNumber())
                .laboratory(laboratoryMapper.mapEntityToDTO(equipmentEntity.getLaboratory()))
                .availability(equipmentEntity.getAvailability())
                .functions(functionMapper.equipmentFunctionsToDTOs(equipmentEntity.getEquipmentFunctions()))
                .build();
    }


    public List<AuthorizedEquipmentDTO> toAuthorizedEquipmentDTOs(List<AuthorizedUserEquipmentsEntity> authorizedUserEquipments) {
        return authorizedUserEquipments.stream()
                .map(authorizedUserEquipment -> toAuthorizedEquipmentDTO(authorizedUserEquipment.getEquipment()))
                .toList();
    }

}
