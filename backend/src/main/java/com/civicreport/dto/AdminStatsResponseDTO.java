package com.civicreport.dto;

public record AdminStatsResponseDTO(
        long total,
        long pending,
        long resolved,
        long highPriority
) {
}
