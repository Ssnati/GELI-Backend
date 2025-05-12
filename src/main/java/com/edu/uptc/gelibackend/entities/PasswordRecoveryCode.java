package com.edu.uptc.gelibackend.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "password_recovery_codes")
public class PasswordRecoveryCode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "password_recovery_code_id")
    private Long id;

    @Column(name = "prc_username", nullable = false, length = 100)
    private String username;

    @Column(name = "prc_email", nullable = false, length = 100)
    private String code;

    @Column(name = "prc_temp_token")
    private String tempToken;

    @Column(name = "prc_created_at")
    private LocalDateTime createdAt;

    @Column(name = "prc_expires_at")
    private LocalDateTime expiresAt;
}