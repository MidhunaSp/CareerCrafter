package com.springboot.careercrafter.controller;

import com.springboot.careercrafter.dto.request.AdminReqDto;
import com.springboot.careercrafter.dto.response.CreatedRespDto;
import com.springboot.careercrafter.service.AdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @PostMapping("/api/admin/add")
    public CreatedRespDto add(@Valid @RequestBody AdminReqDto adminReqDto) {
        return adminService.add(adminReqDto);
    }

    @PutMapping("/api/employer/verify/{adminId}/{employerId}")
    public void verifyEmployer(@PathVariable Long adminId,
                               @PathVariable Long employerId,
                               Principal principal) {
        adminService.verifyEmployer(adminId, employerId, principal.getName());
    }

    // yer/verify/1/1
}