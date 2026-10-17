package com.springboot.careercrafter.controller;

import com.springboot.careercrafter.dto.request.ResumeReqDto;
import com.springboot.careercrafter.dto.response.CreatedRespDto;
import com.springboot.careercrafter.dto.response.ResumeRespDto;
import com.springboot.careercrafter.service.ResumeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class ResumeController {

    private final ResumeService resumeService;

    @PostMapping("/api/resume/add/{jobSeekerId}")
    public CreatedRespDto add(@PathVariable Long jobSeekerId,
                              @Valid @RequestBody ResumeReqDto dto,
                              Principal principal) {
        return resumeService.add(jobSeekerId, dto, principal.getName());
    }

    @GetMapping("/api/resume/jobseeker/{jobSeekerId}")
    public List<ResumeRespDto> getByJobSeekerId(@PathVariable Long jobSeekerId, Principal principal) {
        return resumeService.getByJobSeekerId(jobSeekerId, principal.getName());
    }

    @GetMapping("/api/resume/{id}")
    public ResumeRespDto getById(@PathVariable Long id, Principal principal) {
        return resumeService.getById(id, principal.getName());
    }

    @PutMapping("/api/resume/{id}/default")
    public void makeDefault(@PathVariable Long id, Principal principal) {
        resumeService.makeDefault(id, principal.getName());
    }

    @DeleteMapping("/api/resume/{id}")
    public void deleteById(@PathVariable Long id, Principal principal) {
        resumeService.deleteById(id, principal.getName());
    }
}