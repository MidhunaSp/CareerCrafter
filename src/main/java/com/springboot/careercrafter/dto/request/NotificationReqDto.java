package com.springboot.careercrafter.dto.request;

import com.springboot.careercrafter.enums.NotificationType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record NotificationReqDto(
        @NotBlank(message = "Message is required")
        @Size(max = 1000, message = "Message can be max 1000 characters")
        String message,
        @NotNull(message = "Type is required")
        NotificationType type
) {
}
