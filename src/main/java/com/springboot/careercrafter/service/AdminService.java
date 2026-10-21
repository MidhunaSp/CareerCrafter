package com.springboot.careercrafter.service;

import com.springboot.careercrafter.dto.request.AdminReqDto;
import com.springboot.careercrafter.dto.response.CreatedRespDto;
import com.springboot.careercrafter.enums.NotificationType;
import com.springboot.careercrafter.enums.Role;
import com.springboot.careercrafter.enums.VerificationStatus;
import com.springboot.careercrafter.exception.InvalidCallException;
import com.springboot.careercrafter.exception.OwnershipException;
import com.springboot.careercrafter.exception.ResourceNotFoundException;
import com.springboot.careercrafter.mapper.AdminMapper;
import com.springboot.careercrafter.model.Admin;
import com.springboot.careercrafter.model.Employer;
import com.springboot.careercrafter.model.User;
import com.springboot.careercrafter.repository.AdminRepository;
import com.springboot.careercrafter.repository.EmployerRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserService userService;
    private final AdminMapper adminMapper;
    private final AdminRepository adminRepository;
    private final EmployerRepository employerRepository;
    private final NotificationService notificationService;

    public CreatedRespDto add(@Valid AdminReqDto adminReqDto) {
        User user = userService.createUser(adminReqDto.username(),adminReqDto.password(),Role.ADMIN);
        Admin admin = adminMapper.mapDtoToEntity(adminReqDto);
        admin.setUser(user);
        admin = adminRepository.save(admin);
        return new CreatedRespDto(admin.getId(),"Admin created");
    }

    public void verifyEmployer(Long adminId, Long employerId, String loggedInUsername) {
        // step 1: fetch admin from db using adminId
        Admin admin = adminRepository.findById(adminId)
                .orElseThrow(() -> new ResourceNotFoundException("Admin id invalid & Admin not found"));

        // step 2: check that the logged-in user is this admin
        if (!admin.getUser().getUsername().equals(loggedInUsername))
            throw new OwnershipException("You can verify employers only with your own admin id");

        // step 3: fetch employer from db using employerId
        Employer employer = employerRepository.findById(employerId)
                .orElseThrow(() -> new ResourceNotFoundException("Employer id invalid & Employer not found"));

        // step 4: an employer cannot be verified twice
        if (employer.getVerificationStatus() == VerificationStatus.VERIFIED)
            throw new InvalidCallException("Employer is already verified");

        // step 5: mark as verified and save
        employer.setVerificationStatus(VerificationStatus.VERIFIED);
        employerRepository.save(employer);
        log.info("Admin {} verified employer {} ({})", loggedInUsername, employerId, employer.getCompanyName());

        // step 6: notify employer that his/her acc is verified and they can post jobs
        notificationService.notifyUser(employer.getUser(),
                "Your employer account for " + employer.getCompanyName()
                        + " has been verified. You can now post jobs.",
                NotificationType.SYSTEM);
    }
}