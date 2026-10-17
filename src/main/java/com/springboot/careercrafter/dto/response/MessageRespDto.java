package com.springboot.careercrafter.dto.response;

import java.time.Instant;

public record MessageRespDto(
        Long id,
        Long senderId,
        String senderUsername,
        Long receiverId,
        String receiverUsername,
        String content,
        boolean isRead,
        Instant sentAt,
        Long jobApplicationId
) {
}
