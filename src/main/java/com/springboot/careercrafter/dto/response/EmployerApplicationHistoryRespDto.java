
package com.springboot.careercrafter.dto.response;

import com.springboot.careercrafter.enums.ApplicationStatus;
import java.time.Instant;
import java.util.List;

public record EmployerApplicationHistoryRespDto(
        Long applicationId,
        Long jobId,
        String jobTitle,
        Long applicantId,
        String applicantName,
        ApplicationStatus currentStatus,
        Instant appliedAt,
        List<ApplicationStatusHistoryRespDto> history
) {}
