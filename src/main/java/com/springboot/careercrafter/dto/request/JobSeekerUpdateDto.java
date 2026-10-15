package com.springboot.careercrafter.dto.request;

import jakarta.validation.constraints.Size;

public record JobSeekerUpdateDto(
        @Size(max = 255)
        String fullName,
        @Size(min = 10, max = 10, message = "Phone number must be 10 digits")
        String phone,
        @Size(max = 150)
        String location,
        String education,
        String experience,
        @Size(max = 1000)
        String skills
) {
}
