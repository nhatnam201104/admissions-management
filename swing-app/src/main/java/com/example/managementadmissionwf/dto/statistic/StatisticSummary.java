package com.example.managementadmissionwf.dto.statistic;

/**
 * DTO for overall statistic summary
 */
public record StatisticSummary(
    long totalCandidates,
    long totalAspirations,
    long admitted,
    long rejected,
    double admissionRate,
    double avgScore,
    double maxScore,
    double minScore
) {
    public static StatisticSummary empty() {
        return new StatisticSummary(0, 0, 0, 0, 0.0, 0.0, 0.0, 0.0);
    }
}