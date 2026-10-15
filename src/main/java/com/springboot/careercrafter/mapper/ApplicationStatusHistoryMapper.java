package com.springboot.careercrafter.mapper;

import com.springboot.careercrafter.dto.request.ApplicationStatusHistoryReqDto;
import com.springboot.careercrafter.dto.response.ApplicationStatusHistoryRespDto;
import com.springboot.careercrafter.model.ApplicationStatusHistory;
import org.springframework.stereotype.Component;

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
}