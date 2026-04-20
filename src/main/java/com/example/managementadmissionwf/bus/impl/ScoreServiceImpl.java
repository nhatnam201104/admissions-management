package com.example.managementadmissionwf.bus.impl;

import com.example.managementadmissionwf.bus.interfaces.ScoreService;
import com.example.managementadmissionwf.dal.entity.XtDiemthixettuyen;
import com.example.managementadmissionwf.dal.entity.XtThisinhxettuyen25;
import com.example.managementadmissionwf.dal.repository.CandidateRepository;
import com.example.managementadmissionwf.dal.repository.ScoreRepository;
import com.example.managementadmissionwf.dto.score.ScoreDTO;
import com.example.managementadmissionwf.mapper.ScoreMapper;
import lombok.RequiredArgsConstructor;

import org.springframework.core.NestedRuntimeException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ScoreServiceImpl implements ScoreService {

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

        String filterPhuongThuc = ("Tất cả".equals(phuongThuc) || phuongThuc == null)
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
    // ================= CREATE =================
    @Override
    @Transactional
    public ScoreDTO createScore(ScoreDTO dto) {
        String cccd = normalizeCccd(dto.getCccd());
        dto.setCccd(cccd);

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

    private String getRootCause(Exception e) {
        Throwable cause = ((NestedRuntimeException) e).getMostSpecificCause();
        return (cause != null) ? cause.getMessage() : e.getMessage();
    }
}