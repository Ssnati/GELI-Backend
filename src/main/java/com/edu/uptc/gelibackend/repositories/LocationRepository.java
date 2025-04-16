package com.edu.uptc.gelibackend.repositories;

import com.edu.uptc.gelibackend.entities.LocationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LocationRepository extends JpaRepository<LocationEntity, Long> {

}
