package com.example.managementadmissionwf.bus.impl;

import com.example.managementadmissionwf.bus.interfaces.StatisticService;
import com.example.managementadmissionwf.dto.statistic.MajorStatistic;
import com.example.managementadmissionwf.dto.statistic.MethodStatistic;
import com.example.managementadmissionwf.dto.statistic.ScoreDistribution;
import com.example.managementadmissionwf.dto.statistic.StatisticSummary;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of StatisticService
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class StatisticServiceImpl implements StatisticService {
    
    @PersistenceContext
    private EntityManager entityManager;
    
    @Override
    public StatisticSummary getSummary() {
        try {
            // Total candidates (distinct CCCD)
            Long totalCandidates = ((Number) entityManager
                .createQuery("SELECT COUNT(DISTINCT nv.nnCccd) FROM XtNguyenvongxettuyen nv WHERE nv.isDeleted = false")
                .getSingleResult()).longValue();
            
            // Total aspirations
            Long totalAspirations = ((Number) entityManager
                .createQuery("SELECT COUNT(nv) FROM XtNguyenvongxettuyen nv WHERE nv.isDeleted = false")
                .getSingleResult()).longValue();
            
            // Admitted aspirations count
            Long admittedAspirations = ((Number) entityManager
                .createQuery("SELECT COUNT(nv) FROM XtNguyenvongxettuyen nv WHERE nv.isDeleted = false AND nv.nvKetqua = 'TRUNG_TUYEN'")
                .getSingleResult()).longValue();
            
            // Admitted students count (unique CCCD with TRUNG_TUYEN)
            Long admittedStudents = ((Number) entityManager
                .createQuery("SELECT COUNT(DISTINCT nv.nnCccd) FROM XtNguyenvongxettuyen nv WHERE nv.isDeleted = false AND nv.nvKetqua = 'TRUNG_TUYEN'")
                .getSingleResult()).longValue();
            
            // Rejected count
            Long rejected = ((Number) entityManager
                .createQuery("SELECT COUNT(nv) FROM XtNguyenvongxettuyen nv WHERE nv.isDeleted = false AND nv.nvKetqua = 'TRUOT'")
                .getSingleResult()).longValue();
            
            // Average score
            Double avgScore = ((Number) entityManager
                .createQuery("SELECT AVG(nv.diemXettuyen) FROM XtNguyenvongxettuyen nv WHERE nv.isDeleted = false AND nv.diemXettuyen IS NOT NULL")
                .getSingleResult()).doubleValue();
            
            // Max score
            Double maxScore = ((Number) entityManager
                .createQuery("SELECT MAX(nv.diemXettuyen) FROM XtNguyenvongxettuyen nv WHERE nv.isDeleted = false AND nv.diemXettuyen IS NOT NULL")
                .getSingleResult()).doubleValue();
            
            // Min score
            Double minScore = ((Number) entityManager
                .createQuery("SELECT MIN(nv.diemXettuyen) FROM XtNguyenvongxettuyen nv WHERE nv.isDeleted = false AND nv.diemXettuyen IS NOT NULL")
                .getSingleResult()).doubleValue();
            
            // Calculate admission rate: admitted / (admitted + rejected) only for evaluated students
            // Avoid counting CHO_XET students in denominator
            long evaluatedStudents = admittedStudents + rejected;
            double admissionRate = evaluatedStudents > 0 
                ? (((double) admittedStudents) / evaluatedStudents) * 100 
                : 0.0;
            
            return new StatisticSummary(
                totalCandidates,
                totalAspirations,
                admittedStudents,  // Use students count for display
                rejected,
                round(admissionRate, 1),
                round(avgScore, 1),
                round(maxScore, 1),
                round(minScore, 1)
            );
        } catch (Exception e) {
            log.error("Error getting summary", e);
            return StatisticSummary.empty();
        }
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public List<MajorStatistic> getMajorStatistics() {
        try {
            List<Object[]> results = entityManager
                .createQuery("""
                    SELECT n.manganh, n.tennganh, 
                           COUNT(nv.id) as total,
                           SUM(CASE WHEN nv.nvKetqua = 'TRUNG_TUYEN' THEN 1 ELSE 0 END) as admitted,
                           n.nChitieu as target,
                           AVG(nv.diemXettuyen) as avgScore
                    FROM XtNguyenvongxettuyen nv
                    JOIN nv.nganh n
                    WHERE nv.isDeleted = false
                    GROUP BY n.manganh, n.tennganh, n.nChitieu
                    ORDER BY total DESC
                    """, Object[].class)
                .setMaxResults(10)
                .getResultList();
            
            List<MajorStatistic> stats = new ArrayList<>();
            for (Object[] row : results) {
                String manganh = (String) row[0];
                String tennganh = (String) row[1];
                long total = ((Number) row[2]).longValue();
                long admitted = ((Number) row[3]).longValue();
                int target = row[4] != null ? ((Number) row[4]).intValue() : 0;
                double avgScore = row[5] != null ? ((Number) row[5]).doubleValue() : 0.0;
                double fillRate = target > 0 ? (((double) admitted) / target) * 100 : 0.0;
                
                // Count unique students admitted to this major
                Long admittedStudents = ((Number) entityManager
                    .createQuery("SELECT COUNT(DISTINCT nv.nnCccd) FROM XtNguyenvongxettuyen nv " +
                                 "WHERE nv.isDeleted = false AND nv.nvManganh = :manganh AND nv.nvKetqua = 'TRUNG_TUYEN'")
                    .setParameter("manganh", manganh)
                    .getSingleResult()).longValue();
                
                stats.add(new MajorStatistic(
                    manganh, tennganh, total, admitted, admittedStudents, target,
                    round(avgScore, 1), round(fillRate, 1)
                ));
            }
            return stats;
        } catch (Exception e) {
            log.error("Error getting major statistics", e);
            return new ArrayList<>();
        }
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public List<MethodStatistic> getMethodStatistics() {
        try {
            List<Object[]> results = entityManager
                .createQuery("""
                    SELECT nv.ttPhuongthuc as phuongThuc,
                           COUNT(nv) as total,
                           SUM(CASE WHEN nv.nvKetqua = 'TRUNG_TUYEN' THEN 1 ELSE 0 END) as admitted
                    FROM XtNguyenvongxettuyen nv
                    WHERE nv.isDeleted = false AND nv.ttPhuongthuc IS NOT NULL
                    GROUP BY nv.ttPhuongthuc
                    ORDER BY total DESC
                    """, Object[].class)
                .getResultList();
            
            List<MethodStatistic> stats = new ArrayList<>();
            for (Object[] row : results) {
                String phuongThuc = (String) row[0];
                long total = ((Number) row[1]).longValue();
                long admitted = ((Number) row[2]).longValue();
                double rate = total > 0 ? (((double) admitted) / total) * 100 : 0.0;
                
                stats.add(new MethodStatistic(phuongThuc, total, admitted, round(rate, 1)));
            }
            return stats;
        } catch (Exception e) {
            log.error("Error getting method statistics", e);
            return new ArrayList<>();
        }
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public List<ScoreDistribution> getScoreDistribution() {
        try {
            // Get total count for percentage calculation
            Long total = ((Number) entityManager
                .createQuery("SELECT COUNT(nv) FROM XtNguyenvongxettuyen nv WHERE nv.isDeleted = false AND nv.diemXettuyen IS NOT NULL")
                .getSingleResult()).longValue();
            
            if (total == 0) {
                return List.of(
                    new ScoreDistribution("27+", 0, 0),
                    new ScoreDistribution("24-26.9", 0, 0),
                    new ScoreDistribution("21-23.9", 0, 0),
                    new ScoreDistribution("18-20.9", 0, 0),
                    new ScoreDistribution("15-17.9", 0, 0),
                    new ScoreDistribution("0-14.9", 0, 0)
                );
            }
            
            // Get counts by range - starting from 0
            List<Object[]> results = entityManager
                .createQuery("""
                    SELECT 
                        CASE
                            WHEN nv.diemXettuyen >= 27 THEN '27+'
                            WHEN nv.diemXettuyen >= 24 THEN '24-26.9'
                            WHEN nv.diemXettuyen >= 21 THEN '21-23.9'
                            WHEN nv.diemXettuyen >= 18 THEN '18-20.9'
                            WHEN nv.diemXettuyen >= 15 THEN '15-17.9'
                            ELSE '0-14.9'
                        END as range,
                        COUNT(nv) as count
                    FROM XtNguyenvongxettuyen nv
                    WHERE nv.isDeleted = false AND nv.diemXettuyen IS NOT NULL
                    GROUP BY range
                    ORDER BY range DESC
                    """, Object[].class)
                .getResultList();
            
            // Build complete list with all ranges - start from 0
            java.util.Map<String, Long> countMap = new java.util.LinkedHashMap<>();
            countMap.put("27+", 0L);
            countMap.put("24-26.9", 0L);
            countMap.put("21-23.9", 0L);
            countMap.put("18-20.9", 0L);
            countMap.put("15-17.9", 0L);
            countMap.put("0-14.9", 0L);
            
            for (Object[] row : results) {
                String range = (String) row[0];
                long count = ((Number) row[1]).longValue();
                countMap.put(range, count);
            }
            
            List<ScoreDistribution> distributions = new ArrayList<>();
            for (java.util.Map.Entry<String, Long> entry : countMap.entrySet()) {
            double percentage = (((double) entry.getValue()) / total) * 100;
                distributions.add(new ScoreDistribution(entry.getKey(), entry.getValue(), round(percentage, 1)));
            }
            
            return distributions;
        } catch (Exception e) {
            log.error("Error getting score distribution", e);
            return new ArrayList<>();
        }
    }
    
    private double round(Double value, int places) {
        if (value == null) return 0.0;
        return BigDecimal.valueOf(value).setScale(places, RoundingMode.HALF_UP).doubleValue();
    }
}