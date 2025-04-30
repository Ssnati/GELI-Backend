package com.edu.uptc.gelibackend.specifications;

import com.edu.uptc.gelibackend.entities.EquipmentEntity;
import com.edu.uptc.gelibackend.filters.EquipmentFilterDTO;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class EquipmentSpecification extends BaseSpecification<EquipmentEntity, EquipmentFilterDTO> {
    @Override
    protected Specification<EquipmentEntity> addFilters(Specification<EquipmentEntity> spec, EquipmentFilterDTO filter) {

        if (filter.getEquipmentName() != null && !filter.getEquipmentName().isEmpty()) {
            spec = spec.and((root, query, cb) ->
                    cb.like(cb.lower(root.get("equipmentName")), "%" + filter.getEquipmentName().toLowerCase() + "%"));
        }

        if (filter.getBrand() != null && !filter.getBrand().isEmpty()) {
            spec = spec.and((root, query, cb) ->
                    cb.like(cb.lower(root.get("brand")), "%" + filter.getBrand().toLowerCase() + "%"));
        }

        if (filter.getInventoryNumber() != null && !filter.getInventoryNumber().isEmpty()) {
            spec = spec.and((root, query, cb) ->
                    cb.like(cb.lower(root.get("inventoryNumber")), "%" + filter.getInventoryNumber().toLowerCase() + "%"));
        }

        if (filter.getLaboratoryId() != null) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.get("laboratory").get("id"), filter.getLaboratoryId()));
        }

        if (filter.getAvailability() != null) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.get("availability"), filter.getAvailability()));
        }

        if (filter.getEquipmentObservations() != null && !filter.getEquipmentObservations().isEmpty()) {
            spec = spec.and((root, query, cb) ->
                    cb.like(cb.lower(root.get("equipmentObservations")), "%" + filter.getEquipmentObservations().toLowerCase() + "%"));
        }

        return spec;
    }
}
