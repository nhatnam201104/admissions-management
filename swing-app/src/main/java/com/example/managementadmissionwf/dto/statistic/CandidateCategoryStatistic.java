package com.example.managementadmissionwf.dto.statistic;

/**
 * Candidate count grouped by one candidate attribute, such as region or priority object.
 */
public record CandidateCategoryStatistic(
        String label,
        long total
) {
}
