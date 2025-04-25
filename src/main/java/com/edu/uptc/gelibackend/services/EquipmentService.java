package com.edu.uptc.gelibackend.services;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.edu.uptc.gelibackend.dtos.EquipmentCreationDTO;
import com.edu.uptc.gelibackend.entities.*;
import com.edu.uptc.gelibackend.entities.ids.EquipmentFunctionsId;
import com.edu.uptc.gelibackend.mappers.FunctionMapper;
import com.edu.uptc.gelibackend.repositories.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.edu.uptc.gelibackend.dtos.EquipmentResponseDTO;
import com.edu.uptc.gelibackend.mappers.EquipmentMapper;
import com.edu.uptc.gelibackend.filters.EquipmentFilterDTO;
import com.edu.uptc.gelibackend.specifications.EquipmentSpecification;

import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EquipmentService {

    private final EquipmentRepository equipmentRepo;
    private final FunctionRepository functionRepo;
    private final EquipmentFunctionsRepository functionHistoryRepo;
    private final LaboratoryRepository laboratoryRepo;
    private final EquipmentSpecification equipmentSpecification;
    private final UserRepository userRepo;
    private final EquipmentMapper mapper;
    private final FunctionMapper functionMapper;

    public List<EquipmentResponseDTO> findAll() {
        return equipmentRepo.findAll().stream()
                .map(mapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    public EquipmentResponseDTO findById(Long id) {
        return equipmentRepo.findById(id)
                .map(mapper::toResponseDTO)
                .orElse(null);
    }

    @Transactional
    public EquipmentResponseDTO create(EquipmentCreationDTO dto) {
        this.validateUniqueName(dto.getEquipmentName());
        this.validateInventoryNumber(dto.getInventoryNumber());

        EquipmentEntity equipment = mapper.toEntity(dto);
        equipment.setLaboratory(this.findLaboratoryById(dto.getLaboratoryId()));
        this.setFunctionsToEquipment(equipment, this.findFunctionsByIds(dto.getFunctions()));

        EquipmentEntity save = equipmentRepo.save(equipment);
        this.setUsersToEquipment(save, this.findUsersByIds(dto.getAuthorizedUsersIds()));

        return mapper.toResponseDTO(save);
    }

    private void setUsersToEquipment(EquipmentEntity equipment, List<UserEntity> userEntities) {
        // Crear los registros de AuthorizedUserEquipmentsEntity
        List<AuthorizedUserEquipmentsEntity> equipmentAuthorizedUsers = userEntities.stream()
                .map(user -> AuthorizedUserEquipmentsEntity.builder()
                        .actualStatus(true)
                        .equipment(equipment)
                        .user(user)
                        .build())
                .toList();
        // Crear el historial de cambios para AuthorizedUserEquipmentsEntity
        List<EquipmentAuthorizationHistoryEntity> history = equipmentAuthorizedUsers.stream()
                .map(equipmentAuthorizedUser -> EquipmentAuthorizationHistoryEntity.builder()
                        .authorizedUserEquipments(equipmentAuthorizedUser)
                        .modificationAuthorizationStatusDate(LocalDate.now())
                        .authorizationStatusToDate(true)
                        .build())
                .toList();
    }

    private List<UserEntity> findUsersByIds(List<Long> authorizedUsersIds) {
        return authorizedUsersIds.stream()
                .map(userId -> userRepo.findById(userId)
                        .orElseThrow(() -> new IllegalArgumentException("User not found")))
                .collect(Collectors.toList());
    }

    private List<EquipmentFunctionsEntity> setFunctionsToEquipment(EquipmentEntity equipment, List<FunctionEntity> functions) {
        List<EquipmentFunctionsEntity> equipmentFunctionsEntityList = functions.stream()
                .map(function -> new EquipmentFunctionsEntity(new EquipmentFunctionsId(), equipment, function))
                .toList();
        equipment.setEquipmentFunctions(equipmentFunctionsEntityList);
        return equipmentFunctionsEntityList;
    }

    private LaboratoryEntity findLaboratoryById(Long laboratoryId) {
        return laboratoryRepo.findById(laboratoryId)
                .orElseThrow(() -> new IllegalArgumentException("Laboratory not found"));
    }

    private List<FunctionEntity> findFunctionsByIds(List<Long> functionIds) {
        if (functionIds == null || functionIds.isEmpty()) {
            return List.of();
        }
        List<FunctionEntity> functions = functionRepo.findAllById(functionIds);
        if (functions.size() != functionIds.size()) {
            throw new IllegalArgumentException("Some functions were not found");
        }
        return functions;
    }

    private void validateUniqueName(String equipmentName) {
        if (equipmentRepo.findAll().stream()
                .anyMatch(equipment -> equipment.getEquipmentName().equalsIgnoreCase(equipmentName))) {
            throw new IllegalArgumentException("Equipment's name must be unique");
        }
    }

    public EquipmentResponseDTO update(Long id, EquipmentResponseDTO dto) {
        EquipmentEntity exist = equipmentRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Equipment not found"));

        this.validateUniqueName(dto.getEquipmentName());
        this.validateInventoryNumber(dto.getInventoryNumber());

        exist.setEquipmentName(dto.getEquipmentName());
        exist.setBrand(dto.getBrand());
        exist.setInventoryNumber(dto.getInventoryNumber());
        exist.setLaboratory(laboratoryRepo.findById(dto.getLaboratory().getId())
                .orElseThrow(() -> new IllegalArgumentException("Laboratory not found")));
        EquipmentEntity updatedEntity = equipmentRepo.save(exist);
        return mapper.toResponseDTO(updatedEntity);
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

    public List<EquipmentResponseDTO> filter(EquipmentFilterDTO filter) {
        Specification<EquipmentEntity> spec = equipmentSpecification.build(filter);
        return equipmentRepo.findAll(spec).stream()
                .map(mapper::toResponseDTO)
                .collect(Collectors.toList());
    }
}