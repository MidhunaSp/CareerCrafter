package com.springboot.careercrafter.service;

import com.springboot.careercrafter.dto.request.ApplicationStatusHistoryReqDto;
import com.springboot.careercrafter.dto.response.ApplicationStatusHistoryRespDto;
import com.springboot.careercrafter.dto.response.CreatedRespDto;
import com.springboot.careercrafter.exception.InvalidCallException;
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
    public CreatedRespDto add(Long jobApplicationId, ApplicationStatusHistoryReqDto dto) {
        JobApplication application = jobApplicationRepository.findById(jobApplicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Job application id invalid & Application not found"));

        if (application.getStatus() == dto.status())
            throw new InvalidCallException("Application is already in status " + dto.status());

        application.setStatus(dto.status());
        jobApplicationRepository.save(application);

        ApplicationStatusHistory history = historyMapper.mapDtoToEntity(dto);
        history.setJobApplication(application);

        history = historyRepository.save(history);
        return new CreatedRespDto(history.getId(), "Status history added");
    }

    public List<ApplicationStatusHistoryRespDto> getTimeline(Long jobApplicationId) {
        if (!jobApplicationRepository.existsById(jobApplicationId))
            throw new ResourceNotFoundException("Job application not found");

        return historyRepository.findByJobApplicationIdOrderByChangedAtAsc(jobApplicationId)
                .stream()
                .map(historyMapper::mapEntityToDto)
                .toList();
    }

    public void delete(Long jobApplicationId) {
        historyRepository.deleteById(jobApplicationId);
    }
}