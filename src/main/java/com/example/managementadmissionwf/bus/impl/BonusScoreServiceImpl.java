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
        return bonusScoreRepository.findByCccd(cccd)
                .map(bonusScoreMapper::toDto)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy điểm cộng cho CCCD: " + cccd));
    }

    @Override
    @Transactional
    public BonusScoreDTO createBonusScore(BonusScoreDTO dto) {
        try {
            XtDiemcongxettuyen entity = bonusScoreMapper.toEntity(dto);
            // diemTong sẽ được tự động tính qua @PrePersist/PreUpdate trong Entity nếu bạn đã cấu hình
            XtDiemcongxettuyen savedEntity = bonusScoreRepository.save(entity);
            return bonusScoreMapper.toDto(savedEntity);
        } catch (DataIntegrityViolationException e) {
            throw new RuntimeException("Lỗi: Điểm cộng cho thí sinh có CCCD " + dto.getCccd() + " đã tồn tại.");
        }
    }

    @Override
    @Transactional
    public BonusScoreDTO updateBonusScore(BonusScoreDTO dto) {
        XtDiemcongxettuyen existingEntity = bonusScoreRepository.findByCccd(dto.getCccd())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy điểm cộng để cập nhật cho CCCD: " + dto.getCccd()));
        
        bonusScoreMapper.updateEntityFromDto(dto, existingEntity);
        
        // Tính toán lại tổng điểm trước khi lưu để đảm bảo tính chính xác
        double diemCc = (existingEntity.getDiemCc() != null) ? existingEntity.getDiemCc() : 0.0;
        double diemUtxt = (existingEntity.getDiemUtxt() != null) ? existingEntity.getDiemUtxt() : 0.0;
        existingEntity.setDiemTong(diemCc + diemUtxt);
        
        XtDiemcongxettuyen updatedEntity = bonusScoreRepository.save(existingEntity);
        return bonusScoreMapper.toDto(updatedEntity);
    }

    @Override
    @Transactional
    public void deleteBonusScore(String cccd) {
        XtDiemcongxettuyen entity = bonusScoreRepository.findByCccd(cccd)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy điểm cộng để xóa cho CCCD: " + cccd));
        
        bonusScoreRepository.delete(entity);
    }
}