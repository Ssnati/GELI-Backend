package com.edu.uptc.gelibackend.specifications;

import com.edu.uptc.gelibackend.entities.UserEntity;
import com.edu.uptc.gelibackend.filters.UserFilterDTO;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class UserSpecification extends BaseSpecification<UserEntity, UserFilterDTO> {
    @Override
    protected Specification<UserEntity> addFilters(Specification<UserEntity> spec, UserFilterDTO filter) {

        if (filter.getIdentification() != null && !filter.getIdentification().isEmpty()) {
            spec = spec.and((root, query, cb) ->
                    cb.like(cb.lower(root.get("identification")), "%" + filter.getIdentification().toLowerCase() + "%"));
        }

        if (filter.getModificationStatusDateFrom() != null) {
            spec = spec.and((root, query, cb) ->
                    cb.greaterThanOrEqualTo(root.get("modificationStatusDate"), filter.getModificationStatusDateFrom()));
        }
        if (filter.getModificationStatusDateTo() != null) {
            spec = spec.and((root, query, cb) ->
                    cb.lessThanOrEqualTo(root.get("modificationStatusDate"), filter.getModificationStatusDateTo()));
        }
        if (filter.getCreationDateFrom() != null) {
            spec = spec.and((root, query, cb) ->
                    cb.greaterThanOrEqualTo(root.get("creationDate"), filter.getCreationDateFrom()));
        }
        if (filter.getCreationDateTo() != null) {
            spec = spec.and((root, query, cb) ->
                    cb.lessThanOrEqualTo(root.get("creationDate"), filter.getCreationDateTo()));
        }

        return spec;
    }
}
