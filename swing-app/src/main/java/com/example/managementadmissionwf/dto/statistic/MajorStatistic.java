package com.example.managementadmissionwf.dto.statistic;

/**
 * DTO for statistics by major
 */
public record MajorStatistic(
    String manganh,
    String tennganh,
    long totalAspirations,
    long admitted,           // Số nguyện vọng trúng tuyển
    long admittedStudents,   // Số thí sinh trúng tuyển (unique CCCD)
    int target,
    double avgScore,
    double fillRate
) {}
