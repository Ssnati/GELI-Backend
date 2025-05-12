package com.edu.uptc.gelibackend.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "user_status_history")
public class UserStatusHistoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_status_history_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "ush_user_id", nullable = false)
    private UserEntity user;

    @NotNull
    @Column(name = "ush_status_to_date", nullable = false)
    private Boolean statusToDate;

    @NotNull
    @Column(name = "ush_modified_at", nullable = false)
    private LocalDate modificationStatusDate;
}
