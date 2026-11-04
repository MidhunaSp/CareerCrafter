package com.springboot.careercrafter.controller;

import com.springboot.careercrafter.dto.response.RecommendedJobRespDto;
import com.springboot.careercrafter.service.RecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class RecommendationController {

    private final RecommendationService recommendationService;

    @GetMapping("/api/job/recommended/{jobSeekerId}")
    public List<RecommendedJobRespDto> getRecommendedJobs(@PathVariable Long jobSeekerId,
                                                          @RequestParam(defaultValue = "10") int limit,
                                                          Principal principal) {
        return recommendationService.getRecommendedJobs(jobSeekerId, limit, principal.getName());
    }
}
