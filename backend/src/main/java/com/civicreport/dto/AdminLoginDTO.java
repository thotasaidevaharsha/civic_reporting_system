package com.civicreport.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AdminLoginDTO(
        @NotBlank(message = "zoneId is required")
        @Size(max = 120, message = "zoneId must be at most 120 characters")
        String zoneId,

        @NotBlank(message = "adminId is required")
        @Size(max = 64, message = "adminId must be at most 64 characters")
        String adminId,

        @NotBlank(message = "password is required")
        @Size(max = 120, message = "password must be at most 120 characters")
        String password
) {
}
