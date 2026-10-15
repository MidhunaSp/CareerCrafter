package com.springboot.careercrafter.service;

import com.springboot.careercrafter.dto.request.AdminReqDto;
import com.springboot.careercrafter.dto.response.CreatedRespDto;
import com.springboot.careercrafter.enums.Role;
import com.springboot.careercrafter.enums.VerificationStatus;
import com.springboot.careercrafter.exception.InvalidCallException;
import com.springboot.careercrafter.exception.ResourceNotFoundException;
import com.springboot.careercrafter.mapper.AdminMapper;
import com.springboot.careercrafter.model.Admin;
import com.springboot.careercrafter.model.Employer;
import com.springboot.careercrafter.model.User;
import com.springboot.careercrafter.repository.AdminRepository;
import com.springboot.careercrafter.repository.EmployerRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserService userService;
    private final AdminMapper adminMapper;
    private final AdminRepository adminRepository;
    private final EmployerRepository employerRepository;

    public CreatedRespDto add(@Valid AdminReqDto adminReqDto) {
        User user = userService.createUser(adminReqDto.username(),adminReqDto.password(),Role.ADMIN);
        Admin admin = adminMapper.mapDtoToEntity(adminReqDto);
        admin.setUser(user);
        admin = adminRepository.save(admin);
        return new CreatedRespDto(admin.getId(),"Admin created");
    }

    public void verifyEmployer(Long adminId, Long employerId) {
        if (!adminRepository.existsById(adminId))
            throw new ResourceNotFoundException("Admin id invalid & Admin not found");

        Employer employer = employerRepository.findById(employerId)
                .orElseThrow(() -> new ResourceNotFoundException("Employer id invalid & Employer not found"));

        if (employer.getVerificationStatus() == VerificationStatus.VERIFIED)
            throw new InvalidCallException("Employer is already verified");

        employer.setVerificationStatus(VerificationStatus.VERIFIED);
        employerRepository.save(employer);
    }
}