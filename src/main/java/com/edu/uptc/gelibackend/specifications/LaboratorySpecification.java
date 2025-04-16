package com.edu.uptc.gelibackend.specifications;

import com.edu.uptc.gelibackend.dtos.LaboratoryFilterDTO;
import com.edu.uptc.gelibackend.entities.LaboratoryEntity;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class LaboratorySpecification extends BaseSpecification<LaboratoryEntity, LaboratoryFilterDTO> {

    @Override
    protected Specification<LaboratoryEntity> addFilters(Specification<LaboratoryEntity> spec, LaboratoryFilterDTO filter) {

        if (filter.getLaboratoryName() != null && !filter.getLaboratoryName().isEmpty()) {
            spec = spec.and((root, query, cb) ->
                    cb.like(cb.lower(root.get("laboratoryName")), "%" + filter.getLaboratoryName().toLowerCase() + "%"));
        }

        if (filter.getLaboratoryAvailability() != null) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.get("laboratoryAvailability"), filter.getLaboratoryAvailability()));
        }

        if (filter.getLocationId() != null) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.get("laboratoryLocation").get("id"), filter.getLocationId()));
        }

        if (filter.getLaboratoryDescription() != null && !filter.getLaboratoryDescription().isEmpty()) {
            spec = spec.and((root, query, cb) ->
                    cb.like(cb.lower(root.get("laboratoryDescription")), "%" + filter.getLaboratoryDescription().toLowerCase() + "%"));
        }

        return spec;
    }
}
