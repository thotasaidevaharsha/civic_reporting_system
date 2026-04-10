package com.civicreport.service;

public record AdminSession(
        String token,
        String adminId,
        String adminName,
        String zone
) {
}
