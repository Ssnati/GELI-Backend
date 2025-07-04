package com.edu.uptc.gelibackend.services;

import com.edu.uptc.gelibackend.dtos.*;
import com.edu.uptc.gelibackend.dtos.equipment.*;
import com.edu.uptc.gelibackend.entities.*;
import com.edu.uptc.gelibackend.entities.ids.AuthorizedUserEquipmentsId;
import com.edu.uptc.gelibackend.entities.ids.EquipmentFunctionsId;
import com.edu.uptc.gelibackend.filters.EquipmentFilterDTO;
import com.edu.uptc.gelibackend.mappers.EquipmentMapper;
import com.edu.uptc.gelibackend.mappers.FunctionMapper;
import com.edu.uptc.gelibackend.repositories.*;
import com.edu.uptc.gelibackend.specifications.EquipmentSpecification;
import jakarta.persistence.EntityNotFoundException;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
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
    private final BrandService brandService;
    private final AuthorizedUserEquipmentsRepository authorizedUserEquipmentsRepository;
    private final UserService userService;


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

    public PageResponse<EquipmentForFilterResponseDTO> findAllForFilter(int page, int size) {
        if (size <= 0) {
            throw new IllegalArgumentException("Page size must not be less than or equal to zero");
        }
        // Aplicar paginación
        Pageable pageable = PageRequest.of(page, size);
        Page<EquipmentEntity> pageResult = equipmentRepo.findAll(pageable);

        List<EquipmentForFilterResponseDTO> content = pageResult.getContent().stream()
                .map(mapper::toForFilterResponseDTO)
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

    public EquipmentFunctionsResponseDTO findFunctionsById(Long id) {
        return equipmentRepo.findById(id).map(equipmentEntity -> {
            EquipmentFunctionsResponseDTO dto = mapper.toFunctionsResponseDTO(equipmentEntity);

            List<FunctionDTO> mutableFunctions = new ArrayList<>(dto.getFunctions());
            dto.setFunctions(mutableFunctions); // asegurar lista mutable

            boolean hasNoAplica = mutableFunctions.stream()
                    .anyMatch(func -> "NO APLICA".equalsIgnoreCase(func.getFunctionName()));

            if (!hasNoAplica) {
                functionRepo.findByFunctionName("NO APLICA").ifPresent(noAplicaFunction -> {
                    mutableFunctions.add(0, functionMapper.toDTO(noAplicaFunction));
                });
            }

            return dto;
        }).orElse(null);
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

    @Transactional
    public EquipmentResponseDTO update(Long id, EquipmentUpdateDTO dto) {
        EquipmentEntity existing = equipmentRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("Equipo no encontrado con ID: " + id));

        // Validar inventario solo si cambia
        if (!existing.getInventoryNumber().equalsIgnoreCase(dto.getInventoryNumber())) {
            this.validateInventoryNumber(dto.getInventoryNumber());
        }

        // Validaciones básicas
        if (dto.getBrand() == null || dto.getBrand().getId() == null) {
            throw new BadRequestException("El ID de la marca es obligatorio");
        }

        this.validateBrandExistence(dto.getBrand().getId());

        // Actualizar campos simples
        existing.setEquipmentName(dto.getEquipmentName());
        existing.setInventoryNumber(dto.getInventoryNumber());
        existing.setBrand(brandService.getById(dto.getBrand().getId()));
        existing.setLaboratory(findLaboratoryById(dto.getLaboratoryId()));
        existing.setAvailability(dto.getAvailability());
        existing.setEquipmentObservations(dto.getEquipmentObservations());

        // ✅ Actualizar funciones asociadas
        List<FunctionEntity> newFunctions = findFunctionsByIds(dto.getFunctions());
        this.setFunctionsToEquipment(existing, newFunctions);

        // ✅ (Opcional) también puedes actualizar usuarios autorizados si es parte del update
        // this.setUsersToEquipment(existing, findUsersByIds(dto.getAuthorizedUsersIds()));

        EquipmentEntity saved = equipmentRepo.save(existing);
        return mapper.toResponseDTO(saved);
    }



    private void setFunctionsToEquipment(EquipmentEntity equipment, List<FunctionEntity> newFunctions) {
        if (equipment.getEquipmentFunctions() == null) {
            equipment.setEquipmentFunctions(new ArrayList<>());
        } else {
            equipment.getEquipmentFunctions().clear();
        }

        for (FunctionEntity function : newFunctions) {
            EquipmentFunctionsEntity efe = new EquipmentFunctionsEntity();
            efe.setEquipment(equipment);
            efe.setFunction(function);
            efe.setId(new EquipmentFunctionsId(equipment.getId(), function.getId()));

            equipment.getEquipmentFunctions().add(efe);
        }
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
    public PageResponse<EquipmentFilterResponseDTO> filter(EquipmentFilterDTO filter, int page, int size) {
        if (size <= 0) {
            throw new IllegalArgumentException("Page size must not be less than or equal to zero");
        }
        Pageable pageable = PageRequest.of(page, size);
        Specification<EquipmentEntity> spec = equipmentSpecification.build(filter);
        Page<EquipmentEntity> pageResult = equipmentRepo.findAll(spec, pageable);

        List<EquipmentFilterResponseDTO> content = pageResult.getContent().stream()
                .map(mapper::toFilterResponseDTO)
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

    public boolean existsByInventoryNumberExcludingId(String inventoryNumber, Long excludeId) {
        return equipmentRepo.existsByInventoryNumberIgnoreCaseAndIdNot(inventoryNumber, excludeId);
    }


    public boolean existsByName(String equipmentName) {
        return equipmentRepo.existsByEquipmentNameIgnoreCase(equipmentName);
    }

    public List<EquipmentByUserResponseDTO> getAuthorizedEquipmentsByUserAndLab(String email, Long labId) {
        Long userId = userService.findUserByEmail(email).get().getId();
        return authorizedUserEquipmentsRepository
                .findAuthorizedEquipmentsByUserIdAndLaboratoryId(userId, labId)
                .stream()
                .map(mapper::toByUserResponseDTO)
                .collect(Collectors.toList());
    }

    public EquipmentAvailabilityResponseDTO getAvailability(Long equipmentId) {
        EquipmentEntity equipment = equipmentRepo.findById(equipmentId)
                .orElseThrow(() -> new EntityNotFoundException("Equipo no encontrado"));

        boolean isAvailable = equipment.getAvailability();
        String message = isAvailable
                ? "Este equipo está disponible para su uso."
                : (equipment.getEquipmentObservations() != null
                ? equipment.getEquipmentObservations()
                : "Este equipo está inactivo, sin observaciones registradas.");

        return new EquipmentAvailabilityResponseDTO(isAvailable, message);
    }

}