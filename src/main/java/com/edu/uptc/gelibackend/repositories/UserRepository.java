package com.edu.uptc.gelibackend.repositories;

import com.edu.uptc.gelibackend.entities.UserEntity;

import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface UserRepository extends JpaRepository<UserEntity, Long>, JpaSpecificationExecutor<UserEntity> {

    UserEntity findByIdentification(String identification);

    UserEntity findByEmail(String email);

    @EntityGraph(attributePaths = {"position"})
    Optional<UserEntity> findById(Long id);
}
