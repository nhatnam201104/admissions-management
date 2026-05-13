package com.example.managementadmissionwf.ui.component;

import com.example.managementadmissionwf.bus.interfaces.StatisticService;
import com.example.managementadmissionwf.dto.statistic.MajorStatistic;
import com.example.managementadmissionwf.dto.statistic.MethodStatistic;
import com.example.managementadmissionwf.dto.statistic.ScoreDistribution;
import com.example.managementadmissionwf.dto.statistic.StatisticSummary;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Controller for StatisticPanel
 * Handles data loading and chart updates
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class StatisticController {
    
    private final StatisticService statisticService;
    
    /**
     * Load all statistics data
     */
    public StatisticSummary loadSummary() {
        try {
            return statisticService.getSummary();
        } catch (Exception e) {
            log.error("Error loading summary", e);
            return StatisticSummary.empty();
        }
    }
    
    /**
     * Load major statistics for bar chart
     */
    public List<MajorStatistic> loadMajorStatistics() {
        try {
            return statisticService.getMajorStatistics();
        } catch (Exception e) {
            log.error("Error loading major statistics", e);
            return List.of();
        }
    }
    
    /**
     * Load method statistics for pie chart
     */
    public List<MethodStatistic> loadMethodStatistics() {
        try {
            return statisticService.getMethodStatistics();
        } catch (Exception e) {
            log.error("Error loading method statistics", e);
            return List.of();
        }
    }
    
    /**
     * Load score distribution for histogram
     */
    public List<ScoreDistribution> loadScoreDistribution() {
        try {
            return statisticService.getScoreDistribution();
        } catch (Exception e) {
            log.error("Error loading score distribution", e);
            return List.of();
        }
    }
}