package com.springboot.careercrafter.service;

import com.springboot.careercrafter.dto.response.CreatedRespDto;
import com.springboot.careercrafter.dto.response.JobApplicationRespDto;
import com.springboot.careercrafter.enums.ApplicationStatus;
import com.springboot.careercrafter.exception.InvalidCallException;
import com.springboot.careercrafter.exception.ResourceNotFoundException;
import com.springboot.careercrafter.mapper.JobApplicationMapper;
import com.springboot.careercrafter.model.ApplicationStatusHistory;
import com.springboot.careercrafter.model.Job;
import com.springboot.careercrafter.model.JobApplication;
import com.springboot.careercrafter.model.JobSeeker;
import com.springboot.careercrafter.model.Resume;
import com.springboot.careercrafter.repository.ApplicationStatusHistoryRepository;
import com.springboot.careercrafter.repository.JobApplicationRepository;
import com.springboot.careercrafter.repository.JobRepository;
import com.springboot.careercrafter.repository.JobSeekerRepository;
import com.springboot.careercrafter.repository.ResumeRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class JobApplicationService {

    private final JobApplicationRepository jobApplicationRepository;
    private final JobRepository jobRepository;
    private final JobApplicationMapper jobApplicationMapper;
    private final JobSeekerRepository jobSeekerRepository;
    private final ResumeRepository resumeRepository;
    private final ApplicationStatusHistoryRepository historyRepository;


    @Transactional
    public CreatedRespDto apply(Long jobId, Long applicantId, Long resumeId) {

        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job id invalid & Job not found"));
        JobSeeker applicant = jobSeekerRepository.findById(applicantId)
                .orElseThrow(() -> new ResourceNotFoundException("Job seeker id invalid & Job seeker not found"));

        if (!job.isActive())
            throw new InvalidCallException("Job is no longer active");
        if (jobApplicationRepository.existsByJobIdAndApplicantId(jobId, applicantId))
            throw new InvalidCallException("Already applied to this job");

        Resume resume = null;
        if (resumeId != null) {
            resume = resumeRepository.findById(resumeId)
                    .orElseThrow(() -> new ResourceNotFoundException("Resume id invalid & Resume not found"));
            if (!resume.getJobSeeker().getId().equals(applicantId))
                throw new InvalidCallException("Resume does not belong to this applicant");
        }

        JobApplication application = new JobApplication();
        application.setJob(job);
        application.setApplicant(applicant);
        application.setResume(resume);
        application.setStatus(ApplicationStatus.APPLIED);
        application = jobApplicationRepository.save(application);

        ApplicationStatusHistory history = new ApplicationStatusHistory();
        history.setJobApplication(application);
        history.setStatus(ApplicationStatus.APPLIED);
        history.setNote("Application submitted");
        historyRepository.save(history);

        return new CreatedRespDto(application.getId(), "Applied successfully");
    }

    public List<JobApplicationRespDto> getByApplicantId(Long applicantId, int page, int size) {
        if (!jobSeekerRepository.existsById(applicantId))
            throw new ResourceNotFoundException("Job seeker not found");
        return jobApplicationRepository.findByApplicantIdWithDetails(applicantId, PageRequest.of(page, size))
                .stream()
                .map(jobApplicationMapper::mapEntityToDto)
                .toList();
    }

    public List<JobApplicationRespDto> getByJobId(Long jobId, ApplicationStatus status, int page, int size) {
        if (!jobRepository.existsById(jobId))
            throw new ResourceNotFoundException("Job not found");
        return jobApplicationRepository.findByJobIdWithDetails(jobId, status, PageRequest.of(page, size))
                .stream()
                .map(jobApplicationMapper::mapEntityToDto)
                .toList();
    }

    public JobApplicationRespDto getById(Long id) {
        JobApplication ja = jobApplicationRepository.findDetailsById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Job application not found"));
        return jobApplicationMapper.mapEntityToDto(ja);
    }

    @Transactional
    public void withdraw(Long id) {
        if (!jobApplicationRepository.existsById(id))
            throw new ResourceNotFoundException("Job application not found");

        historyRepository.deleteByJobApplicationId(id);
        jobApplicationRepository.deleteById(id);
    }
}