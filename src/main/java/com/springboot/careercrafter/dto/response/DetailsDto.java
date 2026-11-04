package com.springboot.careercrafter.dto.response;

import com.springboot.careercrafter.enums.Role;

public record DetailsDto(
        String username,
        Boolean isActive,
        Role role
) {
}
