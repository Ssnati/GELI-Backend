package com.edu.uptc.gelibackend.mappers;

import com.edu.uptc.gelibackend.dtos.BrandDTO;
import com.edu.uptc.gelibackend.entities.BrandEntity;
import org.springframework.stereotype.Component;

@Component
public class BrandMapper {

    public BrandDTO toDTO(BrandEntity brandEntity) {
        BrandDTO brandDTO = new BrandDTO();
        brandDTO.setId(brandEntity.getId());
        brandDTO.setBrandName(brandEntity.getBrandName());
        return brandDTO;
    }

    public BrandEntity toEntity(BrandDTO brandDTO) {
        BrandEntity brandEntity = new BrandEntity();
        brandEntity.setId(brandDTO.getId());
        brandEntity.setBrandName(brandDTO.getBrandName());
        return brandEntity;
    }
}
