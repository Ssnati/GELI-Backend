package com.edu.uptc.gelibackend.repositories;

import com.edu.uptc.gelibackend.entities.UserEntity;
import io.micrometer.common.lang.NonNull;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long>, JpaSpecificationExecutor<UserEntity> {

    UserEntity findByIdentification(String identification);

    UserEntity findByEmail(String email);

    @EntityGraph(attributePaths = {"position"})
    @NonNull
    Optional<UserEntity> findById(@NonNull Long id);
}
