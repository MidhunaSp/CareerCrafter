package com.springboot.careercrafter.controller;

import com.springboot.careercrafter.dto.request.ApplicationStatusHistoryReqDto;
import com.springboot.careercrafter.dto.response.ApplicationStatusHistoryRespDto;
import com.springboot.careercrafter.dto.response.CreatedRespDto;
import com.springboot.careercrafter.service.ApplicationStatusHistoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class ApplicationStatusHistoryController {

    private final ApplicationStatusHistoryService historyService;

    @PostMapping("/api/applicationStatusHistory/add/{jobApplicationId}")
    public CreatedRespDto add(@PathVariable Long jobApplicationId,
                              @Valid @RequestBody ApplicationStatusHistoryReqDto dto,
                              Principal principal) {
        return historyService.add(jobApplicationId, dto, principal.getName());
    }

    @GetMapping("/api/applicationStatusHistory/{jobApplicationId}")
    public List<ApplicationStatusHistoryRespDto> getTimeline(@PathVariable Long jobApplicationId,
                                                             Principal principal) {
        return historyService.getTimeline(jobApplicationId, principal.getName());
    }
}