package com.springboot.careercrafter.controller;

import com.springboot.careercrafter.dto.response.CreatedRespDto;
import com.springboot.careercrafter.dto.response.SavedJobRespDto;
import com.springboot.careercrafter.service.SavedJobService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class SavedJobController {

    private final SavedJobService savedJobService;

    @PostMapping("/api/savedJob/add/{jobSeekerId}/{jobId}")
    public CreatedRespDto add(@PathVariable Long jobSeekerId,
                              @PathVariable Long jobId,
                              Principal principal) {
        return savedJobService.add(jobSeekerId, jobId, principal.getName());
    }

    @GetMapping("/api/savedJob/jobseeker/{jobSeekerId}")
    public List<SavedJobRespDto> getByJobSeekerId(@PathVariable Long jobSeekerId, Principal principal) {
        return savedJobService.getByJobSeekerId(jobSeekerId, principal.getName());
    }

    @DeleteMapping("/api/savedJob/{id}")
    public void deleteById(@PathVariable Long id, Principal principal) {
        savedJobService.deleteById(id, principal.getName());
    }
}