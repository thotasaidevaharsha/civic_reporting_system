package com.civicreport.service;

import com.civicreport.dto.AdminLoginDTO;
import com.civicreport.dto.AdminLoginResponseDTO;
import com.civicreport.dto.AdminStatsResponseDTO;

public interface AdminService {
    AdminLoginResponseDTO login(AdminLoginDTO request);

    AdminSession validateToken(String authorizationHeader);

    String resolveAuthorizedZone(String authorizationHeader, String requestedZone);

    AdminStatsResponseDTO getZoneStats(String authorizationHeader, String zone);
}
