package com.example.managementadmissionwf.bus.impl;

import com.example.managementadmissionwf.bus.interfaces.StatisticService;
import com.example.managementadmissionwf.dal.entity.XtNganh;
import com.example.managementadmissionwf.dal.repository.CandidateRepository;
import com.example.managementadmissionwf.dal.repository.MajorRepository;
import com.example.managementadmissionwf.dal.repository.NguyenVongRepository;
import com.example.managementadmissionwf.dto.statistic.CandidateCategoryStatistic;
import com.example.managementadmissionwf.dto.statistic.MajorMethodAdmissionStat;
import com.example.managementadmissionwf.dto.statistic.MajorStatistic;
import com.example.managementadmissionwf.dto.statistic.MethodStatistic;
import com.example.managementadmissionwf.dto.statistic.ScoreDistribution;
import com.example.managementadmissionwf.dto.statistic.StatisticSummary;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Implementation of {@link StatisticService}.
 *
 * <p>Refactored: tất cả truy vấn JPQL trước đây inline tại đây đã được chuyển
 * thành phương thức {@code @Query} trong {@link NguyenVongRepository} và
 * {@link CandidateRepository}. Service hiện chỉ còn việc gọi repository và map
 * sang DTO, dễ test bằng Mockito mà không cần boot context.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class StatisticServiceImpl implements StatisticService {

    private final NguyenVongRepository nguyenVongRepository;
    private final CandidateRepository candidateRepository;
    private final MajorRepository majorRepository;

    @Override
    public StatisticSummary getSummary() {
        try {
            long totalCandidates = candidateRepository.countActive();
            long totalAspirations = nguyenVongRepository.countAllActive();
            long admittedStudents = nguyenVongRepository.countAdmittedStudents();
            long rejected = nguyenVongRepository.countRejected();
            double avgScore = nullSafe(nguyenVongRepository.averageScore());
            double maxScore = nullSafe(nguyenVongRepository.maxScore());
            double minScore = nullSafe(nguyenVongRepository.minScore());

            long evaluatedStudents = admittedStudents + rejected;
            double admissionRate = evaluatedStudents > 0
                    ? (((double) admittedStudents) / evaluatedStudents) * 100
                    : 0.0;

            return new StatisticSummary(
                    totalCandidates,
                    totalAspirations,
                    admittedStudents,
                    rejected,
                    round(admissionRate, 1),
                    round(avgScore, 1),
                    round(maxScore, 1),
                    round(minScore, 1));
        } catch (Exception e) {
            log.error("Error getting summary", e);
            return StatisticSummary.empty();
        }
    }

    @Override
    public List<CandidateCategoryStatistic> getCandidateStatisticsByDoiTuong() {
        return mapCategory(safeList(candidateRepository.countByDoiTuong()));
    }

    @Override
    public List<CandidateCategoryStatistic> getCandidateStatisticsByKhuVuc() {
        return mapCategory(safeList(candidateRepository.countByKhuVuc()));
    }

    private List<CandidateCategoryStatistic> mapCategory(List<Object[]> rows) {
        List<CandidateCategoryStatistic> stats = new ArrayList<>();
        for (Object[] row : rows) {
            String label = normalizeCategory((String) row[0]);
            long total = ((Number) row[1]).longValue();
            stats.add(new CandidateCategoryStatistic(label, total));
        }
        return stats;
    }

    private String normalizeCategory(String value) {
        return value == null || value.isBlank() ? "Chưa có" : value.trim();
    }

    @Override
    public List<MajorStatistic> getMajorStatistics() {
        try {
            List<Object[]> rows = nguyenVongRepository.topMajorStatistics();
            int limit = Math.min(rows.size(), 10);

            List<MajorStatistic> stats = new ArrayList<>();
            for (int i = 0; i < limit; i++) {
                Object[] row = rows.get(i);
                String manganh = (String) row[0];
                String tennganh = (String) row[1];
                long total = ((Number) row[2]).longValue();
                long admitted = ((Number) row[3]).longValue();
                int target = row[4] != null ? ((Number) row[4]).intValue() : 0;
                double avgScore = row[5] != null ? ((Number) row[5]).doubleValue() : 0.0;
                long admittedStudents = nguyenVongRepository.countAdmittedStudentsByMajor(manganh);
                double fillRate = target > 0 ? (((double) admitted) / target) * 100 : 0.0;

                stats.add(new MajorStatistic(
                        manganh, tennganh, total, admitted, admittedStudents, target,
                        round(avgScore, 1), round(fillRate, 1)));
            }
            return stats;
        } catch (Exception e) {
            log.error("Error getting major statistics", e);
            return new ArrayList<>();
        }
    }

    @Override
    public List<MethodStatistic> getMethodStatistics() {
        try {
            List<Object[]> rows = nguyenVongRepository.methodStatistics();
            List<MethodStatistic> stats = new ArrayList<>();
            for (Object[] row : rows) {
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
    public List<ScoreDistribution> getScoreDistribution() {
        try {
            long total = nguyenVongRepository.countDistinctScoredCandidates();
            Map<String, Long> countMap = new LinkedHashMap<>();
            countMap.put("27+", 0L);
            countMap.put("24-26.9", 0L);
            countMap.put("21-23.9", 0L);
            countMap.put("18-20.9", 0L);
            countMap.put("15-17.9", 0L);
            countMap.put("0-14.9", 0L);

            if (total > 0) {
                for (Object[] row : nguyenVongRepository.scoreDistribution()) {
                    countMap.put((String) row[0], ((Number) row[1]).longValue());
                }
            }

            List<ScoreDistribution> distributions = new ArrayList<>();
            for (Map.Entry<String, Long> entry : countMap.entrySet()) {
                double percentage = total > 0 ? (((double) entry.getValue()) / total) * 100 : 0.0;
                distributions.add(new ScoreDistribution(entry.getKey(), entry.getValue(), round(percentage, 1)));
            }
            return distributions;
        } catch (Exception e) {
            log.error("Error getting score distribution", e);
            return new ArrayList<>();
        }
    }

    @Override
    public List<MajorMethodAdmissionStat> getMajorMethodMatrix() {
        try {
            // Lưu cả tổng chỉ tiêu (n_chitieu) và chỉ tiêu theo phương thức
            // (sl_*). LƯU Ý: trong project hiện tại các cột sl_* được
            // MajorServiceImpl ghi đè bằng "đếm số NV đăng ký" mỗi khi cập
            // nhật, nên không phải lúc nào cũng là quota thật. Vì vậy DTO ngoài
            // cùng giữ cả 2 giá trị: methodQuota có thể không chính xác — UI có
            // thể fallback về totalQuota để tính tỉ lệ lấp đầy.
            Map<String, int[]> quotaByMajor = new LinkedHashMap<>();
            Map<String, Map<String, Integer>> quotaByMajorMethod = new LinkedHashMap<>();
            for (XtNganh major : majorRepository.findAll()) {
                int totalQuota = nullSafe(major.getNChitieu());
                quotaByMajor.put(major.getManganh(), new int[]{totalQuota});
                Map<String, Integer> quotas = new LinkedHashMap<>();
                quotas.put("THPT", nullSafe(major.getSlThpt()));
                quotas.put("DGNL", nullSafe(major.getSlDgnl()));
                quotas.put("VSAT", nullSafe(major.getSlVsat()));
                quotas.put("TUYEN_THANG", nullSafe(major.getSlXtt()));
                quotaByMajorMethod.put(major.getManganh(), quotas);
            }

            List<MajorMethodAdmissionStat> result = new ArrayList<>();
            for (Object[] row : nguyenVongRepository.majorMethodMatrix()) {
                String manganh = (String) row[0];
                String tennganh = (String) row[1];
                String phuongThuc = (String) row[2];
                long totalApply = ((Number) row[3]).longValue();
                long admitted = ((Number) row[4]).longValue();
                int methodQuota = quotaByMajorMethod
                        .getOrDefault(manganh, Map.of())
                        .getOrDefault(phuongThuc, 0);
                int totalQuota = quotaByMajor.getOrDefault(manganh, new int[]{0})[0];
                result.add(new MajorMethodAdmissionStat(manganh, tennganh, phuongThuc,
                        totalQuota, methodQuota, admitted, totalApply));
            }
            return result;
        } catch (Exception e) {
            log.error("Error getting major-method matrix", e);
            return new ArrayList<>();
        }
    }


    private double round(Double value, int places) {
        if (value == null) {
            return 0.0;
        }
        return BigDecimal.valueOf(value).setScale(places, RoundingMode.HALF_UP).doubleValue();
    }

    private double nullSafe(Double value) {
        return value != null ? value : 0.0;
    }

    private int nullSafe(Integer value) {
        return value != null ? value : 0;
    }

    private <T> List<T> safeList(List<T> source) {
        return source != null ? source : List.of();
    }
}
