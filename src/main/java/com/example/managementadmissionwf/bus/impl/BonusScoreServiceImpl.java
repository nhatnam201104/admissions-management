package com.example.managementadmissionwf.bus.impl;

import com.example.managementadmissionwf.bus.interfaces.BonusScoreService;
import com.example.managementadmissionwf.dal.entity.XtDiemcongxettuyen;
import com.example.managementadmissionwf.dal.repository.BonusScoreRepository;
import com.example.managementadmissionwf.dto.score.BonusScoreDTO;
import com.example.managementadmissionwf.mapper.BonusScoreMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BonusScoreServiceImpl implements BonusScoreService {

    private final BonusScoreRepository bonusScoreRepository;
    private final BonusScoreMapper bonusScoreMapper; 

    @Override
    @Transactional(readOnly = true)
    public Page<BonusScoreDTO> getAllBonusScores(Pageable pageable) {
        return bonusScoreRepository.findAll(pageable).map(bonusScoreMapper::toDto);
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

        // ✅ check trước khi save
        if (bonusScoreRepository.findByCccd(cccd).isPresent()) {
            throw new RuntimeException("Đã tồn tại điểm cộng cho CCCD: " + cccd);
        }

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
}