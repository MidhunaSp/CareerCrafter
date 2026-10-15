package com.springboot.careercrafter.controller;

import com.springboot.careercrafter.dto.request.ApplicationStatusHistoryReqDto;
import com.springboot.careercrafter.dto.response.ApplicationStatusHistoryRespDto;
import com.springboot.careercrafter.dto.response.CreatedRespDto;
import com.springboot.careercrafter.service.ApplicationStatusHistoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ApplicationStatusHistoryController {

    private final ApplicationStatusHistoryService historyService;

    @PostMapping("/api/applicationStatusHistory/add/{jobApplicationId}")
    public CreatedRespDto add(@PathVariable Long jobApplicationId,
                              @Valid @RequestBody ApplicationStatusHistoryReqDto dto) {
        return historyService.add(jobApplicationId, dto);
    }

    @GetMapping("/api/applicationStatusHistory/{jobApplicationId}")
    public List<ApplicationStatusHistoryRespDto> getTimeline(@PathVariable Long jobApplicationId) {
        return historyService.getTimeline(jobApplicationId);
    }

    @DeleteMapping("/api/applicationStatusHistory/{jobApplicationId}")
    public void deleteApplicationStatusHistory(@PathVariable Long jobApplicationId) {
        historyService.delete(jobApplicationId);
    }
}