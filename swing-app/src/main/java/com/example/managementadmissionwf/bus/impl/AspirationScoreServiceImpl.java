package com.example.managementadmissionwf.bus.impl;

import com.example.managementadmissionwf.bus.interfaces.AspirationScoreService;
import com.example.managementadmissionwf.dal.entity.*;
import com.example.managementadmissionwf.dal.repository.*;
import com.example.managementadmissionwf.dto.score.AspirationScoreResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AspirationScoreServiceImpl implements AspirationScoreService {

    private final ScoreRepository scoreRepository;
    private final BonusScoreRepository bonusScoreRepository;
    private final ConversionTableRepository conversionTableRepository;
    private final NganhTohopRepository nganhTohopRepository;
    private final NguyenVongRepository nguyenVongRepository;
    private final MajorRepository majorRepository;

    @Override
    @Transactional
    public AspirationScoreResult calculateForAspiration(XtNguyenvongxettuyen aspiration) {
        String cccd = aspiration.getNnCccd();
        String maNganh = aspiration.getNvManganh();
        String preferredMethod = aspiration.getTtPhuongthuc();

        // Tìm điểm thi - ưu tiên theo phương thức được chỉ định trong nguyện vọng
        XtDiemthixettuyen score = findScoreByMethod(cccd, preferredMethod);
        if (score == null) {
            log.debug("Không tìm thấy điểm thi cho CCCD={}, phương thức={}, bỏ qua nguyện vọng ngành={}",
                    cccd, preferredMethod, maNganh);
            markInsufficientScore(aspiration);
            return null;
        }

        List<XtNganhTohop> tohops = nganhTohopRepository.findByManganh(maNganh);
        if (tohops.isEmpty()) {
            log.debug("Không có tổ hợp nào cho ngành={}, bỏ qua", maNganh);
            markInsufficientScore(aspiration);
            return null;
        }

        AspirationScoreResult bestResult = null;
        for (XtNganhTohop tohop : tohops) {
            AspirationScoreResult result = calculateForTohop(aspiration, score, tohop);
            if (result != null && (bestResult == null || result.diemXettuyen() > bestResult.diemXettuyen())) {
                bestResult = result;
            }
        }

        if (bestResult != null) {
            aspiration.setDiemThxt(bestResult.diemThxt());
            aspiration.setDiemUtqd(bestResult.diemUuTien());
            aspiration.setDiemCong(bestResult.diemCong());
            aspiration.setDiemXettuyen(bestResult.diemXettuyen());
            aspiration.setNvKetqua(bestResult.datDiemSan() ? "CHO_XET" : "THIEU_DIEM");
        } else {
            markInsufficientScore(aspiration);
        }

        nguyenVongRepository.save(aspiration);
        return bestResult;
    }

    @Override
    @Transactional
    public List<AspirationScoreResult> calculateAllForCccd(String cccd) {
        List<XtNguyenvongxettuyen> aspirations = nguyenVongRepository.findByNnCccd(cccd);
        List<AspirationScoreResult> results = new ArrayList<>();

        for (XtNguyenvongxettuyen aspiration : aspirations) {
            AspirationScoreResult result = calculateForAspiration(aspiration);
            if (result != null) {
                results.add(result);
            }
        }

        return results;
    }

    // ================= TÌM ĐIỂM THI THEO PHƯƠNG THỨC =================

    private XtDiemthixettuyen findScoreByMethod(String cccd, String preferredMethod) {
        if (preferredMethod != null && !preferredMethod.isEmpty()) {
            // Tìm điểm theo phương thức cụ thể
            Optional<XtDiemthixettuyen> scoreByMethod = scoreRepository.findByCccdAndDPhuongthuc(cccd, preferredMethod);
            if (scoreByMethod.isPresent()) {
                return scoreByMethod.get();
            }
        }

        // Fallback: lấy điểm đầu tiên tìm được (ưu tiên THPT > DGNL > VSAT)
        List<XtDiemthixettuyen> allScores = scoreRepository.findAllByCccd(cccd);
        if (allScores.isEmpty()) {
            return null;
        }

        // Ưu tiên THPT trước
        for (XtDiemthixettuyen score : allScores) {
            if ("THPT".equals(score.getDPhuongthuc())) {
                return score;
            }
        }
        // Rồi DGNL
        for (XtDiemthixettuyen score : allScores) {
            if ("DGNL".equals(score.getDPhuongthuc())) {
                return score;
            }
        }
        // Cuối cùng VSAT
        for (XtDiemthixettuyen score : allScores) {
            if ("VSAT".equals(score.getDPhuongthuc())) {
                return score;
            }
        }

        return allScores.get(0);
    }

    // ================= PIPELINE 9 BƯỚC =================

    private AspirationScoreResult calculateForTohop(XtNguyenvongxettuyen aspiration,
                                                     XtDiemthixettuyen score,
                                                     XtNganhTohop tohop) {
        String phuongThuc = score.getDPhuongthuc();

        // Bước 2-3: Map mã môn → lấy điểm gốc
        Double rawMon1 = getScoreField(score, tohop.getThMon1());
        Double rawMon2 = getScoreField(score, tohop.getThMon2());
        Double rawMon3 = getScoreField(score, tohop.getThMon3());

        if (rawMon1 == null || rawMon2 == null || rawMon3 == null) {
            return null;
        }

        // Bước 4: Quy đổi điểm
        Double convertedMon1 = convertScore(rawMon1, phuongThuc, tohop.getMatohop(), tohop.getThMon1());
        Double convertedMon2 = convertScore(rawMon2, phuongThuc, tohop.getMatohop(), tohop.getThMon2());
        Double convertedMon3 = convertScore(rawMon3, phuongThuc, tohop.getMatohop(), tohop.getThMon3());

        // Bước 5-6: Áp dụng hệ số + tính điểm THXT
        double diemThxt = calculateThxt(convertedMon1, convertedMon2, convertedMon3,
                tohop.getHsmon1(), tohop.getHsmon2(), tohop.getHsmon3());

        // Bước 7: Tách điểm cộng và điểm ưu tiên
        double diemCong = 0;
        double diemUtxt = 0;
        Optional<XtDiemcongxettuyen> bonusOpt = bonusScoreRepository.findByCccd(aspiration.getNnCccd());
        if (bonusOpt.isPresent()) {
            diemCong = bonusOpt.get().getDiemCc() != null ? bonusOpt.get().getDiemCc() : 0;
            diemUtxt = bonusOpt.get().getDiemUtxt() != null ? bonusOpt.get().getDiemUtxt() : 0;
        }
        double diemUuTien = calculatePriorityScore(diemUtxt, diemThxt);

        // Bước 8: Điểm xét tuyển cuối
        double diemXettuyen = diemThxt + diemUuTien + diemCong;

        // Bước 9: So sánh điểm sàn
        double diemSan = majorRepository.findByManganh(aspiration.getNvManganh())
                .map(XtNganh::getNDiemsan)
                .orElse(0.0);
        boolean datDiemSan = diemSan > 0 && diemXettuyen >= diemSan;

        return new AspirationScoreResult(
                aspiration.getNnCccd(),
                aspiration.getNvManganh(),
                tohop.getMatohop(),
                phuongThuc,
                rawMon1, rawMon2, rawMon3,
                convertedMon1, convertedMon2, convertedMon3,
                tohop.getHsmon1(), tohop.getHsmon2(), tohop.getHsmon3(),
                diemThxt,
                diemCong,
                diemUuTien,
                diemXettuyen,
                diemSan,
                datDiemSan,
                ""
        );
    }

    // ================= HELPER: MAP MÃ MÔN → ĐIỂM =================

    private Double getScoreField(XtDiemthixettuyen score, String monCode) {
        return switch (monCode) {
            case "TO" -> score.getTo();
            case "LI" -> score.getLi();
            case "HO" -> score.getHo();
            case "SI" -> score.getSi();
            case "SU" -> score.getSu();
            case "DI" -> score.getDi();
            case "VA" -> score.getVa();
            case "N1" -> calculateN1(score);
            case "NL1" -> score.getNl1();
            case "NK1" -> score.getNk1();
            case "NK2" -> score.getNk2();
            default -> {
                log.warn("Mã môn không hợp lệ: {}", monCode);
                yield null;
            }
        };
    }

    private Double calculateN1(XtDiemthixettuyen score) {
        Double n1Thi = score.getN1Thi();
        Double n1Cc = score.getN1Cc();
        if (n1Thi == null && n1Cc == null) return null;
        if (n1Cc == null) return n1Thi;
        if (n1Thi == null) return n1Cc;
        return Math.max(n1Thi, n1Cc);
    }

    // ================= HELPER: QUY ĐỔI ĐIỂM =================

    private Double convertScore(Double original, String phuongThuc, String toHop, String mon) {
        if (original == null) return null;

        Optional<XtBangquydoi> rule = conversionTableRepository
                .findByPhuongThucAndMonAndTohop(phuongThuc, mon, toHop);

        if (rule.isEmpty()) {
            rule = conversionTableRepository
                    .findByPhuongThucAndMonAndTohopIsNull(phuongThuc, mon);
        }

        if (rule.isPresent()) {
            XtBangquydoi r = rule.get();
            if (original >= r.getDDiema() && original <= r.getDDiemb()) {
                double range = r.getDDiemb() - r.getDDiema();
                if (range == 0) return r.getDDiemc();
                double ratio = (original - r.getDDiema()) / range;
                return r.getDDiemc() + ratio * (r.getDDiemd() - r.getDDiemc());
            }
        }

        return original;
    }

    // ================= HELPER: TÍNH ĐIỂM THXT =================

    private double calculateThxt(double mon1, double mon2, double mon3,
                                  double hs1, double hs2, double hs3) {
        double weightedSum = mon1 * hs1 + mon2 * hs2 + mon3 * hs3;

        if (hasCoefficient2(hs1, hs2, hs3)) {
            weightedSum = weightedSum * 3.0 / 4.0;
        }

        return Math.min(weightedSum, 30.0);
    }

    private boolean hasCoefficient2(double hs1, double hs2, double hs3) {
        return hs1 == 2.0 || hs2 == 2.0 || hs3 == 2.0;
    }

    // ================= HELPER: ĐIỂM ƯU TIÊN =================

    private double calculatePriorityScore(double diemUtxt, double diemThxt) {
        if (diemUtxt <= 0) return 0.0;

        if (diemThxt >= 22.5) {
            return ((30.0 - diemThxt) / 7.5) * diemUtxt;
        }

        return diemUtxt;
    }

    // ================= HELPER: MARK INSUFFICIENT =================

    private void markInsufficientScore(XtNguyenvongxettuyen aspiration) {
        aspiration.setDiemThxt(null);
        aspiration.setDiemUtqd(null);
        aspiration.setDiemCong(null);
        aspiration.setDiemXettuyen(null);
        aspiration.setNvKetqua("THIEU_DIEM");
    }
}