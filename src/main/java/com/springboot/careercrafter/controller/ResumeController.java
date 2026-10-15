package com.springboot.careercrafter.controller;

import com.springboot.careercrafter.dto.request.ResumeReqDto;
import com.springboot.careercrafter.dto.response.CreatedRespDto;
import com.springboot.careercrafter.dto.response.ResumeRespDto;
import com.springboot.careercrafter.service.ResumeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ResumeController {

    private final ResumeService resumeService;

    @PostMapping("/api/resume/add/{jobSeekerId}")
    public CreatedRespDto add(@PathVariable Long jobSeekerId,
                              @Valid @RequestBody ResumeReqDto dto) {
        return resumeService.add(jobSeekerId, dto);
    }

    @GetMapping("/api/resume/jobseeker/{jobSeekerId}")
    public List<ResumeRespDto> getByJobSeekerId(@PathVariable Long jobSeekerId) {
        return resumeService.getByJobSeekerId(jobSeekerId);
    }

    @GetMapping("/api/resume/{id}")
    public ResumeRespDto getById(@PathVariable Long id) {
        return resumeService.getById(id);
    }

    @PutMapping("/api/resume/{id}/default")
    public void makeDefault(@PathVariable Long id) {
        resumeService.makeDefault(id);
    }

    @DeleteMapping("/api/resume/{id}")
    public void deleteById(@PathVariable Long id) {
        resumeService.deleteById(id);
    }
}