package com.edu.uptc.gelibackend.services;

import com.edu.uptc.gelibackend.dtos.BrandDTO;
import com.edu.uptc.gelibackend.entities.BrandEntity;
import com.edu.uptc.gelibackend.mappers.BrandMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
}
