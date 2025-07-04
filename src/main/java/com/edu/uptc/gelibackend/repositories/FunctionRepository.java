package com.edu.uptc.gelibackend.repositories;

import com.edu.uptc.gelibackend.entities.FunctionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FunctionRepository extends JpaRepository<FunctionEntity, Long> {

    boolean existsByFunctionNameIgnoreCase(String functionName);

    boolean existsByFunctionNameIgnoreCaseAndIdNot(String functionName, Long id);

    Optional<FunctionEntity> findByFunctionName(String functionName);

}
