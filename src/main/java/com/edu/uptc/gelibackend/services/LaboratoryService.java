package com.edu.uptc.gelibackend.services;

import com.edu.uptc.gelibackend.dtos.LaboratoryDTO;
import com.edu.uptc.gelibackend.dtos.PageResponse;
import com.edu.uptc.gelibackend.filters.LaboratoryFilterDTO;
import com.edu.uptc.gelibackend.entities.LaboratoryEntity;
import com.edu.uptc.gelibackend.entities.LocationEntity;
import com.edu.uptc.gelibackend.mappers.LaboratoryMapper;
import com.edu.uptc.gelibackend.repositories.LaboratoryRepository;
import com.edu.uptc.gelibackend.repositories.LocationRepository;
import com.edu.uptc.gelibackend.specifications.LaboratorySpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import jakarta.ws.rs.NotFoundException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LaboratoryService {

    private final LaboratoryRepository labRepo;
    private final LocationRepository locRepo;
    private final LaboratoryMapper mapper;
    private final LaboratorySpecification labSpecification;

    public List<LaboratoryDTO> findAll() {
        return labRepo.findAll().stream()
                .map(mapper::mapEntityToDTO)
                .collect(Collectors.toList());
    }

    public Optional<LaboratoryDTO> findById(Long id) {
        return labRepo.findById(id)
                .map(mapper::mapEntityToDTO);
    }

    public LaboratoryDTO create(LaboratoryDTO dto) {
        this.validateUniqueName(dto.getLaboratoryName());

        LocationEntity location = locRepo.findById(dto.getLocation().getId())
                .orElseThrow(() -> new NotFoundException("Location not found"));

        LaboratoryEntity entity = mapper.mapDTOToEntity(dto);
        entity.setLaboratoryLocation(location);

        return mapper.mapEntityToDTO(labRepo.save(entity));
    }

    private void validateUniqueName(String laboratoryName) {
        if (labRepo.findAll().stream()
                .anyMatch(lab -> lab.getLaboratoryName().equalsIgnoreCase(laboratoryName))) {
            throw new IllegalArgumentException("Laboratory's name must be unique");
        }
    }

    public LaboratoryDTO update(Long id, LaboratoryDTO dto) {
        LaboratoryEntity existing = labRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("Laboratory not found"));

        LocationEntity location = locRepo.findById(dto.getLocation().getId())
                .orElseThrow(() -> new NotFoundException("Location not found"));

        existing.setLaboratoryName(dto.getLaboratoryName());
        existing.setLaboratoryDescription(dto.getLaboratoryDescription());
        existing.setLaboratoryAvailability(dto.getLaboratoryAvailability());
        existing.setLaboratoryLocation(location);
        existing.setLaboratoryObservations(dto.getLaboratoryObservations());

        return mapper.mapEntityToDTO(labRepo.save(existing));
    }

    public boolean delete(Long id) {
        if (labRepo.existsById(id)) {
            labRepo.deleteById(id);
            return true;
        }
        return false;
    }

    public PageResponse<LaboratoryDTO> filterLaboratories(LaboratoryFilterDTO filters, int page, int size) {
        if (size <= 0) {
            throw new IllegalArgumentException("Page size must be greater than 0");
        }

        Specification<LaboratoryEntity> spec = labSpecification.build(filters);
        Pageable pageable = PageRequest.of(page, size);
        Page<LaboratoryEntity> pageResult = labRepo.findAll(spec, pageable);

        List<LaboratoryDTO> content = pageResult.getContent().stream()
                .map(mapper::mapEntityToDTO)
                .collect(Collectors.toList());

        return new PageResponse<>(
                pageResult.getNumber(),
                pageResult.getSize(),
                pageResult.getTotalElements(),
                pageResult.getTotalPages(),
                content
        );
    }

    public boolean existsByName(String laboratoryName) {
        return labRepo.existsByLaboratoryNameIgnoreCase(laboratoryName);
    }

    public boolean existsByNameExcludingId(String laboratoryName, Long excludeId) {
        return labRepo.existsByLaboratoryNameIgnoreCaseAndIdNot(laboratoryName, excludeId);
    }

}
