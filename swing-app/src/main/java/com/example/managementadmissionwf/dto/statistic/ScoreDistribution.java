package com.example.managementadmissionwf.dto.statistic;

/**
 * DTO for score distribution
 */
public record ScoreDistribution(
    String range,
    long count,
    double percentage
) {}