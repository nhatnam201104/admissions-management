package com.example.managementadmissionwf.bus.impl;

import com.example.managementadmissionwf.dal.entity.*;
import com.example.managementadmissionwf.dal.repository.*;
import com.example.managementadmissionwf.dto.score.AspirationScoreResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AspirationScoreServiceImpl - Unit Tests")
class AspirationScoreServiceImplTest {

    @Mock private ScoreRepository scoreRepository;
    @Mock private BonusScoreRepository bonusScoreRepository;
    @Mock private ConversionTableRepository conversionTableRepository;
    @Mock private NganhTohopRepository nganhTohopRepository;
    @Mock private NguyenVongRepository nguyenVongRepository;
    @Mock private MajorRepository majorRepository;

    @InjectMocks
    private AspirationScoreServiceImpl aspirationScoreService;

    private static final String CCCD = "001234567890";
    private static final String MA_NGANH = "CNTT";
    private static final String MA_TOHOP = "A00";
    private static final String PHUONG_THUC_THPT = "THPT";

    private XtNguyenvongxettuyen buildAspiration(String cccd, String maNganh, String phuongThuc) {
        return XtNguyenvongxettuyen.builder()
                .id(1).nnCccd(cccd).nvManganh(maNganh)
                .nvTt(1).ttPhuongthuc(phuongThuc).isDeleted(false).build();
    }

    private XtDiemthixettuyen buildScore(String cccd, String phuongThuc,
                                          Double to, Double li, Double ho) {
        return XtDiemthixettuyen.builder()
                .id(1).cccd(cccd).sobaodanh("SBD001").dPhuongthuc(phuongThuc)
                .to(to).li(li).ho(ho).isDeleted(false).build();
    }

    private XtNganhTohop buildTohop(String maNganh, String maTohop,
                                     String mon1, Double hs1,
                                     String mon2, Double hs2,
                                     String mon3, Double hs3) {
        return XtNganhTohop.builder()
                .id(1).manganh(maNganh).matohop(maTohop)
                .thMon1(mon1).hsmon1(hs1).thMon2(mon2).hsmon2(hs2)
                .thMon3(mon3).hsmon3(hs3).isDeleted(false).build();
    }

    private XtNganh buildMajor(String maNganh, Double diemSan) {
        return XtNganh.builder()
                .idnganh(1).manganh(maNganh).tennganh("Công nghệ thông tin")
                .nDiemsan(diemSan).nChitieu(100).build();
    }

    private void mockNoConversion() {
        when(conversionTableRepository.findByPhuongThucAndMonAndTohop(any(), any(), any()))
                .thenReturn(Optional.empty());
        when(conversionTableRepository.findByPhuongThucAndMonAndTohopIsNull(any(), any()))
                .thenReturn(Optional.empty());
    }

    // ================= FIND SCORE BY METHOD =================

    @Nested
    @DisplayName("findScoreByMethod (via calculateForAspiration)")
    class FindScoreByMethod {

        @Test
        @DisplayName("should use preferred method score when available")
        void shouldUsePreferredMethodScore_whenAvailable() {
            XtNguyenvongxettuyen aspiration = buildAspiration(CCCD, MA_NGANH, "DGNL");
            XtDiemthixettuyen dgnlScore = buildScore(CCCD, "DGNL", 8.0, 8.0, 8.0);
            XtNganhTohop tohop = buildTohop(MA_NGANH, MA_TOHOP, "TO", 1.0, "LI", 1.0, "HO", 1.0);
            XtNganh major = buildMajor(MA_NGANH, 0.0);

            when(scoreRepository.findByCccdAndDPhuongthuc(CCCD, "DGNL"))
                    .thenReturn(Optional.of(dgnlScore));
            when(nganhTohopRepository.findByManganh(MA_NGANH)).thenReturn(List.of(tohop));
            when(bonusScoreRepository.findByCccd(CCCD)).thenReturn(Optional.empty());
            when(majorRepository.findByManganh(MA_NGANH)).thenReturn(Optional.of(major));
            when(nguyenVongRepository.save(any())).thenReturn(aspiration);
            mockNoConversion();

            AspirationScoreResult result = aspirationScoreService.calculateForAspiration(aspiration);

            assertThat(result).isNotNull();
            assertThat(result.phuongThuc()).isEqualTo("DGNL");
        }

        @Test
        @DisplayName("should fallback to THPT when preferred method not found")
        void shouldFallbackToThpt_whenPreferredMethodNotFound() {
            XtNguyenvongxettuyen aspiration = buildAspiration(CCCD, MA_NGANH, "VSAT");
            XtDiemthixettuyen thptScore = buildScore(CCCD, "THPT", 8.0, 8.0, 8.0);
            XtNganhTohop tohop = buildTohop(MA_NGANH, MA_TOHOP, "TO", 1.0, "LI", 1.0, "HO", 1.0);
            XtNganh major = buildMajor(MA_NGANH, 0.0);

            when(scoreRepository.findByCccdAndDPhuongthuc(CCCD, "VSAT"))
                    .thenReturn(Optional.empty());
            when(scoreRepository.findAllByCccd(CCCD)).thenReturn(List.of(thptScore));
            when(nganhTohopRepository.findByManganh(MA_NGANH)).thenReturn(List.of(tohop));
            when(bonusScoreRepository.findByCccd(CCCD)).thenReturn(Optional.empty());
            when(majorRepository.findByManganh(MA_NGANH)).thenReturn(Optional.of(major));
            when(nguyenVongRepository.save(any())).thenReturn(aspiration);
            mockNoConversion();

            AspirationScoreResult result = aspirationScoreService.calculateForAspiration(aspiration);

            assertThat(result).isNotNull();
            assertThat(result.phuongThuc()).isEqualTo("THPT");
        }

        @Test
        @DisplayName("should return null and mark insufficient when no score found")
        void shouldReturnNullAndMarkInsufficient_whenNoScoreFound() {
            XtNguyenvongxettuyen aspiration = buildAspiration(CCCD, MA_NGANH, "THPT");

            when(scoreRepository.findByCccdAndDPhuongthuc(CCCD, "THPT"))
                    .thenReturn(Optional.empty());
            when(scoreRepository.findAllByCccd(CCCD)).thenReturn(Collections.emptyList());

            AspirationScoreResult result = aspirationScoreService.calculateForAspiration(aspiration);

            assertThat(result).isNull();
            assertThat(aspiration.getNvKetqua()).isEqualTo("THIEU_DIEM");
            assertThat(aspiration.getDiemThxt()).isNull();
        }

        @Test
        @DisplayName("should fallback to DGNL when THPT not available")
        void shouldFallbackToDgnl_whenThptNotAvailable() {
            XtNguyenvongxettuyen aspiration = buildAspiration(CCCD, MA_NGANH, null);
            XtDiemthixettuyen dgnlScore = buildScore(CCCD, "DGNL", 7.0, 7.0, 7.0);
            XtNganhTohop tohop = buildTohop(MA_NGANH, MA_TOHOP, "TO", 1.0, "LI", 1.0, "HO", 1.0);
            XtNganh major = buildMajor(MA_NGANH, 0.0);

            when(scoreRepository.findAllByCccd(CCCD)).thenReturn(List.of(dgnlScore));
            when(nganhTohopRepository.findByManganh(MA_NGANH)).thenReturn(List.of(tohop));
            when(bonusScoreRepository.findByCccd(CCCD)).thenReturn(Optional.empty());
            when(majorRepository.findByManganh(MA_NGANH)).thenReturn(Optional.of(major));
            when(nguyenVongRepository.save(any())).thenReturn(aspiration);
            mockNoConversion();

            AspirationScoreResult result = aspirationScoreService.calculateForAspiration(aspiration);

            assertThat(result).isNotNull();
            assertThat(result.phuongThuc()).isEqualTo("DGNL");
        }
    }

    // ================= CALCULATE FOR TOHOP =================

    @Nested
    @DisplayName("calculateForTohop (via calculateForAspiration)")
    class CalculateForTohop {

        @Test
        @DisplayName("should calculate weighted sum correctly with all coefficients = 1")
        void shouldCalculateWeightedSum_correctlyWithAllCoefficients1() {
            XtNguyenvongxettuyen aspiration = buildAspiration(CCCD, MA_NGANH, PHUONG_THUC_THPT);
            XtDiemthixettuyen score = buildScore(CCCD, PHUONG_THUC_THPT, 8.0, 7.0, 9.0);
            XtNganhTohop tohop = buildTohop(MA_NGANH, MA_TOHOP, "TO", 1.0, "LI", 1.0, "HO", 1.0);
            XtNganh major = buildMajor(MA_NGANH, 0.0);

            when(scoreRepository.findByCccdAndDPhuongthuc(CCCD, PHUONG_THUC_THPT))
                    .thenReturn(Optional.of(score));
            when(nganhTohopRepository.findByManganh(MA_NGANH)).thenReturn(List.of(tohop));
            when(bonusScoreRepository.findByCccd(CCCD)).thenReturn(Optional.empty());
            when(majorRepository.findByManganh(MA_NGANH)).thenReturn(Optional.of(major));
            when(nguyenVongRepository.save(any())).thenReturn(aspiration);
            mockNoConversion();

            AspirationScoreResult result = aspirationScoreService.calculateForAspiration(aspiration);

            assertThat(result).isNotNull();
            assertThat(result.diemThxt()).isEqualTo(24.0);
            assertThat(result.diemXettuyen()).isEqualTo(24.0);
        }

        @Test
        @DisplayName("should calculate weighted sum with mixed coefficients (has hs=2)")
        void shouldCalculateWeightedSum_withMixedCoefficients() {
            XtNguyenvongxettuyen aspiration = buildAspiration(CCCD, MA_NGANH, PHUONG_THUC_THPT);
            XtDiemthixettuyen score = buildScore(CCCD, PHUONG_THUC_THPT, 8.0, 7.0, 6.0);
            XtNganhTohop tohop = buildTohop(MA_NGANH, MA_TOHOP, "TO", 2.0, "LI", 1.0, "HO", 1.0);
            XtNganh major = buildMajor(MA_NGANH, 0.0);

            when(scoreRepository.findByCccdAndDPhuongthuc(CCCD, PHUONG_THUC_THPT))
                    .thenReturn(Optional.of(score));
            when(nganhTohopRepository.findByManganh(MA_NGANH)).thenReturn(List.of(tohop));
            when(bonusScoreRepository.findByCccd(CCCD)).thenReturn(Optional.empty());
            when(majorRepository.findByManganh(MA_NGANH)).thenReturn(Optional.of(major));
            when(nguyenVongRepository.save(any())).thenReturn(aspiration);
            mockNoConversion();

            AspirationScoreResult result = aspirationScoreService.calculateForAspiration(aspiration);

            assertThat(result).isNotNull();
            // has hs=2: (8*2 + 7*1 + 6*1) * 3/4 = 29 * 0.75 = 21.75
            assertThat(result.diemThxt()).isCloseTo(21.75, within(0.01));
        }

        @Test
        @DisplayName("should cap diemThxt at 30.0")
        void shouldCapDiemThxt_atMaximum30() {
            XtNguyenvongxettuyen aspiration = buildAspiration(CCCD, MA_NGANH, PHUONG_THUC_THPT);
            XtDiemthixettuyen score = buildScore(CCCD, PHUONG_THUC_THPT, 10.0, 10.0, 10.0);
            XtNganhTohop tohop = buildTohop(MA_NGANH, MA_TOHOP, "TO", 1.0, "LI", 1.0, "HO", 1.0);
            XtNganh major = buildMajor(MA_NGANH, 0.0);

            when(scoreRepository.findByCccdAndDPhuongthuc(CCCD, PHUONG_THUC_THPT))
                    .thenReturn(Optional.of(score));
            when(nganhTohopRepository.findByManganh(MA_NGANH)).thenReturn(List.of(tohop));
            when(bonusScoreRepository.findByCccd(CCCD)).thenReturn(Optional.empty());
            when(majorRepository.findByManganh(MA_NGANH)).thenReturn(Optional.of(major));
            when(nguyenVongRepository.save(any())).thenReturn(aspiration);
            mockNoConversion();

            AspirationScoreResult result = aspirationScoreService.calculateForAspiration(aspiration);

            assertThat(result).isNotNull();
            assertThat(result.diemThxt()).isEqualTo(30.0);
        }

        @Test
        @DisplayName("should skip tohop when a subject score is missing")
        void shouldSkipTohop_whenSubjectScoreMissing() {
            XtNguyenvongxettuyen aspiration = buildAspiration(CCCD, MA_NGANH, PHUONG_THUC_THPT);
            XtDiemthixettuyen score = buildScore(CCCD, PHUONG_THUC_THPT, 8.0, null, 9.0);
            XtNganhTohop tohop = buildTohop(MA_NGANH, MA_TOHOP, "TO", 1.0, "LI", 1.0, "HO", 1.0);

            when(scoreRepository.findByCccdAndDPhuongthuc(CCCD, PHUONG_THUC_THPT))
                    .thenReturn(Optional.of(score));
            when(nganhTohopRepository.findByManganh(MA_NGANH)).thenReturn(List.of(tohop));
            when(nguyenVongRepository.save(any())).thenReturn(aspiration);

            AspirationScoreResult result = aspirationScoreService.calculateForAspiration(aspiration);

            assertThat(result).isNull();
            assertThat(aspiration.getNvKetqua()).isEqualTo("THIEU_DIEM");
        }

        @Test
        @DisplayName("should return null when no tohop exists for major")
        void shouldReturnNull_whenNoTohopExistsForMajor() {
            XtNguyenvongxettuyen aspiration = buildAspiration(CCCD, MA_NGANH, PHUONG_THUC_THPT);
            XtDiemthixettuyen score = buildScore(CCCD, PHUONG_THUC_THPT, 8.0, 8.0, 8.0);

            when(scoreRepository.findByCccdAndDPhuongthuc(CCCD, PHUONG_THUC_THPT))
                    .thenReturn(Optional.of(score));
            when(nganhTohopRepository.findByManganh(MA_NGANH)).thenReturn(Collections.emptyList());

            AspirationScoreResult result = aspirationScoreService.calculateForAspiration(aspiration);

            assertThat(result).isNull();
            assertThat(aspiration.getNvKetqua()).isEqualTo("THIEU_DIEM");
        }
    }

    // ================= SCORE CONVERSION =================

    @Nested
    @DisplayName("convertScore (via calculateForAspiration)")
    class ConvertScore {

        @Test
        @DisplayName("should apply conversion table when rule matches")
        void shouldApplyConversionTable_whenRuleMatches() {
            XtNguyenvongxettuyen aspiration = buildAspiration(CCCD, MA_NGANH, PHUONG_THUC_THPT);
            XtDiemthixettuyen score = buildScore(CCCD, PHUONG_THUC_THPT, 8.0, 7.0, 9.0);
            XtNganhTohop tohop = buildTohop(MA_NGANH, MA_TOHOP, "TO", 1.0, "LI", 1.0, "HO", 1.0);
            XtNganh major = buildMajor(MA_NGANH, 0.0);

            XtBangquydoi conversionRule = XtBangquydoi.builder()
                    .dPhuongthuc(PHUONG_THUC_THPT).dMon("TO").dTohop(MA_TOHOP)
                    .dDiema(0.0).dDiemb(10.0).dDiemc(5.0).dDiemd(9.0).build();

            when(scoreRepository.findByCccdAndDPhuongthuc(CCCD, PHUONG_THUC_THPT))
                    .thenReturn(Optional.of(score));
            when(nganhTohopRepository.findByManganh(MA_NGANH)).thenReturn(List.of(tohop));
            when(bonusScoreRepository.findByCccd(CCCD)).thenReturn(Optional.empty());
            when(majorRepository.findByManganh(MA_NGANH)).thenReturn(Optional.of(major));
            when(nguyenVongRepository.save(any())).thenReturn(aspiration);
            when(conversionTableRepository.findByPhuongThucAndMonAndTohop(PHUONG_THUC_THPT, "TO", MA_TOHOP))
                    .thenReturn(Optional.of(conversionRule));
            when(conversionTableRepository.findByPhuongThucAndMonAndTohop(eq(PHUONG_THUC_THPT), eq("LI"), any()))
                    .thenReturn(Optional.empty());
            when(conversionTableRepository.findByPhuongThucAndMonAndTohop(eq(PHUONG_THUC_THPT), eq("HO"), any()))
                    .thenReturn(Optional.empty());
            when(conversionTableRepository.findByPhuongThucAndMonAndTohopIsNull(PHUONG_THUC_THPT, "LI"))
                    .thenReturn(Optional.empty());
            when(conversionTableRepository.findByPhuongThucAndMonAndTohopIsNull(PHUONG_THUC_THPT, "HO"))
                    .thenReturn(Optional.empty());

            AspirationScoreResult result = aspirationScoreService.calculateForAspiration(aspiration);

            assertThat(result).isNotNull();
            // TO: ratio=(8-0)/(10-0)=0.8, converted=5+0.8*(9-5)=8.2
            assertThat(result.diemSauQuyDoiMon1()).isCloseTo(8.2, within(0.01));
        }

        @Test
        @DisplayName("should fallback to global conversion when tohop-specific not found")
        void shouldFallbackToGlobalConversion_whenTohopSpecificNotFound() {
            XtNguyenvongxettuyen aspiration = buildAspiration(CCCD, MA_NGANH, PHUONG_THUC_THPT);
            XtDiemthixettuyen score = buildScore(CCCD, PHUONG_THUC_THPT, 8.0, 7.0, 9.0);
            XtNganhTohop tohop = buildTohop(MA_NGANH, MA_TOHOP, "TO", 1.0, "LI", 1.0, "HO", 1.0);
            XtNganh major = buildMajor(MA_NGANH, 0.0);

            XtBangquydoi globalRule = XtBangquydoi.builder()
                    .dPhuongthuc(PHUONG_THUC_THPT).dMon("TO").dTohop(null)
                    .dDiema(5.0).dDiemb(10.0).dDiemc(6.0).dDiemd(9.0).build();

            when(scoreRepository.findByCccdAndDPhuongthuc(CCCD, PHUONG_THUC_THPT))
                    .thenReturn(Optional.of(score));
            when(nganhTohopRepository.findByManganh(MA_NGANH)).thenReturn(List.of(tohop));
            when(bonusScoreRepository.findByCccd(CCCD)).thenReturn(Optional.empty());
            when(majorRepository.findByManganh(MA_NGANH)).thenReturn(Optional.of(major));
            when(nguyenVongRepository.save(any())).thenReturn(aspiration);
            when(conversionTableRepository.findByPhuongThucAndMonAndTohop(PHUONG_THUC_THPT, "TO", MA_TOHOP))
                    .thenReturn(Optional.empty());
            when(conversionTableRepository.findByPhuongThucAndMonAndTohopIsNull(PHUONG_THUC_THPT, "TO"))
                    .thenReturn(Optional.of(globalRule));
            when(conversionTableRepository.findByPhuongThucAndMonAndTohop(eq(PHUONG_THUC_THPT), eq("LI"), any()))
                    .thenReturn(Optional.empty());
            when(conversionTableRepository.findByPhuongThucAndMonAndTohopIsNull(PHUONG_THUC_THPT, "LI"))
                    .thenReturn(Optional.empty());
            when(conversionTableRepository.findByPhuongThucAndMonAndTohop(eq(PHUONG_THUC_THPT), eq("HO"), any()))
                    .thenReturn(Optional.empty());
            when(conversionTableRepository.findByPhuongThucAndMonAndTohopIsNull(PHUONG_THUC_THPT, "HO"))
                    .thenReturn(Optional.empty());

            AspirationScoreResult result = aspirationScoreService.calculateForAspiration(aspiration);

            assertThat(result).isNotNull();
            // TO=8, range 5-10, convert 6-9: ratio=(8-5)/(10-5)=0.6, converted=6+0.6*3=7.8
            assertThat(result.diemSauQuyDoiMon1()).isCloseTo(7.8, within(0.01));
        }

        @Test
        @DisplayName("should return original score when no conversion rule matches")
        void shouldReturnOriginalScore_whenNoConversionRuleMatches() {
            XtNguyenvongxettuyen aspiration = buildAspiration(CCCD, MA_NGANH, PHUONG_THUC_THPT);
            XtDiemthixettuyen score = buildScore(CCCD, PHUONG_THUC_THPT, 8.0, 7.0, 9.0);
            XtNganhTohop tohop = buildTohop(MA_NGANH, MA_TOHOP, "TO", 1.0, "LI", 1.0, "HO", 1.0);
            XtNganh major = buildMajor(MA_NGANH, 0.0);

            when(scoreRepository.findByCccdAndDPhuongthuc(CCCD, PHUONG_THUC_THPT))
                    .thenReturn(Optional.of(score));
            when(nganhTohopRepository.findByManganh(MA_NGANH)).thenReturn(List.of(tohop));
            when(bonusScoreRepository.findByCccd(CCCD)).thenReturn(Optional.empty());
            when(majorRepository.findByManganh(MA_NGANH)).thenReturn(Optional.of(major));
            when(nguyenVongRepository.save(any())).thenReturn(aspiration);
            mockNoConversion();

            AspirationScoreResult result = aspirationScoreService.calculateForAspiration(aspiration);

            assertThat(result).isNotNull();
            assertThat(result.diemSauQuyDoiMon1()).isEqualTo(8.0);
            assertThat(result.diemSauQuyDoiMon2()).isEqualTo(7.0);
            assertThat(result.diemSauQuyDoiMon3()).isEqualTo(9.0);
        }

        @Test
        @DisplayName("should handle zero range conversion (diemA == diemB)")
        void shouldHandleZeroRangeConversion() {
            XtNguyenvongxettuyen aspiration = buildAspiration(CCCD, MA_NGANH, PHUONG_THUC_THPT);
            XtDiemthixettuyen score = buildScore(CCCD, PHUONG_THUC_THPT, 5.0, 7.0, 9.0);
            XtNganhTohop tohop = buildTohop(MA_NGANH, MA_TOHOP, "TO", 1.0, "LI", 1.0, "HO", 1.0);
            XtNganh major = buildMajor(MA_NGANH, 0.0);

            XtBangquydoi zeroRangeRule = XtBangquydoi.builder()
                    .dPhuongthuc(PHUONG_THUC_THPT).dMon("TO").dTohop(MA_TOHOP)
                    .dDiema(5.0).dDiemb(5.0).dDiemc(8.0).dDiemd(8.0).build();

            when(scoreRepository.findByCccdAndDPhuongthuc(CCCD, PHUONG_THUC_THPT))
                    .thenReturn(Optional.of(score));
            when(nganhTohopRepository.findByManganh(MA_NGANH)).thenReturn(List.of(tohop));
            when(bonusScoreRepository.findByCccd(CCCD)).thenReturn(Optional.empty());
            when(majorRepository.findByManganh(MA_NGANH)).thenReturn(Optional.of(major));
            when(nguyenVongRepository.save(any())).thenReturn(aspiration);
            when(conversionTableRepository.findByPhuongThucAndMonAndTohop(PHUONG_THUC_THPT, "TO", MA_TOHOP))
                    .thenReturn(Optional.of(zeroRangeRule));
            when(conversionTableRepository.findByPhuongThucAndMonAndTohop(eq(PHUONG_THUC_THPT), eq("LI"), any()))
                    .thenReturn(Optional.empty());
            when(conversionTableRepository.findByPhuongThucAndMonAndTohopIsNull(PHUONG_THUC_THPT, "LI"))
                    .thenReturn(Optional.empty());
            when(conversionTableRepository.findByPhuongThucAndMonAndTohop(eq(PHUONG_THUC_THPT), eq("HO"), any()))
                    .thenReturn(Optional.empty());
            when(conversionTableRepository.findByPhuongThucAndMonAndTohopIsNull(PHUONG_THUC_THPT, "HO"))
                    .thenReturn(Optional.empty());

            AspirationScoreResult result = aspirationScoreService.calculateForAspiration(aspiration);

            assertThat(result).isNotNull();
            assertThat(result.diemSauQuyDoiMon1()).isEqualTo(8.0);
        }
    }

    // ================= BONUS SCORES =================

    @Nested
    @DisplayName("bonus score handling")
    class BonusScoreHandling {

        @Test
        @DisplayName("should add bonus points when available")
        void shouldAddBonusPoints_whenAvailable() {
            XtNguyenvongxettuyen aspiration = buildAspiration(CCCD, MA_NGANH, PHUONG_THUC_THPT);
            XtDiemthixettuyen score = buildScore(CCCD, PHUONG_THUC_THPT, 8.0, 7.0, 9.0);
            XtNganhTohop tohop = buildTohop(MA_NGANH, MA_TOHOP, "TO", 1.0, "LI", 1.0, "HO", 1.0);
            XtNganh major = buildMajor(MA_NGANH, 0.0);
            XtDiemcongxettuyen bonus = XtDiemcongxettuyen.builder()
                    .cccd(CCCD).diemCc(2.0).diemUtxt(1.0).diemTong(3.0).build();

            when(scoreRepository.findByCccdAndDPhuongthuc(CCCD, PHUONG_THUC_THPT))
                    .thenReturn(Optional.of(score));
            when(nganhTohopRepository.findByManganh(MA_NGANH)).thenReturn(List.of(tohop));
            when(bonusScoreRepository.findByCccd(CCCD)).thenReturn(Optional.of(bonus));
            when(majorRepository.findByManganh(MA_NGANH)).thenReturn(Optional.of(major));
            when(nguyenVongRepository.save(any())).thenReturn(aspiration);
            mockNoConversion();

            AspirationScoreResult result = aspirationScoreService.calculateForAspiration(aspiration);

            assertThat(result).isNotNull();
            assertThat(result.diemCong()).isEqualTo(2.0);
            assertThat(result.diemXettuyen()).isGreaterThan(24.0);
        }

        @Test
        @DisplayName("should use zero bonus when no bonus record exists")
        void shouldUseZeroBonus_whenNoBonusRecordExists() {
            XtNguyenvongxettuyen aspiration = buildAspiration(CCCD, MA_NGANH, PHUONG_THUC_THPT);
            XtDiemthixettuyen score = buildScore(CCCD, PHUONG_THUC_THPT, 8.0, 7.0, 9.0);
            XtNganhTohop tohop = buildTohop(MA_NGANH, MA_TOHOP, "TO", 1.0, "LI", 1.0, "HO", 1.0);
            XtNganh major = buildMajor(MA_NGANH, 0.0);

            when(scoreRepository.findByCccdAndDPhuongthuc(CCCD, PHUONG_THUC_THPT))
                    .thenReturn(Optional.of(score));
            when(nganhTohopRepository.findByManganh(MA_NGANH)).thenReturn(List.of(tohop));
            when(bonusScoreRepository.findByCccd(CCCD)).thenReturn(Optional.empty());
            when(majorRepository.findByManganh(MA_NGANH)).thenReturn(Optional.of(major));
            when(nguyenVongRepository.save(any())).thenReturn(aspiration);
            mockNoConversion();

            AspirationScoreResult result = aspirationScoreService.calculateForAspiration(aspiration);

            assertThat(result).isNotNull();
            assertThat(result.diemCong()).isEqualTo(0.0);
            assertThat(result.diemUuTien()).isEqualTo(0.0);
        }
    }

    // ================= FLOOR SCORE COMPARISON =================

    @Nested
    @DisplayName("floor score comparison")
    class FloorScoreComparison {

        @Test
        @DisplayName("should mark CHO_XET when score meets floor")
        void shouldMarkChoXet_whenScoreMeetsFloor() {
            XtNguyenvongxettuyen aspiration = buildAspiration(CCCD, MA_NGANH, PHUONG_THUC_THPT);
            XtDiemthixettuyen score = buildScore(CCCD, PHUONG_THUC_THPT, 9.0, 9.0, 9.0);
            XtNganhTohop tohop = buildTohop(MA_NGANH, MA_TOHOP, "TO", 1.0, "LI", 1.0, "HO", 1.0);
            XtNganh major = buildMajor(MA_NGANH, 20.0);

            when(scoreRepository.findByCccdAndDPhuongthuc(CCCD, PHUONG_THUC_THPT))
                    .thenReturn(Optional.of(score));
            when(nganhTohopRepository.findByManganh(MA_NGANH)).thenReturn(List.of(tohop));
            when(bonusScoreRepository.findByCccd(CCCD)).thenReturn(Optional.empty());
            when(majorRepository.findByManganh(MA_NGANH)).thenReturn(Optional.of(major));
            when(nguyenVongRepository.save(any())).thenReturn(aspiration);
            mockNoConversion();

            AspirationScoreResult result = aspirationScoreService.calculateForAspiration(aspiration);

            assertThat(result).isNotNull();
            assertThat(result.datDiemSan()).isTrue();
            assertThat(result.diemSan()).isEqualTo(20.0);
            assertThat(aspiration.getNvKetqua()).isEqualTo("CHO_XET");
        }

        @Test
        @DisplayName("should mark THIEU_DIEM when score below floor")
        void shouldMarkThieuDiem_whenScoreBelowFloor() {
            XtNguyenvongxettuyen aspiration = buildAspiration(CCCD, MA_NGANH, PHUONG_THUC_THPT);
            XtDiemthixettuyen score = buildScore(CCCD, PHUONG_THUC_THPT, 5.0, 5.0, 5.0);
            XtNganhTohop tohop = buildTohop(MA_NGANH, MA_TOHOP, "TO", 1.0, "LI", 1.0, "HO", 1.0);
            XtNganh major = buildMajor(MA_NGANH, 25.0);

            when(scoreRepository.findByCccdAndDPhuongthuc(CCCD, PHUONG_THUC_THPT))
                    .thenReturn(Optional.of(score));
            when(nganhTohopRepository.findByManganh(MA_NGANH)).thenReturn(List.of(tohop));
            when(bonusScoreRepository.findByCccd(CCCD)).thenReturn(Optional.empty());
            when(majorRepository.findByManganh(MA_NGANH)).thenReturn(Optional.of(major));
            when(nguyenVongRepository.save(any())).thenReturn(aspiration);
            mockNoConversion();

            AspirationScoreResult result = aspirationScoreService.calculateForAspiration(aspiration);

            assertThat(result).isNotNull();
            assertThat(result.datDiemSan()).isFalse();
            assertThat(aspiration.getNvKetqua()).isEqualTo("THIEU_DIEM");
        }

        @Test
        @DisplayName("should handle floor score = 0 as no floor check")
        void shouldHandleFloorScoreZero_asNoFloorCheck() {
            XtNguyenvongxettuyen aspiration = buildAspiration(CCCD, MA_NGANH, PHUONG_THUC_THPT);
            XtDiemthixettuyen score = buildScore(CCCD, PHUONG_THUC_THPT, 5.0, 5.0, 5.0);
            XtNganhTohop tohop = buildTohop(MA_NGANH, MA_TOHOP, "TO", 1.0, "LI", 1.0, "HO", 1.0);
            XtNganh major = buildMajor(MA_NGANH, 0.0);

            when(scoreRepository.findByCccdAndDPhuongthuc(CCCD, PHUONG_THUC_THPT))
                    .thenReturn(Optional.of(score));
            when(nganhTohopRepository.findByManganh(MA_NGANH)).thenReturn(List.of(tohop));
            when(bonusScoreRepository.findByCccd(CCCD)).thenReturn(Optional.empty());
            when(majorRepository.findByManganh(MA_NGANH)).thenReturn(Optional.of(major));
            when(nguyenVongRepository.save(any())).thenReturn(aspiration);
            mockNoConversion();

            AspirationScoreResult result = aspirationScoreService.calculateForAspiration(aspiration);

            assertThat(result).isNotNull();
            assertThat(result.datDiemSan()).isFalse();
        }
    }

    // ================= BEST TOHOP SELECTION =================

    @Nested
    @DisplayName("best tohop selection")
    class BestTohopSelection {

        @Test
        @DisplayName("should select tohop with highest admission score among multiple")
        void shouldSelectTohopWithHighestScore_amongMultiple() {
            XtNguyenvongxettuyen aspiration = buildAspiration(CCCD, MA_NGANH, PHUONG_THUC_THPT);
            XtDiemthixettuyen score = buildScore(CCCD, PHUONG_THUC_THPT, 8.0, 7.0, 6.0);
            XtNganhTohop tohop1 = buildTohop(MA_NGANH, "A00", "TO", 1.0, "LI", 1.0, "HO", 1.0);
            XtNganhTohop tohop2 = buildTohop(MA_NGANH, "A01", "TO", 2.0, "LI", 1.0, "HO", 1.0);
            XtNganh major = buildMajor(MA_NGANH, 0.0);

            when(scoreRepository.findByCccdAndDPhuongthuc(CCCD, PHUONG_THUC_THPT))
                    .thenReturn(Optional.of(score));
            when(nganhTohopRepository.findByManganh(MA_NGANH))
                    .thenReturn(List.of(tohop1, tohop2));
            when(bonusScoreRepository.findByCccd(CCCD)).thenReturn(Optional.empty());
            when(majorRepository.findByManganh(MA_NGANH)).thenReturn(Optional.of(major));
            when(nguyenVongRepository.save(any())).thenReturn(aspiration);
            mockNoConversion();

            AspirationScoreResult result = aspirationScoreService.calculateForAspiration(aspiration);

            assertThat(result).isNotNull();
            // A00: 8+7+6 = 21; A01: (16+7+6)*0.75 = 22.5 -> A01 wins
            assertThat(result.matohop()).isEqualTo("A01");
        }
    }

    // ================= CALCULATE ALL FOR CCCD =================

    @Nested
    @DisplayName("calculateAllForCccd")
    class CalculateAllForCccd {

        @Test
        @DisplayName("should calculate for all aspirations of a candidate")
        void shouldCalculateForAll_aspirationsOfCandidate() {
            XtNguyenvongxettuyen asp1 = buildAspiration(CCCD, "CNTT", PHUONG_THUC_THPT);
            XtNguyenvongxettuyen asp2 = buildAspiration(CCCD, "KTS", PHUONG_THUC_THPT);
            XtDiemthixettuyen score = buildScore(CCCD, PHUONG_THUC_THPT, 8.0, 7.0, 9.0);
            XtNganhTohop tohop = buildTohop(MA_NGANH, MA_TOHOP, "TO", 1.0, "LI", 1.0, "HO", 1.0);
            XtNganh major = buildMajor(MA_NGANH, 0.0);

            when(nguyenVongRepository.findByNnCccd(CCCD)).thenReturn(List.of(asp1, asp2));
            when(scoreRepository.findByCccdAndDPhuongthuc(CCCD, PHUONG_THUC_THPT))
                    .thenReturn(Optional.of(score));
            when(nganhTohopRepository.findByManganh(anyString())).thenReturn(List.of(tohop));
            when(bonusScoreRepository.findByCccd(CCCD)).thenReturn(Optional.empty());
            when(majorRepository.findByManganh(anyString())).thenReturn(Optional.of(major));
            when(nguyenVongRepository.save(any())).thenReturn(asp1);
            mockNoConversion();

            List<AspirationScoreResult> results = aspirationScoreService.calculateAllForCccd(CCCD);

            assertThat(results).hasSize(2);
        }

        @Test
        @DisplayName("should return empty list when candidate has no aspirations")
        void shouldReturnEmptyList_whenNoAspirations() {
            when(nguyenVongRepository.findByNnCccd(CCCD)).thenReturn(Collections.emptyList());

            List<AspirationScoreResult> results = aspirationScoreService.calculateAllForCccd(CCCD);

            assertThat(results).isEmpty();
        }

        @Test
        @DisplayName("should skip aspirations with insufficient scores in result list")
        void shouldSkipInsufficientScore_aspirationsInResultList() {
            XtNguyenvongxettuyen asp1 = buildAspiration(CCCD, MA_NGANH, PHUONG_THUC_THPT);
            XtNguyenvongxettuyen asp2 = buildAspiration(CCCD, "KTS", "VSAT");

            when(nguyenVongRepository.findByNnCccd(CCCD)).thenReturn(List.of(asp1, asp2));
            when(scoreRepository.findByCccdAndDPhuongthuc(CCCD, PHUONG_THUC_THPT))
                    .thenReturn(Optional.of(buildScore(CCCD, PHUONG_THUC_THPT, 8.0, 7.0, 9.0)));
            when(scoreRepository.findByCccdAndDPhuongthuc(CCCD, "VSAT"))
                    .thenReturn(Optional.empty());
            when(scoreRepository.findAllByCccd(CCCD))
                    .thenReturn(List.of(buildScore(CCCD, PHUONG_THUC_THPT, 8.0, 7.0, 9.0)));
            when(nganhTohopRepository.findByManganh(MA_NGANH))
                    .thenReturn(List.of(buildTohop(MA_NGANH, MA_TOHOP, "TO", 1.0, "LI", 1.0, "HO", 1.0)));
            when(bonusScoreRepository.findByCccd(CCCD)).thenReturn(Optional.empty());
            when(majorRepository.findByManganh(MA_NGANH)).thenReturn(Optional.of(buildMajor(MA_NGANH, 0.0)));
            when(nguyenVongRepository.save(any())).thenReturn(asp1);
            mockNoConversion();

            List<AspirationScoreResult> results = aspirationScoreService.calculateAllForCccd(CCCD);

            assertThat(results).hasSize(1);
            assertThat(results.getFirst().manganh()).isEqualTo(MA_NGANH);
        }
    }

    // ================= PRIORITY SCORE CALCULATION =================

    @Nested
    @DisplayName("priority score calculation (diemUtxt)")
    class PriorityScoreCalculation {

        @Test
        @DisplayName("should return full diemUtxt when diemThxt < 22.5")
        void shouldReturnFullDiemUtxt_whenDiemThxtBelow22_5() {
            XtNguyenvongxettuyen aspiration = buildAspiration(CCCD, MA_NGANH, PHUONG_THUC_THPT);
            XtDiemthixettuyen score = buildScore(CCCD, PHUONG_THUC_THPT, 7.0, 7.0, 7.0);
            XtNganhTohop tohop = buildTohop(MA_NGANH, MA_TOHOP, "TO", 1.0, "LI", 1.0, "HO", 1.0);
            XtNganh major = buildMajor(MA_NGANH, 0.0);
            XtDiemcongxettuyen bonus = XtDiemcongxettuyen.builder()
                    .cccd(CCCD).diemCc(0.0).diemUtxt(2.0).diemTong(2.0).build();

            when(scoreRepository.findByCccdAndDPhuongthuc(CCCD, PHUONG_THUC_THPT))
                    .thenReturn(Optional.of(score));
            when(nganhTohopRepository.findByManganh(MA_NGANH)).thenReturn(List.of(tohop));
            when(bonusScoreRepository.findByCccd(CCCD)).thenReturn(Optional.of(bonus));
            when(majorRepository.findByManganh(MA_NGANH)).thenReturn(Optional.of(major));
            when(nguyenVongRepository.save(any())).thenReturn(aspiration);
            mockNoConversion();

            AspirationScoreResult result = aspirationScoreService.calculateForAspiration(aspiration);

            assertThat(result).isNotNull();
            // diemThxt = 21 < 22.5, diemUuTien = diemUtxt = 2.0
            assertThat(result.diemUuTien()).isEqualTo(2.0);
        }

        @Test
        @DisplayName("should apply reduced priority when diemThxt >= 22.5")
        void shouldApplyReducedPriority_whenDiemThxtAbove22_5() {
            XtNguyenvongxettuyen aspiration = buildAspiration(CCCD, MA_NGANH, PHUONG_THUC_THPT);
            XtDiemthixettuyen score = buildScore(CCCD, PHUONG_THUC_THPT, 8.0, 8.0, 8.0);
            XtNganhTohop tohop = buildTohop(MA_NGANH, MA_TOHOP, "TO", 1.0, "LI", 1.0, "HO", 1.0);
            XtNganh major = buildMajor(MA_NGANH, 0.0);
            XtDiemcongxettuyen bonus = XtDiemcongxettuyen.builder()
                    .cccd(CCCD).diemCc(0.0).diemUtxt(2.0).diemTong(2.0).build();

            when(scoreRepository.findByCccdAndDPhuongthuc(CCCD, PHUONG_THUC_THPT))
                    .thenReturn(Optional.of(score));
            when(nganhTohopRepository.findByManganh(MA_NGANH)).thenReturn(List.of(tohop));
            when(bonusScoreRepository.findByCccd(CCCD)).thenReturn(Optional.of(bonus));
            when(majorRepository.findByManganh(MA_NGANH)).thenReturn(Optional.of(major));
            when(nguyenVongRepository.save(any())).thenReturn(aspiration);
            mockNoConversion();

            AspirationScoreResult result = aspirationScoreService.calculateForAspiration(aspiration);

            assertThat(result).isNotNull();
            // diemThxt = 24 >= 22.5, diemUuTien = ((30-24)/7.5) * 2 = 1.6
            assertThat(result.diemUuTien()).isCloseTo(1.6, within(0.01));
        }
    }

    // ================= ASPIRATION PERSISTENCE =================

    @Nested
    @DisplayName("aspiration persistence")
    class AspirationPersistence {

        @Test
        @DisplayName("should update aspiration entity with calculated scores and save")
        void shouldUpdateAspirationEntity_withCalculatedScores() {
            XtNguyenvongxettuyen aspiration = buildAspiration(CCCD, MA_NGANH, PHUONG_THUC_THPT);
            XtDiemthixettuyen score = buildScore(CCCD, PHUONG_THUC_THPT, 8.0, 7.0, 9.0);
            XtNganhTohop tohop = buildTohop(MA_NGANH, MA_TOHOP, "TO", 1.0, "LI", 1.0, "HO", 1.0);
            XtNganh major = buildMajor(MA_NGANH, 0.0);

            when(scoreRepository.findByCccdAndDPhuongthuc(CCCD, PHUONG_THUC_THPT))
                    .thenReturn(Optional.of(score));
            when(nganhTohopRepository.findByManganh(MA_NGANH)).thenReturn(List.of(tohop));
            when(bonusScoreRepository.findByCccd(CCCD)).thenReturn(Optional.empty());
            when(majorRepository.findByManganh(MA_NGANH)).thenReturn(Optional.of(major));
            when(nguyenVongRepository.save(any())).thenReturn(aspiration);
            mockNoConversion();

            aspirationScoreService.calculateForAspiration(aspiration);

            assertThat(aspiration.getDiemThxt()).isEqualTo(24.0);
            assertThat(aspiration.getDiemXettuyen()).isEqualTo(24.0);
            verify(nguyenVongRepository).save(aspiration);
        }
    }
}
