package com.springboot.careercrafter.mapper;

import com.springboot.careercrafter.dto.request.JobReqDto;
import com.springboot.careercrafter.model.Job;
import org.springframework.stereotype.Component;

@Component
public class JobMapper {

    public static Job mapDtoToEntity(JobReqDto dto) {
        Job job = new Job();
        updateEntity(job, dto);
        return job;
    }

    public static void updateEntity(Job job, JobReqDto dto) {
        job.setTitle(dto.title());
        job.setDescription(dto.description());
        job.setQualifications(dto.qualifications());
        job.setLocation(dto.location());
        job.setIndustry(dto.industry());
        job.setEmploymentType(dto.employmentType());
        job.setMinSalary(dto.minSalary());
        job.setMaxSalary(dto.maxSalary());
        job.setApplicationInstructions(dto.applicationInstructions());
    }
}