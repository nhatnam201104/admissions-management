package com.example.managementadmissionwf.bus.interfaces;

import com.example.managementadmissionwf.dto.statistic.CandidateCategoryStatistic;
import com.example.managementadmissionwf.dto.statistic.MajorMethodAdmissionStat;
import com.example.managementadmissionwf.dto.statistic.MajorStatistic;
import com.example.managementadmissionwf.dto.statistic.MethodStatistic;
import com.example.managementadmissionwf.dto.statistic.ScoreDistribution;
import com.example.managementadmissionwf.dto.statistic.StatisticSummary;

import java.util.List;

/**
 * Service interface for statistics operations.
 */
public interface StatisticService {

    StatisticSummary getSummary();

    /**
     * Top 10 ngành theo số thí sinh đăng ký.
     */
    List<MajorStatistic> getMajorStatistics();

    /**
     * Tổng hợp theo phương thức (tổng + trúng tuyển).
     */
    List<MethodStatistic> getMethodStatistics();

    List<CandidateCategoryStatistic> getCandidateStatisticsByDoiTuong();

    List<CandidateCategoryStatistic> getCandidateStatisticsByKhuVuc();

    /**
     * Phân bố điểm xét tuyển theo 6 khoảng.
     */
    List<ScoreDistribution> getScoreDistribution();

    /**
     * Trúng tuyển theo từng ngành × phương thức.
     * Phục vụ rubric mục 6 desktop: "Danh sách số lượng trúng tuyển từng phương
     * thức theo ngành".
     */
    List<MajorMethodAdmissionStat> getMajorMethodMatrix();
}
