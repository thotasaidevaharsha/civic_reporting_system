package com.civicreport.controller;

import com.civicreport.dto.AdminLoginDTO;
import com.civicreport.dto.AdminLoginResponseDTO;
import com.civicreport.dto.AdminStatsResponseDTO;
import com.civicreport.service.AdminService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @PostMapping("/login")
    public ResponseEntity<AdminLoginResponseDTO> login(@Valid @RequestBody AdminLoginDTO request) {
        return ResponseEntity.ok(adminService.login(request));
    }

    @GetMapping("/stats/{zone}")
    public ResponseEntity<AdminStatsResponseDTO> getZoneStats(
            @RequestHeader("Authorization") String authorizationHeader,
            @PathVariable String zone
    ) {
        return ResponseEntity.ok(adminService.getZoneStats(authorizationHeader, zone));
    }
}
