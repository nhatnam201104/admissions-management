package com.example.thymeleaf_web.service.impl;

import com.example.thymeleaf_web.model.dto.CalculatedAspirationRow;
import com.example.thymeleaf_web.model.dto.CalculatorOutput;
import com.example.thymeleaf_web.model.dto.ConvertedScoreRow;
import com.example.thymeleaf_web.model.dto.MajorSuggestion;
import com.example.thymeleaf_web.model.dto.ScoreCalculatorForm;
import com.example.thymeleaf_web.model.dto.ScoreCalculatorResult;
import com.example.thymeleaf_web.model.dto.TohopResult;
import com.example.thymeleaf_web.model.entity.BangQuyDoi;
import com.example.thymeleaf_web.model.entity.Nganh;
import com.example.thymeleaf_web.model.entity.NganhToHop;
import com.example.thymeleaf_web.repository.BangQuyDoiRepository;
import com.example.thymeleaf_web.repository.NganhRepository;
import com.example.thymeleaf_web.repository.NganhToHopRepository;
import com.example.thymeleaf_web.service.ScoreCalculatorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Cài đặt {@link ScoreCalculatorService} với logic đồng bộ với
 * {@code AspirationScoreServiceImpl} ở module swing-app.
 * <p>
 * Pipeline:
 * <ol>
 *   <li>DGNL: tra bảng {@code xt_bangquydoi (phuongThuc=DGNL, mon IS NULL, tohop=tohopGoc)},
 *       nội suy tuyến tính. Fallback: {@code nl1 × 30 / 1200}.</li>
 *   <li>VSAT/THPT: với mỗi tổ hợp của ngành, tra bảng theo
 *       {@code (phuongThuc, mon, tohop)} hoặc fallback {@code (phuongThuc, mon, tohop IS NULL)}.
 *       Nếu vẫn không có rule → giữ nguyên điểm gốc (đồng nhất với swing).</li>
 *   <li>ĐTHXT = {@code mon1×hs1 + mon2×hs2 + mon3×hs3}; nếu có hệ số 2 thì {@code × 3/4}.
 *       Cap tối đa 30.</li>
 *   <li>Điểm ưu tiên (ĐUT): {@code 0} nếu utxt ≤ 0;
 *       {@code ((30 - thxt - cong) / 7.5) × utxt} nếu {@code thxt + cong ≥ 22.5};
 *       còn lại bằng utxt.</li>
 *   <li>ĐXT = ĐTHXT + ĐUT + Điểm cộng. So sánh điểm sàn / điểm trúng tuyển.</li>
 * </ol>
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ScoreCalculatorServiceImpl implements ScoreCalculatorService {

    private static final double PRIORITY_THRESHOLD = 22.5;
    private static final double MAX_SCORE = 30.0;
    private static final double VSAT_MAX_RAW = 150.0;
    private static final double VSAT_MAX_CONVERTED = 10.0;

    private final NganhRepository nganhRepo;
    private final NganhToHopRepository nganhToHopRepo;
    private final BangQuyDoiRepository bangQuyDoiRepo;

    @Override
    public List<Nganh> getActiveMajors() {
        return nganhRepo.findAllActive().stream()
                .sorted((a, b) -> a.getManganh().compareToIgnoreCase(b.getManganh()))
                .toList();
    }

    @Override
    public ScoreCalculatorResult calculate(ScoreCalculatorForm form) {
        String phuongThuc = canonicalMethod(form.getPhuongThuc());
        Nganh nganh = nganhRepo.findAllActive().stream()
                .filter(n -> n.getManganh().equalsIgnoreCase(form.getManganh()))
                .findFirst()
                .orElse(null);

        double diemUtxt = nullSafe(form.getDoiTuongUuTien(), 0.0)
                + nullSafe(form.getKhuVucUuTien(), 0.0);
        double diemCong = nullSafe(form.getDiemCong(), 0.0);

        List<CalculatedAspirationRow> rows;
        List<ConvertedScoreRow> convertedScores = new ArrayList<>();

        if (nganh == null) {
            rows = List.of();
        } else if ("DGNL".equals(phuongThuc)) {
            rows = calculateDgnl(form, nganh, diemUtxt, diemCong);
        } else {
            rows = calculateSubjectBased(form, nganh, phuongThuc, diemUtxt, diemCong);
        }
        rows.forEach(r -> convertedScores.addAll(r.subjects()));

        return new ScoreCalculatorResult(
                phuongThuc,
                nganh != null ? nganh.getTennganh() : null,
                nganh != null ? nganh.getManganh() : form.getManganh(),
                nganh != null ? nganh.getNDiemsan() : null,
                nganh != null ? nganh.getNDiemtrungtuyen() : null,
                form.getDoiTuongUuTien(),
                form.getKhuVucUuTien(),
                diemUtxt,
                diemCong,
                convertedScores,
                rows
        );
    }

    // ===================== Phương thức ĐGNL =====================

    private List<CalculatedAspirationRow> calculateDgnl(ScoreCalculatorForm form, Nganh nganh,
                                                        double diemUtxt, double diemCong) {
        Double diemDgnl = form.getDiemDgnl();
        if (diemDgnl == null) {
            return List.of();
        }

        String tohopGoc = nganh.getNTohopgoc();
        ConversionResult converted = convertScoreDgnl(diemDgnl, tohopGoc);
        double diemThxt = Math.min(nullSafe(converted.value(), 0.0), MAX_SCORE);
        double diemUuTien = priorityScore(diemUtxt, diemThxt, diemCong);
        double diemXetTuyen = clampScore(diemThxt + diemUuTien + diemCong);

        ConvertedScoreRow row = new ConvertedScoreRow(
                "NL1", "ĐGNL " + (tohopGoc != null ? "(" + tohopGoc + ")" : ""),
                diemDgnl, converted.value(), converted.formula(), converted.converted());

        boolean capApplied = diemUtxt > 0 && (diemThxt + diemCong) >= PRIORITY_THRESHOLD;
        String formula = format(diemThxt) + " + " + format(diemCong) + " + " + format(diemUuTien);

        return List.of(buildRow(tohopGoc != null ? tohopGoc : "—",
                List.of(row), diemThxt, diemUuTien, diemCong, diemXetTuyen,
                capApplied, formula, nganh));
    }

    private ConversionResult convertScoreDgnl(Double nl1, String tohopGoc) {
        if (nl1 == null) {
            return new ConversionResult(null, "Thiếu điểm", false);
        }
        if (tohopGoc != null && !tohopGoc.isBlank()) {
            List<BangQuyDoi> rules = bangQuyDoiRepo
                    .findByPhuongThucMonIsNullAndToHop("DGNL", tohopGoc);
            for (BangQuyDoi r : rules) {
                if (r.getDiemA() == null || r.getDiemB() == null) continue;
                if (nl1 >= r.getDiemA() && nl1 <= r.getDiemB()) {
                    return new ConversionResult(interpolate(nl1, r),
                            buildConversionFormula(nl1, r), true);
                }
            }
        }
        // Fallback đồng bộ swing: nl1 × 30 / 1200
        return new ConversionResult(nl1 * 30.0 / 1200.0,
                format(nl1) + " × 30 / 1200", true);
    }

    // ===================== Phương thức VSAT/THPT =====================

    private List<CalculatedAspirationRow> calculateSubjectBased(ScoreCalculatorForm form, Nganh nganh,
                                                                String phuongThuc, double diemUtxt,
                                                                double diemCong) {
        List<NganhToHop> tohops = nganhToHopRepo.findByManganhOrderByMatohopAsc(nganh.getManganh());
        if (tohops.isEmpty()) {
            return List.of();
        }
        List<CalculatedAspirationRow> rows = new ArrayList<>();
        for (NganhToHop th : tohops) {
            String[] subjects = {th.getThMon1(), th.getThMon2(), th.getThMon3()};
            double[] weights = {nullSafe(th.getHsmon1(), 1.0),
                    nullSafe(th.getHsmon2(), 1.0),
                    nullSafe(th.getHsmon3(), 1.0)};

            List<ConvertedScoreRow> subjectRows = new ArrayList<>();
            double[] convertedValues = new double[3];

            for (int i = 0; i < 3; i++) {
                Double raw = subjectScoreForSlot(form, subjects, i);
                ConversionResult cr = convertScore(raw, phuongThuc, th.getMatohop(), subjects[i]);
                double value = nullSafe(cr.value(), 0.0);
                // Cộng điểm khuyến khích cho môn (sau khi quy đổi)
                if (subjects[i] != null
                        && normalizeSubjectCode(subjects[i]).equalsIgnoreCase(form.getMonCongDiem())) {
                    value = clampSubjectScore(value + nullSafe(form.getMucCongMon(), 0.0));
                }
                convertedValues[i] = value;
                subjectRows.add(new ConvertedScoreRow(
                        normalizeSubjectCode(subjects[i]), subjectLabel(subjects[i]),
                        raw, cr.value(), cr.formula(), cr.converted()));
            }

            double diemThxt = calculateThxt(convertedValues, weights);
            double diemUuTien = priorityScore(diemUtxt, diemThxt, diemCong);
            double diemXetTuyen = clampScore(diemThxt + diemUuTien + diemCong);
            boolean capApplied = diemUtxt > 0 && (diemThxt + diemCong) >= PRIORITY_THRESHOLD;
            String formula = buildWeightedFormula(convertedValues, weights)
                    + " + " + format(diemCong) + " + " + format(diemUuTien);

            rows.add(buildRow(th.getMatohop(), subjectRows, diemThxt, diemUuTien,
                    diemCong, diemXetTuyen, capApplied, formula, nganh));
        }
        return rows;
    }

    private CalculatedAspirationRow buildRow(String matohop, List<ConvertedScoreRow> subjects,
                                             double diemThxt, double diemUuTienQd, double diemCong,
                                             double diemXetTuyen, boolean capApplied,
                                             String formula, Nganh nganh) {
        Boolean datSan = nganh.getNDiemsan() != null
                ? diemXetTuyen >= nganh.getNDiemsan() : null;
        Boolean datTT = nganh.getNDiemtrungtuyen() != null
                ? diemXetTuyen >= nganh.getNDiemtrungtuyen() : null;
        return new CalculatedAspirationRow(matohop, subjects, diemThxt, diemUuTienQd,
                diemCong, diemXetTuyen, capApplied, formula, datSan, datTT);
    }

    // ===================== Lookup điểm môn từ form =====================

    /**
     * Tập mã môn "chuẩn". Đồng bộ với
     * {@code AspirationScoreServiceImpl.STANDARD_SUBJECTS} ở swing-app:
     * mã không thuộc tập này được coi là môn năng khiếu (HAT, VE, GDC,
     * TIENG_DUC, ...) và map theo vị trí trong tổ hợp vào nk1/nk2.
     */
    private static final java.util.Set<String> STANDARD_SUBJECTS = java.util.Set.of(
            "TO", "LI", "LY", "HO", "HH", "SI", "SH",
            "SU", "LS", "DI", "DL", "VA", "NV",
            "AN", "N1", "NL1", "NK1", "NK2"
    );

    private static boolean isTalentSubject(String code) {
        if (code == null) return false;
        return !STANDARD_SUBJECTS.contains(code.trim().toUpperCase(Locale.ROOT));
    }

    /**
     * Tra điểm cho 1 slot trong tổ hợp. Môn năng khiếu được map theo vị trí
     * xuất hiện: môn NK đầu tiên trong tổ hợp → nk1, thứ hai → nk2. Đồng nhất
     * logic với swing-app để dialog/preview ra cùng kết quả.
     */
    private Double subjectScoreForSlot(ScoreCalculatorForm form, String[] subjects, int slotIndex) {
        String code = subjects[slotIndex];
        if (code == null) return null;
        if (isTalentSubject(code)) {
            int rank = 0;
            for (int i = 0; i <= slotIndex; i++) {
                if (isTalentSubject(subjects[i])) rank++;
            }
            return switch (rank) {
                case 1 -> form.getDiemNk1();
                case 2 -> form.getDiemNk2();
                default -> null;
            };
        }
        return subjectScoreFromForm(form, code);
    }

    private Double subjectScoreFromForm(ScoreCalculatorForm form, String subjectCode) {
        if (subjectCode == null) {
            return null;
        }
        return switch (normalizeSubjectCode(subjectCode)) {
            case "TO" -> form.getDiemToan();
            case "LI" -> form.getDiemLy();
            case "HO" -> form.getDiemHoa();
            case "SI" -> form.getDiemSinh();
            case "SU" -> form.getDiemSu();
            case "DI" -> form.getDiemDia();
            case "VA" -> form.getDiemVan();
            case "N1" -> maxNullable(form.getDiemAnh(), form.getDiemAnhCc());
            case "NK1" -> form.getDiemNk1();
            case "NK2" -> form.getDiemNk2();
            default -> null;
        };
    }

    // ===================== Bảng quy đổi (đồng bộ swing) =====================

    private ConversionResult convertScore(Double rawScore, String phuongThuc,
                                          String toHop, String subjectCode) {
        if (rawScore == null) {
            return new ConversionResult(null, "Thiếu điểm", false);
        }
        String code = normalizeSubjectCode(subjectCode);

        // Bước 1: tìm rule khớp đúng (phuongThuc, mon, tohop)
        List<BangQuyDoi> rules = bangQuyDoiRepo
                .findByPhuongThucMonAndToHop(phuongThuc, code, toHop != null ? toHop : "");
        // Bước 2: fallback rule chung (tohop NULL/empty)
        if (rules.isEmpty()) {
            rules = bangQuyDoiRepo.findByPhuongThucMonAndToHopIsNull(phuongThuc, code);
        }

        for (BangQuyDoi r : rules) {
            if (r.getDiemA() == null || r.getDiemB() == null) continue;
            if (rawScore >= r.getDiemA() && rawScore <= r.getDiemB()) {
                return new ConversionResult(interpolate(rawScore, r),
                        buildConversionFormula(rawScore, r), true);
            }
        }
        // Fallback: VSAT raw [0,150] → converted [0,10] tuyến tính.
        // THPT giữ identity. Đồng bộ với swing-app.
        if ("VSAT".equalsIgnoreCase(phuongThuc)) {
            log.warn("VSAT: không tìm thấy rule cho mon={}, tohop={}, raw={} — dùng fallback tuyến tính",
                    code, toHop, rawScore);
            double fallback = rawScore * VSAT_MAX_CONVERTED / VSAT_MAX_RAW;
            return new ConversionResult(fallback,
                    format(rawScore) + " × 10 / 150 (fallback)", true);
        }
        // THPT: giữ nguyên điểm gốc
        return new ConversionResult(rawScore,
                "Không có bảng quy đổi → giữ nguyên điểm", false);
    }

    private double interpolate(Double rawScore, BangQuyDoi rule) {
        double a = rule.getDiemA();
        double b = rule.getDiemB();
        double c = nullSafe(rule.getDiemC(), 0.0);
        double d = nullSafe(rule.getDiemD(), c);
        if (b == a) {
            return c;
        }
        return c + ((rawScore - a) / (b - a)) * (d - c);
    }

    private String buildConversionFormula(Double rawScore, BangQuyDoi r) {
        return format(rawScore) + " ∈ [" + format(r.getDiemA()) + ";" + format(r.getDiemB()) + "] → "
                + format(r.getDiemC()) + " + ((x-" + format(r.getDiemA()) + ")/("
                + format(r.getDiemB()) + "-" + format(r.getDiemA()) + "))×("
                + format(nullSafe(r.getDiemD(), r.getDiemC())) + "-" + format(r.getDiemC()) + ")";
    }

    // ===================== ĐTHXT (đồng bộ swing) =====================

    /**
     * Công thức ĐTHXT đồng bộ với {@code AspirationScoreServiceImpl#calculateThxt}:
     * <pre>
     *   weighted = m1×h1 + m2×h2 + m3×h3
     *   if any hệ số = 2 → weighted × 3/4
     *   cap tối đa 30
     * </pre>
     */
    private double calculateThxt(double[] scores, double[] weights) {
        double weighted = scores[0] * weights[0] + scores[1] * weights[1] + scores[2] * weights[2];
        if (hasCoefficient2(weights)) {
            weighted = weighted * 3.0 / 4.0;
        }
        return Math.min(weighted, MAX_SCORE);
    }

    private boolean hasCoefficient2(double[] weights) {
        for (double w : weights) {
            if (w == 2.0) return true;
        }
        return false;
    }

    private String buildWeightedFormula(double[] scores, double[] weights) {
        String base = format(scores[0]) + "×" + format(weights[0])
                + " + " + format(scores[1]) + "×" + format(weights[1])
                + " + " + format(scores[2]) + "×" + format(weights[2]);
        if (hasCoefficient2(weights)) {
            return "(" + base + ") × 3/4";
        }
        return base;
    }

    // ===================== Điểm ưu tiên (đồng bộ swing) =====================

    /**
     * Công thức điểm ưu tiên đồng bộ với
     * {@code AspirationScoreServiceImpl#calculatePriorityScore}:
     * <pre>
     *   if utxt ≤ 0           → 0
     *   if (thxt + cong) ≥ 22.5 → ((30 - thxt - cong) / 7.5) × utxt
     *   else                  → utxt
     * </pre>
     */
    private double priorityScore(double diemUtxt, double diemThxt, double diemCong) {
        if (diemUtxt <= 0) {
            return 0.0;
        }
        if ((diemThxt + diemCong) >= PRIORITY_THRESHOLD) {
            return Math.max(0.0, ((MAX_SCORE - diemThxt - diemCong) / 7.5) * diemUtxt);
        }
        return diemUtxt;
    }

    private double clampScore(double value) {
        return Math.min(Math.max(value, 0.0), MAX_SCORE);
    }

    private double clampSubjectScore(double value) {
        return Math.min(Math.max(value, 0.0), 10.0);
    }

    private String canonicalMethod(String value) {
        if (value == null || value.isBlank()) {
            return "THPT";
        }
        String n = value.trim().toUpperCase(Locale.ROOT);
        return switch (n) {
            case "DGNL", "DG_NL", "DGNL_HCM" -> "DGNL";
            case "VSAT", "VSAT2025" -> "VSAT";
            default -> "THPT";
        };
    }

    private String normalizeSubjectCode(String code) {
        if (code == null || code.isBlank()) {
            return "-";
        }
        String n = code.trim().toUpperCase(Locale.ROOT);
        return switch (n) {
            case "LY" -> "LI";
            case "AN" -> "N1";
            default -> n;
        };
    }

    private String subjectLabel(String code) {
        return switch (normalizeSubjectCode(code)) {
            case "TO" -> "Toán";
            case "LI" -> "Vật lý";
            case "HO" -> "Hóa học";
            case "SI" -> "Sinh học";
            case "SU" -> "Lịch sử";
            case "DI" -> "Địa lý";
            case "VA" -> "Ngữ văn";
            case "N1" -> "Tiếng Anh";
            case "NL1" -> "ĐGNL";
            case "NK1" -> "Năng khiếu 1";
            case "NK2" -> "Năng khiếu 2";
            default -> code != null ? code : "-";
        };
    }

    private double nullSafe(Double v, double fb) {
        return v != null ? v : fb;
    }

    private Double maxNullable(Double a, Double b) {
        if (a == null) return b;
        if (b == null) return a;
        return Math.max(a, b);
    }

    private String format(Double v) {
        return v != null ? String.format(Locale.US, "%.2f", v) : "-";
    }

    /** Kết quả quy đổi 1 môn / 1 điểm. */
    private record ConversionResult(Double value, String formula, boolean converted) {
    }

    // ============================================================
    // ===== UNIVERSAL CALCULATOR (reference-style, no major) =====
    // ============================================================

    private static final List<String> VSAT_SUBJECTS = List.of(
            "TO", "LI", "HO", "SI", "VA", "SU", "DI", "N1");
    private static final Map<String, String> SUBJECT_NAMES = Map.of(
            "TO", "Toán", "LI", "Vật lí", "HO", "Hóa học", "SI", "Sinh học",
            "VA", "Ngữ văn", "SU", "Lịch sử", "DI", "Địa lí", "N1", "Tiếng Anh");

    @Override
    public CalculatorOutput calculateUniversal(ScoreCalculatorForm form) {
        String phuongThuc = canonicalMethod(form.getPhuongThuc());

        if ("DGNL".equals(phuongThuc)) {
            return calculateUniversalDgnl(form);
        }
        return calculateUniversalSubjectBased(form, phuongThuc);
    }

    /**
     * Flow VSAT/THPT: quy đổi từng môn (không gắn ngành) → tính ĐTHXT
     * cho mọi tổ hợp distinct → gợi ý ngành theo điểm sàn.
     */
    private CalculatorOutput calculateUniversalSubjectBased(ScoreCalculatorForm form,
                                                              String phuongThuc) {
        // 1. Quy đổi điểm từng môn (chỉ những môn có giá trị)
        Map<String, Double> convertedBySubject = new LinkedHashMap<>();
        Map<String, Double> rawBySubject = new LinkedHashMap<>();
        List<ConvertedScoreRow> convertedRows = new ArrayList<>();
        for (String code : VSAT_SUBJECTS) {
            Double raw = subjectScoreFromForm(form, code);
            if (raw == null) continue;
            ConversionResult cr = convertScore(raw, phuongThuc, null, code);
            Double v = cr.value();
            if (v == null) continue;
            convertedBySubject.put(code, v);
            rawBySubject.put(code, raw);
            convertedRows.add(new ConvertedScoreRow(
                    code, SUBJECT_NAMES.getOrDefault(code, code),
                    raw, v, cr.formula(), cr.converted()));
        }

        // 2. Tính tất cả tổ hợp distinct
        List<NganhToHop> distinctTohops = nganhToHopRepo.findDistinctMatohop();
        Map<String, TohopResult> tohopResults = new LinkedHashMap<>();
        for (NganhToHop t : distinctTohops) {
            tohopResults.put(t.getMatohop(), buildTohopResult(t, convertedBySubject));
        }

        // 3. Gợi ý ngành: với mỗi ngành, lấy tổ hợp tốt nhất + áp ưu tiên
        double diemUtxt = nullSafe(form.getDoiTuongUuTien(), 0.0)
                + nullSafe(form.getKhuVucUuTien(), 0.0);
        double diemCong = nullSafe(form.getDiemCong(), 0.0);

        List<MajorSuggestion> suggestions = new ArrayList<>();
        int qualified = 0;
        List<Nganh> activeMajors = nganhRepo.findAllActive();
        for (Nganh n : activeMajors) {
            List<NganhToHop> majorTohops = nganhToHopRepo
                    .findByManganhOrderByMatohopAsc(n.getManganh());
            if (majorTohops.isEmpty()) continue;
            TohopResult best = null;
            for (NganhToHop t : majorTohops) {
                TohopResult r = buildTohopResult(t, convertedBySubject);
                if (!r.fullScore()) continue;
                if (best == null || r.diemThxt() > best.diemThxt()) {
                    best = r;
                }
            }
            if (best == null) continue;
            double diemThxt = best.diemThxt();
            double diemUuTien = priorityScore(diemUtxt, diemThxt, diemCong);
            double diemXt = clampScore(diemThxt + diemUuTien + diemCong);
            String level = classifyLevel(diemXt, n.getNDiemsan());
            if ("VUA_SUC".equals(level)) qualified++;
            suggestions.add(new MajorSuggestion(
                    n.getManganh(), n.getTennganh(), best.matohop(),
                    diemXt, n.getNDiemsan(), level));
        }
        suggestions.sort(Comparator.comparing(MajorSuggestion::diemXt).reversed());

        return new CalculatorOutput(
                phuongThuc,
                convertedRows,
                new ArrayList<>(tohopResults.values()),
                suggestions,
                activeMajors.size(),
                qualified,
                null
        );
    }

    /**
     * DGNL: 1 slider 0-1200, không tổ hợp. Mỗi ngành có tổ hợp gốc riêng
     * (n_tohopgoc) → quy đổi DGNL theo từng tổ hợp gốc khi tính suggestion.
     */
    private CalculatorOutput calculateUniversalDgnl(ScoreCalculatorForm form) {
        Double nl1 = form.getDiemDgnl();
        if (nl1 == null) {
            return new CalculatorOutput("DGNL", List.of(), List.of(), List.of(),
                    nganhRepo.findAllActive().size(), 0, null);
        }
        double diemUtxt = nullSafe(form.getDoiTuongUuTien(), 0.0)
                + nullSafe(form.getKhuVucUuTien(), 0.0);
        double diemCong = nullSafe(form.getDiemCong(), 0.0);

        // Cache quy đổi DGNL theo tohopGoc để tránh tính lại nhiều lần
        Map<String, Double> dgnlCache = new HashMap<>();
        List<MajorSuggestion> suggestions = new ArrayList<>();
        int qualified = 0;
        List<Nganh> activeMajors = nganhRepo.findAllActive();
        Double anyConverted = null;
        for (Nganh n : activeMajors) {
            String tohopGoc = n.getNTohopgoc();
            Double converted = dgnlCache.computeIfAbsent(
                    tohopGoc != null ? tohopGoc : "",
                    k -> convertScoreDgnl(nl1, k.isEmpty() ? null : k).value());
            if (converted == null) continue;
            if (anyConverted == null) anyConverted = converted;
            double diemThxt = Math.min(converted, MAX_SCORE);
            double diemUuTien = priorityScore(diemUtxt, diemThxt, diemCong);
            double diemXt = clampScore(diemThxt + diemUuTien + diemCong);
            String level = classifyLevel(diemXt, n.getNDiemsan());
            if ("VUA_SUC".equals(level)) qualified++;
            suggestions.add(new MajorSuggestion(
                    n.getManganh(), n.getTennganh(),
                    tohopGoc != null ? tohopGoc : "—",
                    diemXt, n.getNDiemsan(), level));
        }
        suggestions.sort(Comparator.comparing(MajorSuggestion::diemXt).reversed());

        return new CalculatorOutput(
                "DGNL",
                List.of(),
                List.of(),
                suggestions,
                activeMajors.size(),
                qualified,
                anyConverted
        );
    }

    private TohopResult buildTohopResult(NganhToHop t, Map<String, Double> converted) {
        String[] codes = {
                normalizeSubjectCode(t.getThMon1()),
                normalizeSubjectCode(t.getThMon2()),
                normalizeSubjectCode(t.getThMon3())
        };
        double[] hs = {
                nullSafe(t.getHsmon1(), 1.0),
                nullSafe(t.getHsmon2(), 1.0),
                nullSafe(t.getHsmon3(), 1.0)
        };
        boolean full = true;
        double[] vals = new double[3];
        for (int i = 0; i < 3; i++) {
            Double v = converted.get(codes[i]);
            if (v == null) { full = false; break; }
            vals[i] = v;
        }
        String text = SUBJECT_NAMES.getOrDefault(codes[0], codes[0]) + " - "
                + SUBJECT_NAMES.getOrDefault(codes[1], codes[1]) + " - "
                + SUBJECT_NAMES.getOrDefault(codes[2], codes[2]);
        Double thxt = full ? calculateThxt(vals, hs) : null;
        return new TohopResult(t.getMatohop(), text, thxt, full);
    }

    /**
     * Phân loại mức độ phù hợp dựa trên điểm sàn:
     *  - VUA_SUC: đạt sàn
     *  - THU_THACH: thiếu &lt; 2 điểm
     *  - KHO: thiếu &gt;= 2 điểm
     *  - CHUA_CONG_BO: ngành chưa công bố sàn
     */
    private String classifyLevel(double diemXt, Double diemSan) {
        if (diemSan == null || diemSan <= 0) return "CHUA_CONG_BO";
        if (diemXt >= diemSan) return "VUA_SUC";
        if (diemSan - diemXt < 2.0) return "THU_THACH";
        return "KHO";
    }
}
