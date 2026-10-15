package com.springboot.careercrafter.dto.response;

import com.springboot.careercrafter.enums.ApplicationStatus;

public record ApplicationStatusStatDto(
        ApplicationStatus status,
        Long count
) {
}