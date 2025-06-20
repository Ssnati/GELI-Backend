package com.edu.uptc.gelibackend.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "positions")
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PositionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "position_id")
    private Long id;

    @NotBlank
    @Column(name = "position_name", nullable = false, unique = true, length = 100)
    private String name;

    /**
     * Inverse side: one position → many users
     */
    @OneToMany(mappedBy = "position", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private List<UserEntity> users = new ArrayList<>();
}
