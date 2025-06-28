package com.edu.uptc.gelibackend.repositories;

import com.edu.uptc.gelibackend.entities.EquipmentEntity;
import com.edu.uptc.gelibackend.entities.LaboratoryEntity;
import com.edu.uptc.gelibackend.entities.AuthorizedUserEquipmentsEntity;
import com.edu.uptc.gelibackend.entities.ids.AuthorizedUserEquipmentsId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AuthorizedUserEquipmentsRepository extends JpaRepository<AuthorizedUserEquipmentsEntity, AuthorizedUserEquipmentsId> {

    @Query("""
        SELECT DISTINCT e.laboratory
        FROM AuthorizedUserEquipmentsEntity aue
        JOIN aue.equipment e
        JOIN e.laboratory l
        WHERE aue.user.id = :userId
    """)
    List<LaboratoryEntity> findAuthorizedLaboratoriesByUserId(@Param("userId") Long userId);

    @Query("""
    SELECT aue.equipment
    FROM AuthorizedUserEquipmentsEntity aue
    WHERE aue.user.id = :userId
      AND aue.equipment.laboratory.id = :laboratoryId
""")
    List<EquipmentEntity> findAuthorizedEquipmentsByUserIdAndLaboratoryId(
            @Param("userId") Long userId,
            @Param("laboratoryId") Long laboratoryId
    );

}
