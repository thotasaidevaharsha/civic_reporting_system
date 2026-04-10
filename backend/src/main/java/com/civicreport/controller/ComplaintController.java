package com.civicreport.controller;

import com.civicreport.dto.ComplaintCreateResponseDTO;
import com.civicreport.dto.ComplaintRequestDTO;
import com.civicreport.dto.ComplaintResponseDTO;
import com.civicreport.dto.UpdateComplaintDTO;
import com.civicreport.model.ComplaintStatus;
import com.civicreport.model.Priority;
import com.civicreport.service.ComplaintService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/complaints")
public class ComplaintController {

    private final ComplaintService complaintService;

    public ComplaintController(ComplaintService complaintService) {
        this.complaintService = complaintService;
    }

    @PostMapping
    public ResponseEntity<ComplaintCreateResponseDTO> createComplaint(
            @Valid @RequestBody ComplaintRequestDTO request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(complaintService.createComplaint(request));
    }

    @GetMapping("/{trackingId}")
    public ResponseEntity<ComplaintResponseDTO> getComplaintByTrackingId(@PathVariable String trackingId) {
        return ResponseEntity.ok(complaintService.getComplaintByTrackingId(trackingId));
    }

    @GetMapping
    public ResponseEntity<List<ComplaintResponseDTO>> getComplaints(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestParam(required = false) String zone,
            @RequestParam(required = false) ComplaintStatus status,
            @RequestParam(required = false) Priority priority
    ) {
        return ResponseEntity.ok(
                complaintService.getComplaintsForAdmin(authorizationHeader, zone, status, priority)
        );
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ComplaintResponseDTO> updateComplaint(
            @RequestHeader("Authorization") String authorizationHeader,
            @PathVariable Long id,
            @Valid @RequestBody UpdateComplaintDTO request
    ) {
        return ResponseEntity.ok(complaintService.updateComplaint(authorizationHeader, id, request));
    }
}
