package com.civicreport.dto;

import com.civicreport.model.Complaint;
import com.civicreport.model.ComplaintStatus;
import com.civicreport.model.Priority;

import java.time.LocalDateTime;

public record ComplaintResponseDTO(
        Long id,
        String trackingId,
        String reporterName,
        String reporterEmail,
        String zone,
        String issueName,
        String description,
        String locationAddress,
        String imageData,
        String assignedDepartment,
        Priority adminPriority,
        ComplaintStatus status,
        String adminNote,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static ComplaintResponseDTO fromEntity(Complaint complaint) {
        return new ComplaintResponseDTO(
                complaint.getId(),
                complaint.getTrackingId(),
                complaint.getReporterName(),
                complaint.getReporterEmail(),
                complaint.getZone(),
                complaint.getIssueName(),
                complaint.getDescription(),
                complaint.getLocationAddress(),
                complaint.getImageData(),
                complaint.getAssignedDepartment(),
                complaint.getAdminPriority(),
                complaint.getStatus(),
                complaint.getAdminNote(),
                complaint.getCreatedAt(),
                complaint.getUpdatedAt()
        );
    }
}
