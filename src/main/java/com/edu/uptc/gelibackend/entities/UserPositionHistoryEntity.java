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
    @Column(name = "user_position_history_id")
    private Long id;

    /**
     * El usuario al que pertenece este cambio
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "uph_user_id", nullable = false)
    private UserEntity user;

    /**
     * Cargo anterior
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uph_position_to_date")
    private PositionEntity oldPosition;

    /**
     * Cargo nuevo
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "uph_new_position", nullable = false)
    private PositionEntity newPosition;

    /**
     * Fecha del cambio
     */
    @Column(name = "uph_modified_at", nullable = false)
    private LocalDate changeDate;
}
