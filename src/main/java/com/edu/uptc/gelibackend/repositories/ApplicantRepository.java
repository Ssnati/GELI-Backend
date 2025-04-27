package com.edu.uptc.gelibackend.repositories;

import com.edu.uptc.gelibackend.entities.ApplicantEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ApplicantRepository extends JpaRepository<ApplicantEntity, Long> {

}
