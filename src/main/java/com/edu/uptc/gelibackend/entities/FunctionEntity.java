package com.edu.uptc.gelibackend.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "`function`")
public class FunctionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_function")
    private Long id;

    @NotNull
    @Column(name = "function_name", nullable = false)
    private String functionName;

    @OneToMany(mappedBy = "function", cascade = CascadeType.ALL)
    @ToString.Exclude
    private List<EquipmentFunctionsEntity> equipmentFunctions;

    @OneToMany(mappedBy = "function", cascade = CascadeType.ALL)
    @ToString.Exclude
    private List<EquipmentFunctionsUsedEntity> equipmentFunctionsUsedList;
}
