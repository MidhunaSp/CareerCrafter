package com.springboot.careercrafter.dto.response;

import com.springboot.careercrafter.enums.ApplicationStatus;

import java.time.Instant;

public record JobApplicationRespDto(
        Long id,
        ApplicationStatus status,
        Instant appliedAt,
        Instant updatedAt,
        Long jobId,
        String jobTitle,
        String companyName,
        Long applicantId,
        String applicantName,
        Long resumeId
) {
}
