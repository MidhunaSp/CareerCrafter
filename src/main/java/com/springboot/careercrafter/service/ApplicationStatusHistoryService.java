package com.springboot.careercrafter.service;

import com.springboot.careercrafter.dto.request.ApplicationStatusHistoryReqDto;
import com.springboot.careercrafter.dto.response.ApplicationStatusHistoryRespDto;
import com.springboot.careercrafter.dto.response.CreatedRespDto;
import com.springboot.careercrafter.exception.InvalidCallException;
import com.springboot.careercrafter.exception.OwnershipException;
import com.springboot.careercrafter.exception.ResourceNotFoundException;
import com.springboot.careercrafter.mapper.ApplicationStatusHistoryMapper;
import com.springboot.careercrafter.model.ApplicationStatusHistory;
import com.springboot.careercrafter.model.JobApplication;
import com.springboot.careercrafter.repository.ApplicationStatusHistoryRepository;
import com.springboot.careercrafter.repository.JobApplicationRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ApplicationStatusHistoryService {

    private final ApplicationStatusHistoryRepository historyRepository;
    private final JobApplicationRepository jobApplicationRepository;
    private final ApplicationStatusHistoryMapper historyMapper;

    @Transactional
    public CreatedRespDto add(Long jobApplicationId, ApplicationStatusHistoryReqDto dto, String loggedInUsername) {
        // step 1: fetch the application
        JobApplication application = jobApplicationRepository.findById(jobApplicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Job application id invalid & Application not found"));

        // step 2: only the employer who posted the job can change the application status
        if (!application.getJob().getEmployer().getUser().getUsername().equals(loggedInUsername))
            throw new OwnershipException("Only the employer of this job can change the application status");

        // step 3: block a change to the status it already has
        if (application.getStatus() == dto.status())
            throw new InvalidCallException("Application is already in status " + dto.status());

        // step 4: keep job_applications.status in sync with the latest history entry
        application.setStatus(dto.status());
        jobApplicationRepository.save(application);

        // step 5: map dto to entity, attach the application and save the history row
        ApplicationStatusHistory history = historyMapper.mapDtoToEntity(dto);
        history.setJobApplication(application);

        history = historyRepository.save(history);
        return new CreatedRespDto(history.getId(), "Status history added");
    }

    public List<ApplicationStatusHistoryRespDto> getTimeline(Long jobApplicationId, String loggedInUsername) {
        JobApplication application = jobApplicationRepository.findById(jobApplicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Job application not found"));

        // only the applicant and the employer of the job can see the timeline
        String applicantUsername = application.getApplicant().getUser().getUsername();
        String employerUsername = application.getJob().getEmployer().getUser().getUsername();
        if (!loggedInUsername.equals(applicantUsername) && !loggedInUsername.equals(employerUsername))
            throw new OwnershipException("This application does not belong to you");

        return historyRepository.findByJobApplicationIdOrderByChangedAtAsc(jobApplicationId)
                .stream()
                .map(historyMapper::mapEntityToDto)
                .toList();
    }

//    public void delete(Long jobApplicationId) {
//        historyRepository.deleteById(jobApplicationId);
//    }
}