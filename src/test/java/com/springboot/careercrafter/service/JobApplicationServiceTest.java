package com.springboot.careercrafter.service;

import com.springboot.careercrafter.dto.response.CreatedRespDto;
import com.springboot.careercrafter.dto.response.JobApplicationRespDto;
import com.springboot.careercrafter.enums.ApplicationStatus;
import com.springboot.careercrafter.exception.ResourceNotFoundException;
import com.springboot.careercrafter.mapper.JobApplicationMapper;
import com.springboot.careercrafter.model.ApplicationStatusHistory;
import com.springboot.careercrafter.model.Job;
import com.springboot.careercrafter.model.JobApplication;
import com.springboot.careercrafter.model.JobSeeker;
import com.springboot.careercrafter.model.Resume;
import com.springboot.careercrafter.model.User;
import com.springboot.careercrafter.repository.ApplicationStatusHistoryRepository;
import com.springboot.careercrafter.repository.JobApplicationRepository;
import com.springboot.careercrafter.repository.JobRepository;
import com.springboot.careercrafter.repository.JobSeekerRepository;
import com.springboot.careercrafter.repository.ResumeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class JobApplicationServiceTest {

    @InjectMocks
    private JobApplicationService jobApplicationService;
    @Mock
    private JobApplicationRepository jobApplicationRepository;
    @Mock
    private JobRepository jobRepository;
    @Mock
    private JobApplicationMapper jobApplicationMapper;
    @Mock
    private JobSeekerRepository jobSeekerRepository;
    @Mock
    private ResumeRepository resumeRepository;
    @Mock
    private ApplicationStatusHistoryRepository historyRepository;

    // ---- Valid Test----
    @Test
    public void getByApplicantIdTest() {
        JobApplicationRespDto dto1 = new JobApplicationRespDto(1L, ApplicationStatus.APPLIED, Instant.now(), Instant.now(), 1L, "Java Developer", "TCS", 1L, "john", null);
        JobApplicationRespDto dto2 = new JobApplicationRespDto(2L, ApplicationStatus.SHORTLISTED, Instant.now(), Instant.now(), 2L, "Tester", "Infosys", 1L, "john", null);

        User user = new User();
        user.setUsername("john");
        JobSeeker applicant = new JobSeeker();
        applicant.setId(1L);
        applicant.setUser(user);

        JobApplication application1 = new JobApplication();
        application1.setId(1L);
        JobApplication application2 = new JobApplication();
        application2.setId(2L);

        when(jobSeekerRepository.findById(1L)).thenReturn(Optional.of(applicant));

        when(jobApplicationRepository.findByApplicantIdWithDetails(1L, PageRequest.of(0, 5)))
                .thenReturn(List.of(application1, application2));

        when(jobApplicationMapper.mapEntityToDto(application1)).thenReturn(dto1);
        when(jobApplicationMapper.mapEntityToDto(application2)).thenReturn(dto2);

        assertEquals(2, jobApplicationService.getByApplicantId(1L, 0, 5, "john").size()); // applicant 1 has 2 applications

        // verify that each mock method was called exactly once with these arguments (num of method invocations)
        verify(jobSeekerRepository, times(1)).findById(1L);
        verify(jobApplicationRepository, times(1)).findByApplicantIdWithDetails(1L, PageRequest.of(0, 5));
        verify(jobApplicationMapper, times(1)).mapEntityToDto(application1);
        verify(jobApplicationMapper, times(1)).mapEntityToDto(application2);

        // no more calls (only wantedNumberOfInvocations called) .. and this guards against unexpected extra DB calls
        verifyNoMoreInteractions(jobSeekerRepository);
        verifyNoMoreInteractions(jobApplicationRepository);
        verifyNoMoreInteractions(jobApplicationMapper);
    }

    // ---Invalid Test---
    @Test
    public void getByApplicantIdTest_Invalid() {
        // findById(7) returns empty -> job seeker not found
        when(jobSeekerRepository.findById(7L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> jobApplicationService.getByApplicantId(7L, 0, 5, "john"));

        verify(jobSeekerRepository, times(1)).findById(7L);

        verify(jobApplicationRepository, times(0)).findByApplicantIdWithDetails(7L,
                PageRequest.of(0, 5));

        // no more calls
        verifyNoMoreInteractions(jobSeekerRepository);
        verifyNoMoreInteractions(jobApplicationRepository);
    }

    @Test
    public void saveTest() {
        // mock
        Long jobId = 1L;
        Long applicantId = 1L;
        String loggedInUsername = "john";

        Job job = new Job();
        job.setId(1L);
        job.setActive(true);

        User user = new User();
        user.setUsername("john");
        JobSeeker applicant = new JobSeeker();
        applicant.setId(1L);
        applicant.setUser(user);

        // resumeId is null in apply(), so the service looks for the applicant's default resume
        Resume resume = new Resume();
        resume.setId(1L);

        JobApplication savedApplication = new JobApplication();
        savedApplication.setId(10L);

        when(jobRepository.findById(jobId)).thenReturn(Optional.of(job));
        when(jobSeekerRepository.findById(applicantId)).thenReturn(Optional.of(applicant));
        when(jobApplicationRepository.existsByJobIdAndApplicantId(jobId, applicantId)).thenReturn(false);
        when(resumeRepository.findDefaultResume(applicantId)).thenReturn(Optional.of(resume));

        when(jobApplicationRepository.save(any(JobApplication.class))).thenReturn(savedApplication);

        CreatedRespDto response = jobApplicationService.apply(jobId, applicantId, null, loggedInUsername);

        assertEquals(10L, response.id());
        assertEquals("Applied successfully", response.message());

        // verify if repo calls are happening only once
        verify(jobRepository, times(1)).findById(jobId);
        verify(jobSeekerRepository, times(1)).findById(applicantId);
        verify(jobApplicationRepository, times(1)).existsByJobIdAndApplicantId(jobId, applicantId);
        verify(resumeRepository, times(1)).findDefaultResume(applicantId);
        verify(jobApplicationRepository, times(1)).save(any(JobApplication.class));
        verify(historyRepository, times(1)).save(any(ApplicationStatusHistory.class));

        verifyNoMoreInteractions(resumeRepository);
    }
}