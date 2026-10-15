package com.springboot.careercrafter.dto.response;

public record EmployerJobCountDto(
        String companyName,
        Long jobs
) {
}
