package com.springboot.careercrafter.dto.request;

import jakarta.validation.constraints.Size;

public record EmployerUpdateDto(
        @Size(max = 255)
        String fullName,
        @Size(max = 200)
        String companyName,
        @Size(min = 10, max = 10, message = "Phone number must be 10 digits")
        String phone
) {
}
