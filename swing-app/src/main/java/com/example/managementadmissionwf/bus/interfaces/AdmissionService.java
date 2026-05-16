package com.example.managementadmissionwf.bus.interfaces;

import com.example.managementadmissionwf.dal.entity.XtDiemthixettuyen;
import com.example.managementadmissionwf.dal.entity.XtNganh;
import com.example.managementadmissionwf.dal.entity.XtNganhTohop;
import com.example.managementadmissionwf.dal.entity.XtNguyenvongxettuyen;

import com.example.managementadmissionwf.dal.repository.CandidateRepository;
import com.example.managementadmissionwf.dal.repository.MajorRepository;
import com.example.managementadmissionwf.dal.repository.NganhTohopRepository;
import com.example.managementadmissionwf.dal.repository.ScoreRepository;
import com.example.managementadmissionwf.dal.repository.NguyenVongRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Service xử lý xét tuyển
 * Logic:
 * 1. Tính điểm xét tuyển cho mỗi nguyện vọng
 * 2. Xét tuyển theo ngành - lấy top nChitieu theo điểm
 * 3. Cập nhật kết quả: TRUNG_TUYEN / TRUOT / CHO_XET
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AdmissionService {

    private final NguyenVongRepository nguyenVongRepository;
    private final CandidateRepository candidateRepository;
    private final ScoreRepository scoreRepository;
    private final MajorRepository majorRepository;
    private final NganhTohopRepository nganhTohopRepository;

    /**
     * Xét tuyển toàn bộ nguyện vọng
     */
    @Transactional
    public int runAdmissionAll() {
        log.info("Starting full admission process...");
        
        List<XtNguyenvongxettuyen> allAspirations = nguyenVongRepository.findAll();
        int updated = processAdmission(allAspirations);
        
        log.info("Full admission completed. Updated {} aspirations.", updated);
        return updated;
    }

    /**
     * Xét tuyển chỉ những nguyện vọng đang chờ xét (CHO_XET)
     */
    @Transactional
    public int runAdmissionPending() {
        log.info("Starting pending admission process...");
        
        List<XtNguyenvongxettuyen> pendingAspirations = nguyenVongRepository
            .findByNvKetqua("CHO_XET");
        int updated = processAdmission(pendingAspirations);
        
        log.info("Pending admission completed. Updated {} aspirations.", updated);
        return updated;
    }

    /**
     * Xử lý xét tuyển cho một danh sách nguyện vọng
     */
    private int processAdmission(List<XtNguyenvongxettuyen> aspirations) {
        // Group by ngành
        Map<String, List<XtNguyenvongxettuyen>> byMajor = aspirations.stream()
            .collect(Collectors.groupingBy(XtNguyenvongxettuyen::getNvManganh));

        // Xử lý từng ngành
        for (Map.Entry<String, List<XtNguyenvongxettuyen>> entry : byMajor.entrySet()) {
            String manganh = entry.getKey();
            List<XtNguyenvongxettuyen> majorAspirations = entry.getValue();
            
            processMajorAdmission(manganh, majorAspirations);
        }

        // Lưu tất cả
        nguyenVongRepository.saveAll(aspirations);
        return aspirations.size();
    }

    /**
     * Xét tuyển cho một ngành cụ thể
     */
    private void processMajorAdmission(String manganh, List<XtNguyenvongxettuyen> aspirations) {
        Optional<XtNganh> majorOpt = majorRepository.findByManganh(manganh);
        if (majorOpt.isEmpty()) {
            log.warn("Major {} not found, skipping", manganh);
            return;
        }

        XtNganh major = majorOpt.get();
        Double diemChuan = major.getNDiemtrungtuyen();
        Integer chitieu = major.getNChitieu();

        // Nếu chưa có điểm chuẩn → tất cả = CHO_XET
        if (diemChuan == null) {
            aspirations.forEach(a -> a.setNvKetqua("CHO_XET"));
            return;
        }

        // Tính điểm cho tất cả nguyện vọng chưa có điểm
        aspirations.forEach(a -> {
            if (a.getDiemXettuyen() == null) {
                calculateScore(a);
            }
        });

        // Sort theo điểm DESC, rồi theo NV
        aspirations.sort((a, b) -> {
            int scoreCompare = Double.compare(
                b.getDiemXettuyen() != null ? b.getDiemXettuyen() : 0,
                a.getDiemXettuyen() != null ? a.getDiemXettuyen() : 0
            );
            if (scoreCompare != 0) return scoreCompare;
            return a.getNvTt().compareTo(b.getNvTt()); // Ưu tiên NV thấp hơn
        });

        // Track thí sinh đã đậu NV nào
        Set<String> admittedStudents = new HashSet<>();
        int admittedCount = 0;

        for (XtNguyenvongxettuyen aspiration : aspirations) {
            String cccd = aspiration.getNnCccd();
            Double diemxt = aspiration.getDiemXettuyen();

            // Thí sinh đã đậu ngành nào → các NV khác = TRUOT
            if (admittedStudents.contains(cccd)) {
                aspiration.setNvKetqua("TRUOT");
                continue;
            }

            // Kiểm tra điều kiện điểm chuẩn
            if (diemxt == null || diemxt < diemChuan) {
                aspiration.setNvKetqua("TRUOT");
                continue;
            }

            // Kiểm tra chỉ tiêu
            if (admittedCount >= chitieu) {
                aspiration.setNvKetqua("TRUOT");
                continue;
            }

            // TRÚNG TUYỂN
            aspiration.setNvKetqua("TRUNG_TUYEN");
            admittedStudents.add(cccd);
            admittedCount++;
        }

        log.info("Major {}: {} admitted out of {} (chitieu={}, diemchuan={})", 
            manganh, admittedCount, aspirations.size(), chitieu, diemChuan);
    }

    /**
     * Tính điểm xét tuyển cho một nguyện vọng
     */
    public void calculateScore(XtNguyenvongxettuyen aspiration) {
        String cccd = aspiration.getNnCccd();
        String manganh = aspiration.getNvManganh();
        String phuongthuc = aspiration.getTtPhuongthuc();

        // Lấy điểm thi của thí sinh
        Optional<XtDiemthixettuyen> scoreOpt = scoreRepository.findByCccdAndDPhuongthuc(cccd, phuongthuc);
        if (scoreOpt.isEmpty()) {
            log.warn("No score found for cccd={}, phuongthuc={}", cccd, phuongthuc);
            return;
        }

        XtDiemthixettuyen score = scoreOpt.get();

        // Lấy tổ hợp xét tuyển của ngành (lấy cái đầu tiên)
        List<XtNganhTohop> tohopList = nganhTohopRepository.findByManganh(manganh);
        if (tohopList.isEmpty()) {
            log.warn("No subject group mapping for major {}", manganh);
            return;
        }

        XtNganhTohop tohop = tohopList.get(0);

        // Tính điểm tổ hợp = mon1*hs1 + mon2*hs2 + mon3*hs3
        double diemThxt = calculateSubjectScore(score, 
            tohop.getThMon1(), tohop.getThMon2(), tohop.getThMon3(),
            tohop.getHsmon1(), tohop.getHsmon2(), tohop.getHsmon3());

        aspiration.setDiemThxt(diemThxt);

        // Tính điểm xét tuyển cuối cùng
        double diemUtqd = aspiration.getDiemUtqd() != null ? aspiration.getDiemUtqd() : 0;
        double diemCong = aspiration.getDiemCong() != null ? aspiration.getDiemCong() : 0;
        double diemXettuyen = diemThxt + diemUtqd + diemCong;

        aspiration.setDiemXettuyen(diemXettuyen);

        log.debug("Score calculated for cccd={}: thxt={}, utqd={}, cong={}, xettuyen={}", 
            cccd, diemThxt, diemUtqd, diemCong, diemXettuyen);
    }

    /**
     * Tính điểm tổ hợp (3 môn nhân hệ số)
     */
    private double calculateSubjectScore(XtDiemthixettuyen score, 
            String mon1, String mon2, String mon3,
            double hs1, double hs2, double hs3) {
        
        Double d1 = getScoreBySubject(score, mon1);
        Double d2 = getScoreBySubject(score, mon2);
        Double d3 = getScoreBySubject(score, mon3);

        if (d1 == null || d2 == null || d3 == null) {
            return 0.0;
        }

        return d1 * hs1 + d2 * hs2 + d3 * hs3;
    }

    /**
     * Lấy điểm theo mã môn
     */
    private Double getScoreBySubject(XtDiemthixettuyen score, String mon) {
        if (mon == null) return null;
        
        return switch (mon.toUpperCase()) {
            case "TO" -> score.getTo();
            case "LY" -> score.getLi();
            case "HO" -> score.getHo();
            case "SI" -> score.getSi();
            case "SU" -> score.getSu();
            case "DI" -> score.getDi();
            case "VA" -> score.getVa();
            default -> null;
        };
    }

    /**
     * Lấy chi tiết điểm của một thí sinh cho modal
     */
    public Map<String, Object> getScoreDetails(String cccd, String manganh) {
        Map<String, Object> details = new HashMap<>();

        // Lấy thông tin thí sinh
        candidateRepository.findByCccd(cccd).ifPresent(candidate -> {
            details.put("hoTen", candidate.getHoVaTen());
            details.put("ngaySinh", candidate.getNgaySinh());
        });

        // Lấy ngành
        majorRepository.findByManganh(manganh).ifPresent(major -> {
            details.put("tenNganh", major.getTennganh());
            details.put("diemChuan", major.getNDiemtrungtuyen());
            details.put("chitieu", major.getNChitieu());
        });

        // Lấy tổ hợp (lấy cái đầu tiên)
        List<XtNganhTohop> tohopList = nganhTohopRepository.findByManganh(manganh);
        if (!tohopList.isEmpty()) {
            XtNganhTohop tohop = tohopList.get(0);
            details.put("tohop", tohop.getMatohop());
            details.put("mon1", tohop.getThMon1());
            details.put("mon2", tohop.getThMon2());
            details.put("mon3", tohop.getThMon3());
            details.put("hs1", tohop.getHsmon1());
            details.put("hs2", tohop.getHsmon2());
            details.put("hs3", tohop.getHsmon3());
        }

        // Lấy điểm thi
        scoreRepository.findByCccdAndDPhuongthuc(cccd, "THPT").ifPresent(score -> {
            details.put("to", score.getTo());
            details.put("li", score.getLi());
            details.put("ho", score.getHo());
            details.put("si", score.getSi());
            details.put("su", score.getSu());
            details.put("di", score.getDi());
            details.put("va", score.getVa());
        });

        return details;
    }
}