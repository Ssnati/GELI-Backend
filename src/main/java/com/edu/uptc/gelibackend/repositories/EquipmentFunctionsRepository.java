package com.edu.uptc.gelibackend.repositories;

import com.edu.uptc.gelibackend.entities.EquipmentFunctionsEntity;
import com.edu.uptc.gelibackend.entities.ids.EquipmentFunctionsId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EquipmentFunctionsRepository extends JpaRepository<EquipmentFunctionsEntity, EquipmentFunctionsId> {

}
