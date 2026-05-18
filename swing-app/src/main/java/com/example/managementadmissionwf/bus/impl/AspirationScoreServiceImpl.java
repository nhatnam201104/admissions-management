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

        // TUYEN_THANG không phụ thuộc điểm thi → đánh dấu CHO_XET, để auto
        // admission xử lý riêng. Không tính điểm, không markInsufficient.
        if ("TUYEN_THANG".equalsIgnoreCase(preferredMethod)) {
            aspiration.setDiemThxt(null);
            aspiration.setDiemUtqd(null);
            aspiration.setDiemCong(null);
            aspiration.setDiemXettuyen(null);
            if (aspiration.getNvKetqua() == null
                    || "THIEU_DIEM".equals(aspiration.getNvKetqua())) {
                aspiration.setNvKetqua("CHO_XET");
            }
            nguyenVongRepository.save(aspiration);
            return null;
        }

        // Tìm điểm thi ĐÚNG theo phương thức được chỉ định trong nguyện vọng.
        // KHÔNG fallback sang phương thức khác — nếu thí sinh chưa nhập điểm
        // cho phương thức này thì đánh dấu THIEU_DIEM, không lấy điểm phương
        // thức khác gán vào (sẽ làm sai lệch điểm xét tuyển).
        XtDiemthixettuyen score = findScoreByMethod(cccd, preferredMethod);
        if (score == null) {
            log.debug("Không tìm thấy điểm thi cho CCCD={}, phương thức={}, đánh dấu THIEU_DIEM cho nguyện vọng ngành={}",
                    cccd, preferredMethod, maNganh);
            markInsufficientScore(aspiration);
            nguyenVongRepository.save(aspiration);
            return null;
        }

        AspirationScoreResult bestResult;
        // Dispatch theo phương thức xét tuyển. 3 phương thức (THPT/VSAT/DGNL)
        // có pipeline tính điểm độc lập:
        //   - DGNL: 1 điểm NL1, tra bảng quy đổi theo tổ hợp gốc của ngành
        //   - VSAT: nhiều môn, tra bảng quy đổi theo (mon, tohop)
        //   - THPT: nhiều môn, KHÔNG quy đổi (raw đã ở thang 10)
        // findScoreByMethod đã đảm bảo score đúng phương thức của NV nên
        // chỉ cần kiểm tra preferredMethod.
        String method = preferredMethod == null ? "" : preferredMethod.trim().toUpperCase();
        bestResult = switch (method) {
            case "DGNL" -> calculateForDgnl(aspiration, score);
            case "VSAT" -> calculateForVsat(aspiration, score);
            case "THPT" -> calculateForThpt(aspiration, score);
            default -> {
                log.warn("Phương thức không hỗ trợ: {} (CCCD={}, ngành={})",
                        preferredMethod, cccd, maNganh);
                yield null;
            }
        };

        if (bestResult != null) {
            aspiration.setDiemThxt(bestResult.diemThxt());
            aspiration.setDiemUtqd(bestResult.diemUuTien());
            aspiration.setDiemCong(bestResult.diemCong());
            aspiration.setDiemXettuyen(bestResult.diemXettuyen());
            aspiration.setTtThm(bestResult.matohop());
            // Đã tính được điểm xét tuyển → trạng thái CHO_XET. KHÔNG dùng
            // datDiemSan ở đây vì THIEU_DIEM chỉ có nghĩa "thí sinh chưa có
            // điểm ứng với phương thức xét tuyển", không liên quan tới việc
            // đạt/không đạt điểm sàn hay điểm chuẩn (do bước xét tuyển
            // sau quyết định).
            aspiration.setNvKetqua("CHO_XET");
        } else {
            // Đã pass findScoreByMethod (có điểm phương thức) nhưng không
            // tính được điểm xét tuyển — nguyên nhân thường là setup ngành
            // (chưa cấu hình tổ hợp) hoặc tổ hợp yêu cầu môn thí sinh không
            // có. Không phải THIEU_DIEM theo định nghĩa của hệ thống.
            // Để CHO_XET cho admin kiểm tra (log warn đã được phát ở
            // logMissingSubject / loadCandidateTohops).
            aspiration.setDiemThxt(null);
            aspiration.setDiemUtqd(null);
            aspiration.setDiemCong(null);
            aspiration.setDiemXettuyen(null);
            aspiration.setNvKetqua("CHO_XET");
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

    /**
     * Lấy điểm thi của thí sinh theo ĐÚNG phương thức của nguyện vọng.
     *
     * <p><b>Lưu ý:</b> KHÔNG fallback sang phương thức khác. Nếu thí sinh
     * chưa có điểm cho phương thức được chỉ định (ví dụ: NV phương thức VSAT
     * nhưng thí sinh mới chỉ nhập điểm THPT), trả về {@code null} để caller
     * đánh dấu THIEU_DIEM. Trước đây code có fallback ưu tiên
     * THPT > DGNL > VSAT — điều này gây bug nghiêm trọng: NV đăng ký phương
     * thức VSAT/DGNL nhưng lại được tính bằng điểm THPT, làm sai lệch điểm
     * xét tuyển và kết quả admission.
     */
    private XtDiemthixettuyen findScoreByMethod(String cccd, String preferredMethod) {
        if (preferredMethod == null || preferredMethod.isBlank()) {
            // Không xác định được phương thức → không thể chọn đúng record
            // điểm. Đánh dấu thiếu điểm để admin sửa lại NV.
            return null;
        }
        return scoreRepository.findByCccdAndDPhuongthuc(cccd, preferredMethod)
                .orElse(null);
    }

    // ================= DGNL: KHÔNG CẦN TỔ HỢP =================

    /**
     * Tính điểm xét tuyển cho phương thức ĐGNL: chỉ dùng cột {@code nl1}.
     * Quy đổi qua bảng quy đổi (DGNL × NL1) hoặc fallback {@code nl1×30/1200}.
     */
    private AspirationScoreResult calculateForDgnl(XtNguyenvongxettuyen aspiration, XtDiemthixettuyen score) {
        Double nl1 = score.getNl1();
        if (nl1 == null) {
            log.debug("ĐGNL: thiếu NL1 cho CCCD={}", aspiration.getNnCccd());
            return null;
        }
        String tohopGoc = majorRepository.findByManganh(aspiration.getNvManganh())
                .map(XtNganh::getNTohopgoc)
                .orElse(null);
        Double converted = convertScoreDgnl(nl1, tohopGoc);
        if (converted == null) {
            converted = nl1 * 30.0 / 1200.0;
        }
        double diemThxt = Math.min(converted, 30.0);

        double diemCong = 0.0;
        double diemUtxt = 0.0;
        Optional<XtDiemcongxettuyen> bonusOpt = bonusScoreRepository.findByCccd(aspiration.getNnCccd());
        if (bonusOpt.isPresent()) {
            diemCong = bonusOpt.get().getDiemCc() != null ? bonusOpt.get().getDiemCc() : 0;
            diemUtxt = bonusOpt.get().getDiemUtxt() != null ? bonusOpt.get().getDiemUtxt() : 0;
        }
        double diemUuTien = calculatePriorityScore(diemUtxt, diemThxt, diemCong);
        double diemXettuyen = diemThxt + diemUuTien + diemCong;

        double diemSan = majorRepository.findByManganh(aspiration.getNvManganh())
                .map(XtNganh::getNDiemsan)
                .orElse(0.0);
        boolean datDiemSan = diemSan <= 0 || diemXettuyen >= diemSan;

        return new AspirationScoreResult(
                aspiration.getNnCccd(),
                aspiration.getNvManganh(),
                null, // tổ hợp không áp dụng cho DGNL
                "DGNL",
                nl1, 0.0, 0.0,
                converted, null, null,
                1.0, 1.0, 1.0,
                diemThxt,
                diemCong,
                diemUuTien,
                diemXettuyen,
                diemSan,
                datDiemSan,
                ""
        );
    }

    // ================= THPT: KHÔNG QUY ĐỔI ĐIỂM =================

    /**
     * Tính điểm xét tuyển cho phương thức THPT. Điểm gốc đã ở thang 10,
     * KHÔNG tra bảng quy đổi. Loop qua các tổ hợp khả thi của ngành, chọn
     * tổ hợp cho điểm xét tuyển cao nhất.
     */
    private AspirationScoreResult calculateForThpt(XtNguyenvongxettuyen aspiration,
                                                    XtDiemthixettuyen score) {
        List<XtNganhTohop> candidateTohops = loadCandidateTohops(aspiration);
        if (candidateTohops == null) {
            return null;
        }
        AspirationScoreResult bestResult = null;
        for (XtNganhTohop tohop : candidateTohops) {
            AspirationScoreResult r = calculateForTohopThpt(aspiration, score, tohop);
            if (r != null && (bestResult == null || r.diemXettuyen() > bestResult.diemXettuyen())) {
                bestResult = r;
            }
        }
        return bestResult;
    }

    private AspirationScoreResult calculateForTohopThpt(XtNguyenvongxettuyen aspiration,
                                                         XtDiemthixettuyen score,
                                                         XtNganhTohop tohop) {
        Double rawMon1 = getScoreField(score, tohop.getThMon1());
        Double rawMon2 = getScoreField(score, tohop.getThMon2());
        Double rawMon3 = getScoreField(score, tohop.getThMon3());
        if (rawMon1 == null || rawMon2 == null || rawMon3 == null) {
            logMissingSubject(aspiration, tohop, "THPT", rawMon1, rawMon2, rawMon3);
            return null;
        }
        // THPT identity: convertedMon = rawMon, KHÔNG tra xt_bangquydoi.
        return buildTohopResult(aspiration, tohop, "THPT",
                rawMon1, rawMon2, rawMon3,
                rawMon1, rawMon2, rawMon3);
    }

    // ================= VSAT: QUY ĐỔI THEO BẢNG =================

    /**
     * Tính điểm xét tuyển cho phương thức VSAT. Mỗi môn được quy đổi từ
     * thang 150 về thang 10 qua bảng {@code xt_bangquydoi}, sau đó áp
     * công thức tổ hợp chung.
     */
    private AspirationScoreResult calculateForVsat(XtNguyenvongxettuyen aspiration,
                                                    XtDiemthixettuyen score) {
        List<XtNganhTohop> candidateTohops = loadCandidateTohops(aspiration);
        if (candidateTohops == null) {
            return null;
        }
        AspirationScoreResult bestResult = null;
        for (XtNganhTohop tohop : candidateTohops) {
            AspirationScoreResult r = calculateForTohopVsat(aspiration, score, tohop);
            if (r != null && (bestResult == null || r.diemXettuyen() > bestResult.diemXettuyen())) {
                bestResult = r;
            }
        }
        return bestResult;
    }

    private AspirationScoreResult calculateForTohopVsat(XtNguyenvongxettuyen aspiration,
                                                         XtDiemthixettuyen score,
                                                         XtNganhTohop tohop) {
        Double rawMon1 = getScoreField(score, tohop.getThMon1());
        Double rawMon2 = getScoreField(score, tohop.getThMon2());
        Double rawMon3 = getScoreField(score, tohop.getThMon3());
        if (rawMon1 == null || rawMon2 == null || rawMon3 == null) {
            logMissingSubject(aspiration, tohop, "VSAT", rawMon1, rawMon2, rawMon3);
            return null;
        }
        Double convertedMon1 = convertScoreVsat(rawMon1, tohop.getMatohop(), tohop.getThMon1());
        Double convertedMon2 = convertScoreVsat(rawMon2, tohop.getMatohop(), tohop.getThMon2());
        Double convertedMon3 = convertScoreVsat(rawMon3, tohop.getMatohop(), tohop.getThMon3());
        return buildTohopResult(aspiration, tohop, "VSAT",
                rawMon1, rawMon2, rawMon3,
                convertedMon1, convertedMon2, convertedMon3);
    }

    // ================= HELPER: LOAD TỔ HỢP CỦA NGÀNH =================

    /**
     * Load danh sách tổ hợp của ngành, filter theo {@code tt_thm} nếu user
     * đã chỉ định 1 tổ hợp cụ thể. Trả về {@code null} (đã set THIEU_DIEM)
     * khi danh sách rỗng hoặc tt_thm invalid — caller chỉ cần return null.
     *
     * <p>Lưu ý: KHÔNG gán THIEU_DIEM trong các case này. THIEU_DIEM theo
     * định nghĩa hệ thống chỉ áp dụng khi thí sinh chưa có điểm cho phương
     * thức của NV — đã được handle ở {@code findScoreByMethod}. Các case ở
     * đây là vấn đề setup ngành/tổ hợp, không phải về điểm thi.
     */
    private List<XtNganhTohop> loadCandidateTohops(XtNguyenvongxettuyen aspiration) {
        String maNganh = aspiration.getNvManganh();
        List<XtNganhTohop> tohops = nganhTohopRepository.findByManganh(maNganh);
        if (tohops.isEmpty()) {
            log.warn("Ngành {} chưa cấu hình tổ hợp xét tuyển nào — bỏ qua tính điểm cho NV CCCD={}",
                    maNganh, aspiration.getNnCccd());
            return null;
        }
        if (aspiration.getTtThm() != null && !aspiration.getTtThm().isBlank()) {
            List<XtNganhTohop> filtered = tohops.stream()
                    .filter(t -> aspiration.getTtThm().equals(t.getMatohop()))
                    .toList();
            if (filtered.isEmpty()) {
                log.warn("Tổ hợp {} không hợp lệ cho ngành {} — bỏ qua tính điểm cho NV CCCD={}",
                        aspiration.getTtThm(), maNganh, aspiration.getNnCccd());
                return null;
            }
            return filtered;
        }
        return tohops;
    }

    // ================= HELPER: BUILD RESULT (TAIL CHUNG) =================

    /**
     * Tail chung sau bước convert: tính ĐTHXT, áp điểm cộng + ưu tiên,
     * so sánh điểm sàn, build {@link AspirationScoreResult}.
     */
    private AspirationScoreResult buildTohopResult(XtNguyenvongxettuyen aspiration,
                                                    XtNganhTohop tohop,
                                                    String phuongThuc,
                                                    double rawMon1, double rawMon2, double rawMon3,
                                                    double convertedMon1, double convertedMon2, double convertedMon3) {
        double diemThxt = calculateThxt(convertedMon1, convertedMon2, convertedMon3,
                tohop.getHsmon1(), tohop.getHsmon2(), tohop.getHsmon3());

        double diemCong = 0;
        double diemUtxt = 0;
        Optional<XtDiemcongxettuyen> bonusOpt = bonusScoreRepository.findByCccd(aspiration.getNnCccd());
        if (bonusOpt.isPresent()) {
            diemCong = bonusOpt.get().getDiemCc() != null ? bonusOpt.get().getDiemCc() : 0;
            diemUtxt = bonusOpt.get().getDiemUtxt() != null ? bonusOpt.get().getDiemUtxt() : 0;
        }
        double diemUuTien = calculatePriorityScore(diemUtxt, diemThxt, diemCong);
        double diemXettuyen = diemThxt + diemUuTien + diemCong;

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

    /**
     * Log chi tiết môn nào của tổ hợp bị thiếu điểm để admin debug khi user
     * báo "đã có điểm nhưng vẫn thiếu" — thường do tổ hợp yêu cầu môn không
     * có trong record điểm thi của thí sinh.
     */
    private void logMissingSubject(XtNguyenvongxettuyen aspiration, XtNganhTohop tohop,
                                    String phuongThuc, Double m1, Double m2, Double m3) {
        List<String> missing = new ArrayList<>();
        if (m1 == null) missing.add(tohop.getThMon1());
        if (m2 == null) missing.add(tohop.getThMon2());
        if (m3 == null) missing.add(tohop.getThMon3());
        log.warn("Tổ hợp {} ngành {} thiếu môn {} cho CCCD={} phương thức={}",
                tohop.getMatohop(), aspiration.getNvManganh(),
                String.join(", ", missing), aspiration.getNnCccd(), phuongThuc);
    }

    // ================= HELPER: MAP MÃ MÔN → ĐIỂM =================

    private Double getScoreField(XtDiemthixettuyen score, String monCode) {
        // Normalize: bảng xt_tohop_monthi định nghĩa Tiếng Anh là "AN" (A01,
        // B03, D01...), nhưng cột điểm trong xt_diemthixettuyen lưu N1_THI/N1_CC.
        // Cũ chỉ map "N1" → mọi tổ hợp có "AN" đều bị thiếu điểm. Map cả 2.
        String code = monCode != null ? monCode.trim().toUpperCase() : "";
        return switch (code) {
            case "TO" -> score.getTo();
            case "LI", "LY" -> score.getLi();
            case "HO", "HH" -> score.getHo();
            case "SI", "SH" -> score.getSi();
            case "SU", "LS" -> score.getSu();
            case "DI", "DL" -> score.getDi();
            case "VA", "NV" -> score.getVa();
            case "AN", "N1" -> calculateN1(score);
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

    // ================= HELPER: QUY ĐỔI ĐIỂM VSAT =================

    private static final double VSAT_MAX_RAW = 150.0;
    private static final double VSAT_MAX_CONVERTED = 10.0;

    /**
     * Quy đổi điểm môn VSAT từ thang 150 về thang 10. Tra bảng
     * {@code xt_bangquydoi} với phuongThuc='VSAT', match (mon, tohop)
     * trước, fallback (mon, tohop IS NULL). Nội suy tuyến tính trong
     * khoảng [diema, diemb] → [diemc, diemd]. Nếu không có rule nào
     * khớp, dùng fallback {@code raw × 10/150}.
     *
     * <p>Method này CHỈ dùng cho VSAT — THPT không quy đổi (raw đã ở
     * thang 10), DGNL có {@link #convertScoreDgnl} riêng.
     */
    private Double convertScoreVsat(Double original, String toHop, String mon) {
        if (original == null) return null;

        java.util.List<XtBangquydoi> rules =
                conversionTableRepository.findAllByPhuongThucAndMonAndTohop("VSAT", mon, toHop);
        if (rules.isEmpty()) {
            rules = conversionTableRepository.findAllByPhuongThucAndMonAndTohopIsNull("VSAT", mon);
        }

        for (XtBangquydoi r : rules) {
            if (r.getDDiema() == null || r.getDDiemb() == null) continue;
            if (original >= r.getDDiema() && original <= r.getDDiemb()) {
                double range = r.getDDiemb() - r.getDDiema();
                if (range == 0) return r.getDDiemc();
                double ratio = (original - r.getDDiema()) / range;
                double c = r.getDDiemc() != null ? r.getDDiemc() : 0.0;
                double d = r.getDDiemd() != null ? r.getDDiemd() : c;
                return c + ratio * (d - c);
            }
        }

        // Fallback tuyến tính khi data bị thiếu rule
        log.warn("VSAT: không tìm thấy rule cho mon={}, tohop={}, raw={} — dùng fallback tuyến tính",
                mon, toHop, original);
        return original * VSAT_MAX_CONVERTED / VSAT_MAX_RAW;
    }

    /**
     * Quy đổi điểm DGNL (NL1) theo bảng bách phân vị tương ứng với tổ hợp gốc.
     * Tra cứu rule theo phuongThuc='DGNL', mon IS NULL, tohop=tohopGoc.
     * Nội suy tuyến tính trong khoảng [diema, diemb] → [diemc, diemd].
     * Fallback: nl1 × 30 / 1200 khi không tìm thấy rule phù hợp.
     */
    private Double convertScoreDgnl(Double nl1, String tohopGoc) {
        if (nl1 == null) return null;

        List<XtBangquydoi> rules = (tohopGoc != null)
                ? conversionTableRepository.findAllByPhuongThucAndMonIsNullAndTohop("DGNL", tohopGoc)
                : List.of();

        for (XtBangquydoi r : rules) {
            if (r.getDDiema() == null || r.getDDiemb() == null) continue;
            if (nl1 >= r.getDDiema() && nl1 <= r.getDDiemb()) {
                double range = r.getDDiemb() - r.getDDiema();
                if (range == 0) return r.getDDiemc();
                double ratio = (nl1 - r.getDDiema()) / range;
                double c = r.getDDiemc() != null ? r.getDDiemc() : 0.0;
                double d = r.getDDiemd() != null ? r.getDDiemd() : c;
                return c + ratio * (d - c);
            }
        }

        // Fallback tuyến tính
        return nl1 * 30.0 / 1200.0;
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

    private double calculatePriorityScore(double diemUtxt, double diemThxt, double diemCong) {
        if (diemUtxt <= 0) return 0.0;

        if ((diemThxt + diemCong) >= 22.5) {
            return ((30.0 - diemThxt - diemCong) / 7.5) * diemUtxt;
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
