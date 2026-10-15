package com.springboot.careercrafter.dto.response;

import com.springboot.careercrafter.enums.ApplicationStatus;

import java.time.Instant;

public record ApplicationStatusHistoryRespDto(
        Long id,
        ApplicationStatus status,
        String note,
        Instant changedAt
) {
}
