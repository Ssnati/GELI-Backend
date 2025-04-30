package com.edu.uptc.gelibackend.repositories;

import com.edu.uptc.gelibackend.entities.PositionEntity;

import java.util.Optional;

import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PositionRepository extends JpaRepository<PositionEntity, Long> {

    boolean existsByNameIgnoreCase(String name);

    Optional<PositionEntity> findByNameIgnoreCase(String name);
}
