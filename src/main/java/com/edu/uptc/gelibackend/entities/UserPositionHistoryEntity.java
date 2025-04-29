package com.edu.uptc.gelibackend.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "user_position_history")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserPositionHistoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * El usuario al que pertenece este cambio
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    /**
     * Cargo anterior
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "old_position_id")
    private Position oldPosition;

    /**
     * Cargo nuevo
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "new_position_id", nullable = false)
    private Position newPosition;

    /**
     * Fecha del cambio
     */
    @Column(name = "change_date", nullable = false)
    private LocalDate changeDate;
}
