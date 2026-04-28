package com.example.managementadmissionwf.bus.impl;

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

@Service
@RequiredArgsConstructor
public class ScoreServiceImpl implements ScoreService {

    private static final String FILTER_ALL = "Tất cả";

    private static final Set<String> VALID_METHODS = Set.of("THPT", "DGNL", "VSAT");

    private final ScoreRepository scoreRepository;
    private final ScoreMapper scoreMapper;
    private final CandidateRepository candidateRepository;
    // ================= GET =================
    @Override
    @Transactional(readOnly = true)
    public Page<ScoreDTO> getAllScores(Pageable pageable) {
        return scoreRepository.findAll(pageable)
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
        String cleanCccd = normalizeCccd(cccd);

        return scoreRepository.findByCccd(cleanCccd)
                .map(scoreMapper::toDto)
                .orElseThrow(() ->
                        new RuntimeException("Không tìm thấy thí sinh với CCCD: " + cleanCccd));
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
                    validateImportedScore(dto);

                    if (scoreRepository.existsByCccdAndIsDeletedFalse(dto.getCccd())) {
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

        validateRequiredFields(dto);

        if (!candidateRepository.existsByCccd(cccd)) {
            throw new RuntimeException("CCCD không tồn tại trong hệ thống thí sinh!");
        }

        if (scoreRepository.existsByCccdAndIsDeletedFalse(cccd)) {
            throw new RuntimeException("Thí sinh này đã có điểm!");
        }

        XtDiemthixettuyen existing = scoreRepository.findByCccdIncludeDeleted(cccd);

        if (existing != null) {
            existing.setIsDeleted(false);
            scoreMapper.updateEntityFromDto(dto, existing);
            return scoreMapper.toDto(scoreRepository.save(existing));
        }

        XtDiemthixettuyen entity = scoreMapper.toEntity(dto);
        entity.setIsDeleted(false);
        
        return scoreMapper.toDto(scoreRepository.save(entity));
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

        validateRequiredFields(dto);

        XtDiemthixettuyen existing = scoreRepository.findByCccd(cccd)
                .orElseThrow(() ->
                        new RuntimeException("Không tìm thấy thí sinh với CCCD: " + cccd));

        try {
            scoreMapper.updateEntityFromDto(dto, existing);
            return scoreMapper.toDto(scoreRepository.save(existing));

        } catch (DataIntegrityViolationException e) {
            throw new RuntimeException("Cập nhật thất bại: " + getRootCause(e));
        }
    }

    // ================= DELETE (SOFT) =================
    @Override
    @Transactional
    public void deleteScore(String cccd) {
        String cleanCccd = normalizeCccd(cccd);

        XtDiemthixettuyen entity = scoreRepository.findByCccd(cleanCccd)
                .orElseThrow(() ->
                        new RuntimeException("Không tìm thấy thí sinh với CCCD: " + cleanCccd));

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

    private void validateImportedScore(ScoreDTO dto) {
        String cccd = normalizeCccd(dto.getCccd());
        dto.setCccd(cccd);

        if (!cccd.matches("\\d{12}")) {
            throw new RuntimeException("CCCD phải gồm đúng 12 chữ số");
        }
        if (!candidateRepository.existsByCccd(cccd)) {
            throw new RuntimeException("CCCD không tồn tại trong hệ thống thí sinh");
        }

        validateRequiredFields(dto);

        if (!VALID_METHODS.contains(dto.getPhuongThuc())) {
            throw new RuntimeException("Phương thức phải là THPT, DGNL hoặc VSAT");
        }

        validateRange("Toán", dto.getToan(), 0, 10);
        validateRange("Lý", dto.getLy(), 0, 10);
        validateRange("Hóa", dto.getHoa(), 0, 10);
        validateRange("Sinh", dto.getSinh(), 0, 10);
        validateRange("Sử", dto.getSu(), 0, 10);
        validateRange("Địa", dto.getDia(), 0, 10);
        validateRange("Văn", dto.getVan(), 0, 10);
        validateRange("N1 Thi", dto.getN1Thi(), 0, 10);
        validateRange("N1 CC", dto.getN1Cc(), 0, 10);
        validateRange("NL1", dto.getNl1(), 0, 1200);
        validateRange("NK1", dto.getNk1(), 0, 100);
        validateRange("NK2", dto.getNk2(), 0, 100);
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
}
