package com.springboot.careercrafter.service;

import com.springboot.careercrafter.dto.request.ResumeReqDto;
import com.springboot.careercrafter.dto.response.CreatedRespDto;
import com.springboot.careercrafter.dto.response.ResumeRespDto;
import com.springboot.careercrafter.exception.OwnershipException;
import com.springboot.careercrafter.exception.ResourceNotFoundException;
import com.springboot.careercrafter.mapper.ResumeMapper;
import com.springboot.careercrafter.model.JobSeeker;
import com.springboot.careercrafter.model.Resume;
import com.springboot.careercrafter.repository.JobSeekerRepository;
import com.springboot.careercrafter.repository.ResumeRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ResumeService {

    private final ResumeRepository resumeRepository;
    private final JobSeekerRepository jobSeekerRepository;
    private final ResumeMapper resumeMapper;

    @Transactional
    public CreatedRespDto add(Long jobSeekerId, ResumeReqDto dto, String loggedInUsername) {
        // step 1: fetch job seeker from db
        JobSeeker jobSeeker = jobSeekerRepository.findById(jobSeekerId)
                .orElseThrow(() -> new ResourceNotFoundException("Job seeker id invalid & Job seeker not found"));

        // step 2: check that the logged-in user owns this job seeker account
        if (!jobSeeker.getUser().getUsername().equals(loggedInUsername))
            throw new OwnershipException("You can add resumes only to your own account");

        List<Resume> existing = resumeRepository.findByJobSeekerIdOrderByUploadedAtDesc(jobSeekerId);

        // step 3: the first resume is always the default
        boolean makeDefault = dto.isDefault() || existing.isEmpty();

        // step 4: if this one is the new default, un-default the older ones
        if (makeDefault && !existing.isEmpty()) {
            existing.forEach(r -> r.setDefault(false));
            resumeRepository.saveAll(existing);
        }

        // step 5: map dto to entity, attach job seeker and save
        Resume resume = resumeMapper.mapDtoToEntity(dto);
        resume.setJobSeeker(jobSeeker);
        resume.setDefault(makeDefault);

        resume = resumeRepository.save(resume);
        return new CreatedRespDto(resume.getId(), "Resume added");
    }

    public List<ResumeRespDto> getByJobSeekerId(Long jobSeekerId, String loggedInUsername) {
        JobSeeker jobSeeker = jobSeekerRepository.findById(jobSeekerId)
                .orElseThrow(() -> new ResourceNotFoundException("Job seeker not found"));

        // only the owner can see the resume list
        if (!jobSeeker.getUser().getUsername().equals(loggedInUsername))
            throw new OwnershipException("These resumes belong to another job seeker");

        return resumeRepository.findByJobSeekerIdOrderByUploadedAtDesc(jobSeekerId)
                .stream()
                .map(resumeMapper::mapEntityToDto)
                .toList();
    }

    public ResumeRespDto getById(Long id, String loggedInUsername) {
        Resume resume = resumeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Resume not found"));
        checkOwner(resume, loggedInUsername);
        return resumeMapper.mapEntityToDto(resume);
    }

    @Transactional
    public void makeDefault(Long id, String loggedInUsername) {
        Resume resume = resumeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Resume not found"));
        checkOwner(resume, loggedInUsername);

        // exactly one default per job seeker: this one true, all others false
        List<Resume> all = resumeRepository
                .findByJobSeekerIdOrderByUploadedAtDesc(resume.getJobSeeker().getId());
        all.forEach(r -> r.setDefault(r.getId().equals(id)));
        resumeRepository.saveAll(all);
    }


    @Transactional
    public void deleteById(Long id, String loggedInUsername) {
        Resume resume = resumeRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Resume not found"));

        checkOwner(resume, loggedInUsername);

        Long jobSeekerId = resume.getJobSeeker().getId();
        boolean wasDefault = resume.isDefault();

        resumeRepository.delete(resume);
        log.info("Resume deleted: id={} by {}", id, loggedInUsername);
        resumeRepository.flush();

        if (wasDefault) {
            List<Resume> remaining =
                    resumeRepository.findByJobSeekerIdOrderByUploadedAtDesc(
                            jobSeekerId);

            if (!remaining.isEmpty()) {
                Resume newDefault = remaining.get(0);
                newDefault.setDefault(true);
                resumeRepository.save(newDefault);
            }
        }
    }

    private void checkOwner(Resume resume, String loggedInUsername) {
        // compare the resume owner's username with the logged-in username
        if (!resume.getJobSeeker().getUser().getUsername().equals(loggedInUsername))
            throw new OwnershipException("This resume belongs to another job seeker");
    }
}