package com.civicreport.dto;

public record ErrorResponseDTO(
        String error,
        int status
) {
}
