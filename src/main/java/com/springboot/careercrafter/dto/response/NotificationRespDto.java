package com.springboot.careercrafter.dto.response;

import com.springboot.careercrafter.enums.NotificationType;

import java.time.Instant;

public record NotificationRespDto(
        Long id,
        Long userId,
        String message,
        NotificationType type,
        boolean isRead,
        Instant createdAt
) {
}
