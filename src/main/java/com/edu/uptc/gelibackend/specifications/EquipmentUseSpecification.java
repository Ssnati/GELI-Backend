package com.edu.uptc.gelibackend.specifications;

import com.edu.uptc.gelibackend.entities.EquipmentUseEntity;
import com.edu.uptc.gelibackend.filters.BaseFilterDTO;
import com.edu.uptc.gelibackend.filters.EquipmentUseFilterDTO;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class EquipmentUseSpecification extends BaseSpecification<EquipmentUseEntity, BaseFilterDTO> {
    @Override
    protected Specification<EquipmentUseEntity> addFilters(Specification<EquipmentUseEntity> spec, BaseFilterDTO filter) {
        if (!(filter instanceof EquipmentUseFilterDTO equipmentUseFilter)) {
            return null;
        }
        if (equipmentUseFilter.getIsInUse() != null) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.get("isInUse"), equipmentUseFilter.getIsInUse()));
        }
        if (equipmentUseFilter.getIsAvailable() != null) {
            spec = spec.and((root, query, cb) ->
                    cb.and(
                            cb.isFalse(root.get("isInUse")),
                            cb.equal(root.get("isAvailable"), equipmentUseFilter.getIsAvailable())
                    )
            );
        }

        if (equipmentUseFilter.getIsVerified() != null) {
            spec = spec.and((root, query, cb) ->
                    cb.and(
                            cb.isFalse(root.get("isInUse")),
                            cb.equal(root.get("isVerified"), equipmentUseFilter.getIsVerified())
                    )
            );
        }

        if (equipmentUseFilter.getSamplesNumberFrom() > 0) {
            spec = spec.and((root, query, cb) ->
                    cb.and(
                            cb.isFalse(root.get("isInUse")),
                            cb.greaterThanOrEqualTo(root.get("samplesNumber"), equipmentUseFilter.getSamplesNumberFrom())
                    )
            );
        }

        if (equipmentUseFilter.getSamplesNumberTo() > 0) {
            spec = spec.and((root, query, cb) ->
                    cb.and(
                            cb.isFalse(root.get("isInUse")),
                            cb.lessThanOrEqualTo(root.get("samplesNumber"), equipmentUseFilter.getSamplesNumberTo())
                    )
            );
        }
        // Filtro por fecha (sin hora)
        if (equipmentUseFilter.getUseDateFrom() != null) {
            String dateFrom = equipmentUseFilter.getUseDateFrom().toString(); // "2025-06-25"
            spec = spec.and((root, query, cb) ->
                    cb.greaterThanOrEqualTo(
                            cb.function("TO_CHAR", String.class, root.get("startUseTime"), cb.literal("YYYY-MM-DD")),
                            dateFrom
                    )
            );
        }

        if (equipmentUseFilter.getUseDateTo() != null) {
            String dateTo = equipmentUseFilter.getUseDateTo().toString(); // "2025-06-27"
            spec = spec.and((root, query, cb) ->
                    cb.lessThanOrEqualTo(
                            cb.function("TO_CHAR", String.class, root.get("startUseTime"), cb.literal("YYYY-MM-DD")),
                            dateTo
                    )
            );
        }


// Filtro por hora (sin fecha)
        if (equipmentUseFilter.getStartTimeFrom() != null) {
            String timeFrom = equipmentUseFilter.getStartTimeFrom().toString(); // "08:00"
            spec = spec.and((root, query, cb) ->
                    cb.greaterThanOrEqualTo(
                            cb.function("TO_CHAR", String.class, root.get("startUseTime"), cb.literal("HH24:MI")),
                            timeFrom
                    )
            );
        }

        if (equipmentUseFilter.getEndTimeTo() != null) {
            String timeTo = equipmentUseFilter.getEndTimeTo().toString(); // "18:00"
            spec = spec.and((root, query, cb) ->
                    cb.lessThanOrEqualTo(
                            cb.function("TO_CHAR", String.class, root.get("startUseTime"), cb.literal("HH24:MI")),
                            timeTo
                    )
            );
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

        if (equipmentUseFilter.getEquipmentName() != null && !equipmentUseFilter.getEquipmentName().isBlank()) {
            spec = spec.and((root, query, cb) ->
                    cb.like(cb.lower(root.get("equipment").get("equipmentName")), "%" + equipmentUseFilter.getEquipmentName().toLowerCase() + "%")
            );
        }

        if (equipmentUseFilter.getEquipmentInventoryCode() != null && !equipmentUseFilter.getEquipmentInventoryCode().isBlank()) {
            spec = spec.and((root, query, cb) ->
                    cb.like(cb.lower(root.get("equipment").get("inventoryNumber")), "%" + equipmentUseFilter.getEquipmentInventoryCode().toLowerCase() + "%")
            );
        }


        return spec;
    }
}
