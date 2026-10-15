package com.springboot.careercrafter.service;

import com.springboot.careercrafter.dto.request.ResumeReqDto;
import com.springboot.careercrafter.dto.response.CreatedRespDto;
import com.springboot.careercrafter.dto.response.ResumeRespDto;
import com.springboot.careercrafter.exception.ResourceNotFoundException;
import com.springboot.careercrafter.mapper.ResumeMapper;
import com.springboot.careercrafter.model.JobSeeker;
import com.springboot.careercrafter.model.Resume;
import com.springboot.careercrafter.repository.JobSeekerRepository;
import com.springboot.careercrafter.repository.ResumeRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ResumeService {

    private final ResumeRepository resumeRepository;
    private final JobSeekerRepository jobSeekerRepository;
    private final ResumeMapper resumeMapper;

    @Transactional
    public CreatedRespDto add(Long jobSeekerId, ResumeReqDto dto) {
        // Step 1: Fetch job seeker from DB
        JobSeeker jobSeeker = jobSeekerRepository.findById(jobSeekerId)
                .orElseThrow(() -> new ResourceNotFoundException("Job seeker id invalid & Job seeker not found"));

        List<Resume> existing = resumeRepository.findByJobSeekerIdOrderByUploadedAtDesc(jobSeekerId);

        // Step 2: The first resume is always the default
        boolean makeDefault = dto.isDefault() || existing.isEmpty();

        // Step 3: If this one is the new default, un-default the older ones
        if (makeDefault && !existing.isEmpty()) {
            existing.forEach(r -> r.setDefault(false));
            resumeRepository.saveAll(existing);
        }

        // Step 4: Map Dto to Entity, attach job seeker and save
        Resume resume = resumeMapper.mapDtoToEntity(dto);
        resume.setJobSeeker(jobSeeker);
        resume.setDefault(makeDefault);

        resume = resumeRepository.save(resume);
        return new CreatedRespDto(resume.getId(), "Resume added");
    }

    public List<ResumeRespDto> getByJobSeekerId(Long jobSeekerId) {
        if (!jobSeekerRepository.existsById(jobSeekerId))
            throw new ResourceNotFoundException("Job seeker not found");
        return resumeRepository.findByJobSeekerIdOrderByUploadedAtDesc(jobSeekerId)
                .stream()
                .map(resumeMapper::mapEntityToDto)
                .toList();
    }

    public ResumeRespDto getById(Long id) {
        Resume resume = resumeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Resume not found"));
        return resumeMapper.mapEntityToDto(resume);
    }

    @Transactional
    public void makeDefault(Long id) {
        Resume resume = resumeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Resume not found"));

        // exactly one default per job seeker: this one true, all others false
        List<Resume> all = resumeRepository
                .findByJobSeekerIdOrderByUploadedAtDesc(resume.getJobSeeker().getId());
        all.forEach(r -> r.setDefault(r.getId().equals(id)));
        resumeRepository.saveAll(all);
    }

    public void deleteById(Long id) {
        Resume resume = resumeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Resume not found"));
        resumeRepository.delete(resume);
    }
}