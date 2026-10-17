package com.springboot.careercrafter.dto.response;

import java.time.Instant;

public record SavedJobRespDto(
        Long id,
        Instant savedAt,
        Long jobId,
        String jobTitle,
        String companyName,
        String location
) {
}
