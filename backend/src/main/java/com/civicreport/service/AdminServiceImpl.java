package com.civicreport.service;

import com.civicreport.dto.AdminLoginDTO;
import com.civicreport.dto.AdminLoginResponseDTO;
import com.civicreport.dto.AdminStatsResponseDTO;
import com.civicreport.exception.UnauthorizedException;
import com.civicreport.model.Admin;
import com.civicreport.model.Complaint;
import com.civicreport.model.ComplaintStatus;
import com.civicreport.model.Priority;
import com.civicreport.repository.AdminRepository;
import com.civicreport.repository.ComplaintRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class AdminServiceImpl implements AdminService {

    private final AdminRepository adminRepository;
    private final ComplaintRepository complaintRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final Map<String, AdminSession> sessions;

    public AdminServiceImpl(
            AdminRepository adminRepository,
            ComplaintRepository complaintRepository,
            BCryptPasswordEncoder passwordEncoder
    ) {
        this.adminRepository = adminRepository;
        this.complaintRepository = complaintRepository;
        this.passwordEncoder = passwordEncoder;
        this.sessions = new ConcurrentHashMap<>();
    }

    @Override
    public AdminLoginResponseDTO login(AdminLoginDTO request) {
        Admin admin = adminRepository.findByZoneIgnoreCaseAndAdminIdIgnoreCase(
                        request.zoneId().trim(),
                        request.adminId().trim()
                )
                .orElseThrow(() -> new UnauthorizedException("Invalid zone, admin ID, or password"));

        if (!passwordEncoder.matches(request.password(), admin.getPassword())) {
            throw new UnauthorizedException("Invalid zone, admin ID, or password");
        }

        String token = UUID.randomUUID().toString();
        AdminSession session = new AdminSession(token, admin.getAdminId(), admin.getName(), admin.getZone());
        sessions.put(token, session);

        return new AdminLoginResponseDTO(token, admin.getName(), admin.getZone());
    }

    @Override
    public AdminSession validateToken(String authorizationHeader) {
        if (authorizationHeader == null || authorizationHeader.isBlank()) {
            throw new UnauthorizedException("Missing Authorization header");
        }
        if (!authorizationHeader.startsWith("Bearer ")) {
            throw new UnauthorizedException("Invalid Authorization header format");
        }

        String token = authorizationHeader.substring("Bearer ".length()).trim();
        if (token.isBlank()) {
            throw new UnauthorizedException("Authentication token is missing");
        }

        AdminSession session = sessions.get(token);
        if (session == null) {
            throw new UnauthorizedException("Authentication token is invalid or expired");
        }
        return session;
    }

    @Override
    public String resolveAuthorizedZone(String authorizationHeader, String requestedZone) {
        AdminSession session = validateToken(authorizationHeader);
        if (requestedZone == null || requestedZone.isBlank()) {
            return session.zone();
        }
        if (!session.zone().equalsIgnoreCase(requestedZone.trim())) {
            throw new UnauthorizedException("You are not allowed to access complaints outside your zone");
        }
        return session.zone();
    }

    @Override
    public AdminStatsResponseDTO getZoneStats(String authorizationHeader, String zone) {
        String authorizedZone = resolveAuthorizedZone(authorizationHeader, zone);
        List<Complaint> complaints = complaintRepository.findByZoneIgnoreCase(authorizedZone);

        long pending = complaints.stream()
                .filter(complaint -> complaint.getStatus() == ComplaintStatus.PENDING)
                .count();
        long resolved = complaints.stream()
                .filter(complaint -> complaint.getStatus() == ComplaintStatus.RESOLVED)
                .count();
        long highPriority = complaints.stream()
                .filter(complaint -> effectivePriority(complaint) == Priority.HIGH)
                .count();

        return new AdminStatsResponseDTO(complaints.size(), pending, resolved, highPriority);
    }

    private Priority effectivePriority(Complaint complaint) {
        return complaint.getAdminPriority();
    }
}
