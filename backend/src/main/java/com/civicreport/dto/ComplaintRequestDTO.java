package com.civicreport.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ComplaintRequestDTO(
        @NotBlank(message = "Reporter name is required")
        @Size(max = 120, message = "Reporter name must be at most 120 characters")
        String reporterName,

        @Email(message = "Reporter email must be a valid email address")
        @Size(max = 120, message = "Reporter email must be at most 120 characters")
        String reporterEmail,

        @NotBlank(message = "Zone is required")
        @Size(max = 120, message = "Zone must be at most 120 characters")
        String zone,

        @NotBlank(message = "Issue name is required")
        @Size(max = 180, message = "Issue name must be at most 180 characters")
        String issueName,

        @NotBlank(message = "Description is required")
        @Size(min = 20, max = 2000, message = "Description must be between 20 and 2000 characters")
        String description,

        @NotBlank(message = "Location address is required")
        @Size(max = 255, message = "Location address must be at most 255 characters")
        String locationAddress,

        @Size(max = 3000000, message = "Image data is too large")
        String imageData
) {
}
