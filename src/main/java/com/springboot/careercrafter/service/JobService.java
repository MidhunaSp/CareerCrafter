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
        // step 1: fetch employer from db using employerid
        Employer employer = employerRepository.findById(employerId)
                .orElseThrow(() -> new ResourceNotFoundException("Employer id invalid & Employer not found"));

        // step 2: check that the logged-in user owns this employer account
        if (!employer.getUser().getUsername().equals(loggedInUsername))
            throw new OwnershipException("You can post jobs only for your own employer account");

        // step 3: only verified employers are allowed to post jobs
        if (employer.getVerificationStatus() != VerificationStatus.VERIFIED)
            throw new InvalidCallException("Employer is not verified yet");

        // step 4: validate that minsalary is not greater than maxsalary
        checkSalaryRange(dto);

        // step 5: map jobreqdto to job entity
        Job job = JobMapper.mapDtoToEntity(dto);

        // step 6: attach the employer to job and mark the job as active
        job.setEmployer(employer);
        job.setActive(true);

        // step 7: save job and return id with message
        job = jobRepository.save(job);
        return new CreatedRespDto(job.getId(), "Job posted");
    }

    public List<JobRespDto> getActiveJobs(int page, int size) {
        // step 1: create pageable with page number and size
        // step 2: fetch only the active jobs for that page and return
        return jobRepository.getActiveJobs(PageRequest.of(page, size));
    }

    public JobRespDto getById(Long id) {
        // step 1: fetch job details from db using id, throw exception if not found
        return jobRepository.getJobDetailsById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found"));
    }

    public List<JobRespDto> filter(String location, String industry, EmploymentType employmentType,
                                   Double minSalary, String keyword, int page, int size) {
        // step 1: create pageable with page number and size
        // step 2: run the custom filter query (all filters are optional) and return
        return jobRepository.filterJobs(location, industry, employmentType, minSalary, keyword,
                PageRequest.of(page, size));
    }

    public List<JobRespDto> getByEmployerId(Long employerId, int page, int size) {
        // step 1: check that the employer exists, throw exception if not
        if (!employerRepository.existsById(employerId))
            throw new ResourceNotFoundException("Employer not found");

        // step 2: fetch the jobs posted by this employer (paginated) and return
        return jobRepository.getJobsByEmployerId(employerId, PageRequest.of(page, size));
    }

    public void update(Long id, JobReqDto dto, String loggedInUsername) {
        // step 1: fetch job from db using id, throw exception if not found
        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found"));

        // step 2: check that the logged-in user is the owner of this job
        checkOwner(job, loggedInUsername);

        // step 3: validate that minsalary is not greater than maxsalary
        checkSalaryRange(dto);

        // step 4: copy the new values from dto into the existing job entity
        JobMapper.updateEntity(job, dto);

        // step 5: save the updated job
        jobRepository.save(job);
    }

    public void close(Long id, String loggedInUsername) {
        // step 1: fetch job from db using id, throw exception if not found
        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found"));

        // step 2: check that the logged-in user is the owner of this job
        checkOwner(job, loggedInUsername);

        // step 3: mark the job as inactive (closed) instead of deleting it
        job.setActive(false);

        // step 4: save the job
        jobRepository.save(job);
    }

    public void deleteById(Long id, String loggedInUsername) {
        // step 1: fetch job from db using id, throw exception if not found
        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found"));

        // step 2: check that the logged-in user is the owner of this job
        checkOwner(job, loggedInUsername);

        // step 3: delete the job from db
        jobRepository.delete(job);
    }

    private void checkOwner(Job job, String loggedInUsername) {
        // step 1: compare the job's employer username with the logged-in username
        // step 2: throw exception if they don't match
        if (!job.getEmployer().getUser().getUsername().equals(loggedInUsername))
            throw new OwnershipException("This job belongs to another employer");
    }

    private void checkSalaryRange(JobReqDto dto) {
        if (dto.minSalary() != null && dto.maxSalary() != null && dto.minSalary() > dto.maxSalary())
            throw new InvalidCallException("Min salary cannot be greater than max salary");
    }
}