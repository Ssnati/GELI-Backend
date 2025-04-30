package com.edu.uptc.gelibackend.repositories;

import com.edu.uptc.gelibackend.entities.LaboratoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface LaboratoryRepository extends JpaRepository<LaboratoryEntity, Long>, JpaSpecificationExecutor<LaboratoryEntity> {
    Boolean existsByLaboratoryNameIgnoreCase(String laboratoryName);
}
