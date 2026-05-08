package com.example.managementadmissionwf.bus.impl;

import com.example.managementadmissionwf.bus.interfaces.AdmissionResultService;
import com.example.managementadmissionwf.dal.entity.*;
import com.example.managementadmissionwf.dal.repository.*;
import com.example.managementadmissionwf.dto.admission.AdmissionResultDTO;
import com.example.managementadmissionwf.dto.CalculationStep;
import com.example.managementadmissionwf.utils.AdmissionConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class AdmissionResultServiceImpl implements AdmissionResultService {

    @Autowired
    private NguyenVongRepository nguyenVongRepository;
    
    @Autowired
    private CandidateRepository candidateRepository;
    
    @Autowired
    private MajorRepository majorRepository;
    
    @Autowired
    private NganhTohopRepository nganhTohopRepository;
    
    @Autowired
    private ScoreRepository scoreRepository;
    
    @Autowired
    private BangquydoiRepository bangquydoiRepository;

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
                .filter(r -> manganh.equals(r.getNvManganh()) || manganh.equals(r.getNvManganh()))
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<AdmissionResultDTO> search(String keyword, String ketQua, String manganh, String phuongThuc) {
        List<XtNguyenvongxettuyen> all = nguyenVongRepository.findAll();
        
        return all.stream()
                // Lọc theo từ khóa: CCCD, SBD, Họ tên
                .filter(r -> {
                    if (keyword == null || keyword.isEmpty()) return true;
                    
                    var candidateOpt = candidateRepository.findByCccd(r.getNnCccd());
                    String sobaodanh = candidateOpt.map(XtThisinhxettuyen25::getSobaodanh).orElse("");
                    String hoVaTen = candidateOpt.map(XtThisinhxettuyen25::getHoVaTen).orElse("");
                    String cccd = r.getNnCccd();
                    
                    String lowerKeyword = keyword.toLowerCase();
                    return cccd.toLowerCase().contains(lowerKeyword)
                            || sobaodanh.toLowerCase().contains(lowerKeyword)
                            || hoVaTen.toLowerCase().contains(lowerKeyword);
                })
                // Lọc theo kết quả
                .filter(r -> ketQua == null || ketQua.equals("Tất cả") || ketQua.equals(r.getNvKetqua()))
                // Lọc theo ngành
                .filter(r -> manganh == null || manganh.equals("Tất cả") 
                        || r.getNvManganh().equals(manganh)
                        || getTenNganhByManganh(r.getNvManganh()).contains(manganh))
                // Lọc theo phương thức
                .filter(r -> phuongThuc == null || phuongThuc.equals("Tất cả") || phuongThuc.equals(r.getTtPhuongthuc()))
                .map(this::toDTO)
                .collect(Collectors.toList());
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

    @Override
    public void exportToExcel(List<AdmissionResultDTO> results) {
        System.out.println("Đang xuất " + results.size() + " dòng ra file Excel...");
    }

    @Override
    public void exportToPDF(List<AdmissionResultDTO> results) {
        System.out.println("Đang xuất " + results.size() + " dòng ra file PDF...");
    }

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
        
        // Lấy nguyện vọng theo ID
        Optional<XtNguyenvongxettuyen> nvOpt = nguyenVongRepository.findById(id);
        if (nvOpt.isEmpty()) {
            return details; // Trả về map rỗng nếu không tìm thấy
        }
        
        var nv = nvOpt.get();
        String cccd = nv.getNnCccd();
        String manganh = nv.getNvManganh();
        String tohopMa = nv.getTtThm();
        String phuongThuc = nv.getTtPhuongthuc();
        
        // Lấy thông tin ngành (điểm chuẩn, chỉ tiêu)
        var majorOpt = majorRepository.findByManganh(manganh);
        if (majorOpt.isPresent()) {
            var major = majorOpt.get();
            details.put("tenNganh", major.getTennganh());
            details.put("chiTieu", major.getNChitieu() != null ? major.getNChitieu() : 0);
            details.put("diemChuan", major.getNDiemtrungtuyen() != null ? major.getNDiemtrungtuyen() : null);
        }
        
        // Lấy thông tin thí sinh (bao gồm ngày sinh)
        var candidateOpt = candidateRepository.findByCccd(cccd);
        String hoVaTen = candidateOpt.map(XtThisinhxettuyen25::getHoVaTen).orElse("-");
        String sobaodanh = candidateOpt.map(XtThisinhxettuyen25::getSobaodanh).orElse("-");
        details.put("hoTen", hoVaTen);
        details.put("sobaodanh", sobaodanh);
        
        // Lấy ngày sinh (format dd/MM/yyyy)
        var ngaySinh = candidateOpt.map(XtThisinhxettuyen25::getNgaySinh).orElse(null);
        details.put("ngaySinh", ngaySinh != null ? ngaySinh.format(dtf) : "N/A");
        
        // Lấy thông tin từ nguyện vọng
        details.put("diemCong", nv.getDiemCong() != null ? nv.getDiemCong() : 0.0);
        details.put("diemUtqd", nv.getDiemUtqd() != null ? nv.getDiemUtqd() : 0.0);
        details.put("diemThxt", nv.getDiemThxt() != null ? nv.getDiemThxt() : 0.0);
        details.put("diemXettuyen", nv.getDiemXettuyen() != null ? nv.getDiemXettuyen() : 0.0);
        details.put("ketQua", nv.getNvKetqua() != null ? nv.getNvKetqua() : "-");
        details.put("nvTt", nv.getNvTt());
        details.put("tohop", tohopMa != null ? tohopMa : "N/A");
        details.put("phuongThuc", phuongThuc != null ? phuongThuc : "THPT");
        
        // Tìm tổ hợp trong xt_nganh_tohop theo tổ hợp được chọn (ttThm)
        List<XtNganhTohop> tohopList = nganhTohopRepository.findByManganh(manganh);
        XtNganhTohop matchedTohop = null;
        if (tohopMa != null && !tohopList.isEmpty()) {
            matchedTohop = tohopList.stream()
                .filter(t -> tohopMa.equals(t.getMatohop()))
                .findFirst()
                .orElse(null);
        }
        
        if (matchedTohop != null) {
            details.put("mon1", matchedTohop.getThMon1());
            details.put("mon2", matchedTohop.getThMon2());
            details.put("mon3", matchedTohop.getThMon3());
            details.put("hs1", matchedTohop.getHsmon1() != null ? matchedTohop.getHsmon1() : 1.0);
            details.put("hs2", matchedTohop.getHsmon2() != null ? matchedTohop.getHsmon2() : 1.0);
            details.put("hs3", matchedTohop.getHsmon3() != null ? matchedTohop.getHsmon3() : 1.0);
        } else {
            details.put("mon1", "-");
            details.put("mon2", "-");
            details.put("mon3", "-");
            details.put("hs1", 1.0);
            details.put("hs2", 1.0);
            details.put("hs3", 1.0);
        }
        
        // Lấy điểm chi tiết từ bảng điểm thi (filter theo phương thức từ nguyện vọng)
        var scoreOpt = scoreRepository.findByCccdAndDPhuongthuc(cccd, phuongThuc);
        if (scoreOpt.isPresent()) {
            var score = scoreOpt.get();
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
            
            // VSAT: Thêm điểm đã quy đổi thang 10
            if ("VSAT".equals(phuongThuc)) {
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
                
                // Thêm thông tin 3 môn xét tuyển và tổng điểm
                if (matchedTohop != null) {
                    // Lấy điểm quy đổi của 3 môn theo tổ hợp
                    Double d1 = null, d2 = null, d3 = null;
                    String m1 = matchedTohop.getThMon1() != null ? matchedTohop.getThMon1().toUpperCase() : "";
                    String m2 = matchedTohop.getThMon2() != null ? matchedTohop.getThMon2().toUpperCase() : "";
                    String m3 = matchedTohop.getThMon3() != null ? matchedTohop.getThMon3().toUpperCase() : "";
                    
                    d1 = getConvertedScoreBySubject(m1, toConv, liConv, hoConv, siConv, suConv, diConv, vaConv);
                    d2 = getConvertedScoreBySubject(m2, toConv, liConv, hoConv, siConv, suConv, diConv, vaConv);
                    d3 = getConvertedScoreBySubject(m3, toConv, liConv, hoConv, siConv, suConv, diConv, vaConv);
                    
                    details.put("vsatMon1Score", d1);
                    details.put("vsatMon2Score", d2);
                    details.put("vsatMon3Score", d3);
                    
                    // Tổng điểm 3 môn
                    double sum = 0;
                    if (d1 != null) sum += d1;
                    if (d2 != null) sum += d2;
                    if (d3 != null) sum += d3;
                    details.put("vsatTongDiem3Mon", sum);
                }
            }
        } else {
            // Nếu không có điểm thi, set null để hiển thị N/A
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
        }
        
        // Tính toán các bước trung gian cho công thức (Bước 2-4)
        calculateFormulaBreakdown(details, nv, cccd, manganh, phuongThuc, matchedTohop, scoreOpt.orElse(null));
        
        return details;
    }
    
    /**
     * Tính toán chi tiết công thức tính điểm theo 5 bước
     * Thêm vào details để hiển thị trong ScoreDetailDialog
     */
    private void calculateFormulaBreakdown(Map<String, Object> details, XtNguyenvongxettuyen nv,
                                           String cccd, String manganh, String phuongThuc,
                                           XtNganhTohop matchedTohop, XtDiemthixettuyen score) {
        // Lấy tổ hợp gốc của ngành
        String tohopGoc = majorRepository.findByManganh(manganh)
                .map(XtNganh::getNTohopgoc).orElse(null);
        String tohopThucTe = nv.getTtThm();
        
        Double diemCong = nv.getDiemCong() != null ? nv.getDiemCong() : 0.0;
        Double diemUtqd = nv.getDiemUtqd() != null ? nv.getDiemUtqd() : 0.0;
        
        // === Bước 2: Tính ĐTHXT ===
        double dthxt = calculateDTHXT(details, phuongThuc, matchedTohop, score);
        details.put("dthxt", dthxt);
        
        // === Bước 3: Tính ĐTHGXT ===
        double dthgxt = calculateDTHGXT(phuongThuc, dthxt, tohopGoc, tohopThucTe);
        details.put("dthgxt", dthgxt);
        
        // === Bước 4: Tính ĐƯT ===
        double dut = calculateDUT(dthgxt, dthxt, diemCong, diemUtqd);
        details.put("dut", dut);
        
        // === Bước 5: Tổng hợp công thức ===
        String formulaSteps = buildFormulaSteps(phuongThuc, details, matchedTohop, dthxt, dthgxt, dut, tohopGoc, diemCong, diemUtqd);
        details.put("formulaSteps", formulaSteps);
        
        // === Build structured steps for UI table ===
        List<CalculationStep> steps = buildStructuredSteps(phuongThuc, details, matchedTohop, score,
            dthxt, dthgxt, dut, tohopGoc, diemCong, diemUtqd);
        details.put("calculationSteps", steps);
    }
    
    /**
     * Build structured list of CalculationStep for UI table display
     */
    private List<CalculationStep> buildStructuredSteps(String phuongThuc, Map<String, Object> details,
                                                        XtNganhTohop matchedTohop, XtDiemthixettuyen score,
                                                        double dthxt, double dthgxt, double dut,
                                                        String tohopGoc, double diemCong, double diemUtqd) {
        List<CalculationStep> steps = new ArrayList<>();
        double diemXettuyen = dthgxt + diemCong + dut;
        boolean isCapApplied = (dthgxt + diemCong) >= 22.5 && diemUtqd > 0;
        boolean isIncomplete = isDataIncomplete(phuongThuc, details);
        
        // Check if any required score is missing
        if (isIncomplete) {
            details.put("isIncomplete", true);
        }
        
        // Step 1: Converted Points - use actual score from database
        String step1Formula = buildStep1Formula(phuongThuc, details, matchedTohop, score);
        double step1Result = buildStep1Result(phuongThuc, details, matchedTohop, score);
        steps.add(new CalculationStep(1, "Điểm đã quy đổi", step1Formula, step1Result, false, false, isIncomplete));
        
        // Step 2: Base Calculation (ĐTHXT) - pass rawScore for interpolation formula display
        String step2Formula = buildStep2Formula(phuongThuc, matchedTohop, step1Result, step1Result);
        steps.add(new CalculationStep(2, "Công thức ĐTHXT", step2Formula, dthxt, false, false, isIncomplete));
        
        // Step 3: Deviation Adjustment (ĐTHGXT)
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
        
        // Step 4: Priority Points (ĐƯT)
        String step4Formula = buildStep4Formula(dthgxt, dthxt, diemCong, diemUtqd, isCapApplied);
        steps.add(new CalculationStep(4, "Điểm ưu tiên (ĐƯT)", step4Formula, dut, false, isCapApplied, isIncomplete));
        
        // Step 5: Final Total (ĐXT)
        String step5Formula = String.format("%.2f + %.2f + %.2f", dthgxt, diemCong, dut);
        steps.add(new CalculationStep(5, "Điểm Xét Tuyển (ĐXT)", step5Formula, diemXettuyen, true, false, isIncomplete));
        
        return steps;
    }
    
    private boolean isDataIncomplete(String phuongThuc, Map<String, Object> details) {
        // Check based on phuongThuc - DGNL only needs nl1, THPT/VSAT need TO, LI, HO
        if ("DGNL".equals(phuongThuc)) {
            return details.get("nl1") == null;
        }
        // THPT/VSAT require subject scores
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
                d1 != null ? d1 : 0, d2 != null ? d2 : 0, d3 != null ? d3 : 0);
        } else {
            // THPT: direct scores - use actual score from database
            double[] scores = getScoresForFormula(details, phuongThuc, matchedTohop, score);
            return String.format("%.2f + %.2f + %.2f", scores[0], scores[1], scores[2]);
        }
    }
    
    private double buildStep1Result(String phuongThuc, Map<String, Object> details, 
                                     XtNganhTohop matchedTohop, XtDiemthixettuyen score) {
        if ("DGNL".equals(phuongThuc)) {
            Double nl1 = (Double) details.get("nl1");
            return nl1 != null ? nl1 : 0;
        } else if ("VSAT".equals(phuongThuc)) {
            Double sum = (Double) details.get("vsatTongDiem3Mon");
            return sum != null ? sum : 0;
        } else {
            // THPT: use actual score from database
            double[] scores = getScoresForFormula(details, phuongThuc, matchedTohop, score);
            return scores[0] + scores[1] + scores[2];
        }
    }
    
    /**
     * Utility method for linear interpolation using conversion table
     * Formula: y = c + ((x - a) / (b - a)) * (d - c)
     */
    private double interpolate(double x, List<XtBangquydoi> rules) {
        for (XtBangquydoi rule : rules) {
            if (x >= rule.getDDiema() && x <= rule.getDDiemb()) {
                double a = rule.getDDiema(), b = rule.getDDiemb();
                double c = rule.getDDiemc(), d = rule.getDDiemd() != null ? rule.getDDiemd() : c + 2;
                return c + ((x - a) / (b - a)) * (d - c);
            }
        }
        return 0.0; // Không tìm thấy khoảng phù hợp
    }
    
    /**
     * Build interpolation formula string for display in UI
     */
    private String buildInterpolationFormula(double x, List<XtBangquydoi> rules) {
        for (XtBangquydoi rule : rules) {
            if (x >= rule.getDDiema() && x <= rule.getDDiemb()) {
                double a = rule.getDDiema(), b = rule.getDDiemb();
                double c = rule.getDDiemc(), d = rule.getDDiemd() != null ? rule.getDDiemd() : c + 2;
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
        } else {
            double w1 = matchedTohop != null && matchedTohop.getHsmon1() != null ? matchedTohop.getHsmon1() : 1.0;
            double w2 = matchedTohop != null && matchedTohop.getHsmon2() != null ? matchedTohop.getHsmon2() : 1.0;
            double w3 = matchedTohop != null && matchedTohop.getHsmon3() != null ? matchedTohop.getHsmon3() : 1.0;
            double W = w1 + w2 + w3;
            return String.format("[(%.2f/%.1f)] × 3.0", weightedSum, W);
        }
    }
    
    private String buildStep4Formula(double dthgxt, double dthxt, double diemCong, double diemUtqd, boolean isCapApplied) {
        if (diemUtqd <= 0) {
            return "0.00 (không có ưu tiên)";
        }
        if (isCapApplied) {
            double heSo = (30.0 - dthxt - diemCong) / 7.5;
            return String.format("(30-%.2f-%.2f)/7.5 × %.2f", dthxt, diemCong, diemUtqd);
        } else {
            return String.format("%.2f (đầy đủ)", diemUtqd);
        }
    }
    
    /**
     * Bước 2: Tính Điểm Tổ Hợp Xét Tuyển (ĐTHXT) - thang 30
     */
    private double calculateDTHXT(Map<String, Object> details, String phuongThuc, 
                                    XtNganhTohop matchedTohop, XtDiemthixettuyen score) {
        // DGNL only needs score, not matchedTohop
        if (score == null) return 0.0;
        
        switch (phuongThuc) {
            case "DGNL" -> {
                // ĐGNL: điểm đã quy đổi về thang 30
                Double nl1 = score.getNl1() != null ? score.getNl1() : 0.0;
                // Use interpolation from conversion table if available
                List<XtBangquydoi> rules = bangquydoiRepository.findByDPhuongthuc("DGNL");
                if (rules.isEmpty()) {
                    // Fallback to linear conversion if no rules found
                    return nl1 * 30.0 / 1200.0;
                }
                return interpolate(nl1, rules);
            }
            case "VSAT", "THPT" -> {
                // THPT/VSAT: tính theo công thức có hệ số
                double w1 = matchedTohop.getHsmon1() != null ? matchedTohop.getHsmon1() : 1.0;
                double w2 = matchedTohop.getHsmon2() != null ? matchedTohop.getHsmon2() : 1.0;
                double w3 = matchedTohop.getHsmon3() != null ? matchedTohop.getHsmon3() : 1.0;
                double W = w1 + w2 + w3;
                if (W == 0) W = 3.0;
                
                double[] scores = getScoresForFormula(details, phuongThuc, matchedTohop, score);
                double weightedSum = scores[0] * w1 + scores[1] * w2 + scores[2] * w3;
                return (weightedSum / W) * 3.0;
            }
            default -> {
                return 0.0;
            }
        }
    }
    
    /**
     * Lấy điểm 3 môn đã quy đổi cho công thức
     */
    private double[] getScoresForFormula(Map<String, Object> details, String phuongThuc,
                                          XtNganhTohop matchedTohop, XtDiemthixettuyen score) {
        double d1, d2, d3;
        
        if ("VSAT".equals(phuongThuc)) {
            // VSAT: lấy điểm đã quy đổi từ details
            d1 = details.get("vsatMon1Score") != null ? (Double) details.get("vsatMon1Score") : 0.0;
            d2 = details.get("vsatMon2Score") != null ? (Double) details.get("vsatMon2Score") : 0.0;
            d3 = details.get("vsatMon3Score") != null ? (Double) details.get("vsatMon3Score") : 0.0;
        } else {
            // THPT: lấy điểm gốc (thang 10)
            String m1 = matchedTohop.getThMon1();
            String m2 = matchedTohop.getThMon2();
            String m3 = matchedTohop.getThMon3();
            d1 = getSubjectScore(score, m1);
            d2 = getSubjectScore(score, m2);
            d3 = getSubjectScore(score, m3);
        }
        
        return new double[]{d1, d2, d3};
    }
    
    /**
     * Lấy điểm 1 môn theo mã môn
     */
    private double getSubjectScore(XtDiemthixettuyen score, String subject) {
        if (score == null || subject == null) return 0.0;
        return switch (subject.toUpperCase()) {
            case "TO" -> score.getTo() != null ? score.getTo() : 0.0;
            case "LI" -> score.getLi() != null ? score.getLi() : 0.0;
            case "HO" -> score.getHo() != null ? score.getHo() : 0.0;
            case "SI" -> score.getSi() != null ? score.getSi() : 0.0;
            case "SU" -> score.getSu() != null ? score.getSu() : 0.0;
            case "DI" -> score.getDi() != null ? score.getDi() : 0.0;
            case "VA" -> score.getVa() != null ? score.getVa() : 0.0;
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
    
    /**
     * Bước 3: Tính Điểm Tổ Hợp Gốc Xét Tuyển (ĐTHGXT)
     * ĐGNL: không áp dụng ma trận độ lệch
     * THPT/VSAT: trừ mức chênh lệch từ ma trận
     */
    private double calculateDTHGXT(String phuongThuc, double dthxt, String tohopGoc, String tohopThucTe) {
        if ("DGNL".equals(phuongThuc)) {
            return dthxt; // Không áp dụng ma trận
        }
        // THPT/VSAT: trừ độ lệch
        double deviaton = getDeviationScore(tohopGoc, tohopThucTe);
        return dthxt - deviaton;
    }
    
    /**
     * Lấy mức độ lệch từ ma trận
     */
    private double getDeviationScore(String tohopGoc, String tohopThucTe) {
        // Sử dụng AdmissionConstants nếu có, hoặc trả về 0 nếu không có trong ma trận
        try {
            return AdmissionConstants.getDeviationScore(tohopGoc, tohopThucTe);
        } catch (Exception e) {
            return 0.0; // Không có trong ma trận → mức chênh lệch = 0
        }
    }
    
    /**
     * Bước 4: Tính Điểm Ưu Tiên (ĐƯT)
     * Nếu (ĐTHGXT + ĐC) < 22.5 → ĐƯT = MĐƯT
     * Nếu (ĐTHGXT + ĐC) ≥ 22.5 → ĐƯT = [(30 - ĐTHXT - ĐC) / 7.5] × MĐƯT
     */
    private double calculateDUT(double dthgxt, double dthxt, double diemCong, Double mucUuTien) {
        if (mucUuTien == null || mucUuTien <= 0) {
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
    
    /**
     * Xây dựng chuỗi mô tả các bước tính toán công thức
     */
    private String buildFormulaSteps(String phuongThuc, Map<String, Object> details,
                                      XtNganhTohop matchedTohop, double dthxt, double dthgxt,
                                      double dut, String tohopGoc, double diemCong, double diemUtqd) {
        StringBuilder sb = new StringBuilder();
        
        sb.append("═══ CÔNG THỨC TÍNH ĐIỂM - ").append(phuongThuc).append(" ═══\n\n");
        
        // Bước 2: ĐTHXT
        sb.append("📌 Bước 2: Tính Điểm Tổ Hợp Xét Tuyển (ĐTHXT)\n");
        sb.append("   ĐTHXT = ").append(String.format("%.2f", dthxt)).append(" / 30\n\n");
        
        // Bước 3: ĐTHGXT
        sb.append("📌 Bước 3: Tính Điểm Tổ Hợp Gốc (ĐTHGXT)\n");
        if ("DGNL".equals(phuongThuc)) {
            sb.append("   ĐTHGXT = ĐTHXT (không áp dụng ma trận độ lệch)\n");
        } else {
            double deviaton = getDeviationScore(tohopGoc, matchedTohop != null ? matchedTohop.getMatohop() : null);
            sb.append("   ĐTHGXT = ĐTHXT - Mức chênh lệch\n");
            sb.append("          = ").append(String.format("%.2f", dthxt));
            sb.append(" - ").append(String.format("%.2f", deviaton));
            sb.append(" = ").append(String.format("%.2f", dthgxt)).append("\n");
        }
        sb.append("   ĐTHGXT = ").append(String.format("%.2f", dthgxt)).append(" / 30\n\n");
        
        // Bước 4: ĐƯT
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
        
        // Bước 5: ĐXT
        sb.append("📌 Bước 5: Tính Điểm Xét Tuyển (ĐXT)\n");
        sb.append("   ĐXT = ĐTHGXT + ĐC + ĐƯT\n");
        sb.append("      = ").append(String.format("%.2f", dthgxt));
        sb.append(" + ").append(String.format("%.2f", diemCong));
        sb.append(" + ").append(String.format("%.2f", dut));
        sb.append(" = ").append(String.format("%.2f", dthgxt + diemCong + dut)).append(" / 30\n");
        
        return sb.toString();
    }
    
    /**
     * Quy đổi điểm 1 môn VSAT về thang 10
     * Mã môn đã thống nhất: TO, LI, HO, SI, SU, DI, VA, AN, NV
     */
    private Double convertSubjectScore(Double rawScore, String monCode) {
        if (rawScore == null) return null;
        
        // Sử dụng trực tiếp monCode để tìm bảng quy đổi
        List<XtBangquydoi> rules = bangquydoiRepository.findByDPhuongthucAndDMon("VSAT", monCode.toUpperCase());
        if (rules.isEmpty()) {
            // Nếu không có bảng quy đổi, giữ nguyên
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
    
    /**
     * Lấy điểm quy đổi của môn dựa trên mã môn (đã thống nhất TO, LI, HO, SI, SU, DI, VA, AN)
     */
    private Double getConvertedScoreBySubject(String monCode, Double toConv, Double liConv, 
                                              Double hoConv, Double siConv, Double suConv, 
                                              Double diConv, Double vaConv) {
        if (monCode == null) return null;
        switch (monCode.toUpperCase()) {
            case "TO": return toConv;
            case "LI": return liConv;
            case "HO": return hoConv;
            case "SI": return siConv;
            case "SU": return suConv;
            case "DI": return diConv;
            case "VA": return vaConv;
            case "AN": return null; // Anh dùng n1Thi/n1Cc
            default: return null;
        }
    }
    
    /**
     * Xét tuyển tự động cho tất cả nguyện vọng
     * Logic:
     * 1. Load NV = CHO_XET, sort [diem DESC, nv_tt ASC]
     * 2. Kiểm tra: đã đậu NV nào chưa? Còn quota không? Tie-breaker?
     * 3. Update kết quả và điểm chuẩn tự động
     */
    @Override
    public int handleAutomaticAdmission() {
        // 1. Lấy quota theo phương thức
        Map<String, Map<String, Integer>> quotaByMajorMethod = buildQuotaMap();
        
        // 2. Map đếm số đã trúng tuyển theo ngành+phương thức (từ DB)
        Map<String, Integer> admissionCount = new HashMap<>();
        
        // 3. Đọc từ DB các thí sinh đã đậu ở lần xét trước (TRUNG_TUYEN)
        Map<String, Boolean> admittedCandidates = new HashMap<>();
        nguyenVongRepository.findByNvKetqua("TRUNG_TUYEN").forEach(nv -> {
            admittedCandidates.put(nv.getNnCccd(), true);
        });
        
        // 4. Đếm số đã đậu theo ngành+phương thức từ DB
        nguyenVongRepository.findByNvKetqua("TRUNG_TUYEN").forEach(nv -> {
            String key = nv.getNvManganh() + "_" + nv.getTtPhuongthuc();
            admissionCount.put(key, admissionCount.getOrDefault(key, 0) + 1);
        });
        
        // 5. Map lưu điểm cuối của NV trúng tuyển theo ngành+phươngthức
        Map<String, Double> lastAdmittedScoreByMethod = new HashMap<>();
        
        // 6. Chỉ xét NV đang ở trạng thái CHO_XET, sort [diem DESC, nv_tt ASC]
        List<XtNguyenvongxettuyen> pendingNVs = nguyenVongRepository.findAll().stream()
                .filter(nv -> "CHO_XET".equals(nv.getNvKetqua()))
                .sorted((a, b) -> {
                    // Sort by điểm DESC, nv_tt ASC
                    int diemCompare = Double.compare(
                        b.getDiemXettuyen() != null ? b.getDiemXettuyen() : 0,
                        a.getDiemXettuyen() != null ? a.getDiemXettuyen() : 0
                    );
                    if (diemCompare != 0) return diemCompare;
                    // Tie-breaker: nv_tt nhỏ hơn (ưu tiên cao hơn)
                    return Integer.compare(
                        a.getNvTt() != null ? a.getNvTt() : 999,
                        b.getNvTt() != null ? b.getNvTt() : 999
                    );
                })
                .collect(Collectors.toList());
        
        int trungTuyenCount = 0;
        
        for (XtNguyenvongxettuyen nv : pendingNVs) {
            String cccd = nv.getNnCccd();
            String manganh = nv.getNvManganh();
            String phuongThuc = nv.getTtPhuongthuc();
            String majorMethodKey = manganh + "_" + phuongThuc;
            
            // Lấy quota cho ngành + phương thức
            int quota = getQuotaForMajorMethod(quotaByMajorMethod, manganh, phuongThuc);
            
            // KIỂM TRA 1: Thí sinh đã đậu NV nào trước đó chưa?
            if (Boolean.TRUE.equals(admittedCandidates.get(cccd))) {
                nv.setNvKetqua("TRUOT");
                nguyenVongRepository.save(nv);
                continue;
            }
            
            // KIỂM TRA 2: Ngành + phương thức còn quota không?
            int currentAdmitted = admissionCount.getOrDefault(majorMethodKey, 0);
            
            if (currentAdmitted < quota) {
                // Đủ quota -> Đậu
                nv.setNvKetqua("TRUNG_TUYEN");
                nv.setLyDo("Trúng tuyển theo phương thức " + phuongThuc);
                admittedCandidates.put(cccd, true);
                admissionCount.put(majorMethodKey, currentAdmitted + 1);
                trungTuyenCount++;
                
                // Lưu điểm của NV cuối cùng đậu trong phương thức này
                lastAdmittedScoreByMethod.put(majorMethodKey, nv.getDiemXettuyen());
            } else {
                // Hết quota -> Rớt với lý do rõ ràng
                nv.setNvKetqua("TRUOT");
                nv.setLyDo("Đạt quota (" + (currentAdmitted + 1) + "/" + quota + ") cho ngành " + manganh);
            }
            
            nguyenVongRepository.save(nv);
        }
        
        // Tính điểm chuẩn chung cho mỗi ngành = MIN điểm cuối của tất cả phương thức
        Map<String, Double> finalBenchmarkScores = new HashMap<>();
        
        // Group các điểm theo ngành (không có phương thức)
        Map<String, List<Double>> scoresByMajor = new HashMap<>();
        for (Map.Entry<String, Double> entry : lastAdmittedScoreByMethod.entrySet()) {
            String key = entry.getKey();
            String manganh = key.split("_")[0];
            scoresByMajor.computeIfAbsent(manganh, k -> new ArrayList<>()).add(entry.getValue());
        }
        
        // Tính MIN điểm cho mỗi ngành
        for (Map.Entry<String, List<Double>> entry : scoresByMajor.entrySet()) {
            String manganh = entry.getKey();
            List<Double> scores = entry.getValue();
            Double minScore = scores.stream().filter(Objects::nonNull).min(Double::compare).orElse(null);
            if (minScore != null) {
                finalBenchmarkScores.put(manganh, minScore);
            }
        }
        
        // Update điểm chuẩn chung vào bảng ngành
        for (Map.Entry<String, Double> entry : finalBenchmarkScores.entrySet()) {
            String manganh = entry.getKey();
            Double diemChuan = entry.getValue();
            
            majorRepository.findByManganh(manganh).ifPresent(major -> {
                major.setNDiemtrungtuyen(diemChuan);
                majorRepository.save(major);
                log.info("Cập nhật điểm chuẩn ngành {} = {}", manganh, diemChuan);
            });
        }
        
        log.info("Xét tuyển tự động hoàn thành: {} thí sinh trúng tuyển trên {} nguyện vọng", 
                 trungTuyenCount, pendingNVs.size());
        
        return trungTuyenCount;
    }
    
    /**
     * Build map quota cho tất cả ngành + phương thức
     */
    private Map<String, Map<String, Integer>> buildQuotaMap() {
        Map<String, Map<String, Integer>> result = new HashMap<>();
        
        for (XtNganh major : majorRepository.findAll()) {
            Map<String, Integer> methodQuota = new HashMap<>();
            
            if (Boolean.TRUE.equals(major.getNThpt())) {
                methodQuota.put("THPT", major.getSlThpt() != null ? major.getSlThpt() : 0);
            }
            if (Boolean.TRUE.equals(major.getNDgnl())) {
                methodQuota.put("DGNL", major.getSlDgnl() != null ? major.getSlDgnl() : 0);
            }
            if (Boolean.TRUE.equals(major.getNVsat())) {
                methodQuota.put("VSAT", major.getSlVsat() != null ? major.getSlVsat() : 0);
            }
            if (Boolean.TRUE.equals(major.getNTuyenthang())) {
                methodQuota.put("TUYEN_THANG", major.getSlXtt() != null ? major.getSlXtt() : 0);
            }
            
            result.put(major.getManganh(), methodQuota);
        }
        
        return result;
    }
    
    /**
     * Lấy quota cho ngành + phương thức cụ thể
     */
    private int getQuotaForMajorMethod(Map<String, Map<String, Integer>> quotaMap, 
                                        String manganh, String phuongThuc) {
        Map<String, Integer> methodQuota = quotaMap.get(manganh);
        if (methodQuota == null) return 0;
        return methodQuota.getOrDefault(phuongThuc, 0);
    }
    
    /**
     * Cập nhật điểm chuẩn vào bảng ngành
     */
    private void updateBenchmarkScore(String manganh, String phuongThuc, Double diemXettuyen) {
        // Điểm chuẩn được cập nhật cuối cùng ở vòng lặp chính
        // Không cần update riêng ở đây nữa
    }

    private AdmissionResultDTO toDTO(XtNguyenvongxettuyen entity) {
        // Lấy họ tên từ bảng thí sinh
        var candidateOpt = candidateRepository.findByCccd(entity.getNnCccd());
        String hoVaTen = candidateOpt.map(XtThisinhxettuyen25::getHoVaTen).orElse(entity.getNnCccd());
        String sobaodanh = candidateOpt.map(XtThisinhxettuyen25::getSobaodanh).orElse("-");
        
        // Lấy tên ngành và điểm chuẩn
        var majorOpt = majorRepository.findByManganh(entity.getNvManganh());
        String tennganh = majorOpt.map(XtNganh::getTennganh).orElse(entity.getNvManganh());
        Double diemChuan = majorOpt.map(XtNganh::getNDiemtrungtuyen).orElse(null);
        
        // Lấy tổ hợp môn từ nguyện vọng (trường ttThm lưu mã tổ hợp)
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