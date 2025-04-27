package com.edu.uptc.gelibackend.services;

import com.edu.uptc.gelibackend.dtos.EquipmentUseDTO;
import com.edu.uptc.gelibackend.dtos.EquipmentUseResponseDTO;
import com.edu.uptc.gelibackend.entities.*;
import com.edu.uptc.gelibackend.entities.ids.EquipmentFunctionsUsedId;
import com.edu.uptc.gelibackend.mappers.EquipmentUseMapper;
import com.edu.uptc.gelibackend.repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EquipmentUseService {

    private final EquipmentUseRepository equipmentUseRepo;
    private final UserRepository userRepo;
    private final EquipmentRepository equipmentRepo;
    private final ApplicantRepository applicantRepo;
    private final FunctionRepository functionRepo;
    private final EquipmentUseMapper mapper;

    @Transactional
    public Optional<EquipmentUseResponseDTO> startEquipmentUse(EquipmentUseDTO equipmentUseDTO) {
        validateEquipmentUseCreationData(equipmentUseDTO);

        EquipmentUseEntity entity = buildEquipmentUseEntity(equipmentUseDTO);

        EquipmentUseEntity savedEntity = equipmentUseRepo.save(entity);

        return Optional.of(mapper.toResponseDTO(savedEntity));
    }

    private void validateEquipmentUseCreationData(EquipmentUseDTO equipmentUseDTO) {
        if (equipmentUseDTO.getUserId() == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }
        if (equipmentUseDTO.getEquipmentId() == null) {
            throw new IllegalArgumentException("Equipment ID cannot be null");
        }
        if (equipmentUseDTO.getApplicantId() == null) {
            throw new IllegalArgumentException("Applicant ID cannot be null");
        }
    }

    private EquipmentUseEntity buildEquipmentUseEntity(EquipmentUseDTO equipmentUseDTO) {
        EquipmentUseEntity entity = mapper.toEntity(equipmentUseDTO);

        entity.setUser(findUserById(equipmentUseDTO.getUserId()));
        EquipmentEntity equipmentEntity = findEquipmentById(equipmentUseDTO.getEquipmentId());
        entity.setEquipment(equipmentEntity);
        entity.setApplicant(findApplicantById(equipmentUseDTO.getApplicantId()));

        List<FunctionEntity> functionEntityList = validateEquipmentUsedFunctions(equipmentUseDTO, equipmentEntity);
        assignFunctionsToEntity(entity, functionEntityList);

        entity.setUseDate(LocalDate.now());
        entity.setStartTime(LocalTime.now());

        return entity;
    }

    private UserEntity findUserById(Long userId) {
        return userRepo.findById(userId).orElseThrow(() -> new IllegalArgumentException("User with ID " + userId + " not found"));
    }

    private EquipmentEntity findEquipmentById(Long equipmentId) {
        return equipmentRepo.findById(equipmentId).orElseThrow(() -> new IllegalArgumentException("Equipment with ID " + equipmentId + " not found"));
    }

    private ApplicantEntity findApplicantById(Long applicantId) {
        return applicantRepo.findById(applicantId).orElseThrow(() -> new IllegalArgumentException("Applicant with ID " + applicantId + " not found"));
    }

    private List<FunctionEntity> findFunctionById(List<Long> usedFunctions) {
        return functionRepo.findAllById(usedFunctions);
    }

    private List<FunctionEntity> validateEquipmentUsedFunctions(EquipmentUseDTO equipmentUseDTO, EquipmentEntity entity) {
        List<FunctionEntity> equipmentFunctions = entity.getEquipmentFunctions().stream()
                .map(EquipmentFunctionsEntity::getFunction)
                .toList();
        List<Long> requestedFunctionsId = equipmentUseDTO.getUsedFunctions();
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
                .map(function -> new EquipmentFunctionsUsedEntity(new EquipmentFunctionsUsedId(), entity, function))
                .toList();
        entity.setEquipmentFunctionsUsedList(equipmentFunctionsUsedList);
    }
}