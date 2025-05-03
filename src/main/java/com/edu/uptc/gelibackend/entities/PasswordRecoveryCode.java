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
    private Long id;

    private String username;
    private String code;

    @Column(name = "temp_token")
    private String tempToken;

    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;
}