package com.edu.uptc.gelibackend.repositories;

import com.edu.uptc.gelibackend.entities.UserStatusHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserStatusHistoryRepository extends JpaRepository<UserStatusHistoryEntity, Long> {

}