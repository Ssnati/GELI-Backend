package com.edu.uptc.gelibackend.repositories;

import com.edu.uptc.gelibackend.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface UserRepository extends JpaRepository<UserEntity, Long>, JpaSpecificationExecutor<UserEntity> {

    UserEntity findByIdentification(String identification);

    UserEntity findByEmail(String email);
}
