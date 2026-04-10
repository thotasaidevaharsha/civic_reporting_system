package com.civicreport.service;

import com.civicreport.dto.ComplaintCreateResponseDTO;
import com.civicreport.dto.ComplaintRequestDTO;
import com.civicreport.dto.ComplaintResponseDTO;
import com.civicreport.dto.UpdateComplaintDTO;
import com.civicreport.model.ComplaintStatus;
import com.civicreport.model.Priority;

import java.util.List;

public interface ComplaintService {
    ComplaintCreateResponseDTO createComplaint(ComplaintRequestDTO request);

    ComplaintResponseDTO getComplaintByTrackingId(String trackingId);

    List<ComplaintResponseDTO> getComplaintsForAdmin(
            String authorizationHeader,
            String zone,
            ComplaintStatus status,
            Priority priority
    );

    ComplaintResponseDTO updateComplaint(String authorizationHeader, Long id, UpdateComplaintDTO request);
}
