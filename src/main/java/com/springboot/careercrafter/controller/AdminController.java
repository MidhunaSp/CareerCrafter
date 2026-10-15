package com.springboot.careercrafter.controller;

import com.springboot.careercrafter.dto.request.AdminReqDto;
import com.springboot.careercrafter.dto.response.CreatedRespDto;
import com.springboot.careercrafter.service.AdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @PostMapping("/api/admin/add")
    public CreatedRespDto add(@Valid @RequestBody AdminReqDto adminReqDto) {
        return adminService.add(adminReqDto);
    }

    @PutMapping("/api/employer/verify/{adminId}/{employerId}")
    public void verifyEmployer(@PathVariable Long adminId,@PathVariable Long employerId) {
        adminService.verifyEmployer(adminId,employerId);
    }
}

// /api/employer/verify/1/1