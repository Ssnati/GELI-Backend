package com.edu.uptc.gelibackend.specifications;

import com.edu.uptc.gelibackend.entities.EquipmentEntity;
import com.edu.uptc.gelibackend.filters.EquipmentFilterDTO;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;

@Component
public class EquipmentSpecification extends BaseSpecification<EquipmentEntity, EquipmentFilterDTO> {

    @Override
    protected Specification<EquipmentEntity> addFilters(Specification<EquipmentEntity> spec, EquipmentFilterDTO filter) {

        // Buscar por nombre o código de inventario (OR)
        if (filter.getEquipmentName() != null && !filter.getEquipmentName().isEmpty()) {
            spec = spec.and((root, query, cb) -> cb.or(
                    cb.like(cb.lower(root.get("equipmentName")), "%" + filter.getEquipmentName().toLowerCase() + "%"),
                    cb.like(cb.lower(root.get("inventoryNumber")), "%" + filter.getEquipmentName().toLowerCase() + "%")
            ));
        }

        // Marca por ID
        if (filter.getBrandId() != null) {
            spec = spec.and((root, query, cb)
                    -> cb.equal(root.get("brand").get("id"), filter.getBrandId()));
        }

        // Laboratorio por ID
        if (filter.getLaboratoryId() != null) {
            spec = spec.and((root, query, cb)
                    -> cb.equal(root.get("laboratory").get("id"), filter.getLaboratoryId()));
        }

        // Disponibilidad
        if (filter.getAvailability() != null) {
            spec = spec.and((root, query, cb)
                    -> cb.equal(root.get("availability"), filter.getAvailability()));
        }

        // Función (verifica si alguna de las funciones del equipo coincide)
        if (filter.getFunctionId() != null) {
            spec = spec.and((root, query, cb) -> {
                Join<?, ?> join = root.join("equipmentFunctions", JoinType.INNER);
                return cb.equal(join.get("function").get("id"), filter.getFunctionId());
            });
        }

        return spec;
    }
}
