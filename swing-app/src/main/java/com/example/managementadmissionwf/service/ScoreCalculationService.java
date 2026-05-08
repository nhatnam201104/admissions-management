    package com.example.managementadmissionwf.service;

import com.example.managementadmissionwf.dal.entity.*;
import com.example.managementadmissionwf.dal.repository.*;
import com.example.managementadmissionwf.utils.AdmissionConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Service tính điểm xét tuyển theo 5 bước
 */
@Service
@RequiredArgsConstructor
public class ScoreCalculationService {

    private final ScoreRepository scoreRepository;
    private final NganhTohopRepository nganhTohopRepository;
    private final MajorRepository majorRepository;
    private final BangquydoiRepository bangquydoiRepository;

    /**
     * Tính điểm xét tuyển cho một nguyện vọng
     */
    public Double calculateScore(String cccd, String manganh, String tohopMon,
                                 String phuongThuc, Double diemCong, Double diemUtqd) {
        return switch (phuongThuc.toUpperCase()) {
            case "THPT" -> calculateTHPT(cccd, manganh, tohopMon, diemCong, diemUtqd);
            case "DGNL" -> calculateDGNL(cccd, manganh, diemCong, diemUtqd);
            case "VSAT" -> calculateVSAT(cccd, manganh, tohopMon, diemCong, diemUtqd);
            case "TUYEN_THANG" -> calculateTuyenThang(diemCong, diemUtqd);
            default -> null;
        };
    }

    // ==================== PHƯƠNG THỨC THPT ====================
    
    /**
     * Tính điểm xét tuyển THPT
     * - Điểm trực tiếp từ xt_diemthixettuyen (đã ở thang 10)
     * - Nhân hệ số từ xt_nganh_tohop
     * - Áp dụng ma trận độ lệch
     */
    private Double calculateTHPT(String cccd, String manganh, String tohopMon,
                                  Double diemCong, Double diemUtqd) {
        Optional<XtNganh> majorOpt = majorRepository.findByManganh(manganh);
        if (majorOpt.isEmpty()) return null;
        String tohopGoc = majorOpt.get().getNTohopgoc();

        Optional<XtDiemthixettuyen> scoreOpt = scoreRepository.findByCccdAndDPhuongthuc(cccd, "THPT");
        if (scoreOpt.isEmpty()) return null;
        XtDiemthixettuyen score = scoreOpt.get();

        List<XtNganhTohop> tohops = nganhTohopRepository.findByManganh(manganh);
        XtNganhTohop tohop = tohops.stream()
            .filter(t -> tohopMon.equals(t.getMatohop()))
            .findFirst().orElse(null);
        if (tohop == null) return null;

        double[] scores = getScoresTHPT(score, tohop);
        double dthxt = calculateDTHXT(scores, tohop);
        double dthgxt = dthxt - AdmissionConstants.getDeviationScore(tohopGoc, tohopMon);
        double dut = calculateDUT(dthgxt, dthxt, diemCong, diemUtqd);

        return Math.min(30.0, dthgxt + diemCong + dut);
    }

    private double[] getScoresTHPT(XtDiemthixettuyen score, XtNganhTohop tohop) {
        return new double[]{
            getSubjectScore(score, tohop.getThMon1()),
            getSubjectScore(score, tohop.getThMon2()),
            getSubjectScore(score, tohop.getThMon3())
        };
    }

    // ==================== PHƯƠNG THỨC ĐGNL ====================
    
    /**
     * Tính điểm xét tuyển ĐGNL
     * - Điểm từ NL1 + NK1 + NK2
     * - Quy đổi qua bảng xt_bangquydoi về thang 30
     * - Không áp dụng ma trận độ lệch
     */
    private Double calculateDGNL(String cccd, String manganh,
                                  Double diemCong, Double diemUtqd) {
        Optional<XtDiemthixettuyen> scoreOpt = scoreRepository.findByCccdAndDPhuongthuc(cccd, "DGNL");
        if (scoreOpt.isEmpty()) return null;
        XtDiemthixettuyen score = scoreOpt.get();

        double dgnlScore = convertDGNLScore(score);
        double dthxt = dgnlScore;
        double dthgxt = dthxt;
        double dut = calculateDUT(dthgxt, dthxt, diemCong, diemUtqd);

        return Math.min(30.0, dthgxt + diemCong + dut);
    }

    /**
     * Quy đổi điểm ĐGNL qua bảng quy đổi
     * Chỉ sử dụng NL1 (thang 1200), bỏ qua NK1, NK2
     */
    private double convertDGNLScore(XtDiemthixettuyen score) {
        Double nl1 = score.getNl1();

        // Nếu không có điểm NL1, trả về 0
        if (nl1 == null || nl1 <= 0) {
            return 0.0;
        }

        // Chỉ lấy NL1 (thang 1200), bỏ qua NK1, NK2 theo quy tắc
        double totalDgnl = nl1;

        // Quy đổi qua bảng xt_bangquydoi
        List<XtBangquydoi> conversionRules = bangquydoiRepository.findByDPhuongthuc("DGNL");
        
        if (conversionRules.isEmpty()) {
            // Nếu không có bảng quy đổi, quy đổi tuyến tính: 0-1200 → 0-30
            return totalDgnl * 30.0 / 1200.0;
        }

        // Tìm rule phù hợp trong bảng quy đổi
        for (XtBangquydoi rule : conversionRules) {
            if (rule.getDDiema() != null && rule.getDDiemb() != null) {
                if (totalDgnl >= rule.getDDiema() && totalDgnl <= rule.getDDiemb()) {
                    double a = rule.getDDiema();
                    double b = rule.getDDiemb();
                    double c = rule.getDDiemc();
                    double d = rule.getDDiemd() != null ? rule.getDDiemd() : 30.0;
                    
                    return c + ((totalDgnl - a) / (b - a)) * (d - c);
                }
            }
        }

        return 0.0;
    }

    // ==================== PHƯƠNG THỨC V-SAT ====================
    
    /**
     * Tính điểm xét tuyển V-SAT
     * - Điểm từ xt_diemthixettuyen
     * - Quy đổi qua bảng xt_bangquydoi về thang 10
     * - Áp dụng ma trận độ lệch
     */
    private Double calculateVSAT(String cccd, String manganh, String tohopMon,
                                   Double diemCong, Double diemUtqd) {
        Optional<XtNganh> majorOpt = majorRepository.findByManganh(manganh);
        if (majorOpt.isEmpty()) return null;
        String tohopGoc = majorOpt.get().getNTohopgoc();

        Optional<XtDiemthixettuyen> scoreOpt = scoreRepository.findByCccdAndDPhuongthuc(cccd, "VSAT");
        if (scoreOpt.isEmpty()) return null;
        XtDiemthixettuyen score = scoreOpt.get();

        List<XtNganhTohop> tohops = nganhTohopRepository.findByManganh(manganh);
        XtNganhTohop tohop = tohops.stream()
            .filter(t -> tohopMon.equals(t.getMatohop()))
            .findFirst().orElse(null);
        if (tohop == null) return null;

        double[] scores = getScoresVSAT(score, tohop, manganh, tohopMon);
        double dthxt = calculateDTHXT(scores, tohop);
        double dthgxt = dthxt - AdmissionConstants.getDeviationScore(tohopGoc, tohopMon);
        double dut = calculateDUT(dthgxt, dthxt, diemCong, diemUtqd);

        return Math.min(30.0, dthgxt + diemCong + dut);
    }

    private double[] getScoresVSAT(XtDiemthixettuyen score, XtNganhTohop tohop,
                                    String manganh, String tohopMon) {
        return new double[]{
            convertSubjectScore(score, tohop.getThMon1(), "VSAT"),
            convertSubjectScore(score, tohop.getThMon2(), "VSAT"),
            convertSubjectScore(score, tohop.getThMon3(), "VSAT")
        };
    }

    /**
     * Quy đổi điểm 1 môn qua bảng quy đổi
     */
    private double convertSubjectScore(XtDiemthixettuyen score, String mon, String phuongThuc) {
        double rawScore = getSubjectScore(score, mon);
        
        List<XtBangquydoi> rules = bangquydoiRepository.findByDPhuongthucAndDMon(phuongThuc, mon);
        
        if (rules.isEmpty()) {
            return rawScore;
        }

        for (XtBangquydoi rule : rules) {
            if (rawScore >= rule.getDDiema() && rawScore <= rule.getDDiemb()) {
                double a = rule.getDDiema();
                double b = rule.getDDiemb();
                double c = rule.getDDiemc();
                double d = rule.getDDiemd() != null ? rule.getDDiemd() : c + 2;
                
                return c + ((rawScore - a) / (b - a)) * (d - c);
            }
        }

        return rawScore;
    }

    // ==================== PHƯƠNG THỨC TUYỂN THẲNG ====================
    
    private Double calculateTuyenThang(Double diemCong, Double diemUtqd) {
        double diem = (diemCong != null ? diemCong : 0.0) 
                     + (diemUtqd != null ? diemUtqd : 0.0);
        return Math.min(30.0, Math.max(diem, 22.0));
    }

    // ==================== HELPER METHODS ====================

    private double getSubjectScore(XtDiemthixettuyen score, String subject) {
        if (subject == null) return 0.0;
        
        return switch (subject.toUpperCase()) {
            case "TO" -> score.getTo() != null ? score.getTo() : 0.0;
            case "LY", "LI" -> score.getLi() != null ? score.getLi() : 0.0;
            case "HO" -> score.getHo() != null ? score.getHo() : 0.0;
            case "SI" -> score.getSi() != null ? score.getSi() : 0.0;
            case "SU" -> score.getSu() != null ? score.getSu() : 0.0;
            case "DI" -> score.getDi() != null ? score.getDi() : 0.0;
            case "VA" -> score.getVa() != null ? score.getVa() : 0.0;
            case "AN" -> getNgoaiNguScore(score);
            case "NL1" -> score.getNl1() != null ? score.getNl1() : 0.0;
            case "NK1" -> score.getNk1() != null ? score.getNk1() : 0.0;
            case "NK2" -> score.getNk2() != null ? score.getNk2() : 0.0;
            default -> 0.0;
        };
    }

    private double getNgoaiNguScore(XtDiemthixettuyen score) {
        Double n1Thi = score.getN1Thi();
        Double n1Cc = score.getN1Cc();
        
        if (n1Thi == null && n1Cc == null) return 0.0;
        if (n1Thi == null) return n1Cc;
        if (n1Cc == null) return n1Thi;
        return Math.max(n1Thi, n1Cc);
    }

    /**
     * Bước 2: Tính Điểm Tổ Hợp Xét Tuyển (ĐTHXT)
     */
    private double calculateDTHXT(double[] scores, XtNganhTohop tohop) {
        double w1 = tohop.getHsmon1() != null ? tohop.getHsmon1() : 1.0;
        double w2 = tohop.getHsmon2() != null ? tohop.getHsmon2() : 1.0;
        double w3 = tohop.getHsmon3() != null ? tohop.getHsmon3() : 1.0;
        
        double W = w1 + w2 + w3;
        if (W == 0) W = 3.0;

        double weightedSum = scores[0] * w1 + scores[1] * w2 + scores[2] * w3;
        return (weightedSum / W) * 3.0;
    }

    /**
     * Bước 4: Tính Điểm Ưu Tiên (ĐƯT)
     */
    private double calculateDUT(double dthgxt, double dthxt, double diemCong, Double mucUuTien) {
        if (mucUuTien == null || mucUuTien <= 0 || mucUuTien == 0.0) {
            return 0.0;
        }

        double tongDiem = dthgxt + diemCong;
        
        if (tongDiem < 22.5) {
            return mucUuTien;
        } else {
            double heSo = (30.0 - dthxt - diemCong) / 7.5;
            return heSo * mucUuTien;
        }
    }
}