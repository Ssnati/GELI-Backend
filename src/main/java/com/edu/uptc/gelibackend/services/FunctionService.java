package com.edu.uptc.gelibackend.services;

import com.edu.uptc.gelibackend.dtos.BrandDTO;
import com.edu.uptc.gelibackend.dtos.FunctionDTO;
import com.edu.uptc.gelibackend.entities.BrandEntity;
import com.edu.uptc.gelibackend.entities.FunctionEntity;
import com.edu.uptc.gelibackend.mappers.FunctionMapper;
import com.edu.uptc.gelibackend.repositories.FunctionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FunctionService {

    private final FunctionRepository functionRepository;
    private final FunctionMapper mapper;

    public List<FunctionDTO> getAllFunctions() {
        return functionRepository.findAll().stream()
                .map(mapper::toDTO)
                .toList();
    }

    public Optional<FunctionDTO> getFunctionById(Long id) {
        return functionRepository.findById(id)
                .map(mapper::toDTO);
    }

    @Transactional
    public FunctionDTO createFunction(FunctionDTO function) {
        validateUniqueName(function.getFunctionName());

        FunctionEntity functionEntity = mapper.toEntity(function);
        functionEntity.setId(null); // Ensure the ID is null for a new entity
        FunctionEntity savedFunction = functionRepository.save(functionEntity);
        return mapper.toDTO(savedFunction);
    }

    private void validateUniqueName(String functionName) {
        if (functionRepository.existsByFunctionNameIgnoreCase(functionName)) {
            throw new IllegalArgumentException("Function name already exists: " + functionName);
        }
    }

    @Transactional
    public FunctionDTO update(Long id, FunctionDTO dto) {
        FunctionEntity existing = functionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Function not found with id: " + id));

        // Check uniqueness ignoring current entity
        if (functionRepository.existsByFunctionNameIgnoreCaseAndIdNot(dto.getFunctionName(), id)) {
            throw new RuntimeException("Another function with name '" + dto.getFunctionName() + "' already exists.");
        }

        existing.setFunctionName(dto.getFunctionName());
        FunctionEntity saved = functionRepository.save(existing);
        return mapper.toDTO(saved);
    }

    @Transactional(readOnly = true)
    public boolean existsByNameIgnoreCase(String functionName) {
        return functionRepository.existsByFunctionNameIgnoreCase(functionName);
    }
}
