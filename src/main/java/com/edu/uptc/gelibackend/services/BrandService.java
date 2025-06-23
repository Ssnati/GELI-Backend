package com.edu.uptc.gelibackend.services;

import com.edu.uptc.gelibackend.dtos.BrandDTO;
import com.edu.uptc.gelibackend.dtos.LocationDTO;
import com.edu.uptc.gelibackend.entities.BrandEntity;
import com.edu.uptc.gelibackend.entities.LocationEntity;
import com.edu.uptc.gelibackend.mappers.BrandMapper;
import com.edu.uptc.gelibackend.repositories.BrandRepository;
import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BrandService {

    private final BrandRepository brandRepository;
    private final BrandMapper brandMapper;

    public List<BrandDTO> getAllBrands() {
        return brandRepository.findAll()
                .stream()
                .map(brandMapper::toDTO)
                .toList();
    }

    public Optional<BrandDTO> getBrandById(Long id) {
        return brandRepository.findById(id)
                .map(brandMapper::toDTO);
    }

    public BrandDTO createBrand(BrandDTO brandDTO) {
        this.validateUniqueName(brandDTO.getBrandName());

        BrandEntity entity = brandMapper.toEntity(brandDTO);
        entity.setId(null); // Ensure the ID is null for a new entity
        BrandEntity save = brandRepository.save(entity);
        return brandMapper.toDTO(save);
    }

    private void validateUniqueName(String brandName) {
        brandRepository.findByBrandNameIgnoreCase(brandName)
                .ifPresent(brand -> {
                    throw new IllegalArgumentException("Brand name already exists");
                });
    }

    @Transactional
    public BrandDTO update(Long id, BrandDTO dto) {
        BrandEntity existing = brandRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Brand not found with id: " + id));

        // Check uniqueness ignoring current entity
        if (brandRepository.existsByBrandNameIgnoreCaseAndIdNot(dto.getBrandName(), id)) {
            throw new RuntimeException("Another position with name '" + dto.getBrandName() + "' already exists.");
        }

        existing.setBrandName(dto.getBrandName());
        BrandEntity saved = brandRepository.save(existing);
        return brandMapper.toDTO(saved);
    }

    @Transactional(readOnly = true)
    public boolean existsByNameIgnoreCase(String brandName) {
        return brandRepository.existsByBrandNameIgnoreCase(brandName);
    }

    public BrandEntity getById(Long id) {
        return brandRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Marca no encontrada con ID: " + id));
    }
}
