package com.edu.uptc.gelibackend.repositories;

import com.edu.uptc.gelibackend.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

    UserEntity findByIdentification(String identification);
}
