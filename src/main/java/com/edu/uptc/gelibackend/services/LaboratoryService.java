package com.edu.uptc.gelibackend.services;

import com.edu.uptc.gelibackend.dtos.LaboratoryDTO;
import com.edu.uptc.gelibackend.mappers.LaboratoryMapper;
import com.edu.uptc.gelibackend.repositories.LaboratoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LaboratoryService {

    @Autowired
    private LaboratoryRepository laboratoryRepository;

    @Autowired
    private LaboratoryMapper laboratoryMapper;

    public List<LaboratoryDTO> findAll() {
        return laboratoryRepository.findAll().stream()
                .map(laboratoryMapper::mapLaboratoryToLaboratoryDTO)
                .collect(Collectors.toList());
    }
}
