package com.edu.uptc.gelibackend.repositories;

import com.edu.uptc.gelibackend.entities.BrandEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BrandRepository extends JpaRepository<BrandEntity, Long> {

    Optional<BrandEntity> findByBrandNameIgnoreCase(String name);

    boolean existsByBrandNameIgnoreCase(String brandName);
    boolean existsByBrandNameIgnoreCaseAndIdNot(String brandName, Long id);
}
