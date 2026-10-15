package com.springboot.careercrafter.service;

import com.springboot.careercrafter.dto.request.EmployerReqDto;
import com.springboot.careercrafter.dto.request.EmployerUpdateDto;
import com.springboot.careercrafter.dto.response.CreatedRespDto;
import com.springboot.careercrafter.dto.response.EmployerRespDto;
import com.springboot.careercrafter.enums.Role;
import com.springboot.careercrafter.enums.VerificationStatus;
import com.springboot.careercrafter.exception.InvalidCallException;
import com.springboot.careercrafter.exception.ResourceNotFoundException;
import com.springboot.careercrafter.mapper.EmployerMapper;
import com.springboot.careercrafter.model.Employer;
import com.springboot.careercrafter.model.User;
import com.springboot.careercrafter.repository.EmployerRepository;
import com.springboot.careercrafter.repository.UserRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EmployerService {

    private final EmployerRepository employerRepository;
    private final EmployerMapper employerMapper;
    private final UserRepository userRepository;
    private final UserService userService;

    public CreatedRespDto add(@Valid EmployerReqDto employerReqDto) {
        User user = userService.createUser(employerReqDto.username(),employerReqDto.password(),Role.EMPLOYER);
        Employer employer = employerMapper.mapDtoToEntity(employerReqDto);
        employer.setUser(user);
        employer.setVerificationStatus(VerificationStatus.PENDING);
        employer = employerRepository.save(employer);
        return new CreatedRespDto(employer.getId(),"Employer created");
    }

    public EmployerRespDto getById(long employerId) {
       Employer employer = employerRepository.findById(employerId)
               .orElseThrow(()-> new ResourceNotFoundException("Employer not found"));
       return employerMapper.mapEntityToDto(employer);
    }


    public List<EmployerRespDto> getByVerificationStatus(VerificationStatus status) {
        return employerRepository.findByVerificationStatus(status)
                .stream()
                .map(employerMapper::mapEntityToDto)
                .toList();
    }

    public EmployerRespDto getByUsername(String username) {
        Employer employer = employerRepository.findByUserUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Employer not found with username: " + username));
        return employerMapper.mapEntityToDto(employer);
    }

    public List<EmployerRespDto> getAll() {
        return employerRepository.findAll()
                .stream()
                .map(employerMapper::mapEntityToDto)
                .toList();
    }

    public List<EmployerRespDto> filter(VerificationStatus status, String companyName, String fullName) {
        return employerRepository.filterEmployers(status, companyName, fullName)
                .stream()
                .map(employerMapper::mapEntityToDto)
                .toList();
    }

    public void update(Long id, EmployerUpdateDto dto) {
        Employer employer = employerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employer not found"));
        boolean flag = false;

        if (StringUtils.hasText(dto.fullName())) {
            employer.setFullName(dto.fullName());
            flag = true;
        }
        if (StringUtils.hasText(dto.companyName())) {
            employer.setCompanyName(dto.companyName());
            flag = true;
        }
        if (StringUtils.hasText(dto.phone())) {
            employer.setPhone(dto.phone());
            flag = true;
        }

        if (!flag)
            throw new InvalidCallException("Update op terminated");

        employerRepository.save(employer);
    }

    @Transactional
    public void deleteById(Long id) {
        Employer employer = employerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employer not found"));
        User user = employer.getUser();

        employerRepository.delete(employer);
        userRepository.delete(user);
        userRepository.flush();
    }

}