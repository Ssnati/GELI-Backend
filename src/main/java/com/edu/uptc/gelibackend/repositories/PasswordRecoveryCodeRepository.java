package com.edu.uptc.gelibackend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.edu.uptc.gelibackend.entities.PasswordRecoveryCode;

import java.util.Optional;

@Repository
public interface PasswordRecoveryCodeRepository extends JpaRepository<PasswordRecoveryCode, Long> {

    Optional<PasswordRecoveryCode> findByUsername(String username);

    @Modifying
    @Query("UPDATE PasswordRecoveryCode p SET p.tempToken = :tempToken WHERE p.username = :username")
    void updateTempTokenByUsername(@Param("username") String username, @Param("tempToken") String tempToken);

    Optional<PasswordRecoveryCode> findByTempToken(String tempToken);

    void deleteByUsername(String username);
}
