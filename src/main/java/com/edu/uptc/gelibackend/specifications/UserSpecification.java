package com.edu.uptc.gelibackend.specifications;

import com.edu.uptc.gelibackend.entities.UserEntity;
import com.edu.uptc.gelibackend.entities.UserStatusHistoryEntity;
import com.edu.uptc.gelibackend.filters.UserFilterDTO;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import jakarta.persistence.criteria.Predicate;   // ← IMPORT NECESARIO
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class UserSpecification extends BaseSpecification<UserEntity, UserFilterDTO> {

    @Override
    protected Specification<UserEntity> addFilters(Specification<UserEntity> spec, UserFilterDTO filter) {
        if (filter.getFirstName() != null && !filter.getFirstName().isEmpty()) {
            String raw = filter.getFirstName().trim().toLowerCase();
            String[] parts = raw.split("\\s+");

            spec = spec.and((root, query, cb) -> {
                if (parts.length >= 2) {
                    // partes separadas
                    return cb.and(
                            cb.like(cb.lower(root.get("firstName")), "%" + parts[0] + "%"),
                            cb.like(cb.lower(root.get("lastName")), "%" + parts[1] + "%")
                    );
                } else {
                    // sólo una palabra: OR sobre todos los campos
                    String term = "%" + parts[0] + "%";
                    return cb.or(
                            cb.like(cb.lower(root.get("firstName")), term),
                            cb.like(cb.lower(root.get("lastName")), term),
                            cb.like(cb.lower(root.get("identification")), term),
                            cb.like(cb.lower(root.get("email")), term)
                    );
                }
            });
        }

        // Estado (propiedad 'state')
        if (filter.getEnabledStatus() != null) {
            spec = spec.and((root, query, cb)
                    -> cb.equal(root.get("state"), filter.getEnabledStatus()));
        }

        // Rol (propiedad 'role')
        if (filter.getRole() != null && !filter.getRole().isEmpty()) {
            spec = spec.and((root, query, cb)
                    -> cb.like(cb.lower(root.get("role")), "%" + filter.getRole().toLowerCase() + "%"));
        }

        // Fecha de creación ('createDateUser')
        if (filter.getCreationDateFrom() != null) {
            spec = spec.and((root, query, cb)
                    -> cb.greaterThanOrEqualTo(root.get("createDateUser"), filter.getCreationDateFrom()));
        }
        if (filter.getCreationDateTo() != null) {
            spec = spec.and((root, query, cb)
                    -> cb.lessThanOrEqualTo(root.get("createDateUser"), filter.getCreationDateTo()));
        }

        // Fecha de última modificación mediante subquery a status_history
        if (filter.getModificationStatusDateFrom() != null || filter.getModificationStatusDateTo() != null) {
            spec = spec.and((root, query, cb) -> {
                Subquery<LocalDate> sq = query.subquery(LocalDate.class);
                Root<UserStatusHistoryEntity> hist = sq.from(UserStatusHistoryEntity.class);

                // Explicitly cast the path to LocalDate
                sq.select(cb.greatest(hist.<LocalDate>get("modificationStatusDate")));
                sq.where(cb.equal(hist.get("user"), root));

                Predicate datePred = cb.conjunction();  // ← usa Predicate de jakarta.persistence.criteria
                if (filter.getModificationStatusDateFrom() != null) {
                    datePred = cb.and(datePred,
                            cb.greaterThanOrEqualTo(sq, filter.getModificationStatusDateFrom()));
                }
                if (filter.getModificationStatusDateTo() != null) {
                    datePred = cb.and(datePred,
                            cb.lessThanOrEqualTo(sq, filter.getModificationStatusDateTo()));
                }
                return datePred;
            });
        }

        if (filter.getPositionId() != null) {
            spec = spec.and((root, query, cb)
                    -> cb.equal(root.get("position").get("id"), filter.getPositionId()));
        }
        return spec;
    }
}
