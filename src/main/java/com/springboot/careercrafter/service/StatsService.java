package com.springboot.careercrafter.service;

import com.springboot.careercrafter.dto.response.ApplicationStatusStatDto;
import com.springboot.careercrafter.dto.response.EmployerJobCountDto;
import com.springboot.careercrafter.dto.response.IndustryCountDto;
import com.springboot.careercrafter.dto.response.JobApplicantCountDto;
import com.springboot.careercrafter.repository.JobApplicationRepository;
import com.springboot.careercrafter.repository.JobRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StatsService {

    private final JobRepository jobRepository;
    private final JobApplicationRepository jobApplicationRepository;

    public List<JobApplicantCountDto> getApplicantCountPerJob() {
        return jobRepository.getApplicantCountPerJob();
    }

    public List<EmployerJobCountDto> getJobCountPerEmployer() {
        return jobRepository.getJobCountPerEmployer();
    }

    public List<IndustryCountDto> getJobCountByIndustry() {
        return jobRepository.getJobCountByIndustry();
    }

    public List<ApplicationStatusStatDto> getApplicationStatusStat() {
        return jobApplicationRepository.getApplicationStatusStat();
    }
}