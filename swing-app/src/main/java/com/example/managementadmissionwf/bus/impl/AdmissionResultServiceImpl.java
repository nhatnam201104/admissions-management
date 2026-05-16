package com.example.managementadmissionwf.bus.impl;

import com.example.managementadmissionwf.bus.interfaces.AdmissionResultService;
import com.example.managementadmissionwf.bus.interfaces.AspirationScoreService;
import com.example.managementadmissionwf.dal.entity.XtBangquydoi;
import com.example.managementadmissionwf.dal.entity.XtDiemthixettuyen;
import com.example.managementadmissionwf.dal.entity.XtNganh;
import com.example.managementadmissionwf.dal.entity.XtNganhTohop;
import com.example.managementadmissionwf.dal.entity.XtNguyenvongxettuyen;
import com.example.managementadmissionwf.dal.entity.XtThisinhxettuyen25;
import com.example.managementadmissionwf.dal.repository.BangquydoiRepository;
import com.example.managementadmissionwf.dal.repository.CandidateRepository;
import com.example.managementadmissionwf.dal.repository.MajorRepository;
import com.example.managementadmissionwf.dal.repository.NganhTohopRepository;
import com.example.managementadmissionwf.dal.repository.NguyenVongRepository;
import com.example.managementadmissionwf.dal.repository.ScoreRepository;
import com.example.managementadmissionwf.dto.CalculationStep;
import com.example.managementadmissionwf.dto.admission.AdmissionResultDTO;
import com.example.managementadmissionwf.dto.admission.AspirationImportDTO;
import com.example.managementadmissionwf.dto.common.ImportResult;
import com.example.managementadmissionwf.util.ExcelUtil;
import com.example.managementadmissionwf.utils.AdmissionConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.io.OutputStream;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Triển khai {@link AdmissionResultService}.
 *
 * <p>Đã refactor để giảm coupling: phần xuất Excel/PDF được chuyển sang
 * {@link AdmissionExportService}; phần truy vấn thuần gọi repository không
 * còn JPQL inline.
 */
@Slf4j
@Service
public class AdmissionResultServiceImpl implements AdmissionResultService {

    @Autowired private NguyenVongRepository nguyenVongRepository;
    @Autowired private CandidateRepository candidateRepository;
    @Autowired private MajorRepository majorRepository;
    @Autowired private NganhTohopRepository nganhTohopRepository;
    @Autowired private ScoreRepository scoreRepository;
    @Autowired private BangquydoiRepository bangquydoiRepository;
    @Autowired private AdmissionExportService exportService;
    @Autowired private AspirationScoreService aspirationScoreService;
    @Autowired private AspirationImportHelper aspirationImportHelper;



    // ============================================================
    // CRUD / Search
    // ============================================================

    @Override
    public List<AdmissionResultDTO> getAllResults() {
        return nguyenVongRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<AdmissionResultDTO> getByResult(String ketQua) {
        return nguyenVongRepository.findByNvKetqua(ketQua).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<AdmissionResultDTO> getByMajor(String manganh) {
        return nguyenVongRepository.findAll().stream()
                .filter(r -> manganh != null && manganh.equals(r.getNvManganh()))
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<AdmissionResultDTO> search(String keyword, String ketQua, String manganh, String phuongThuc) {
        // Cache map cccd -> candidate để giảm N+1 query khi search.
        Map<String, XtThisinhxettuyen25> candidateMap = candidateRepository.findAll().stream()
                .collect(Collectors.toMap(XtThisinhxettuyen25::getCccd, c -> c, (a, b) -> a));

        return nguyenVongRepository.findAll().stream()
                .filter(r -> matchesKeyword(r, keyword, candidateMap))
                .filter(r -> ketQua == null || ketQua.equals("Tất cả") || ketQua.equals(r.getNvKetqua()))
                .filter(r -> manganh == null || manganh.equals("Tất cả")
                        || r.getNvManganh().equals(manganh)
                        || getTenNganhByManganh(r.getNvManganh()).contains(manganh))
                .filter(r -> phuongThuc == null || phuongThuc.equals("Tất cả") || phuongThuc.equals(r.getTtPhuongthuc()))
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    private boolean matchesKeyword(XtNguyenvongxettuyen r, String keyword,
                                   Map<String, XtThisinhxettuyen25> candidateMap) {
        if (keyword == null || keyword.isEmpty()) {
            return true;
        }
        String cccd = r.getNnCccd();
        XtThisinhxettuyen25 candidate = candidateMap.get(cccd);
        String sobaodanh = candidate != null ? candidate.getSobaodanh() : "";
        String hoVaTen = candidate != null ? candidate.getHoVaTen() : "";
        String low = keyword.toLowerCase();
        return cccd.toLowerCase().contains(low)
                || (sobaodanh != null && sobaodanh.toLowerCase().contains(low))
                || (hoVaTen != null && hoVaTen.toLowerCase().contains(low));
    }

    private String getTenNganhByManganh(String manganh) {
        return majorRepository.findByManganh(manganh)
                .map(XtNganh::getTennganh)
                .orElse("");
    }

    @Override
    public void updateResult(Integer id, String ketQua) {
        nguyenVongRepository.findById(id).ifPresent(entity -> {
            entity.setNvKetqua(ketQua);
            nguyenVongRepository.save(entity);
        });
    }

    // ============================================================
    // Export — delegate to AdmissionExportService
    // ============================================================

    @Override
    public void exportToExcel(List<AdmissionResultDTO> results, OutputStream outputStream,
                              Map<String, Boolean> columns) {
        exportService.exportToExcel(results, outputStream, columns);
    }

    @Override
    public void exportToPDF(List<AdmissionResultDTO> results, OutputStream outputStream,
                            Map<String, Boolean> columns) {
        exportService.exportToPDF(results, outputStream, columns);
    }

    // ============================================================
    // Import nguyện vọng từ Excel
    // ============================================================

    /**
     * Import danh sách nguyện vọng từ Excel. Đặc điểm:
     * - KHÔNG gắn @Transactional ở đây — mỗi dòng tự commit qua
     *   {@link #saveAspirationRow(AspirationImportDTO)} (REQUIRES_NEW). Nếu
     *   1 dòng fail, các dòng khác vẫn commit thành công, không bị "rollback
     *   lan" như trước (đó là lý do "không lỗi nhưng không có dòng nào").
     * - Tính điểm được gọi sau khi save → không ảnh hưởng tới việc tạo NV.
     */
    @Override
    public ImportResult<AspirationImportDTO> importAspirationsFromExcel(InputStream inputStream) {
        ImportResult<AspirationImportDTO> result = new ImportResult<>();
        List<String> errors = new ArrayList<>();
        List<AspirationImportDTO> validData = new ArrayList<>();
        try {
            List<AspirationImportDTO> imported = ExcelUtil.importExcel(inputStream, AspirationImportDTO.class);
            result.setTotalRows(imported.size());
            int rowIdx = 2;
            for (AspirationImportDTO dto : imported) {
                try {
                    // Mỗi row chạy trong 1 transaction riêng (REQUIRES_NEW) qua
                    // helper bean — đảm bảo row khác không bị rollback theo.
                    String error = aspirationImportHelper.saveAspirationRow(dto);
                    if (error != null) {
                        errors.add("Dòng " + rowIdx + ": " + error);
                    } else {
                        validData.add(dto);
                    }
                } catch (Exception ex) {
                    errors.add("Dòng " + rowIdx + ": " + ex.getMessage());
                    log.warn("Import row {} failed", rowIdx, ex);
                }
                rowIdx++;
            }
            result.setSuccessCount(validData.size());
            result.setErrorCount(errors.size());
            result.setErrors(errors);
            result.setValidData(validData);
        } catch (Exception e) {
            throw new RuntimeException("Lỗi đọc file Excel: " + e.getMessage(), e);
        }
        return result;
    }



    // ============================================================
    // Score detail breakdown (5 bước công thức)
    // ============================================================

    @Override
    public String getManganhById(Integer id) {
        return nguyenVongRepository.findById(id)
                .map(XtNguyenvongxettuyen::getNvManganh)
                .orElse(null);
    }

    @Override
    public Map<String, Object> getScoreDetails(Integer id) {
        Map<String, Object> details = new HashMap<>();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        Optional<XtNguyenvongxettuyen> nvOpt = nguyenVongRepository.findById(id);
        if (nvOpt.isEmpty()) {
            return details;
        }
        XtNguyenvongxettuyen nv = nvOpt.get();
        String cccd = nv.getNnCccd();
        String manganh = nv.getNvManganh();
        String tohopMa = nv.getTtThm();
        String phuongThuc = nv.getTtPhuongthuc();

        // Major info
        majorRepository.findByManganh(manganh).ifPresent(major -> {
            details.put("tenNganh", major.getTennganh());
            details.put("chiTieu", major.getNChitieu() != null ? major.getNChitieu() : 0);
            details.put("diemChuan", major.getNDiemtrungtuyen());
        });

        // Candidate info (incl. ngay sinh)
        Optional<XtThisinhxettuyen25> candidateOpt = candidateRepository.findByCccd(cccd);
        details.put("hoTen", candidateOpt.map(XtThisinhxettuyen25::getHoVaTen).orElse("-"));
        details.put("sobaodanh", candidateOpt.map(XtThisinhxettuyen25::getSobaodanh).orElse("-"));
        details.put("ngaySinh", candidateOpt.map(XtThisinhxettuyen25::getNgaySinh)
                .map(d -> d.format(dtf)).orElse("N/A"));

        // Aspiration aggregated info
        details.put("diemCong", nv.getDiemCong() != null ? nv.getDiemCong() : 0.0);
        details.put("diemUtqd", nv.getDiemUtqd() != null ? nv.getDiemUtqd() : 0.0);
        details.put("diemThxt", nv.getDiemThxt() != null ? nv.getDiemThxt() : 0.0);
        details.put("diemXettuyen", nv.getDiemXettuyen() != null ? nv.getDiemXettuyen() : 0.0);
        details.put("ketQua", nv.getNvKetqua() != null ? nv.getNvKetqua() : "-");
        details.put("nvTt", nv.getNvTt());
        details.put("tohop", tohopMa != null ? tohopMa : "N/A");
        details.put("phuongThuc", phuongThuc != null ? phuongThuc : "THPT");

        // Subject combination (chosen tổ hợp)
        XtNganhTohop matchedTohop = findMatchedTohop(manganh, tohopMa);
        if (matchedTohop != null) {
            details.put("mon1", matchedTohop.getThMon1());
            details.put("mon2", matchedTohop.getThMon2());
            details.put("mon3", matchedTohop.getThMon3());
            details.put("hs1", nullSafe(matchedTohop.getHsmon1(), 1.0));
            details.put("hs2", nullSafe(matchedTohop.getHsmon2(), 1.0));
            details.put("hs3", nullSafe(matchedTohop.getHsmon3(), 1.0));
        } else {
            details.put("mon1", "-");
            details.put("mon2", "-");
            details.put("mon3", "-");
            details.put("hs1", 1.0);
            details.put("hs2", 1.0);
            details.put("hs3", 1.0);
        }

        // Score per subject
        Optional<XtDiemthixettuyen> scoreOpt = scoreRepository.findByCccdAndDPhuongthuc(cccd, phuongThuc);
        scoreOpt.ifPresentOrElse(score -> {
            details.put("to", score.getTo());
            details.put("li", score.getLi());
            details.put("ho", score.getHo());
            details.put("si", score.getSi());
            details.put("su", score.getSu());
            details.put("di", score.getDi());
            details.put("va", score.getVa());
            details.put("nl1", score.getNl1());
            details.put("nk1", score.getNk1());
            details.put("nk2", score.getNk2());
            details.put("n1", score.getN1Thi() != null ? score.getN1Thi() : score.getN1Cc());

            if ("VSAT".equals(phuongThuc)) {
                attachVsatConvertedScores(details, score, matchedTohop);
            }
        }, () -> {
            details.put("to", null);
            details.put("li", null);
            details.put("ho", null);
            details.put("si", null);
            details.put("su", null);
            details.put("di", null);
            details.put("va", null);
            details.put("nl1", null);
            details.put("nk1", null);
            details.put("nk2", null);
        });

        calculateFormulaBreakdown(details, nv, manganh, phuongThuc, matchedTohop, scoreOpt.orElse(null));
        return details;
    }

    private XtNganhTohop findMatchedTohop(String manganh, String tohopMa) {
        if (tohopMa == null || tohopMa.isBlank()) {
            return null;
        }
        return nganhTohopRepository.findByManganh(manganh).stream()
                .filter(t -> tohopMa.equals(t.getMatohop()))
                .findFirst()
                .orElse(null);
    }

    private void attachVsatConvertedScores(Map<String, Object> details, XtDiemthixettuyen score,
                                           XtNganhTohop matchedTohop) {
        Double toConv = convertSubjectScore(score.getTo(), "TO");
        Double liConv = convertSubjectScore(score.getLi(), "LI");
        Double hoConv = convertSubjectScore(score.getHo(), "HO");
        Double siConv = convertSubjectScore(score.getSi(), "SI");
        Double suConv = convertSubjectScore(score.getSu(), "SU");
        Double diConv = convertSubjectScore(score.getDi(), "DI");
        Double vaConv = convertSubjectScore(score.getVa(), "VA");

        details.put("toConverted", toConv);
        details.put("liConverted", liConv);
        details.put("hoConverted", hoConv);
        details.put("siConverted", siConv);
        details.put("suConverted", suConv);
        details.put("diConverted", diConv);
        details.put("vaConverted", vaConv);

        if (matchedTohop != null) {
            String m1 = upper(matchedTohop.getThMon1());
            String m2 = upper(matchedTohop.getThMon2());
            String m3 = upper(matchedTohop.getThMon3());
            Double d1 = pickConverted(m1, toConv, liConv, hoConv, siConv, suConv, diConv, vaConv);
            Double d2 = pickConverted(m2, toConv, liConv, hoConv, siConv, suConv, diConv, vaConv);
            Double d3 = pickConverted(m3, toConv, liConv, hoConv, siConv, suConv, diConv, vaConv);
            details.put("vsatMon1Score", d1);
            details.put("vsatMon2Score", d2);
            details.put("vsatMon3Score", d3);
            double sum = nullSafe(d1, 0.0) + nullSafe(d2, 0.0) + nullSafe(d3, 0.0);
            details.put("vsatTongDiem3Mon", sum);
        }
    }

    /**
     * Tính chi tiết công thức 5 bước. Output các key: dthxt, dthgxt, dut,
     * formulaSteps, calculationSteps.
     */
    private void calculateFormulaBreakdown(Map<String, Object> details, XtNguyenvongxettuyen nv,
                                           String manganh, String phuongThuc,
                                           XtNganhTohop matchedTohop, XtDiemthixettuyen score) {
        String tohopGoc = majorRepository.findByManganh(manganh)
                .map(XtNganh::getNTohopgoc).orElse(null);
        double diemCong = nullSafe(nv.getDiemCong(), 0.0);
        double diemUtqd = nullSafe(nv.getDiemUtqd(), 0.0);

        double dthxt = calculateDTHXT(details, phuongThuc, matchedTohop, score);
        double dthgxt = calculateDTHGXT(phuongThuc, dthxt, tohopGoc, nv.getTtThm());
        double dut = calculateDUT(dthgxt, dthxt, diemCong, diemUtqd);

        details.put("dthxt", dthxt);
        details.put("dthgxt", dthgxt);
        details.put("dut", dut);
        details.put("formulaSteps", buildFormulaSteps(phuongThuc, matchedTohop, dthxt, dthgxt, dut,
                tohopGoc, diemCong, diemUtqd));
        details.put("calculationSteps", buildStructuredSteps(phuongThuc, details, matchedTohop, score,
                dthxt, dthgxt, dut, tohopGoc, diemCong, diemUtqd));
    }

    private List<CalculationStep> buildStructuredSteps(String phuongThuc, Map<String, Object> details,
                                                       XtNganhTohop matchedTohop, XtDiemthixettuyen score,
                                                       double dthxt, double dthgxt, double dut,
                                                       String tohopGoc, double diemCong, double diemUtqd) {
        List<CalculationStep> steps = new ArrayList<>();
        double diemXettuyen = dthgxt + diemCong + dut;
        boolean isCapApplied = (dthgxt + diemCong) >= 22.5 && diemUtqd > 0;
        boolean isIncomplete = isDataIncomplete(phuongThuc, details);
        if (isIncomplete) {
            details.put("isIncomplete", true);
        }

        String step1Formula = buildStep1Formula(phuongThuc, details, matchedTohop, score);
        double step1Result = buildStep1Result(phuongThuc, details, matchedTohop, score);
        steps.add(new CalculationStep(1, "Điểm đã quy đổi", step1Formula, step1Result, false, false, isIncomplete));

        String step2Formula = buildStep2Formula(phuongThuc, matchedTohop, step1Result, step1Result);
        steps.add(new CalculationStep(2, "Công thức ĐTHXT", step2Formula, dthxt, false, false, isIncomplete));

        String step3Formula;
        double step3Result;
        if ("DGNL".equals(phuongThuc)) {
            step3Formula = String.format("%.2f (không áp dụng độ lệch)", dthxt);
            step3Result = dthxt;
        } else {
            double deviation = getDeviationScore(tohopGoc, matchedTohop != null ? matchedTohop.getMatohop() : null);
            step3Formula = String.format("%.2f - %.2f", dthxt, deviation);
            step3Result = dthgxt;
        }
        steps.add(new CalculationStep(3, "Điều chỉnh ĐTHGXT", step3Formula, step3Result, false, false, isIncomplete));

        String step4Formula = buildStep4Formula(dthxt, diemCong, diemUtqd, isCapApplied);
        steps.add(new CalculationStep(4, "Điểm ưu tiên (ĐƯT)", step4Formula, dut, false, isCapApplied, isIncomplete));

        String step5Formula = String.format("%.2f + %.2f + %.2f", dthgxt, diemCong, dut);
        steps.add(new CalculationStep(5, "Điểm Xét Tuyển (ĐXT)", step5Formula, diemXettuyen, true, false, isIncomplete));
        return steps;
    }

    private boolean isDataIncomplete(String phuongThuc, Map<String, Object> details) {
        if ("DGNL".equals(phuongThuc)) {
            return details.get("nl1") == null;
        }
        return details.get("to") == null || details.get("li") == null || details.get("ho") == null;
    }

    private String buildStep1Formula(String phuongThuc, Map<String, Object> details,
                                     XtNganhTohop matchedTohop, XtDiemthixettuyen score) {
        if ("DGNL".equals(phuongThuc)) {
            Double nl1 = (Double) details.get("nl1");
            return String.format("%.0f (thang 1200)", nl1 != null ? nl1 : 0);
        } else if ("VSAT".equals(phuongThuc)) {
            Double d1 = (Double) details.get("vsatMon1Score");
            Double d2 = (Double) details.get("vsatMon2Score");
            Double d3 = (Double) details.get("vsatMon3Score");
            return String.format("%.2f + %.2f + %.2f",
                    nullSafe(d1, 0.0), nullSafe(d2, 0.0), nullSafe(d3, 0.0));
        }
        double[] scores = getScoresForFormula(details, phuongThuc, matchedTohop, score);
        return String.format("%.2f + %.2f + %.2f", scores[0], scores[1], scores[2]);
    }

    private double buildStep1Result(String phuongThuc, Map<String, Object> details,
                                    XtNganhTohop matchedTohop, XtDiemthixettuyen score) {
        if ("DGNL".equals(phuongThuc)) {
            Double nl1 = (Double) details.get("nl1");
            return nl1 != null ? nl1 : 0;
        }
        if ("VSAT".equals(phuongThuc)) {
            Double sum = (Double) details.get("vsatTongDiem3Mon");
            return sum != null ? sum : 0;
        }
        double[] scores = getScoresForFormula(details, phuongThuc, matchedTohop, score);
        return scores[0] + scores[1] + scores[2];
    }

    private double interpolate(double x, List<XtBangquydoi> rules) {
        for (XtBangquydoi rule : rules) {
            if (x >= rule.getDDiema() && x <= rule.getDDiemb()) {
                double a = rule.getDDiema(), b = rule.getDDiemb();
                double c = rule.getDDiemc();
                double d = rule.getDDiemd() != null ? rule.getDDiemd() : c + 2;
                return c + ((x - a) / (b - a)) * (d - c);
            }
        }
        return 0.0;
    }

    private String buildInterpolationFormula(double x, List<XtBangquydoi> rules) {
        for (XtBangquydoi rule : rules) {
            if (x >= rule.getDDiema() && x <= rule.getDDiemb()) {
                double a = rule.getDDiema(), b = rule.getDDiemb();
                double c = rule.getDDiemc();
                double d = rule.getDDiemd() != null ? rule.getDDiemd() : c + 2;
                return String.format("%.0f ∈ [%.0f,%.0f] → %.0f + ((%.0f-%.0f)/(%.0f-%.0f)) × (%.0f-%.0f)",
                        x, a, b, c, x, a, b, a, d, c);
            }
        }
        return String.format("%.0f (không có trong bảng quy đổi)", x);
    }

    private String buildStep2Formula(String phuongThuc, XtNganhTohop matchedTohop,
                                     double weightedSum, double rawScore) {
        if ("DGNL".equals(phuongThuc)) {
            List<XtBangquydoi> rules = bangquydoiRepository.findByDPhuongthuc("DGNL");
            if (!rules.isEmpty()) {
                return buildInterpolationFormula(rawScore, rules);
            }
            return String.format("%.0f / 40 (thang 30)", rawScore);
        }
        double w1 = matchedTohop != null && matchedTohop.getHsmon1() != null ? matchedTohop.getHsmon1() : 1.0;
        double w2 = matchedTohop != null && matchedTohop.getHsmon2() != null ? matchedTohop.getHsmon2() : 1.0;
        double w3 = matchedTohop != null && matchedTohop.getHsmon3() != null ? matchedTohop.getHsmon3() : 1.0;
        double w = w1 + w2 + w3;
        return String.format("[(%.2f/%.1f)] × 3.0", weightedSum, w);
    }

    private String buildStep4Formula(double dthxt, double diemCong, double diemUtqd, boolean isCapApplied) {
        if (diemUtqd <= 0) {
            return "0.00 (không có ưu tiên)";
        }
        if (isCapApplied) {
            return String.format("(30-%.2f-%.2f)/7.5 × %.2f", dthxt, diemCong, diemUtqd);
        }
        return String.format("%.2f (đầy đủ)", diemUtqd);
    }

    private double calculateDTHXT(Map<String, Object> details, String phuongThuc,
                                  XtNganhTohop matchedTohop, XtDiemthixettuyen score) {
        if (score == null) {
            return 0.0;
        }
        switch (phuongThuc) {
            case "DGNL" -> {
                double nl1 = nullSafe(score.getNl1(), 0.0);
                List<XtBangquydoi> rules = bangquydoiRepository.findByDPhuongthuc("DGNL");
                if (rules.isEmpty()) {
                    return nl1 * 30.0 / 1200.0;
                }
                return interpolate(nl1, rules);
            }
            case "VSAT", "THPT" -> {
                if (matchedTohop == null) {
                    return 0.0;
                }
                double w1 = nullSafe(matchedTohop.getHsmon1(), 1.0);
                double w2 = nullSafe(matchedTohop.getHsmon2(), 1.0);
                double w3 = nullSafe(matchedTohop.getHsmon3(), 1.0);
                double w = w1 + w2 + w3;
                if (w == 0) {
                    w = 3.0;
                }
                double[] scores = getScoresForFormula(details, phuongThuc, matchedTohop, score);
                double weightedSum = scores[0] * w1 + scores[1] * w2 + scores[2] * w3;
                return (weightedSum / w) * 3.0;
            }
            default -> {
                return 0.0;
            }
        }
    }

    private double[] getScoresForFormula(Map<String, Object> details, String phuongThuc,
                                         XtNganhTohop matchedTohop, XtDiemthixettuyen score) {
        if ("VSAT".equals(phuongThuc)) {
            return new double[]{
                    nullSafe((Double) details.get("vsatMon1Score"), 0.0),
                    nullSafe((Double) details.get("vsatMon2Score"), 0.0),
                    nullSafe((Double) details.get("vsatMon3Score"), 0.0)
            };
        }
        if (matchedTohop == null) {
            return new double[]{0.0, 0.0, 0.0};
        }
        return new double[]{
                getSubjectScore(score, matchedTohop.getThMon1()),
                getSubjectScore(score, matchedTohop.getThMon2()),
                getSubjectScore(score, matchedTohop.getThMon3())
        };
    }

    private double getSubjectScore(XtDiemthixettuyen score, String subject) {
        if (score == null || subject == null) {
            return 0.0;
        }
        return switch (subject.toUpperCase()) {
            case "TO" -> nullSafe(score.getTo(), 0.0);
            case "LI" -> nullSafe(score.getLi(), 0.0);
            case "HO" -> nullSafe(score.getHo(), 0.0);
            case "SI" -> nullSafe(score.getSi(), 0.0);
            case "SU" -> nullSafe(score.getSu(), 0.0);
            case "DI" -> nullSafe(score.getDi(), 0.0);
            case "VA" -> nullSafe(score.getVa(), 0.0);
            case "AN" -> {
                Double n1Thi = score.getN1Thi();
                Double n1Cc = score.getN1Cc();
                if (n1Thi == null && n1Cc == null) yield 0.0;
                if (n1Thi == null) yield n1Cc;
                if (n1Cc == null) yield n1Thi;
                yield Math.max(n1Thi, n1Cc);
            }
            default -> 0.0;
        };
    }

    private double calculateDTHGXT(String phuongThuc, double dthxt, String tohopGoc, String tohopThucTe) {
        if ("DGNL".equals(phuongThuc)) {
            return dthxt;
        }
        return dthxt - getDeviationScore(tohopGoc, tohopThucTe);
    }

    private double getDeviationScore(String tohopGoc, String tohopThucTe) {
        try {
            return AdmissionConstants.getDeviationScore(tohopGoc, tohopThucTe);
        } catch (Exception e) {
            return 0.0;
        }
    }

    private double calculateDUT(double dthgxt, double dthxt, double diemCong, double mucUuTien) {
        if (mucUuTien <= 0) {
            return 0.0;
        }
        double tongDiem = dthgxt + diemCong;
        if (tongDiem < 22.5) {
            return mucUuTien;
        }
        double heSo = (30.0 - dthxt - diemCong) / 7.5;
        return heSo * mucUuTien;
    }

    private String buildFormulaSteps(String phuongThuc, XtNganhTohop matchedTohop,
                                     double dthxt, double dthgxt, double dut,
                                     String tohopGoc, double diemCong, double diemUtqd) {
        StringBuilder sb = new StringBuilder();
        sb.append("═══ CÔNG THỨC TÍNH ĐIỂM - ").append(phuongThuc).append(" ═══\n\n");

        sb.append("📌 Bước 2: Tính Điểm Tổ Hợp Xét Tuyển (ĐTHXT)\n");
        sb.append("   ĐTHXT = ").append(String.format("%.2f", dthxt)).append(" / 30\n\n");

        sb.append("📌 Bước 3: Tính Điểm Tổ Hợp Gốc (ĐTHGXT)\n");
        if ("DGNL".equals(phuongThuc)) {
            sb.append("   ĐTHGXT = ĐTHXT (không áp dụng ma trận độ lệch)\n");
        } else {
            double deviation = getDeviationScore(tohopGoc, matchedTohop != null ? matchedTohop.getMatohop() : null);
            sb.append("   ĐTHGXT = ĐTHXT - Mức chênh lệch\n");
            sb.append("          = ").append(String.format("%.2f", dthxt));
            sb.append(" - ").append(String.format("%.2f", deviation));
            sb.append(" = ").append(String.format("%.2f", dthgxt)).append("\n");
        }
        sb.append("   ĐTHGXT = ").append(String.format("%.2f", dthgxt)).append(" / 30\n\n");

        sb.append("📌 Bước 4: Tính Điểm Ưu Tiên (ĐƯT)\n");
        sb.append("   Tổng = ĐTHGXT + ĐC = ").append(String.format("%.2f", dthgxt));
        sb.append(" + ").append(String.format("%.2f", diemCong));
        sb.append(" = ").append(String.format("%.2f", dthgxt + diemCong)).append("\n");

        if (dthgxt + diemCong < 22.5) {
            sb.append("   Vì Tổng < 22.5 → ĐƯT = Mức ưu tiên\n");
        } else {
            sb.append("   Vì Tổng ≥ 22.5 → ĐƯT = [(30 - ĐTHXT - ĐC) / 7.5] × MĐƯT\n");
            double heSo = (30.0 - dthxt - diemCong) / 7.5;
            sb.append("             = [").append(String.format("%.2f", heSo)).append("] × ");
            sb.append(String.format("%.2f", diemUtqd)).append("\n");
        }
        sb.append("   ĐƯT = ").append(String.format("%.2f", dut)).append(" / 30\n\n");

        sb.append("📌 Bước 5: Tính Điểm Xét Tuyển (ĐXT)\n");
        sb.append("   ĐXT = ĐTHGXT + ĐC + ĐƯT\n");
        sb.append("      = ").append(String.format("%.2f", dthgxt));
        sb.append(" + ").append(String.format("%.2f", diemCong));
        sb.append(" + ").append(String.format("%.2f", dut));
        sb.append(" = ").append(String.format("%.2f", dthgxt + diemCong + dut)).append(" / 30\n");
        return sb.toString();
    }

    private Double convertSubjectScore(Double rawScore, String monCode) {
        if (rawScore == null) {
            return null;
        }
        List<XtBangquydoi> rules = bangquydoiRepository.findByDPhuongthucAndDMon("VSAT", monCode.toUpperCase());
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

    private Double pickConverted(String monCode, Double toConv, Double liConv,
                                 Double hoConv, Double siConv, Double suConv,
                                 Double diConv, Double vaConv) {
        if (monCode == null) {
            return null;
        }
        return switch (monCode.toUpperCase()) {
            case "TO" -> toConv;
            case "LI" -> liConv;
            case "HO" -> hoConv;
            case "SI" -> siConv;
            case "SU" -> suConv;
            case "DI" -> diConv;
            case "VA" -> vaConv;
            default -> null;
        };
    }

    // ============================================================
    // Auto admission (xét tuyển tự động)
    // ============================================================

    @Override
    public int handleAutomaticAdmission() {
        Map<String, Map<String, Integer>> quotaByMajorMethod = buildQuotaMap();
        Map<String, Integer> admissionCount = new HashMap<>();
        Map<String, Boolean> admittedCandidates = new HashMap<>();

        for (XtNguyenvongxettuyen nv : nguyenVongRepository.findByNvKetqua("TRUNG_TUYEN")) {
            admittedCandidates.put(nv.getNnCccd(), true);
            String key = nv.getNvManganh() + "_" + nv.getTtPhuongthuc();
            admissionCount.merge(key, 1, Integer::sum);
        }

        Map<String, Double> cutoffByMajorMethod = new HashMap<>();

        // Pending NV: gồm CHO_XET (đã có điểm), THIEU_DIEM (vẫn xét cho TUYEN_THANG),
        // và sắp xếp ưu tiên TUYEN_THANG trước (auto pass), sau đó theo điểm.
        List<XtNguyenvongxettuyen> pending = nguyenVongRepository.findAll().stream()
                .filter(nv -> "CHO_XET".equals(nv.getNvKetqua())
                        || ("TUYEN_THANG".equals(nv.getTtPhuongthuc())
                            && !"TRUNG_TUYEN".equals(nv.getNvKetqua())
                            && !"TRUOT".equals(nv.getNvKetqua())))
                .sorted((a, b) -> {
                    // TUYEN_THANG ưu tiên trước
                    boolean aTT = "TUYEN_THANG".equals(a.getTtPhuongthuc());
                    boolean bTT = "TUYEN_THANG".equals(b.getTtPhuongthuc());
                    if (aTT && !bTT) return -1;
                    if (!aTT && bTT) return 1;
                    int cmp = Double.compare(
                            b.getDiemXettuyen() != null ? b.getDiemXettuyen() : 0,
                            a.getDiemXettuyen() != null ? a.getDiemXettuyen() : 0);
                    if (cmp != 0) return cmp;
                    return Integer.compare(
                            a.getNvTt() != null ? a.getNvTt() : 999,
                            b.getNvTt() != null ? b.getNvTt() : 999);
                })
                .collect(Collectors.toList());

        int trungTuyenCount = 0;
        for (XtNguyenvongxettuyen nv : pending) {
            String cccd = nv.getNnCccd();
            String manganh = nv.getNvManganh();
            String phuongThuc = nv.getTtPhuongthuc();
            String key = manganh + "_" + phuongThuc;
            int quota = getQuotaForMajorMethod(quotaByMajorMethod, manganh, phuongThuc);

            if (Boolean.TRUE.equals(admittedCandidates.get(cccd))) {
                nv.setNvKetqua("TRUOT");
                nv.setLyDo("Đã trúng tuyển nguyện vọng khác");
                nguyenVongRepository.save(nv);
                continue;
            }

            int currentAdmitted = admissionCount.getOrDefault(key, 0);
            // TUYEN_THANG: auto đỗ bỏ qua điểm sàn / điểm xét tuyển. Vẫn check
            // còn quota theo ngành (sl_xtt) — nếu hết quota thì TRUOT.
            boolean isTuyenThang = "TUYEN_THANG".equals(phuongThuc);
            boolean canAdmit = isTuyenThang
                    ? currentAdmitted < quota
                    : currentAdmitted < quota;
            if (canAdmit) {
                nv.setNvKetqua("TRUNG_TUYEN");
                nv.setLyDo(isTuyenThang
                        ? "Tuyển thẳng (auto đỗ, không xét điểm)"
                        : "Trúng tuyển theo phương thức " + phuongThuc);
                admittedCandidates.put(cccd, true);
                admissionCount.put(key, currentAdmitted + 1);
                cutoffByMajorMethod.put(key, nv.getDiemXettuyen());
                trungTuyenCount++;

                // Trừ chỉ tiêu phương thức tương ứng trong xt_nganh.sl_*
                decrementMajorMethodQuota(manganh, phuongThuc);
            } else {
                nv.setNvKetqua("TRUOT");
                nv.setLyDo("Đạt quota (" + (currentAdmitted) + "/" + quota + ") cho ngành " + manganh);
            }
            nguyenVongRepository.save(nv);
        }


        // Điểm chuẩn ngành = MIN cutoff trên tất cả phương thức
        Map<String, List<Double>> scoresByMajor = new HashMap<>();
        for (Map.Entry<String, Double> entry : cutoffByMajorMethod.entrySet()) {
            String manganh = entry.getKey().split("_")[0];
            scoresByMajor.computeIfAbsent(manganh, k -> new ArrayList<>()).add(entry.getValue());
        }
        for (Map.Entry<String, List<Double>> entry : scoresByMajor.entrySet()) {
            Double minScore = entry.getValue().stream().filter(Objects::nonNull).min(Double::compare).orElse(null);
            if (minScore != null) {
                String manganh = entry.getKey();
                majorRepository.findByManganh(manganh).ifPresent(major -> {
                    major.setNDiemtrungtuyen(minScore);
                    majorRepository.save(major);
                    log.info("Cập nhật điểm chuẩn ngành {} = {}", manganh, minScore);
                });
            }
        }

        log.info("Xét tuyển tự động hoàn thành: {} thí sinh trúng tuyển trên {} nguyện vọng",
                trungTuyenCount, pending.size());
        return trungTuyenCount;
    }

    private Map<String, Map<String, Integer>> buildQuotaMap() {
        Map<String, Map<String, Integer>> result = new HashMap<>();
        for (XtNganh major : majorRepository.findAll()) {
            Map<String, Integer> methodQuota = new HashMap<>();
            if (Boolean.TRUE.equals(major.getNThpt())) {
                methodQuota.put("THPT", nullSafe(major.getSlThpt(), 0));
            }
            if (Boolean.TRUE.equals(major.getNDgnl())) {
                methodQuota.put("DGNL", nullSafe(major.getSlDgnl(), 0));
            }
            if (Boolean.TRUE.equals(major.getNVsat())) {
                methodQuota.put("VSAT", nullSafe(major.getSlVsat(), 0));
            }
            if (Boolean.TRUE.equals(major.getNTuyenthang())) {
                methodQuota.put("TUYEN_THANG", nullSafe(major.getSlXtt(), 0));
            }
            result.put(major.getManganh(), methodQuota);
        }
        return result;
    }

    private int getQuotaForMajorMethod(Map<String, Map<String, Integer>> quotaMap,
                                       String manganh, String phuongThuc) {
        return quotaMap.getOrDefault(manganh, Map.of()).getOrDefault(phuongThuc, 0);
    }

    /**
     * Trừ 1 chỉ tiêu của phương thức tương ứng trong xt_nganh.sl_*. Cập nhật
     * thẳng vào DB (giảm 1) khi 1 thí sinh trúng tuyển. Không cho phép xuống
     * dưới 0.
     */
    private void decrementMajorMethodQuota(String manganh, String phuongThuc) {
        if (manganh == null || phuongThuc == null) {
            return;
        }
        majorRepository.findByManganh(manganh).ifPresent(major -> {
            switch (phuongThuc) {
                case "THPT" -> major.setSlThpt(Math.max(0, nullSafe(major.getSlThpt(), 0) - 1));
                case "DGNL" -> major.setSlDgnl(Math.max(0, nullSafe(major.getSlDgnl(), 0) - 1));
                case "VSAT" -> major.setSlVsat(Math.max(0, nullSafe(major.getSlVsat(), 0) - 1));
                case "TUYEN_THANG" -> major.setSlXtt(Math.max(0, nullSafe(major.getSlXtt(), 0) - 1));
                default -> {
                    log.warn("Phương thức không hợp lệ khi trừ quota: {}", phuongThuc);
                    return;
                }
            }
            majorRepository.save(major);
            log.debug("Trừ 1 chỉ tiêu {} của ngành {}", phuongThuc, manganh);
        });
    }


    // ============================================================
    // Helpers
    // ============================================================

    private double nullSafe(Double value, double fallback) {
        return value != null ? value : fallback;
    }

    private int nullSafe(Integer value, int fallback) {
        return value != null ? value : fallback;
    }

    private String upper(String value) {
        return value != null ? value.toUpperCase() : "";
    }

    private AdmissionResultDTO toDTO(XtNguyenvongxettuyen entity) {
        Optional<XtThisinhxettuyen25> candidateOpt = candidateRepository.findByCccd(entity.getNnCccd());
        String hoVaTen = candidateOpt.map(XtThisinhxettuyen25::getHoVaTen).orElse(entity.getNnCccd());
        String sobaodanh = candidateOpt.map(XtThisinhxettuyen25::getSobaodanh).orElse("-");

        Optional<XtNganh> majorOpt = majorRepository.findByManganh(entity.getNvManganh());
        String tennganh = majorOpt.map(XtNganh::getTennganh).orElse(entity.getNvManganh());
        Double diemChuan = majorOpt.map(XtNganh::getNDiemtrungtuyen).orElse(null);

        String tohop = entity.getTtThm() != null ? entity.getTtThm() : "-";

        return AdmissionResultDTO.builder()
                .id(entity.getId())
                .cccd(entity.getNnCccd())
                .hoTen(hoVaTen)
                .sobaodanh(sobaodanh)
                .manganh(entity.getNvManganh())
                .tennganh(tennganh)
                .nvTt(entity.getNvTt())
                .diemXettuyen(entity.getDiemXettuyen())
                .ketQua(entity.getNvKetqua())
                .phuongThuc(entity.getTtPhuongthuc())
                .tohop(tohop)
                .diemChuan(diemChuan)
                .lyDo(entity.getLyDo())
                .build();
    }
}
