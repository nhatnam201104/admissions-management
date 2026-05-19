package com.example.managementadmissionwf.utils;

import com.example.managementadmissionwf.dal.entity.XtThisinhxettuyen25;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("PriorityScoreCalculator - UEF mapping")
class PriorityScoreCalculatorTest {

    private static XtThisinhxettuyen25 candidate(String khuVuc, String doiTuong) {
        return XtThisinhxettuyen25.builder()
                .cccd("001234567890")
                .khuVuc(khuVuc)
                .doiTuong(doiTuong)
                .build();
    }

    @Test
    @DisplayName("DOI_TUONG_POINTS chỉ chứa Không/UT1/UT2")
    void doiTuongPoints_onlyUefCodes() {
        assertThat(PriorityScoreCalculator.DOI_TUONG_POINTS)
                .containsOnlyKeys("Không", "UT1", "UT2")
                .containsEntry("Không", 0.00)
                .containsEntry("UT1", 2.00)
                .containsEntry("UT2", 1.00);
    }

    @Test
    @DisplayName("KHU_VUC_POINTS chứa đúng 4 mã")
    void khuVucPoints_hasFourRegions() {
        assertThat(PriorityScoreCalculator.KHU_VUC_POINTS)
                .containsOnlyKeys("KV1", "KV2-NT", "KV2", "KV3")
                .containsEntry("KV1", 0.75)
                .containsEntry("KV2-NT", 0.50)
                .containsEntry("KV2", 0.25)
                .containsEntry("KV3", 0.00);
    }

    @Test
    @DisplayName("displayLabel render đúng cho UT1, Không, KV3")
    void displayLabel_format() {
        assertThat(PriorityScoreCalculator.displayLabel("UT1", PriorityScoreCalculator.DOI_TUONG_POINTS))
                .isEqualTo("UT1 (+2đ)");
        assertThat(PriorityScoreCalculator.displayLabel("Không", PriorityScoreCalculator.DOI_TUONG_POINTS))
                .isEqualTo("Không (0đ)");
        assertThat(PriorityScoreCalculator.displayLabel("KV3", PriorityScoreCalculator.KHU_VUC_POINTS))
                .isEqualTo("KV3 (0đ)");
        assertThat(PriorityScoreCalculator.displayLabel("KV2-NT", PriorityScoreCalculator.KHU_VUC_POINTS))
                .isEqualTo("KV2-NT (+0.5đ)");
    }

    @Test
    @DisplayName("calculateForCandidate UT1 + KV1 = 2.75")
    void calculateForCandidate_ut1Kv1() {
        double result = PriorityScoreCalculator.calculateForCandidate(candidate("KV1", "UT1"));
        assertThat(result).isEqualTo(2.75);
    }

    @Test
    @DisplayName("calculateForCandidate UT2 + KV2-NT = 1.5")
    void calculateForCandidate_ut2KvNt() {
        double result = PriorityScoreCalculator.calculateForCandidate(candidate("KV2-NT", "UT2"));
        assertThat(result).isEqualTo(1.5);
    }

    @Test
    @DisplayName("calculateForCandidate Không + KV3 = 0")
    void calculateForCandidate_none() {
        double result = PriorityScoreCalculator.calculateForCandidate(candidate("KV3", "Không"));
        assertThat(result).isZero();
    }

    @Test
    @DisplayName("Mã legacy KT1/KT2/KT3 trả về 0 (graceful)")
    void calculateForCandidate_legacyCodes_returnZero() {
        assertThat(PriorityScoreCalculator.calculateForCandidate(candidate("KV3", "KT1"))).isZero();
        assertThat(PriorityScoreCalculator.calculateForCandidate(candidate("KV3", "KT2"))).isZero();
        assertThat(PriorityScoreCalculator.calculateForCandidate(candidate("KV3", "KT3"))).isZero();
    }

    @Test
    @DisplayName("Mã không khớp danh mục trả về 0 (graceful)")
    void calculateForCandidate_unknownCode_returnZero() {
        assertThat(PriorityScoreCalculator.calculateForCandidate(candidate("KV99", "FOO"))).isZero();
    }

    @Test
    @DisplayName("Null candidate trả về 0")
    void calculateForCandidate_nullCandidate_returnZero() {
        assertThat(PriorityScoreCalculator.calculateForCandidate(null)).isZero();
    }

    @Test
    @DisplayName("Cap 22.5 không kích hoạt khi điểm < 22.5")
    void applyCap_belowThreshold() {
        double result = PriorityScoreCalculator.applyCap(2.0, 20.0, 0.0);
        assertThat(result).isEqualTo(2.0);
    }

    @Test
    @DisplayName("Cap 22.5 giảm tuyến tính khi điểm >= 22.5")
    void applyCap_aboveThreshold() {
        double result = PriorityScoreCalculator.applyCap(2.0, 25.0, 0.0);
        assertThat(result).isEqualTo(2.0 * (30.0 - 25.0) / 7.5);
    }
}
