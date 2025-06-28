package com.edu.uptc.gelibackend.repositories;

import com.edu.uptc.gelibackend.entities.EquipmentUseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EquipmentUseRepository extends JpaRepository<EquipmentUseEntity, Long>, JpaSpecificationExecutor<EquipmentUseEntity> {
    List<EquipmentUseEntity> findByEquipmentIdAndIsInUseTrue(Long equipmentId);
}
