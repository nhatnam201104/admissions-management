package com.example.managementadmissionwf.dto.statistic;

/**
 * DTO for statistics by admission method
 */
public record MethodStatistic(
    String phuongThuc,
    long total,
    long admitted,
    double rate
) {}