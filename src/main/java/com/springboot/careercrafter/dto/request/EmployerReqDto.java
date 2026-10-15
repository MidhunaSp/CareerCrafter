package com.springboot.careercrafter.dto.request;

import jakarta.validation.constraints.*;

public record EmployerReqDto(
        @NotBlank(message = "Full name is required")
        @Size(max = 255)
        String fullName,
        @NotBlank(message = "Company name is required")
        @Size(max = 200)
        String companyName,
        @Size(max = 20, message = "Phone number must be 10 digits")
        String phone,
        @NotBlank(message = "Username is required")
        @Size(min = 3, max = 150, message = "Username should be 3 to 150 characters")
        String username,
        @NotBlank(message = "Password is required")
        String password
) {
}
