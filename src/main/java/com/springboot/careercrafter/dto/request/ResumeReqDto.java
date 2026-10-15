package com.springboot.careercrafter.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ResumeReqDto(
        @NotBlank(message = "File name is required")
        @Size(max = 255)
        String fileName,
        @NotBlank(message = "File path is required")
        @Size(max = 500)
        String filePath,
        @NotBlank(message = "Label is required")
        @Size(max = 150)
        String label,
        boolean isDefault
) {
}