package com.edu.uptc.gelibackend.repositories;

import com.edu.uptc.gelibackend.entities.UserPositionHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface UserPositionHistoryRepository
        extends JpaRepository<UserPositionHistoryEntity, Long> {

    List<UserPositionHistoryEntity> findByUserIdOrderByChangeDateDesc(Long userId);
}
