package com.edu.uptc.gelibackend.repositories;

import com.edu.uptc.gelibackend.entities.Position;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PositionRepository extends JpaRepository<Position, Long> {

    boolean existsByNameIgnoreCase(String name);

    Optional<Position> findByNameIgnoreCase(String name);
}
