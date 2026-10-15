package com.springboot.careercrafter.dto.request;

import com.springboot.careercrafter.enums.ApplicationStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ApplicationStatusHistoryReqDto(
        @NotNull(message = "Status is required")
        ApplicationStatus status,
        @Size(max = 500, message = "Note can be max 500 characters")
        String note
) {
}
