package com.edu.uptc.gelibackend.specifications;

import com.edu.uptc.gelibackend.filters.BaseFilterDTO;
import org.springframework.data.jpa.domain.Specification;

public abstract class BaseSpecification<T, F extends BaseFilterDTO> {

    public Specification<T> build(F filter) {
        Specification<T> spec = Specification.where(null);
        spec = addFilters(spec, filter);
        return spec;
    }

    protected abstract Specification<T> addFilters(Specification<T> spec, F filter);
}
