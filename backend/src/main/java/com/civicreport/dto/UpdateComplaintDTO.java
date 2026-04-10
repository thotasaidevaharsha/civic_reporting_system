package com.civicreport.dto;

import com.civicreport.model.ComplaintStatus;
import com.civicreport.model.Priority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UpdateComplaintDTO(
        @NotBlank(message = "Assigned department is required")
        @Size(max = 120, message = "Assigned department must be at most 120 characters")
        String assignedDepartment,

        @NotNull(message = "Admin priority is required")
        Priority adminPriority,

        @NotNull(message = "Status is required")
        ComplaintStatus status,

        @Size(max = 1000, message = "Admin note must be at most 1000 characters")
        String adminNote
) {
}
