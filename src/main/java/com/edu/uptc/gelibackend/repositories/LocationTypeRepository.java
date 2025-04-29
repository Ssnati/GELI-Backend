package com.edu.uptc.gelibackend.repositories;

import com.edu.uptc.gelibackend.entities.LocationTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LocationTypeRepository extends JpaRepository<LocationTypeEntity, Long> {

}
