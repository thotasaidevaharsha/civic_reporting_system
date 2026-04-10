package com.civicreport.dto;

public record AdminLoginResponseDTO(
        String token,
        String adminName,
        String zone
) {
}
