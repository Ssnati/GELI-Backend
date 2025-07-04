package com.edu.uptc.gelibackend.repositories;

import com.edu.uptc.gelibackend.entities.EquipmentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface EquipmentRepository extends JpaRepository<EquipmentEntity, Long>, JpaSpecificationExecutor<EquipmentEntity> {
    Boolean existsByInventoryNumberIgnoreCase(String inventoryNumber);

    Boolean existsByInventoryNumberIgnoreCaseAndIdNot(String inventoryNumber, Long id);


    Boolean existsByEquipmentNameIgnoreCase(String equipmentName);


}
