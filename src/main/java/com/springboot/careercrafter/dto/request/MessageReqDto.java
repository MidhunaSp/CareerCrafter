package com.springboot.careercrafter.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record MessageReqDto(
        @NotBlank(message = "Content is required")
        @Size(max = 2000, message = "Message can be max 2000 characters")
        String content
) {
}
