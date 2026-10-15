package com.springboot.careercrafter.service;

import com.springboot.careercrafter.dto.request.JobReqDto;
import com.springboot.careercrafter.dto.response.CreatedRespDto;
import com.springboot.careercrafter.dto.response.JobRespDto;
import com.springboot.careercrafter.enums.EmploymentType;
import com.springboot.careercrafter.enums.VerificationStatus;
import com.springboot.careercrafter.exception.InvalidCallException;
import com.springboot.careercrafter.exception.OwnershipException;
import com.springboot.careercrafter.exception.ResourceNotFoundException;
import com.springboot.careercrafter.mapper.JobMapper;
import com.springboot.careercrafter.model.Employer;
import com.springboot.careercrafter.model.Job;
import com.springboot.careercrafter.repository.EmployerRepository;
import com.springboot.careercrafter.repository.JobRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class JobService {

    private final JobRepository jobRepository;
    private final EmployerRepository employerRepository;
    private final JobMapper jobMapper;

    public CreatedRespDto add(Long employerId, JobReqDto dto, String loggedInUsername) {
        Employer employer = employerRepository.findById(employerId)
                .orElseThrow(() -> new ResourceNotFoundException("Employer id invalid & Employer not found"));

        if (!employer.getUser().getUsername().equals(loggedInUsername))
            throw new OwnershipException("You can post jobs only for your own employer account");

        if (employer.getVerificationStatus() != VerificationStatus.VERIFIED)
            throw new InvalidCallException("Employer is not verified yet");

        checkSalaryRange(dto);

        Job job = jobMapper.mapDtoToEntity(dto);

        job.setEmployer(employer);
        job.setActive(true);

        job = jobRepository.save(job);
        return new CreatedRespDto(job.getId(), "Job posted");
    }

    public List<JobRespDto> getActiveJobs(int page, int size) {
        return jobRepository.getActiveJobs(PageRequest.of(page, size));
    }

    public JobRespDto getById(Long id) {
        return jobRepository.getJobDetailsById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found"));
    }

    public List<JobRespDto> filter(String location, String industry, EmploymentType employmentType,
                                   Double minSalary, String keyword, int page, int size) {
        return jobRepository.filterJobs(location, industry, employmentType, minSalary, keyword,
                PageRequest.of(page, size));
    }

    public List<JobRespDto> getByEmployerId(Long employerId, int page, int size) {
        if (!employerRepository.existsById(employerId))
            throw new ResourceNotFoundException("Employer not found");
        return jobRepository.getJobsByEmployerId(employerId, PageRequest.of(page, size));
    }

    public void update(Long id, JobReqDto dto, String loggedInUsername) {
        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found"));

        checkOwner(job, loggedInUsername);
        checkSalaryRange(dto);

        jobMapper.updateEntity(job, dto);
        jobRepository.save(job);
    }

    public void close(Long id, String loggedInUsername) {
        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found"));

        checkOwner(job, loggedInUsername);

        job.setActive(false);
        jobRepository.save(job);
    }

    public void deleteById(Long id, String loggedInUsername) {
        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found"));

        checkOwner(job, loggedInUsername);

        jobRepository.delete(job);
    }

    private void checkOwner(Job job, String loggedInUsername) {
        if (!job.getEmployer().getUser().getUsername().equals(loggedInUsername))
            throw new OwnershipException("This job belongs to another employer");
    }

    private void checkSalaryRange(JobReqDto dto) {
        if (dto.minSalary() != null && dto.maxSalary() != null && dto.minSalary() > dto.maxSalary())
            throw new InvalidCallException("Min salary cannot be greater than max salary");
    }
}