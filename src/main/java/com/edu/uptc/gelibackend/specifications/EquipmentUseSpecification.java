package com.edu.uptc.gelibackend.specifications;

import com.edu.uptc.gelibackend.entities.EquipmentUseEntity;
import com.edu.uptc.gelibackend.filters.BaseFilterDTO;
import com.edu.uptc.gelibackend.filters.EquipmentUseFilterDTO;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class EquipmentUseSpecification extends BaseSpecification<EquipmentUseEntity, BaseFilterDTO> {
    @Override
    protected Specification<EquipmentUseEntity> addFilters(Specification<EquipmentUseEntity> spec, BaseFilterDTO filter) {
        if (!(filter instanceof EquipmentUseFilterDTO equipmentUseFilter)) {
            return null;
        }
        if (equipmentUseFilter.getIsInUse() != null && equipmentUseFilter.getIsInUse()) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.get("isInUse"), equipmentUseFilter.getIsInUse()));
        }
        if (equipmentUseFilter.getIsVerified() != null && equipmentUseFilter.getIsVerified()) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.get("isVerified"), equipmentUseFilter.getIsVerified()));
        }
        if (equipmentUseFilter.getIsAvailable() != null && equipmentUseFilter.getIsAvailable()) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.get("isAvailable"), equipmentUseFilter.getIsAvailable()));
        }
        if (equipmentUseFilter.getSamplesNumberFrom() > 0) {
            spec = spec.and((root, query, cb) ->
                    cb.greaterThanOrEqualTo(root.get("samplesNumber"), equipmentUseFilter.getSamplesNumberFrom()));
        }
        if (equipmentUseFilter.getSamplesNumberTo() > 0) {
            spec = spec.and((root, query, cb) ->
                    cb.lessThanOrEqualTo(root.get("samplesNumber"), equipmentUseFilter.getSamplesNumberTo()));
        }
        if (equipmentUseFilter.getUseDateFrom() != null) {
            spec = spec.and((root, query, cb) ->
                    cb.greaterThanOrEqualTo(root.get("useDate"), equipmentUseFilter.getUseDateFrom()));
        }
        if (equipmentUseFilter.getUseDateTo() != null) {
            spec = spec.and((root, query, cb) ->
                    cb.lessThanOrEqualTo(root.get("useDate"), equipmentUseFilter.getUseDateTo()));
        }
        if (equipmentUseFilter.getStartUseTimeFrom() != null) {
            spec = spec.and((root, query, cb) ->
                    cb.greaterThanOrEqualTo(root.get("startUseTime"), equipmentUseFilter.getStartUseTimeFrom()));
        }
        if (equipmentUseFilter.getEndUseTimeTo() != null) {
            spec = spec.and((root, query, cb) ->
                    cb.lessThanOrEqualTo(root.get("endUseTime"), equipmentUseFilter.getEndUseTimeTo()));
        }
        if (equipmentUseFilter.getEquipmentId() != null) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.get("equipment").get("id"), equipmentUseFilter.getEquipmentId()));
        }
        if (equipmentUseFilter.getUserId() != null) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.get("user").get("id"), equipmentUseFilter.getUserId()));
        }
        if (equipmentUseFilter.getLaboratoryId() != null) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.join("equipment").get("laboratory").get("id"), equipmentUseFilter.getLaboratoryId()));
        }
        if (equipmentUseFilter.getUsedFunctionsIds() != null && !equipmentUseFilter.getUsedFunctionsIds().isEmpty()) {
            spec = spec.and((root, query, cb) -> {
                var usedFunctions = root.join("equipmentFunctionsUsedList").join("function");
                return usedFunctions.get("id").in(equipmentUseFilter.getUsedFunctionsIds());
            });
        }

        return spec;
    }
}