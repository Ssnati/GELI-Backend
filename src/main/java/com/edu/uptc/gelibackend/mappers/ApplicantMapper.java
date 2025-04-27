package com.edu.uptc.gelibackend.mappers;

import com.edu.uptc.gelibackend.dtos.ApplicantDTO;
import com.edu.uptc.gelibackend.entities.ApplicantEntity;
import org.springframework.stereotype.Component;

@Component
public class ApplicantMapper {

    public ApplicantDTO toDTO(ApplicantEntity applicantEntity) {
        return new ApplicantDTO(applicantEntity.getId(), applicantEntity.getApplicantType());
    }

    public ApplicantEntity toEntity(ApplicantDTO applicantDTO) {
        return new ApplicantEntity(applicantDTO.getId(), applicantDTO.getApplicantType());
    }
}
