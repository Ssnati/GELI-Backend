package com.edu.uptc.gelibackend.services;

import com.edu.uptc.gelibackend.entities.LaboratoryEntity;
import com.edu.uptc.gelibackend.repositories.LaboratoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LaboratoryService {

    @Autowired
    private LaboratoryRepository laboratoryRepository;

    public List<LaboratoryEntity> findAll() {
        return laboratoryRepository.findAll();
    }
}
