package com.springboot.careercrafter.mapper;

import com.springboot.careercrafter.dto.response.JobApplicationRespDto;
import com.springboot.careercrafter.model.JobApplication;
import org.springframework.stereotype.Component;

@Component
public class JobApplicationMapper {

    public JobApplicationRespDto mapEntityToDto(JobApplication ja) {
        return new JobApplicationRespDto(
                ja.getId(),
                ja.getStatus(),
                ja.getAppliedAt(),
                ja.getUpdatedAt(),
                ja.getJob().getId(),
                ja.getJob().getTitle(),
                ja.getJob().getEmployer().getCompanyName(),
                ja.getApplicant().getId(),
                ja.getApplicant().getFullName(),
                ja.getResume() == null ? null : ja.getResume().getId()
        );
    }
}