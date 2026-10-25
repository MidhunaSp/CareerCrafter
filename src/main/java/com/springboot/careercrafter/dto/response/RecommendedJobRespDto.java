package com.springboot.careercrafter.dto.response;

import java.util.List;

public record RecommendedJobRespDto(
        JobRespDto job,
        int score,
        List<String> matchedSkills,
        boolean locationMatch
) {
}
