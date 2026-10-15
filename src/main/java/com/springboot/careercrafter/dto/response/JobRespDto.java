package com.springboot.careercrafter.dto.response;

import com.springboot.careercrafter.enums.EmploymentType;
import java.time.LocalDateTime;

public record JobRespDto(
        Long id,
        String title,
        String description,
        String qualifications,
        String location,
        String industry,
        EmploymentType employmentType,
        Double minSalary,
        Double maxSalary,
        String applicationInstructions,
        boolean active,
        LocalDateTime createdAt,
        Long employerId,
        String companyName
) {
}