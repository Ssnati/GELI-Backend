package com.edu.uptc.gelibackend.services;

import com.edu.uptc.gelibackend.dtos.EquipmentDTO;
import com.edu.uptc.gelibackend.entities.EquipmentEntity;
import com.edu.uptc.gelibackend.mappers.EquipmentMapper;
import com.edu.uptc.gelibackend.repositories.EquipmentRepository;
import com.edu.uptc.gelibackend.repositories.FunctionRepository;
import com.edu.uptc.gelibackend.repositories.LaboratoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EquipmentService {

    private final EquipmentRepository equipmentRepo;
    private FunctionRepository functionRepo;
    private final EquipmentMapper mapper;
    private final LaboratoryRepository laboratoryRepo;
//    private final EquipmentSpecification equipmentSpecification;

    public List<EquipmentDTO> findAll() {
        return equipmentRepo.findAll().stream()
                .map(mapper::mapEntityToDTO)
                .collect(Collectors.toList());
    }

    public EquipmentDTO findById(Long id) {
        return equipmentRepo.findById(id)
                .map(mapper::mapEntityToDTO)
                .orElse(null);
    }

    public EquipmentDTO create(EquipmentDTO dto) {
        this.validateUniqueName(dto.getEquipmentName());
        return mapper.mapEntityToDTO(equipmentRepo.save(mapper.mapDTOToEntity(dto)));
    }

    private void validateUniqueName(String equipmentName) {
        if (equipmentRepo.findAll().stream()
                .anyMatch(equipment -> equipment.getEquipmentName().equalsIgnoreCase(equipmentName))) {
            throw new IllegalArgumentException("Equipment's name must be unique");
        }
    }

    public EquipmentDTO update(Long id, EquipmentDTO dto) {
        EquipmentEntity exist = equipmentRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Equipment not found"));

        this.validateUniqueName(dto.getEquipmentName());
        this.validateInventoryNumber(dto.getInventoryNumber());

        exist.setEquipmentName(dto.getEquipmentName());
        exist.setBrand(dto.getBrand());
        exist.setInventoryNumber(dto.getInventoryNumber());
        exist.setLaboratory(laboratoryRepo.findById(dto.getLaboratory().getId())
                .orElseThrow(() -> new IllegalArgumentException("Laboratory not found")));
        return dto;
    }

    private void validateInventoryNumber(String inventoryNumber) {
        if (equipmentRepo.findAll().stream()
                .anyMatch(equipment -> equipment.getInventoryNumber().equalsIgnoreCase(inventoryNumber))) {
            throw new IllegalArgumentException("Inventory number must be unique");
        }
    }

    public boolean delete(Long id) {
        if (equipmentRepo.existsById(id)) {
            equipmentRepo.deleteById(id);
            return true;
        }
        return false;
    }


}