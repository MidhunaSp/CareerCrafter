package com.springboot.careercrafter.service;

import com.springboot.careercrafter.dto.request.JobSeekerReqDto;
import com.springboot.careercrafter.dto.request.JobSeekerUpdateDto;
import com.springboot.careercrafter.dto.response.CreatedRespDto;
import com.springboot.careercrafter.dto.response.JobSeekerRespDto;
import com.springboot.careercrafter.enums.Role;
import com.springboot.careercrafter.exception.InvalidCallException;
import com.springboot.careercrafter.exception.ResourceNotFoundException;
import com.springboot.careercrafter.mapper.JobSeekerMapper;
import com.springboot.careercrafter.model.JobSeeker;
import com.springboot.careercrafter.model.User;
import com.springboot.careercrafter.repository.JobSeekerRepository;
import com.springboot.careercrafter.repository.UserRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class JobSeekerService {

    private final JobSeekerRepository jobSeekerRepository;
    private final UserRepository userRepository;
    private final JobSeekerMapper jobSeekerMapper;
    private final UserService userService;

    public CreatedRespDto add(@Valid JobSeekerReqDto jobSeekerReqDto) {
        User user = userService.createUser(jobSeekerReqDto.username(),jobSeekerReqDto.password(),Role.JOB_SEEKER);
        JobSeeker jobSeeker = jobSeekerMapper.mapDtoToEntity(jobSeekerReqDto);
        jobSeeker.setUser(user);
        jobSeeker = jobSeekerRepository.save(jobSeeker);
        return new CreatedRespDto(jobSeeker.getId(),"JobSeeker created");
    }

    public List<JobSeekerRespDto> getAll() {
        return jobSeekerRepository.findAll()
                .stream()
                .map(jobSeekerMapper::mapEntityToDto)
                .toList();
    }

    public JobSeekerRespDto getById(Long id) {
        JobSeeker js = jobSeekerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Job seeker not found"));
        return jobSeekerMapper.mapEntityToDto(js);
    }

    public JobSeekerRespDto getByUsername(String username) {
        JobSeeker js = jobSeekerRepository.findByUserUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Job seeker not found with username: " + username));
        return jobSeekerMapper.mapEntityToDto(js);
    }

    public List<JobSeekerRespDto> filter(String fullName, String location, String skills, String education) {
        return jobSeekerRepository.filterJobSeekers(fullName, location, skills, education)
                .stream()
                .map(jobSeekerMapper::mapEntityToDto)
                .toList();
    }

    public void update(Long id, JobSeekerUpdateDto dto) {
        JobSeeker js = jobSeekerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Job seeker not found"));
        boolean flag = false;

        if (StringUtils.hasText(dto.fullName())) {
            js.setFullName(dto.fullName());
            flag = true;
        }
        if (StringUtils.hasText(dto.phone())) { js.setPhone(dto.phone()); flag = true; }
        if (StringUtils.hasText(dto.location())) { js.setLocation(dto.location()); flag = true; }
        if (StringUtils.hasText(dto.education())) { js.setEducation(dto.education()); flag = true; }
        if (StringUtils.hasText(dto.experience())) { js.setExperience(dto.experience()); flag = true; }
        if (StringUtils.hasText(dto.skills())) { js.setSkills(dto.skills()); flag = true; }

        if (!flag)
            throw new InvalidCallException("Update op terminated");

        jobSeekerRepository.save(js);
    }

    @Transactional
    public void deleteById(Long id) {
        JobSeeker js = jobSeekerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Job seeker not found"));
        User user = js.getUser();

        jobSeekerRepository.delete(js);
        userRepository.delete(user);
        userRepository.flush();
    }
}