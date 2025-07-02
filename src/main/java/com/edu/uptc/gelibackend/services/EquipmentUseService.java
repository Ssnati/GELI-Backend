package com.edu.uptc.gelibackend.services;

import com.edu.uptc.gelibackend.dtos.EquipmentEndUseDTO;
import com.edu.uptc.gelibackend.dtos.EquipmentStartUseDTO;
import com.edu.uptc.gelibackend.dtos.EquipmentUseResponseDTO;
import com.edu.uptc.gelibackend.dtos.PageResponse;
import com.edu.uptc.gelibackend.dtos.equipment.EquipmentFilterResponseDTO;
import com.edu.uptc.gelibackend.dtos.equipment.use.EquipmentAvailabilityStatusDTO;
import com.edu.uptc.gelibackend.entities.*;
import com.edu.uptc.gelibackend.entities.ids.EquipmentFunctionsUsedId;
import com.edu.uptc.gelibackend.filters.EquipmentUseFilterDTO;
import com.edu.uptc.gelibackend.mappers.EquipmentUseMapper;
import com.edu.uptc.gelibackend.repositories.EquipmentRepository;
import com.edu.uptc.gelibackend.repositories.EquipmentUseRepository;
import com.edu.uptc.gelibackend.repositories.FunctionRepository;
import com.edu.uptc.gelibackend.repositories.UserRepository;
import com.edu.uptc.gelibackend.specifications.EquipmentUseSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class EquipmentUseService {
    private final EquipmentUseRepository equipmentUseRepo;

    private final UserRepository userRepo;
    private final EquipmentRepository equipmentRepo;
    private final FunctionRepository functionRepo;
    private final EquipmentUseMapper mapper;
    private final EquipmentUseSpecification specification;
    private final UserService userService;

    @Transactional
    public Optional<EquipmentUseResponseDTO> startEquipmentUse(EquipmentStartUseDTO equipmentStartUseDTO, String username) {
        validateEquipmentUseCreationData(equipmentStartUseDTO);

        EquipmentUseEntity entity = buildEquipmentUseEntity(equipmentStartUseDTO, username);

        EquipmentUseEntity savedEntity = equipmentUseRepo.save(entity);

        return Optional.of(mapper.toResponseDTO(savedEntity));
    }

    private void validateEquipmentUseCreationData(EquipmentStartUseDTO equipmentStartUseDTO) {
        if (equipmentStartUseDTO.getUserId() == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }
        if (equipmentStartUseDTO.getEquipmentId() == null) {
            throw new IllegalArgumentException("Equipment ID cannot be null");
        }
    }

    private EquipmentUseEntity buildEquipmentUseEntity(EquipmentStartUseDTO equipmentStartUseDTO, String username) {
        EquipmentUseEntity entity = EquipmentUseEntity.builder()
                .isVerified(true) // Inicialmente se asume verificado
                .isAvailable(true)  //Inicialmente disponible
                .samplesNumber(0)
                .equipmentFunctionsUsedList(List.of())
                .build();

        entity.setUser(findUserByEmail(username));
        EquipmentEntity equipmentEntity = findEquipmentById(equipmentStartUseDTO.getEquipmentId());
        entity.setEquipment(equipmentEntity);
        entity.setStartUseTime(ZonedDateTime.now(ZoneId.of("America/Bogota")).toLocalDateTime());
        entity.setIsInUse(true);

        return entity;
    }

    private UserEntity findUserById(Long userId) {
        return userRepo.findById(userId).orElseThrow(() -> new IllegalArgumentException("User with ID " + userId + " not found"));
    }

    private UserEntity findUserByEmail(String username) {
        return userRepo.findByEmail(username.toUpperCase());
    }

    private EquipmentEntity findEquipmentById(Long equipmentId) {
        return equipmentRepo.findById(equipmentId).orElseThrow(() -> new IllegalArgumentException("Equipment with ID " + equipmentId + " not found"));
    }

    private List<FunctionEntity> findFunctionById(List<Long> usedFunctions) {
        return functionRepo.findAllById(usedFunctions);
    }

    private List<FunctionEntity> validateEquipmentUsedFunctions(EquipmentEndUseDTO equipmentEndUseDTO, EquipmentEntity entity) {
        List<FunctionEntity> equipmentFunctions = entity.getEquipmentFunctions().stream()
                .map(EquipmentFunctionsEntity::getFunction)
                .toList();
        List<Long> requestedFunctionsId = equipmentEndUseDTO.getUsedFunctions();
        List<FunctionEntity> functionEntityList = findFunctionById(requestedFunctionsId);
        for (FunctionEntity function : functionEntityList) {
            if (equipmentFunctions.stream().noneMatch(equipmentFunction -> Objects.equals(equipmentFunction.getId(), function.getId()))) {
                throw new IllegalArgumentException("The function " + function.getFunctionName() + " is not available for this equipment");
            }
        }
        return functionEntityList;
    }

    private void assignFunctionsToEntity(EquipmentUseEntity entity, List<FunctionEntity> functionEntityList) {
        List<EquipmentFunctionsUsedEntity> equipmentFunctionsUsedList = functionEntityList.stream()
                .map(function -> new EquipmentFunctionsUsedEntity(new EquipmentFunctionsUsedId(function.getId(), entity.getId()), entity, function))
                .toList();
        entity.setEquipmentFunctionsUsedList(new ArrayList<>(equipmentFunctionsUsedList));
    }

    public Optional<EquipmentUseResponseDTO> endEquipmentUse(Long id, EquipmentEndUseDTO equipmentEndUseDTO) {
        EquipmentUseEntity equipmentUseEntity = validateEquipmentUseIsAlreadyStarted(id);
        mapper.completeEntityWithEndDTO(equipmentUseEntity, equipmentEndUseDTO);

        List<FunctionEntity> functionEntityList = validateEquipmentUsedFunctions(equipmentEndUseDTO, equipmentUseEntity.getEquipment());
        assignFunctionsToEntity(equipmentUseEntity, functionEntityList);
        equipmentUseEntity.setEndUseTime(ZonedDateTime.now(ZoneId.of("America/Bogota")).toLocalDateTime());
        equipmentUseEntity.setIsInUse(false);

        equipmentUseRepo.save(equipmentUseEntity);

        return Optional.of(mapper.toResponseDTO(equipmentUseEntity));
    }

    private EquipmentUseEntity validateEquipmentUseIsAlreadyStarted(Long id) {
        EquipmentUseEntity equipmentUseEntity = equipmentUseRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Equipment use with ID " + id + " not found"));
        if (equipmentUseEntity.getEndUseTime() != null || !equipmentUseEntity.getIsInUse()) {
            throw new IllegalArgumentException("Equipment use with ID " + id + " is already ended");
        }
        return equipmentUseEntity;
    }

    public List<EquipmentUseResponseDTO> getAllEquipmentUses() {
        return equipmentUseRepo.findAll().stream()
                .map(mapper::toResponseDTO)
                .toList();
    }

    public Optional<EquipmentUseResponseDTO> getEquipmentUse(Long id) {
        return equipmentUseRepo.findById(id).map(mapper::toResponseDTO);
    }

    public PageResponse<EquipmentUseResponseDTO> filter(EquipmentUseFilterDTO filter, int page, int size) {
        if (size <= 0) {
            throw new IllegalArgumentException("Page size must not be less than or equal to zero");
        }
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "startUseTime"));
        Specification<EquipmentUseEntity> spec = specification.build(filter);
        Page<EquipmentUseEntity> pageResult = equipmentUseRepo.findAll(spec, pageable);

        List<EquipmentUseResponseDTO> content = pageResult.getContent().stream()
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

    public EquipmentAvailabilityStatusDTO getEquipmentAvailabilityStatus(Long equipmentId, String email) {
        Long userId = userService.findUserByEmail(email).get().getId();
        List<EquipmentUseEntity> usages = equipmentUseRepo.findByEquipmentIdAndIsInUseTrue(equipmentId);


        if (usages.isEmpty()) {
            return new EquipmentAvailabilityStatusDTO("AVAILABLE", "✅ El equipo está disponible.");
        }

        for (EquipmentUseEntity usage : usages) {
            if (usage.getUser().getId().equals(userId)) {
                return new EquipmentAvailabilityStatusDTO("IN_USE_BY_YOU", "⚠️ Este equipo ya está siendo usado por usted.");
            }
        }

        return new EquipmentAvailabilityStatusDTO(
                "IN_USE_BY_ANOTHER",
                "❌ Este equipo está siendo usado por otro(s) usuario(s)."
        );
    }
}