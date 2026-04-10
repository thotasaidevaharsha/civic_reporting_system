package com.civicreport.service;

import com.civicreport.dto.ComplaintCreateResponseDTO;
import com.civicreport.dto.ComplaintRequestDTO;
import com.civicreport.dto.ComplaintResponseDTO;
import com.civicreport.dto.UpdateComplaintDTO;
import com.civicreport.exception.ResourceNotFoundException;
import com.civicreport.exception.UnauthorizedException;
import com.civicreport.model.Complaint;
import com.civicreport.model.ComplaintStatus;
import com.civicreport.model.Priority;
import com.civicreport.repository.ComplaintRepository;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ComplaintServiceImpl implements ComplaintService {

    private final ComplaintRepository complaintRepository;
    private final AdminService adminService;

    public ComplaintServiceImpl(ComplaintRepository complaintRepository, AdminService adminService) {
        this.complaintRepository = complaintRepository;
        this.adminService = adminService;
    }

    @Override
    public ComplaintCreateResponseDTO createComplaint(ComplaintRequestDTO request) {
        Complaint complaint = new Complaint();
        complaint.setReporterName(request.reporterName().trim());
        complaint.setReporterEmail(blankToNull(request.reporterEmail()));
        complaint.setZone(request.zone().trim());
        complaint.setIssueName(request.issueName().trim());
        complaint.setDescription(request.description().trim());
        complaint.setLocationAddress(request.locationAddress().trim());
        complaint.setImageData(blankToNull(request.imageData()));
        complaint.setAdminPriority(Priority.MEDIUM);
        complaint.setStatus(ComplaintStatus.PENDING);

        Complaint saved = complaintRepository.save(complaint);
        return new ComplaintCreateResponseDTO(saved.getTrackingId(), "Complaint submitted successfully");
    }

    @Override
    public ComplaintResponseDTO getComplaintByTrackingId(String trackingId) {
        Complaint complaint = complaintRepository.findByTrackingIdIgnoreCase(trackingId.trim())
                .orElseThrow(() -> new ResourceNotFoundException("Complaint not found for tracking ID: " + trackingId));

        return ComplaintResponseDTO.fromEntity(complaint);
    }

    @Override
    public List<ComplaintResponseDTO> getComplaintsForAdmin(
            String authorizationHeader,
            String zone,
            ComplaintStatus status,
            Priority priority
    ) {
        String authorizedZone = adminService.resolveAuthorizedZone(authorizationHeader, zone);

        Specification<Complaint> spec = zoneEquals(authorizedZone);
        if (status != null) {
            spec = spec.and(statusEquals(status));
        }
        if (priority != null) {
            spec = spec.and(priorityEquals(priority));
        }

        return complaintRepository.findAll(spec, Sort.by(Sort.Direction.DESC, "createdAt"))
                .stream()
                .map(ComplaintResponseDTO::fromEntity)
                .toList();
    }

    @Override
    public ComplaintResponseDTO updateComplaint(String authorizationHeader, Long id, UpdateComplaintDTO request) {
        AdminSession session = adminService.validateToken(authorizationHeader);
        Complaint complaint = complaintRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Complaint not found for id: " + id));

        if (!complaint.getZone().equalsIgnoreCase(session.zone())) {
            throw new UnauthorizedException("You are not allowed to update complaints outside your zone");
        }

        complaint.setAssignedDepartment(request.assignedDepartment().trim());
        complaint.setAdminPriority(request.adminPriority());
        complaint.setStatus(request.status());
        complaint.setAdminNote(blankToNull(request.adminNote()));

        Complaint saved = complaintRepository.save(complaint);
        return ComplaintResponseDTO.fromEntity(saved);
    }

    private String blankToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isBlank() ? null : trimmed;
    }

    private Specification<Complaint> zoneEquals(String zone) {
        return (root, query, builder) -> builder.equal(builder.lower(root.get("zone")), zone.toLowerCase());
    }

    private Specification<Complaint> statusEquals(ComplaintStatus status) {
        return (root, query, builder) -> builder.equal(root.get("status"), status);
    }

    private Specification<Complaint> priorityEquals(Priority priority) {
        return (root, query, builder) -> builder.equal(root.get("adminPriority"), priority);
    }
}
