package com.edu.uptc.gelibackend.services;

import com.edu.uptc.gelibackend.dtos.EquipmentUseDTO;
import com.edu.uptc.gelibackend.dtos.EquipmentUseResponseDTO;
import com.edu.uptc.gelibackend.entities.ApplicantEntity;
import com.edu.uptc.gelibackend.entities.EquipmentEntity;
import com.edu.uptc.gelibackend.entities.EquipmentUseEntity;
import com.edu.uptc.gelibackend.entities.UserEntity;
import com.edu.uptc.gelibackend.mappers.EquipmentUseMapper;
import com.edu.uptc.gelibackend.repositories.ApplicantRepository;
import com.edu.uptc.gelibackend.repositories.EquipmentRepository;
import com.edu.uptc.gelibackend.repositories.EquipmentUseRepository;
import com.edu.uptc.gelibackend.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EquipmentUseService {

    private final EquipmentUseRepository equipmentUseRepo;
    private final UserRepository userRepo;
    private final EquipmentRepository equipmentRepo;
    private final ApplicantRepository applicantRepo;
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
        entity.setEquipment(findEquipmentById(equipmentUseDTO.getEquipmentId()));
        entity.setApplicant(findApplicantById(equipmentUseDTO.getApplicantId()));
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
}