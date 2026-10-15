package com.springboot.careercrafter.dto.request;

import com.springboot.careercrafter.enums.EmploymentType;
import jakarta.validation.constraints.*;

public record JobReqDto(
        @NotBlank(message = "Title is required")
        @Size(max = 200)
        String title,
        @NotBlank(message = "Description is required")
        String description,
        @Size(max = 1000)
        String qualifications,
        @NotBlank(message = "Location is required")
        @Size(max = 150)
        String location,
        @Size(max = 150)
        String industry,
        @NotNull(message = "Employment type is required")
        EmploymentType employmentType,
        @PositiveOrZero(message = "Min salary cannot be negative")
        Double minSalary,
        @PositiveOrZero(message = "Max salary cannot be negative")
        Double maxSalary,
        @Size(max = 200, message = "Instructions can be max 200 characters")
        String applicationInstructions
) {
}