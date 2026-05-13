package com.example.managementadmissionwf.bus.interfaces;

import com.example.managementadmissionwf.dto.statistic.MajorStatistic;
import com.example.managementadmissionwf.dto.statistic.MethodStatistic;
import com.example.managementadmissionwf.dto.statistic.ScoreDistribution;
import com.example.managementadmissionwf.dto.statistic.StatisticSummary;

import java.util.List;

/**
 * Service interface for statistics operations
 */
public interface StatisticService {
    
    /**
     * Get overall summary statistics
     */
    StatisticSummary getSummary();
    
    /**
     * Get statistics by major (top 10)
     */
    List<MajorStatistic> getMajorStatistics();
    
    /**
     * Get statistics by admission method
     */
    List<MethodStatistic> getMethodStatistics();
    
    /**
     * Get score distribution (5 ranges)
     */
    List<ScoreDistribution> getScoreDistribution();
}