package com.edu.uptc.gelibackend.services;

import com.edu.uptc.gelibackend.dtos.PositionDTO;
import com.edu.uptc.gelibackend.entities.PositionEntity;
import com.edu.uptc.gelibackend.mappers.PositionMapper;
import com.edu.uptc.gelibackend.repositories.PositionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PositionService {

    private final PositionRepository positionRepo;
    private final PositionMapper mapper;

    @Transactional(readOnly = true)
    public List<PositionDTO> getAll() {
        return positionRepo
                .findAll()
                .stream()
                .map(mapper::toDto)
                .toList();
    }

    @Transactional
    public PositionDTO create(PositionDTO dto) {
        // avoid duplicates
        if (positionRepo.existsByNameIgnoreCase(dto.getName())) {
            throw new RuntimeException("Position already exists: " + dto.getName());
        }
        PositionEntity entity = mapper.toEntity(dto);
        PositionEntity saved = positionRepo.save(entity);
        return mapper.toDto(saved);
    }
}
