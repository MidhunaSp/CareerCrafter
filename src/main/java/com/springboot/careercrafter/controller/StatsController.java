package com.springboot.careercrafter.controller;

import com.springboot.careercrafter.dto.response.ApplicationStatusStatDto;
import com.springboot.careercrafter.dto.response.EmployerJobCountDto;
import com.springboot.careercrafter.dto.response.IndustryCountDto;
import com.springboot.careercrafter.dto.response.JobApplicantCountDto;
import com.springboot.careercrafter.service.StatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class StatsController {

    private final StatsService statsService;

    @GetMapping("/api/stats/applicants-per-job")
    public List<JobApplicantCountDto> getApplicantCountPerJob() {
        return statsService.getApplicantCountPerJob();
    }

    @GetMapping("/api/stats/jobs-per-employer")
    public List<EmployerJobCountDto> getJobCountPerEmployer() {
        return statsService.getJobCountPerEmployer();
    }

    @GetMapping("/api/stats/jobs-per-industry")
    public List<IndustryCountDto> getJobCountByIndustry() {
        return statsService.getJobCountByIndustry();
    }

    @GetMapping("/api/stats/applications-by-status")
    public List<ApplicationStatusStatDto> getApplicationStatusStat() {
        return statsService.getApplicationStatusStat();
    }
}
// today is a very good day that i got a very good reply from my trainer.. for showing my project (to showcase what i have developed.. the apis).