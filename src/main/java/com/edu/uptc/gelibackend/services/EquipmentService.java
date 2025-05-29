package com.edu.uptc.gelibackend.services;

import com.edu.uptc.gelibackend.dtos.EquipmentCreationDTO;
import com.edu.uptc.gelibackend.dtos.EquipmentResponseDTO;
import com.edu.uptc.gelibackend.dtos.EquipmentUpdateDTO;
import com.edu.uptc.gelibackend.dtos.PageResponse;
import com.edu.uptc.gelibackend.entities.*;
import com.edu.uptc.gelibackend.entities.ids.AuthorizedUserEquipmentsId;
import com.edu.uptc.gelibackend.entities.ids.EquipmentFunctionsId;
import com.edu.uptc.gelibackend.filters.EquipmentFilterDTO;
import com.edu.uptc.gelibackend.mappers.EquipmentMapper;
import com.edu.uptc.gelibackend.mappers.FunctionMapper;
import com.edu.uptc.gelibackend.repositories.*;
import com.edu.uptc.gelibackend.specifications.EquipmentSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EquipmentService {

    private final EquipmentRepository equipmentRepo;
    private final FunctionRepository functionRepo;
    private final EquipmentFunctionsRepository functionHistoryRepo;
    private final LaboratoryRepository laboratoryRepo;
    private final BrandRepository brandRepo;
    private final EquipmentSpecification equipmentSpecification;
    private final UserRepository userRepo;
    private final EquipmentMapper mapper;
    private final FunctionMapper functionMapper;

    public PageResponse<EquipmentResponseDTO> findAll(int page, int size) {
        if (size <= 0) {
            throw new IllegalArgumentException("Page size must not be less than or equal to zero");
        }
        // Aplicar paginación
        Pageable pageable = PageRequest.of(page, size);
        Page<EquipmentEntity> pageResult = equipmentRepo.findAll(pageable);

        List<EquipmentResponseDTO> content = pageResult.getContent().stream()
                .map(mapper::toResponseDTO)
                .collect(Collectors.toList());

        return new PageResponse<>(
                pageResult.getNumber(),
                pageResult.getSize(),
                pageResult.getTotalElements(),
                pageResult.getTotalPages(),
                content
        );
    }

    public EquipmentResponseDTO findById(Long id) {
        return equipmentRepo.findById(id)
                .map(mapper::toResponseDTO)
                .orElse(null);
    }

    @Transactional
    public EquipmentResponseDTO create(EquipmentCreationDTO dto) {
//        this.validateUniqueName(dto.getEquipmentName());
        this.validateInventoryNumber(dto.getInventoryNumber());
        this.validateBrandExistence(dto.getBrand().getId());

        EquipmentEntity equipment = mapper.toEntity(dto);
        equipment.setLaboratory(this.findLaboratoryById(dto.getLaboratoryId()));
        this.setFunctionsToEquipment(equipment, this.findFunctionsByIds(dto.getFunctions()));
        this.setUsersToEquipment(equipment, this.findUsersByIds(dto.getAuthorizedUsersIds()));

        EquipmentEntity save = equipmentRepo.save(equipment);
        return mapper.toResponseDTO(save);
    }

    private void validateBrandExistence(Long id) {

    }

    private void setUsersToEquipment(EquipmentEntity equipment, List<UserEntity> userEntities) {
        // Crear los registros de AuthorizedUserEquipmentsEntity
        List<AuthorizedUserEquipmentsEntity> equipmentAuthorizedUsers = userEntities.stream()
                .map(user -> AuthorizedUserEquipmentsEntity.builder()
                        .id(new AuthorizedUserEquipmentsId())
                        .actualStatus(true)
                        .equipment(equipment)
                        .user(user)
                        .build())
                .toList();
        // Crear el historial de cambios para AuthorizedUserEquipmentsEntity
        List<EquipmentAuthorizationHistoryEntity> history = equipmentAuthorizedUsers.stream()
                .map(equipmentAuthorizedUser -> EquipmentAuthorizationHistoryEntity.builder()
                        .authorizedUserEquipments(equipmentAuthorizedUser)
                        .modificationAuthorizationStatusDate(ZonedDateTime.now(ZoneId.of("America/Bogota")).toLocalDate())
                        .authorizationStatusToDate(true)
                        .build())
                .toList();
        // Guardar los registros de AuthorizedUserEquipmentsEntity
        equipment.setAuthorizedUsersEquipments(equipmentAuthorizedUsers);
        // Guardar el historial de cambios
        equipmentAuthorizedUsers.forEach(equipmentAuthorizedUser ->
                equipmentAuthorizedUser.setEquipmentAuthorizationHistory(new ArrayList<>(history)));
    }

    private List<UserEntity> findUsersByIds(List<Long> authorizedUsersIds) {
        return authorizedUsersIds.stream()
                .map(userId -> userRepo.findById(userId)
                        .orElseThrow(() -> new IllegalArgumentException("User not found")))
                .collect(Collectors.toList());
    }

    private List<EquipmentFunctionsEntity> setFunctionsToEquipment(EquipmentEntity equipment, List<FunctionEntity> functions) {
        if (functions == null || functions.isEmpty()) {
            return List.of();
        }

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

    public EquipmentResponseDTO update(Long id, EquipmentUpdateDTO dto) {
        EquipmentEntity exist = equipmentRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Equipment not found"));

        // Se valida el cambio de marca y se asigna si se hizo cambio
        if (dto.getBrandId() != null && !dto.getBrandId().equals(exist.getBrand().getId())) {
            exist.setBrand(brandRepo.findById(dto.getBrandId())
                    .orElseThrow(() -> new IllegalArgumentException("Brand not found")));
        }


        // Se valida que el laboratorio exista y se asigna si se hizo cambio
        if (dto.getLaboratoryId() != null && !dto.getLaboratoryId().equals(exist.getLaboratory().getId())) {
            exist.setLaboratory(laboratoryRepo.findById(dto.getLaboratoryId())
                    .orElseThrow(() -> new IllegalArgumentException("Laboratory not found")));
        }

        // Se maneja el cambio de estado de disponibilidad en caso de que haya cambiado
        if (dto.getAvailability() != exist.getAvailability()) {
            exist.setAvailability(dto.getAvailability());
            exist.setEquipmentObservations(dto.getEquipmentObservations());
        }

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

    @Transactional(readOnly = true)
    public PageResponse<EquipmentResponseDTO> filter(EquipmentFilterDTO filter, int page, int size) {
        if (size <= 0) {
            throw new IllegalArgumentException("Page size must not be less than or equal to zero");
        }
        Pageable pageable = PageRequest.of(page, size);
        Specification<EquipmentEntity> spec = equipmentSpecification.build(filter);
        Page<EquipmentEntity> pageResult = equipmentRepo.findAll(spec, pageable);

        List<EquipmentResponseDTO> content = pageResult.getContent().stream()
                .map(mapper::toResponseDTO)
                .toList();

        return new PageResponse<>(
                pageResult.getNumber(),
                pageResult.getSize(),
                pageResult.getTotalElements(),
                pageResult.getTotalPages(),
                content
        );
    }

    public boolean existsByInventoryNumber(String inventoryNumber) {
        return equipmentRepo.existsByInventoryNumberIgnoreCase(inventoryNumber);
    }

    public boolean existsByName(String equipmentName) {
        return equipmentRepo.existsByEquipmentNameIgnoreCase(equipmentName);
    }
}