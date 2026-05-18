package com.example.thymeleaf_web.service.impl;

import com.example.thymeleaf_web.model.dto.AspirationDto;
import com.example.thymeleaf_web.model.dto.CalculationStep;
import com.example.thymeleaf_web.model.dto.ConvertedScoreRow;
import com.example.thymeleaf_web.model.dto.ScoreCalculationDetail;
import com.example.thymeleaf_web.model.dto.ScoreLookupExamScore;
import com.example.thymeleaf_web.model.dto.ScoreLookupResult;
import com.example.thymeleaf_web.model.entity.BangQuyDoi;
import com.example.thymeleaf_web.model.entity.DiemCong;
import com.example.thymeleaf_web.model.entity.DiemThi;
import com.example.thymeleaf_web.model.entity.Nganh;
import com.example.thymeleaf_web.model.entity.NganhToHop;
import com.example.thymeleaf_web.model.entity.NguyenVong;
import com.example.thymeleaf_web.model.entity.Thisinh;
import com.example.thymeleaf_web.repository.BangQuyDoiRepository;
import com.example.thymeleaf_web.repository.DiemCongRepository;
import com.example.thymeleaf_web.repository.DiemThiRepository;
import com.example.thymeleaf_web.repository.NganhRepository;
import com.example.thymeleaf_web.repository.NganhToHopRepository;
import com.example.thymeleaf_web.repository.NguyenVongRepository;
import com.example.thymeleaf_web.repository.ThisinhRepository;
import com.example.thymeleaf_web.service.ScoreLookupService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ScoreLookupServiceImpl implements ScoreLookupService {

    private final ThisinhRepository thisinhRepo;
    private final DiemThiRepository diemThiRepo;
    private final DiemCongRepository diemCongRepo;
    private final NguyenVongRepository nguyenVongRepo;
    private final NganhRepository nganhRepo;
    private final NganhToHopRepository nganhToHopRepo;
    private final BangQuyDoiRepository bangQuyDoiRepo;

    @Override
    public Optional<ScoreLookupResult> lookupByCccd(String cccd) {
        Thisinh thisinh = thisinhRepo.findByCccdActive(cccd).orElse(null);
        if (thisinh == null) {
            return Optional.empty();
        }

        return Optional.of(buildResult(thisinh));
    }

    @Override
    public Optional<ScoreLookupResult> lookupByCccdAndNgaySinh(String cccd, LocalDate ngaySinh) {
        if (ngaySinh == null) {
            return Optional.empty();
        }

        return thisinhRepo.findByCccdAndNgaySinhActive(cccd, ngaySinh)
                .map(this::buildResult);
    }

    private ScoreLookupResult buildResult(Thisinh thisinh) {
        String cccd = thisinh.getCccd();
        List<DiemThi> diemThiRows = diemThiRepo.findAllByCccdActive(cccd);
        List<ScoreLookupExamScore> diemThiList = diemThiRows.stream()
                .map(this::toExamScore)
                .toList();
        DiemCong diemCong = diemCongRepo.findByCccdActive(cccd).orElse(null);
        List<NguyenVong> nguyenVongs = nguyenVongRepo.findByCccdActive(cccd);

        Map<String, Nganh> nganhMap = nganhRepo.findAllActive().stream()
                .collect(Collectors.toMap(Nganh::getManganh, nganh -> nganh, (a, b) -> a));

        // Sort theo thứ tự ưu tiên (nv_tt) tăng dần để xác định nguyện vọng
        // được nhận chính thức (NV đậu có thứ tự thấp nhất). Theo quy chế
        // tuyển sinh, mỗi thí sinh chỉ được trúng tuyển VÀO MỘT nguyện vọng.
        List<NguyenVong> sortedNvs = nguyenVongs.stream()
                .sorted(Comparator.comparing(NguyenVong::getNvTt,
                        Comparator.nullsLast(Comparator.naturalOrder())))
                .toList();

        // Tìm NV ưu tiên cao nhất có ketQua = TRUNG_TUYEN.
        Integer winningNvTt = sortedNvs.stream()
                .filter(nv -> "TRUNG_TUYEN".equalsIgnoreCase(nv.getNvKetqua()))
                .map(NguyenVong::getNvTt)
                .findFirst()
                .orElse(null);

        List<AspirationDto> aspirationDtos = sortedNvs.stream()
                .map(nv -> {
                    Nganh nganh = nganhMap.get(nv.getNvManganh());
                    boolean finalAdmitted = winningNvTt != null
                            && winningNvTt.equals(nv.getNvTt());
                    return new AspirationDto(
                            nv.getNvTt(),
                            nv.getNvManganh(),
                            nganh != null ? nganh.getTennganh() : nv.getNvManganh(),
                            nv.getTtPhuongthuc(),
                            nv.getTtThm(),
                            nv.getDiemThxt(),
                            nv.getDiemUtqd(),
                            nv.getDiemCong(),
                            nv.getDiemXettuyen(),
                            nv.getNvKetqua(),
                            finalAdmitted,
                            buildCalculationDetail(nv, diemThiRows, diemCong)
                    );
                })
                .toList();


        String displayName = thisinh.getHoVaTen() != null
                ? thisinh.getHoVaTen()
                : thisinh.getHo() + " " + thisinh.getTen();

        return new ScoreLookupResult(
                thisinh.getCccd(),
                thisinh.getSobaodanh(),
                displayName,
                thisinh.getNgaySinh(),
                diemThiList,
                diemCong != null ? diemCong.getDiemCc() : null,
                diemCong != null ? diemCong.getDiemUtxt() : null,
                diemCong != null ? diemCong.getDiemTong() : null,
                aspirationDtos
        );
    }

    @Override
    public List<Nganh> getAllActiveNganh() {
        return nganhRepo.findAllActive().stream()
                .sorted((a, b) -> a.getManganh().compareToIgnoreCase(b.getManganh()))
                .toList();
    }

    private ScoreCalculationDetail buildCalculationDetail(NguyenVong nv,
                                                          List<DiemThi> diemThiRows,
                                                          DiemCong diemCongEntity) {
        // Chuẩn hoá phương thức: hỗ trợ các biến thể đang có trong DB
        // (XT_TT → TUYEN_THANG, XET_THPT → THPT). Nếu thí sinh chưa được
        // ghi phương thức, mặc định là THPT để hiển thị công thức cơ bản.
        String phuongThuc = canonicalMethod(nv.getTtPhuongthuc());

        DiemThi diemThi = findScoreByMethod(diemThiRows, phuongThuc);
        // Nếu tìm theo đúng phương thức không có, dùng dòng đầu để vẫn show
        // được điểm gốc — đánh dấu incomplete trong các step bên dưới.
        if (diemThi == null && !diemThiRows.isEmpty()) {
            diemThi = diemThiRows.get(0);
        }

        double diemCong = nullSafe(nv.getDiemCong(),
                nullSafe(diemCongEntity != null ? diemCongEntity.getDiemCc() : null, 0.0));
        double mucUuTien = nullSafe(diemCongEntity != null ? diemCongEntity.getDiemUtxt() : null,
                nullSafe(nv.getDiemUtqd(), 0.0));
        double diemThxtDaLuu = nullSafe(nv.getDiemThxt(), 0.0);
        double diemUuTienDaLuu = nullSafe(nv.getDiemUtqd(),
                calculatePriorityScore(mucUuTien, diemThxtDaLuu));
        double diemXetTuyenDaLuu = nullSafe(nv.getDiemXettuyen(),
                diemThxtDaLuu + diemCong + diemUuTienDaLuu);

        ScoreCalculationDetail detail = switch (phuongThuc) {
            case "DGNL" ->
                    buildDgnlDetail(nv, diemThi, diemCong, mucUuTien, diemUuTienDaLuu, diemXetTuyenDaLuu);
            case "THPT", "VSAT" ->
                    buildSubjectCombinationDetail(nv, diemThi, phuongThuc, diemCong, mucUuTien,
                            diemUuTienDaLuu, diemXetTuyenDaLuu);
            case "TUYEN_THANG" ->
                    buildDirectAdmissionDetail(nv, diemCong, mucUuTien, diemXetTuyenDaLuu);
            default ->
                    buildGenericDetail(nv, phuongThuc, diemCong, mucUuTien,
                            diemUuTienDaLuu, diemXetTuyenDaLuu);
        };

        // Nếu kết quả vẫn rỗng (thiếu hết data), fallback sang generic để
        // thí sinh vẫn xem được công thức tổng quát.
        if (!detail.hasDetails()) {
            return buildGenericDetail(nv, phuongThuc, diemCong, mucUuTien,
                    diemUuTienDaLuu, diemXetTuyenDaLuu);
        }
        return detail;
    }

    /**
     * Trả về 1 breakdown tối thiểu cho mọi phương thức: hiển thị đúng các
     * giá trị đã lưu trong DB (ĐTHXT, ĐUT, ĐC, ĐXT). Dùng cho phương thức
     * không có script tính riêng hoặc khi dữ liệu input không đủ.
     */
    private ScoreCalculationDetail buildGenericDetail(NguyenVong nv, String phuongThuc,
                                                      double diemCong, double mucUuTien,
                                                      double diemUuTienDaLuu, double diemXetTuyenDaLuu) {
        double diemThxt = nullSafe(nv.getDiemThxt(), 0.0);
        boolean capApplied = mucUuTien > 0 && diemThxt >= 22.5;
        boolean incomplete = nv.getDiemThxt() == null || nv.getDiemXettuyen() == null;
        List<CalculationStep> steps = List.of(
                new CalculationStep(1, "Điểm tổ hợp xét tuyển (ĐTHXT)",
                        "Điểm đã lưu cho nguyện vọng",
                        diemThxt, false, false, incomplete),
                new CalculationStep(2, "Điểm cộng",
                        format(diemCong),
                        diemCong, false, false, false),
                new CalculationStep(3, "Điểm ưu tiên (ĐƯT)",
                        buildPriorityFormula(diemThxt, mucUuTien, capApplied),
                        diemUuTienDaLuu, false, capApplied, false),
                new CalculationStep(4, "Điểm xét tuyển (ĐXT)",
                        format(diemThxt) + " + " + format(diemCong) + " + "
                                + format(diemUuTienDaLuu),
                        diemXetTuyenDaLuu, true, false, incomplete)
        );
        return new ScoreCalculationDetail(phuongThuc, nv.getTtThm(), List.of(), steps, incomplete);
    }

    private String canonicalMethod(String value) {
        String normalized = normalize(value);
        if (normalized == null) {
            return "THPT";
        }
        return switch (normalized) {
            case "XT_TT", "XETTHANG", "XET_TT", "XETTUYENTHANG" -> "TUYEN_THANG";
            case "XET_THPT", "DIEM_THPT", "TN_THPT" -> "THPT";
            case "DG_NL", "DGNL_HCM", "DANHGIA_NANGLUC" -> "DGNL";
            case "VSAT", "VSAT2025" -> "VSAT";
            default -> normalized;
        };
    }


    private ScoreCalculationDetail buildDgnlDetail(NguyenVong nv, DiemThi diemThi,
                                                   double diemCong, double mucUuTien,
                                                   double diemUuTienDaLuu, double diemXetTuyenDaLuu) {
        if (diemThi == null || diemThi.getNl1() == null) {
            return ScoreCalculationDetail.empty();
        }

        ConversionResult converted = convertScore(diemThi.getNl1(), "DGNL", null, "NL1");
        double diemThxt = nullSafe(nv.getDiemThxt(), nullSafe(converted.value(), 0.0));
        boolean capApplied = mucUuTien > 0 && diemThxt >= 22.5;

        List<ConvertedScoreRow> convertedScores = List.of(new ConvertedScoreRow(
                "NL1",
                "Đánh giá năng lực",
                diemThi.getNl1(),
                converted.value(),
                converted.formula(),
                converted.converted()
        ));

        List<CalculationStep> steps = new ArrayList<>();
        steps.add(new CalculationStep(1, "Quy đổi ĐGNL", converted.formula(), diemThxt, false, false, false));
        steps.add(new CalculationStep(2, "Điểm cộng", format(diemCong), diemCong, false, false, false));
        steps.add(new CalculationStep(3, "Điểm ưu tiên (ĐƯT)",
                buildPriorityFormula(diemThxt, mucUuTien, capApplied),
                diemUuTienDaLuu, false, capApplied, false));
        steps.add(new CalculationStep(4, "Điểm xét tuyển (ĐXT)",
                format(diemThxt) + " + " + format(diemCong) + " + " + format(diemUuTienDaLuu),
                diemXetTuyenDaLuu, true, false, false));

        return new ScoreCalculationDetail("DGNL", nv.getTtThm(), convertedScores, steps, false);
    }

    private ScoreCalculationDetail buildSubjectCombinationDetail(NguyenVong nv, DiemThi diemThi,
                                                                 String phuongThuc, double diemCong,
                                                                 double mucUuTien, double diemUuTienDaLuu,
                                                                 double diemXetTuyenDaLuu) {
        if (diemThi == null) {
            return ScoreCalculationDetail.empty();
        }

        NganhToHop toHop = findMatchedToHop(nv.getNvManganh(), nv.getTtThm());
        if (toHop == null) {
            return ScoreCalculationDetail.empty();
        }

        String[] subjects = {toHop.getThMon1(), toHop.getThMon2(), toHop.getThMon3()};
        double[] weights = {
                nullSafe(toHop.getHsmon1(), 1.0),
                nullSafe(toHop.getHsmon2(), 1.0),
                nullSafe(toHop.getHsmon3(), 1.0)
        };

        List<ConvertedScoreRow> convertedScores = new ArrayList<>();
        double[] convertedValues = new double[3];
        boolean incomplete = false;

        for (int i = 0; i < subjects.length; i++) {
            Double raw = getSubjectScore(diemThi, subjects[i]);
            ConversionResult converted = convertScore(raw, phuongThuc, toHop.getMatohop(), subjects[i]);
            if (raw == null || converted.value() == null) {
                incomplete = true;
            }
            convertedValues[i] = nullSafe(converted.value(), 0.0);
            convertedScores.add(new ConvertedScoreRow(
                    normalizeSubjectCode(subjects[i]),
                    subjectLabel(subjects[i]),
                    raw,
                    converted.value(),
                    converted.formula(),
                    converted.converted()
            ));
        }

        double diemThxtTinh = calculateWeightedScore(convertedValues, weights);
        double diemThxt = nullSafe(nv.getDiemThxt(), diemThxtTinh);
        boolean capApplied = mucUuTien > 0 && diemThxt >= 22.5;

        List<CalculationStep> steps = new ArrayList<>();
        steps.add(new CalculationStep(1, "Quy đổi điểm",
                "Xem bảng điểm quy đổi theo từng môn",
                convertedValues[0] + convertedValues[1] + convertedValues[2],
                false, false, incomplete));
        steps.add(new CalculationStep(2, "Tính điểm tổ hợp (ĐTHXT)",
                buildWeightedFormula(convertedValues, weights),
                diemThxt, false, false, incomplete));
        steps.add(new CalculationStep(3, "Điểm cộng", format(diemCong),
                diemCong, false, false, false));
        steps.add(new CalculationStep(4, "Điểm ưu tiên (ĐƯT)",
                buildPriorityFormula(diemThxt, mucUuTien, capApplied),
                diemUuTienDaLuu, false, capApplied, false));
        steps.add(new CalculationStep(5, "Điểm xét tuyển (ĐXT)",
                format(diemThxt) + " + " + format(diemCong) + " + " + format(diemUuTienDaLuu),
                diemXetTuyenDaLuu, true, false, incomplete));

        return new ScoreCalculationDetail(phuongThuc, toHop.getMatohop(), convertedScores, steps, incomplete);
    }

    private ScoreCalculationDetail buildDirectAdmissionDetail(NguyenVong nv, double diemCong,
                                                              double mucUuTien, double diemXetTuyenDaLuu) {
        List<CalculationStep> steps = List.of(
                new CalculationStep(1, "Điểm cộng", format(diemCong), diemCong, false, false, false),
                new CalculationStep(2, "Điểm ưu tiên", format(mucUuTien), mucUuTien, false, false, false),
                new CalculationStep(3, "Điểm xét tuyển tuyển thẳng",
                        "max(" + format(diemCong) + " + " + format(mucUuTien) + ", 22.00)",
                        diemXetTuyenDaLuu, true, false, false)
        );
        return new ScoreCalculationDetail("TUYEN_THANG", nv.getTtThm(), List.of(), steps, false);
    }

    private DiemThi findScoreByMethod(List<DiemThi> diemThiRows, String phuongThuc) {
        return diemThiRows.stream()
                .filter(diemThi -> phuongThuc.equals(normalize(diemThi.getPhuongThuc())))
                .findFirst()
                .orElse(null);
    }

    private NganhToHop findMatchedToHop(String manganh, String matohop) {
        List<NganhToHop> tohops = nganhToHopRepo.findByManganhOrderByMatohopAsc(manganh);
        if (tohops.isEmpty()) {
            return null;
        }
        if (matohop == null || matohop.isBlank()) {
            return tohops.get(0);
        }
        return tohops.stream()
                .filter(toHop -> matohop.equalsIgnoreCase(toHop.getMatohop()))
                .findFirst()
                .orElse(null);
    }

    private ConversionResult convertScore(Double rawScore, String phuongThuc, String toHop, String subjectCode) {
        if (rawScore == null) {
            return new ConversionResult(null, "Thiếu dữ liệu điểm", false);
        }
        if ("THPT".equals(phuongThuc)) {
            return new ConversionResult(rawScore, "Giữ nguyên điểm thi THPT", false);
        }

        String conversionSubject = conversionSubjectCode(subjectCode);
        Optional<BangQuyDoi> rule = findConversionRule(rawScore, phuongThuc, toHop, conversionSubject);
        if (rule.isPresent()) {
            BangQuyDoi bangQuyDoi = rule.get();
            double converted = interpolate(rawScore, bangQuyDoi);
            return new ConversionResult(converted, buildConversionFormula(rawScore, bangQuyDoi), true);
        }

        if ("DGNL".equals(phuongThuc) && "NL1".equals(conversionSubject)) {
            return new ConversionResult(rawScore * 30.0 / 1200.0, format(rawScore) + " × 30 / 1200", true);
        }

        return new ConversionResult(rawScore, "Không có bảng quy đổi phù hợp, giữ nguyên điểm gốc", false);
    }

    private Optional<BangQuyDoi> findConversionRule(Double rawScore, String phuongThuc,
                                                    String toHop, String subjectCode) {
        List<BangQuyDoi> rules = bangQuyDoiRepo
                .findByPhuongThucIgnoreCaseAndMonIgnoreCaseOrderByDiemAAsc(phuongThuc, subjectCode);

        Optional<BangQuyDoi> exactToHopRule = rules.stream()
                .filter(rule -> !isBlank(toHop) && !isBlank(rule.getToHop())
                        && toHop.equalsIgnoreCase(rule.getToHop()))
                .filter(rule -> contains(rule, rawScore))
                .findFirst();
        if (exactToHopRule.isPresent()) {
            return exactToHopRule;
        }

        return rules.stream()
                .filter(rule -> isBlank(rule.getToHop()))
                .filter(rule -> contains(rule, rawScore))
                .findFirst();
    }

    private boolean contains(BangQuyDoi rule, Double rawScore) {
        return rawScore != null
                && rule.getDiemA() != null
                && rule.getDiemB() != null
                && rawScore >= rule.getDiemA()
                && rawScore <= rule.getDiemB();
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

    private String buildConversionFormula(Double rawScore, BangQuyDoi rule) {
        double a = rule.getDiemA();
        double b = rule.getDiemB();
        double c = nullSafe(rule.getDiemC(), 0.0);
        double d = nullSafe(rule.getDiemD(), c);
        if (b == a) {
            return format(c);
        }
        return format(rawScore) + " trong [" + format(a) + ", " + format(b) + "] → "
                + format(c) + " + ((" + format(rawScore) + " - " + format(a) + ") / ("
                + format(b) + " - " + format(a) + ")) × (" + format(d) + " - " + format(c) + ")";
    }

    private Double getSubjectScore(DiemThi diemThi, String subjectCode) {
        if (diemThi == null || subjectCode == null) {
            return null;
        }
        return switch (normalizeSubjectCode(subjectCode)) {
            case "TO" -> diemThi.getToan();
            case "LI" -> diemThi.getLy();
            case "HO" -> diemThi.getHoa();
            case "SI" -> diemThi.getSinh();
            case "SU" -> diemThi.getSu();
            case "DI" -> diemThi.getDia();
            case "VA" -> diemThi.getVan();
            case "N1" -> maxNullable(diemThi.getN1Thi(), diemThi.getN1Cc());
            case "NL1" -> diemThi.getNl1();
            case "NK1" -> diemThi.getNk1();
            case "NK2" -> diemThi.getNk2();
            default -> null;
        };
    }

    private Double maxNullable(Double first, Double second) {
        if (first == null) {
            return second;
        }
        if (second == null) {
            return first;
        }
        return Math.max(first, second);
    }

    private double calculateWeightedScore(double[] scores, double[] weights) {
        double weightSum = weights[0] + weights[1] + weights[2];
        if (weightSum == 0) {
            weightSum = 3.0;
        }
        double weightedSum = scores[0] * weights[0] + scores[1] * weights[1] + scores[2] * weights[2];
        return (weightedSum / weightSum) * 3.0;
    }

    private String buildWeightedFormula(double[] scores, double[] weights) {
        double weightSum = weights[0] + weights[1] + weights[2];
        if (weightSum == 0) {
            weightSum = 3.0;
        }
        return "[(" + format(scores[0]) + "×" + format(weights[0])
                + " + " + format(scores[1]) + "×" + format(weights[1])
                + " + " + format(scores[2]) + "×" + format(weights[2])
                + ") / " + format(weightSum) + "] × 3";
    }

    private double calculatePriorityScore(double mucUuTien, double diemThxt) {
        if (mucUuTien <= 0) {
            return 0.0;
        }
        if (diemThxt >= 22.5) {
            return Math.max(0.0, ((30.0 - diemThxt) / 7.5) * mucUuTien);
        }
        return mucUuTien;
    }

    private String buildPriorityFormula(double diemThxt, double mucUuTien, boolean capApplied) {
        if (mucUuTien <= 0) {
            return "0.00 (không có ưu tiên)";
        }
        if (capApplied) {
            return "((30 - " + format(diemThxt) + ") / 7.5) × " + format(mucUuTien);
        }
        return format(mucUuTien) + " (giữ nguyên vì ĐTHXT < 22.50)";
    }

    private String normalize(String value) {
        return value == null || value.isBlank() ? null : value.trim().toUpperCase(Locale.ROOT);
    }

    private String normalizeSubjectCode(String subjectCode) {
        String normalized = normalize(subjectCode);
        if (normalized == null) {
            return "-";
        }
        return switch (normalized) {
            case "LY" -> "LI";
            case "AN" -> "N1";
            default -> normalized;
        };
    }

    private String conversionSubjectCode(String subjectCode) {
        return normalizeSubjectCode(subjectCode);
    }

    private String subjectLabel(String subjectCode) {
        return switch (normalizeSubjectCode(subjectCode)) {
            case "TO" -> "Toán";
            case "LI" -> "Vật lý";
            case "HO" -> "Hóa học";
            case "SI" -> "Sinh học";
            case "SU" -> "Lịch sử";
            case "DI" -> "Địa lý";
            case "VA" -> "Ngữ văn";
            case "N1" -> "Ngoại ngữ";
            case "NL1" -> "ĐGNL";
            case "NK1" -> "Năng khiếu 1";
            case "NK2" -> "Năng khiếu 2";
            default -> subjectCode != null ? subjectCode : "-";
        };
    }

    private double nullSafe(Double value, double fallback) {
        return value != null ? value : fallback;
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private String format(Double value) {
        return value != null ? String.format(Locale.US, "%.2f", value) : "-";
    }

    private record ConversionResult(Double value, String formula, boolean converted) {
    }

    private ScoreLookupExamScore toExamScore(DiemThi diemThi) {
        return new ScoreLookupExamScore(
                diemThi.getPhuongThuc(),
                diemThi.getToan(),
                diemThi.getLy(),
                diemThi.getHoa(),
                diemThi.getSinh(),
                diemThi.getSu(),
                diemThi.getDia(),
                diemThi.getVan(),
                diemThi.getN1Thi(),
                diemThi.getN1Cc(),
                diemThi.getNl1(),
                diemThi.getNk1(),
                diemThi.getNk2()
        );
    }
}
