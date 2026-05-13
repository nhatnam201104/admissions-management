package com.example.managementadmissionwf.bus.impl;

import com.example.managementadmissionwf.bus.interfaces.AspirationScoreService;
import com.example.managementadmissionwf.bus.interfaces.ScoreService;
import com.example.managementadmissionwf.dal.entity.XtDiemthixettuyen;
import com.example.managementadmissionwf.dal.entity.XtThisinhxettuyen25;
import com.example.managementadmissionwf.dal.repository.CandidateRepository;
import com.example.managementadmissionwf.dal.repository.ScoreRepository;
import com.example.managementadmissionwf.dto.common.ImportResult;
import com.example.managementadmissionwf.dto.score.ScoreDTO;
import com.example.managementadmissionwf.mapper.ScoreMapper;
import com.example.managementadmissionwf.util.ExcelUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.core.NestedRuntimeException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class ScoreServiceImpl implements ScoreService {

    private static final String FILTER_ALL = "Tất cả";

    private static final String METHOD_THPT = "THPT";
    private static final String METHOD_DGNL = "DGNL";
    private static final String METHOD_VSAT = "VSAT";
    private static final double THPT_MAX_SCORE = 10;
    private static final double VSAT_MAX_SCORE = 150;
    private static final double DGNL_NL1_MAX_SCORE = 1200;
    private static final double DGNL_NK_MAX_SCORE = 100;

    private static final Set<String> VALID_METHODS = Set.of(METHOD_THPT, METHOD_DGNL, METHOD_VSAT);

    private final ScoreRepository scoreRepository;
    private final ScoreMapper scoreMapper;
    private final CandidateRepository candidateRepository;
    private final AspirationScoreService aspirationScoreService;
    // ================= GET =================
    @Override
    @Transactional(readOnly = true)
    public Page<ScoreDTO> getAllScores(Pageable pageable) {
        return scoreRepository.findAllWithActiveCandidate(pageable)
                .map(scoreMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ScoreDTO> searchScores(String keyword, String phuongThuc, Pageable pageable) {
        String searchKey = (keyword == null || keyword.trim().isEmpty())
                ? null
                : "%" + keyword.trim() + "%";

        String filterPhuongThuc = (FILTER_ALL.equals(phuongThuc) || phuongThuc == null)
                ? null
                : phuongThuc;

        return scoreRepository.searchScores(searchKey, filterPhuongThuc, pageable)
                .map(scoreMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public ScoreDTO getScoreByCccd(String cccd) {
        return getScoreByCccdAndPhuongThuc(cccd, METHOD_THPT);
    }

    @Override
    @Transactional(readOnly = true)
    public ScoreDTO getScoreByCccdAndPhuongThuc(String cccd, String phuongThuc) {
        String cleanCccd = normalizeCccd(cccd);
        String method = normalizeMethod(phuongThuc);

        return scoreRepository.findByCccdAndDPhuongthuc(cleanCccd, method)
                .map(scoreMapper::toDto)
                .orElseThrow(() ->
                        new RuntimeException("Không tìm thấy điểm thi với CCCD: " + cleanCccd + " và phương thức: " + method));
    }

    @Override
    public boolean existsCandidateByCccd(String cccd) {
        return candidateRepository.existsByCccd(cccd.trim());
    }

    @Override
    public XtThisinhxettuyen25 getCandidateByCccd(String cccd) {
        return scoreRepository.findCandidateByCccd(cccd.trim());
    }

    @Override
    @Transactional(readOnly = true)
    public void exportExcel(OutputStream outputStream, String keyword, String phuongThuc) {
        try {
            String searchKey = (keyword == null || keyword.trim().isEmpty())
                    ? null
                    : "%" + keyword.trim() + "%";
            String filterPhuongThuc = (FILTER_ALL.equals(phuongThuc) || phuongThuc == null)
                    ? null
                    : phuongThuc;

            List<ScoreDTO> data = scoreRepository
                    .searchScores(searchKey, filterPhuongThuc, Pageable.unpaged())
                    .getContent()
                    .stream()
                    .map(scoreMapper::toDto)
                    .toList();

            ExcelUtil.exportExcel(data, ScoreDTO.class, outputStream);
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi xuất file Excel: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public ImportResult<ScoreDTO> importExcel(InputStream inputStream) {
        ImportResult<ScoreDTO> result = new ImportResult<>();
        List<String> errors = new ArrayList<>();
        List<ScoreDTO> validData = new ArrayList<>();

        try {
            List<ScoreDTO> importedData = ExcelUtil.importExcel(inputStream, ScoreDTO.class);
            result.setTotalRows(importedData.size());

            int rowIndex = 2;
            for (ScoreDTO dto : importedData) {
                try {
                    prepareImportedScore(dto);
                    validateScore(dto);

                    if (scoreRepository.existsByCccdAndDPhuongthuc(dto.getCccd(), dto.getPhuongThuc())) {
                        updateScore(dto);
                    } else {
                        createScore(dto);
                    }

                    validData.add(dto);
                } catch (Exception e) {
                    errors.add("Dòng " + rowIndex + ": " + e.getMessage());
                }
                rowIndex++;
            }

            result.setSuccessCount(validData.size());
            result.setErrorCount(errors.size());
            result.setErrors(errors);
            result.setValidData(validData);
            return result;
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi đọc file Excel: " + e.getMessage(), e);
        }
    }
    // ================= CREATE =================
    @Override
    @Transactional
    public ScoreDTO createScore(ScoreDTO dto) {
        String cccd = normalizeCccd(dto.getCccd());
        dto.setCccd(cccd);

        validateScore(dto);

        // Kiểm tra trùng (cccd + phuongThuc) - mỗi thí sinh chỉ có 1 record cho mỗi phương thức
        if (scoreRepository.existsByCccdAndDPhuongthuc(cccd, dto.getPhuongThuc())) {
            throw new RuntimeException("Thí sinh đã có điểm cho phương thức: " + dto.getPhuongThuc());
        }

        XtDiemthixettuyen entity = scoreMapper.toEntity(dto);
        entity.setIsDeleted(false);

        ScoreDTO saved = scoreMapper.toDto(scoreRepository.save(entity));
        recalculateAspirationScores(cccd);
        return saved;
    }

    @Override
    public boolean existsByCccd(String cccd) {
        return scoreRepository.existsByCccdAndIsDeletedFalse(cccd);
    }

    // ================= UPDATE =================
    @Override
    @Transactional
    public ScoreDTO updateScore(ScoreDTO dto) {
        String cccd = normalizeCccd(dto.getCccd());
        dto.setCccd(cccd);

        validateScore(dto);

        XtDiemthixettuyen existing = scoreRepository.findByCccdAndDPhuongthuc(cccd, dto.getPhuongThuc())
                .orElseThrow(() ->
                        new RuntimeException("Không tìm thấy điểm thi cho CCCD: " + cccd + " với phương thức: " + dto.getPhuongThuc()));

        try {
            scoreMapper.updateEntityFromDto(dto, existing);
            ScoreDTO saved = scoreMapper.toDto(scoreRepository.save(existing));
            recalculateAspirationScores(cccd);
            return saved;

        } catch (DataIntegrityViolationException e) {
            throw new RuntimeException("Cập nhật thất bại: " + getRootCause(e));
        }
    }

    // ================= DELETE (SOFT) =================
    @Override
    @Transactional
    public void deleteScore(String cccd, String phuongThuc) {
        String cleanCccd = normalizeCccd(cccd);
        String method = normalizeMethod(phuongThuc);

        XtDiemthixettuyen entity = scoreRepository.findByCccdAndDPhuongthuc(cleanCccd, method)
                .orElseThrow(() ->
                        new RuntimeException("Không tìm thấy điểm thi với CCCD: " + cleanCccd + " và phương thức: " + method));

        entity.setIsDeleted(true);
        scoreRepository.save(entity);
    }

    // ================= UTIL =================
    private String normalizeCccd(String cccd) {
        if (cccd == null || cccd.trim().isEmpty()) {
            throw new RuntimeException("CCCD không được để trống");
        }
        return cccd.trim();
    }

    private void validateRequiredFields(ScoreDTO dto) {
        if (dto.getSobaodanh() == null || dto.getSobaodanh().trim().isEmpty()) {
            throw new RuntimeException("Số báo danh không được để trống");
        }

        if (dto.getPhuongThuc() == null || dto.getPhuongThuc().trim().isEmpty()) {
            throw new RuntimeException("Phương thức không được để trống");
        }
    }

    private void prepareImportedScore(ScoreDTO dto) {
        if (dto.getCccd() != null) {
            dto.setCccd(dto.getCccd().trim());
        }
        if (dto.getSobaodanh() != null) {
            dto.setSobaodanh(dto.getSobaodanh().trim());
        }
        if (dto.getPhuongThuc() != null) {
            dto.setPhuongThuc(dto.getPhuongThuc().trim().toUpperCase());
        }

        if (dto.getCccd() != null && (dto.getSobaodanh() == null || dto.getSobaodanh().isBlank())) {
            XtThisinhxettuyen25 candidate = scoreRepository.findCandidateByCccd(dto.getCccd());
            if (candidate != null) {
                dto.setSobaodanh(candidate.getSobaodanh());
            }
        }

    }

    private void validateScore(ScoreDTO dto) {
        String cccd = normalizeCccd(dto.getCccd());
        dto.setCccd(cccd);

        if (!cccd.matches("\\d{12}")) {
            throw new RuntimeException("CCCD phải gồm đúng 12 chữ số");
        }
        if (!candidateRepository.existsByCccd(cccd)) {
            throw new RuntimeException("CCCD không tồn tại trong hệ thống thí sinh");
        }

        validateRequiredFields(dto);

        String phuongThuc = normalizeMethod(dto.getPhuongThuc());
        dto.setPhuongThuc(phuongThuc);

        validateScoreRangeByMethod(dto);
    }

    private String normalizeMethod(String phuongThuc) {
        if (phuongThuc == null || phuongThuc.trim().isEmpty()) {
            throw new RuntimeException("Phương thức không được để trống");
        }

        String normalized = phuongThuc.trim().toUpperCase();
        if (!VALID_METHODS.contains(normalized)) {
            throw new RuntimeException("Phương thức phải là THPT, DGNL hoặc VSAT");
        }
        return normalized;
    }

    private void validateScoreRangeByMethod(ScoreDTO dto) {
        switch (dto.getPhuongThuc()) {
            case METHOD_DGNL -> {
                validateRange("NL1", dto.getNl1(), 0, DGNL_NL1_MAX_SCORE);
                validateRange("NK1", dto.getNk1(), 0, DGNL_NK_MAX_SCORE);
                validateRange("NK2", dto.getNk2(), 0, DGNL_NK_MAX_SCORE);
            }
            case METHOD_VSAT -> {
                validateRange("Toán", dto.getToan(), 0, VSAT_MAX_SCORE);
                validateRange("Lý", dto.getLy(), 0, VSAT_MAX_SCORE);
                validateRange("Hóa", dto.getHoa(), 0, VSAT_MAX_SCORE);
                validateRange("Sinh", dto.getSinh(), 0, VSAT_MAX_SCORE);
                validateRange("Sử", dto.getSu(), 0, VSAT_MAX_SCORE);
                validateRange("Địa", dto.getDia(), 0, VSAT_MAX_SCORE);
                validateRange("Văn", dto.getVan(), 0, VSAT_MAX_SCORE);
                validateRange("Anh", dto.getN1Thi(), 0, VSAT_MAX_SCORE);
            }
            default -> {
                validateRange("Toán", dto.getToan(), 0, THPT_MAX_SCORE);
                validateRange("Lý", dto.getLy(), 0, THPT_MAX_SCORE);
                validateRange("Hóa", dto.getHoa(), 0, THPT_MAX_SCORE);
                validateRange("Sinh", dto.getSinh(), 0, THPT_MAX_SCORE);
                validateRange("Sử", dto.getSu(), 0, THPT_MAX_SCORE);
                validateRange("Địa", dto.getDia(), 0, THPT_MAX_SCORE);
                validateRange("Văn", dto.getVan(), 0, THPT_MAX_SCORE);
                validateRange("N1 Thi", dto.getN1Thi(), 0, THPT_MAX_SCORE);
                validateRange("N1 CC", dto.getN1Cc(), 0, THPT_MAX_SCORE);
            }
        }
    }

    private void validateRange(String fieldName, Double value, double min, double max) {
        if (value == null) {
            return;
        }
        if (value < min || value > max) {
            throw new RuntimeException(fieldName + " phải từ " + formatLimit(min) + " đến " + formatLimit(max));
        }
    }

    private String formatLimit(double value) {
        if (value == Math.rint(value)) {
            return String.valueOf((long) value);
        }
        return String.valueOf(value);
    }

    private String getRootCause(Exception e) {
        Throwable cause = ((NestedRuntimeException) e).getMostSpecificCause();
        return (cause != null) ? cause.getMessage() : e.getMessage();
    }

    private void recalculateAspirationScores(String cccd) {
        try {
            aspirationScoreService.calculateAllForCccd(cccd);
        } catch (Exception e) {
            log.warn("Lỗi khi tính lại điểm nguyện vọng cho CCCD={}: {}", cccd, e.getMessage());
        }
    }
}
