package com.example.managementadmissionwf.bus.impl;

import com.example.managementadmissionwf.bus.interfaces.BonusScoreService;
import com.example.managementadmissionwf.dal.entity.XtDiemcongxettuyen;
import com.example.managementadmissionwf.dal.entity.XtThisinhxettuyen25;
import com.example.managementadmissionwf.dal.repository.BonusScoreRepository;
import com.example.managementadmissionwf.dal.repository.CandidateRepository;
import com.example.managementadmissionwf.dto.common.ImportResult;
import com.example.managementadmissionwf.dto.score.BonusScoreDTO;
import com.example.managementadmissionwf.dto.score.BonusScoreViewDTO;
import com.example.managementadmissionwf.mapper.BonusScoreMapper;
import com.example.managementadmissionwf.utils.ExcelUtil;
import com.example.managementadmissionwf.utils.PriorityScoreCalculator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class BonusScoreServiceImpl implements BonusScoreService {

    private final BonusScoreRepository bonusScoreRepository;
    private final BonusScoreMapper bonusScoreMapper;
    private final CandidateRepository candidateRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<BonusScoreDTO> getAllBonusScores(Pageable pageable) {
        return bonusScoreRepository.findAll(pageable).map(bonusScoreMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BonusScoreViewDTO> searchBonusScoreViews(String keyword, Pageable pageable) {
        String cleanKeyword = keyword != null && !keyword.trim().isEmpty()
                ? keyword.trim()
                : null;
        return bonusScoreRepository.searchDisplayRows(cleanKeyword, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public BonusScoreDTO getBonusScoreByCccd(String cccd) {
        if (cccd == null || cccd.trim().isEmpty()) {
            throw new RuntimeException("CCCD không được để trống");
        }
        String cleanCccd = cccd.trim();
        return bonusScoreRepository.findByCccd(cleanCccd)
                .map(bonusScoreMapper::toDto)
                .orElseThrow(() ->
                        new RuntimeException("Chưa có điểm cộng cho CCCD: " + cleanCccd));
    }

    @Override
    @Transactional
    public BonusScoreDTO createBonusScore(BonusScoreDTO dto) {
        String cccd = dto.getCccd().trim();
        if (bonusScoreRepository.findByCccd(cccd).isPresent()) {
            throw new RuntimeException("Đã tồn tại điểm cộng cho CCCD: " + cccd);
        }
        autoFillUtxtFromCandidate(dto);
        try {
            XtDiemcongxettuyen entity = bonusScoreMapper.toEntity(dto);
            entity.setCccd(cccd);
            return bonusScoreMapper.toDto(bonusScoreRepository.save(entity));
        } catch (DataIntegrityViolationException e) {
            throw new RuntimeException("Lỗi dữ liệu: " + e.getMostSpecificCause().getMessage());
        }
    }

    @Override
    @Transactional
    public BonusScoreDTO updateBonusScore(BonusScoreDTO dto) {
        String cccd = dto.getCccd().trim();
        XtDiemcongxettuyen entity = bonusScoreRepository.findByCccd(cccd)
                .orElseThrow(() ->
                        new RuntimeException("Không tìm thấy điểm cộng cho CCCD: " + cccd));
        autoFillUtxtFromCandidate(dto);
        bonusScoreMapper.updateEntityFromDto(dto, entity);
        return bonusScoreMapper.toDto(bonusScoreRepository.save(entity));
    }

    @Override
    @Transactional
    public void deleteBonusScore(String cccd) {
        String cleanCccd = cccd.trim();
        XtDiemcongxettuyen entity = bonusScoreRepository.findByCccd(cleanCccd)
                .orElseThrow(() ->
                        new RuntimeException("Không tìm thấy điểm cộng cho CCCD: " + cleanCccd));
        entity.setIsDeleted(true);
        bonusScoreRepository.save(entity);
    }

    /**
     * Tự động điền điểm ưu tiên (diemUtxt) từ khu_vuc + doi_tuong của thí
     * sinh nếu user không nhập. Vẫn cho phép user override bằng cách nhập
     * giá trị > 0.
     */
    private void autoFillUtxtFromCandidate(BonusScoreDTO dto) {
        if (dto.getDiemUtxt() != null && dto.getDiemUtxt() > 0) {
            return;
        }
        Optional<XtThisinhxettuyen25> candidateOpt = candidateRepository.findByCccd(dto.getCccd().trim());
        if (candidateOpt.isPresent()) {
            double auto = PriorityScoreCalculator.calculateForCandidate(candidateOpt.get());
            dto.setDiemUtxt(auto);
            log.info("Auto-filled diemUtxt={} for CCCD={} (KV={}, ĐT={})",
                    auto, dto.getCccd(),
                    candidateOpt.get().getKhuVuc(), candidateOpt.get().getDoiTuong());
        }
    }

    /**
     * Import điểm cộng từ Excel. Mỗi dòng map sang
     * {@link BonusScoreDTO} qua {@code @ExcelColumn}. Nếu user để trống cột
     * "Điểm UTXT", giá trị được auto-fill theo khu vực/đối tượng của thí sinh.
     */
    @Transactional
    public ImportResult<BonusScoreDTO> importExcel(InputStream inputStream) {
        ImportResult<BonusScoreDTO> result = new ImportResult<>();
        List<String> errors = new ArrayList<>();
        List<BonusScoreDTO> validData = new ArrayList<>();
        try {
            List<BonusScoreDTO> imported = ExcelUtil.importExcel(inputStream, BonusScoreDTO.class);
            result.setTotalRows(imported.size());
            int row = 2;
            for (BonusScoreDTO dto : imported) {
                try {
                    if (dto.getCccd() == null || dto.getCccd().isBlank()) {
                        errors.add("Dòng " + row + ": CCCD trống");
                        row++;
                        continue;
                    }
                    String cccd = dto.getCccd().trim();
                    dto.setCccd(cccd);
                    if (!candidateRepository.existsByCccd(cccd)) {
                        errors.add("Dòng " + row + ": CCCD không tồn tại: " + cccd);
                        row++;
                        continue;
                    }

                    Optional<XtDiemcongxettuyen> existing = bonusScoreRepository.findByCccd(cccd);
                    if (existing.isPresent()) {
                        // update
                        autoFillUtxtFromCandidate(dto);
                        bonusScoreMapper.updateEntityFromDto(dto, existing.get());
                        bonusScoreRepository.save(existing.get());
                    } else {
                        // create
                        autoFillUtxtFromCandidate(dto);
                        XtDiemcongxettuyen entity = bonusScoreMapper.toEntity(dto);
                        entity.setCccd(cccd);
                        bonusScoreRepository.save(entity);
                    }
                    validData.add(dto);
                } catch (Exception ex) {
                    errors.add("Dòng " + row + ": " + ex.getMessage());
                }
                row++;
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

    /**
     * Recompute lại điểm ưu tiên cho toàn bộ thí sinh dựa trên khu vực + đối
     * tượng hiện tại. Hữu ích sau khi cập nhật danh sách thí sinh.
     * @return số bản ghi được cập nhật.
     */
    @Transactional
    public int recomputeAllPriorityPoints() {
        int updated = 0;
        for (XtDiemcongxettuyen entity : bonusScoreRepository.findAll()) {
            if (Boolean.TRUE.equals(entity.getIsDeleted())) {
                continue;
            }
            Optional<XtThisinhxettuyen25> candidate = candidateRepository.findByCccd(entity.getCccd());
            if (candidate.isEmpty()) {
                continue;
            }
            double auto = PriorityScoreCalculator.calculateForCandidate(candidate.get());
            if (entity.getDiemUtxt() == null || entity.getDiemUtxt() != auto) {
                entity.setDiemUtxt(auto);
                bonusScoreRepository.save(entity);
                updated++;
            }
        }
        log.info("Recompute priority points: updated {} records", updated);
        return updated;
    }

    @Override
    @Transactional
    public void upsertForCandidate(String cccd) {
        String cleanCccd = cccd.trim();
        XtThisinhxettuyen25 candidate = candidateRepository.findByCccd(cleanCccd)
                .orElseThrow(() -> new RuntimeException(
                        "Không tìm thấy thí sinh để tạo điểm cộng: CCCD=" + cleanCccd));
        double diemUtxt = PriorityScoreCalculator.calculateForCandidate(candidate);

        Optional<XtDiemcongxettuyen> existing = bonusScoreRepository.findByCccd(cleanCccd);
        XtDiemcongxettuyen entity;
        if (existing.isPresent()) {
            entity = existing.get();
            entity.setDiemUtxt(diemUtxt);
            // diemCc giữ nguyên; diemTong tự recompute trong @PreUpdate
        } else {
            entity = XtDiemcongxettuyen.builder()
                    .cccd(cleanCccd)
                    .diemCc(0.0)
                    .diemUtxt(diemUtxt)
                    .build();
        }
        bonusScoreRepository.save(entity);
        log.info("Upsert bonus row CCCD={}, diemUtxt={} (KV={}, ĐT={})",
                cleanCccd, diemUtxt, candidate.getKhuVuc(), candidate.getDoiTuong());
    }
}
