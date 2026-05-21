package com.example.managementadmissionwf.bus.impl;

import com.example.managementadmissionwf.dal.entity.*;
import com.example.managementadmissionwf.dal.repository.*;
import com.example.managementadmissionwf.dto.score.AspirationScoreResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

/**
 * Test cho rule mới: tổ hợp xét tuyển có môn năng khiếu (HAT, VE, TIENG_DUC,
 * ...) được map theo vị trí xuất hiện vào nk1/nk2 trên record điểm thí sinh.
 *
 * <p>Trước fix: switch trên monCode rơi xuống default → null → mọi NV ngành
 * năng khiếu bị THIEU_DIEM oan.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("AspirationScoreServiceImpl - talent subject mapping")
class AspirationScoreServiceImplTalentTest {

    @Mock private ScoreRepository scoreRepository;
    @Mock private BonusScoreRepository bonusScoreRepository;
    @Mock private ConversionTableRepository conversionTableRepository;
    @Mock private NganhTohopRepository nganhTohopRepository;
    @Mock private NguyenVongRepository nguyenVongRepository;
    @Mock private MajorRepository majorRepository;

    @InjectMocks
    private AspirationScoreServiceImpl service;

    private static final String CCCD = "001234567890";
    private static final String MA_NGANH = "TKDH";
    private static final String MA_TOHOP = "H00";

    private XtNguyenvongxettuyen aspiration(String phuongThuc) {
        return XtNguyenvongxettuyen.builder()
                .id(1).nnCccd(CCCD).nvManganh(MA_NGANH)
                .nvTt(1).ttPhuongthuc(phuongThuc).isDeleted(false).build();
    }

    private XtDiemthixettuyen thptScoreWithNk(Double to, Double va, Double nk1, Double nk2) {
        return XtDiemthixettuyen.builder()
                .id(1).cccd(CCCD).sobaodanh("SBD001").dPhuongthuc("THPT")
                .to(to).va(va).nk1(nk1).nk2(nk2)
                .isDeleted(false).build();
    }

    private XtNganhTohop tohop(String mon1, String mon2, String mon3) {
        return XtNganhTohop.builder()
                .id(1).manganh(MA_NGANH).matohop(MA_TOHOP)
                .thMon1(mon1).hsmon1(1.0)
                .thMon2(mon2).hsmon2(1.0)
                .thMon3(mon3).hsmon3(1.0)
                .isDeleted(false).build();
    }

    private XtNganh major() {
        return XtNganh.builder()
                .idnganh(1).manganh(MA_NGANH).tennganh("Thiết kế đồ họa")
                .nDiemsan(0.0).nChitieu(100).build();
    }

    private void stubCommon(XtDiemthixettuyen score, XtNganhTohop t) {
        when(scoreRepository.findByCccdAndDPhuongthuc(CCCD, "THPT")).thenReturn(Optional.of(score));
        when(nganhTohopRepository.findByManganh(MA_NGANH)).thenReturn(List.of(t));
        when(bonusScoreRepository.findByCccd(CCCD)).thenReturn(Optional.empty());
        when(majorRepository.findByManganh(MA_NGANH)).thenReturn(Optional.of(major()));
        when(nguyenVongRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        lenient().when(conversionTableRepository.findAllByPhuongThucAndMonAndTohop(any(), any(), any()))
                .thenReturn(Collections.emptyList());
        lenient().when(conversionTableRepository.findAllByPhuongThucAndMonAndTohopIsNull(any(), any()))
                .thenReturn(Collections.emptyList());
    }

    @Test
    @DisplayName("Tổ hợp [TO, VA, HAT] với nk1=8.0 → HAT map vào nk1, tính được điểm")
    void singleTalentSubject_mapsToNk1() {
        XtDiemthixettuyen score = thptScoreWithNk(9.0, 8.0, 8.0, null);
        XtNganhTohop t = tohop("TO", "VA", "HAT");
        stubCommon(score, t);

        AspirationScoreResult result = service.calculateForAspiration(aspiration("THPT"));

        assertThat(result).isNotNull();
        // ĐTHXT = (9 + 8 + 8) / 3 * 3 = 25.0
        assertThat(result.diemThxt()).isEqualTo(25.0);
    }

    @Test
    @DisplayName("Tổ hợp [VE, HAT, TO] với nk1=8.0, nk2=7.5 → VE→nk1, HAT→nk2")
    void twoTalentSubjects_mapByPosition() {
        XtDiemthixettuyen score = thptScoreWithNk(9.0, null, 8.0, 7.5);
        XtNganhTohop t = tohop("VE", "HAT", "TO");
        stubCommon(score, t);

        AspirationScoreResult result = service.calculateForAspiration(aspiration("THPT"));

        assertThat(result).isNotNull();
        // ĐTHXT = (8.0 + 7.5 + 9.0) / 3 * 3 = 24.5
        assertThat(result.diemThxt()).isEqualTo(24.5);
    }

    @Test
    @DisplayName("Tổ hợp [HAT, VE, MUA] (3 môn NK) → môn thứ 3 null, không tính được điểm")
    void threeTalentSubjects_thirdReturnsNull() {
        XtDiemthixettuyen score = thptScoreWithNk(null, null, 8.0, 7.0);
        XtNganhTohop t = tohop("HAT", "VE", "MUA");
        // calculateForTohopThpt return null khi 1 slot null → no result
        when(scoreRepository.findByCccdAndDPhuongthuc(CCCD, "THPT")).thenReturn(Optional.of(score));
        when(nganhTohopRepository.findByManganh(MA_NGANH)).thenReturn(List.of(t));

        AspirationScoreResult result = service.calculateForAspiration(aspiration("THPT"));

        assertThat(result).isNull();
    }

    @Test
    @DisplayName("Tổ hợp [TO, LI, HO] (không NK) → tính bình thường, không động đến nk1/nk2")
    void noTalentSubject_normalCalc() {
        XtDiemthixettuyen score = XtDiemthixettuyen.builder()
                .id(1).cccd(CCCD).sobaodanh("SBD001").dPhuongthuc("THPT")
                .to(8.0).li(7.0).ho(6.0)
                .isDeleted(false).build();
        XtNganhTohop t = tohop("TO", "LI", "HO");
        stubCommon(score, t);

        AspirationScoreResult result = service.calculateForAspiration(aspiration("THPT"));

        assertThat(result).isNotNull();
        // ĐTHXT = (8 + 7 + 6) / 3 * 3 = 21.0
        assertThat(result.diemThxt()).isEqualTo(21.0);
    }

    @Test
    @DisplayName("Tổ hợp ĐGNL [NL1, NK1, NK2] (literal) → vẫn map đúng cột tương ứng")
    void literalNkCodes_stillWork() {
        // Đây là behavior cũ — đảm bảo refactor không break tổ hợp dùng literal NK1/NK2.
        // NK1/NK2 nằm trong STANDARD_SUBJECTS → đi qua getScoreField, map case NK1→nk1, NK2→nk2.
        XtDiemthixettuyen score = XtDiemthixettuyen.builder()
                .id(1).cccd(CCCD).sobaodanh("SBD001").dPhuongthuc("DGNL")
                .nl1(800.0).nk1(8.0).nk2(7.0)
                .isDeleted(false).build();
        XtNganhTohop t = tohop("NL1", "NK1", "NK2");
        when(scoreRepository.findByCccdAndDPhuongthuc(CCCD, "DGNL")).thenReturn(Optional.of(score));
        lenient().when(nganhTohopRepository.findByManganh(MA_NGANH)).thenReturn(List.of(t));
        when(bonusScoreRepository.findByCccd(CCCD)).thenReturn(Optional.empty());
        when(majorRepository.findByManganh(MA_NGANH)).thenReturn(Optional.of(major()));
        when(nguyenVongRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        AspirationScoreResult result = service.calculateForAspiration(aspiration("DGNL"));

        assertThat(result).isNotNull();
        // DGNL không tính qua công thức 3-môn THPT — chỉ verify result không null,
        // nghĩa là pipeline không bị NPE/return null vì literal NK codes.
    }
}
