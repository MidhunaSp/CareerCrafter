package com.springboot.careercrafter.mapper;

import com.springboot.careercrafter.dto.request.ApplicationStatusHistoryReqDto;
import com.springboot.careercrafter.dto.response.ApplicationStatusHistoryRespDto;
import com.springboot.careercrafter.dto.response.EmployerApplicationHistoryRespDto;
import com.springboot.careercrafter.model.ApplicationStatusHistory;
import com.springboot.careercrafter.model.JobApplication;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ApplicationStatusHistoryMapper {

    public ApplicationStatusHistory mapDtoToEntity(ApplicationStatusHistoryReqDto dto) {
        ApplicationStatusHistory history = new ApplicationStatusHistory();
        history.setStatus(dto.status());
        history.setNote(dto.note());
        return history;
    }

    public ApplicationStatusHistoryRespDto mapEntityToDto(ApplicationStatusHistory h) {
        return new ApplicationStatusHistoryRespDto(
                h.getId(),
                h.getStatus(),
                h.getNote(),
                h.getChangedAt()
        );
    }

    public EmployerApplicationHistoryRespDto mapToEmployerHistoryDto(JobApplication application,
                                                                     List<ApplicationStatusHistoryRespDto> history) {
        return new EmployerApplicationHistoryRespDto(
                application.getId(),
                application.getJob().getId(),
                application.getJob().getTitle(),
                application.getApplicant().getId(),
                application.getApplicant().getFullName(),
                application.getStatus(),
                application.getAppliedAt(),
                history
        );
    }
}