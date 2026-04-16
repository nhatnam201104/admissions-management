package com.example.managementadmissionwf.bus.impl;

import com.example.managementadmissionwf.bus.interfaces.ScoreService;
import com.example.managementadmissionwf.dal.entity.XtDiemthixettuyen;
import com.example.managementadmissionwf.dal.repository.ScoreRepository;
import com.example.managementadmissionwf.dto.score.ScoreDTO;
import com.example.managementadmissionwf.mapper.ScoreMapper;
import lombok.RequiredArgsConstructor;
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

    @Override
    @Transactional(readOnly = true)
    public Page<ScoreDTO> getAllScores(Pageable pageable) {
        return scoreRepository.findAll(pageable).map(scoreMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ScoreDTO> searchScores(String keyword, String phuongThuc, Pageable pageable) {
        String searchKey = (keyword == null || keyword.trim().isEmpty()) ? null : "%" + keyword.trim() + "%";
        String filterPhuongThuc = "Tất cả".equals(phuongThuc) ? null : phuongThuc;

        return scoreRepository.searchScores(searchKey, filterPhuongThuc, pageable)
                .map(scoreMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public ScoreDTO getScoreByCccd(String cccd) {
        return scoreRepository.findByCccd(cccd)
                .map(scoreMapper::toDto)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy điểm thi cho CCCD: " + cccd));
    }

    @Override
    @Transactional
    public ScoreDTO createScore(ScoreDTO dto) {
        try {
            XtDiemthixettuyen entity = scoreMapper.toEntity(dto);
            entity.setIsDeleted(false);
            
            XtDiemthixettuyen savedEntity = scoreRepository.save(entity);
            return scoreMapper.toDto(savedEntity);
            
        } catch (DataIntegrityViolationException e) {
            throw new RuntimeException("Lỗi: Thí sinh có CCCD " + dto.getCccd() + " đã tồn tại.");
        }
    }

    @Override
    @Transactional
    public ScoreDTO updateScore(ScoreDTO dto) {
        XtDiemthixettuyen existingEntity = scoreRepository.findByCccd(dto.getCccd())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy điểm thi để cập nhật cho CCCD: " + dto.getCccd()));
        
        scoreMapper.updateEntityFromDto(dto, existingEntity);
        
        XtDiemthixettuyen updatedEntity = scoreRepository.save(existingEntity);
        return scoreMapper.toDto(updatedEntity);
    }

    @Override
    @Transactional
    public void deleteScore(String cccd) {
        XtDiemthixettuyen entity = scoreRepository.findByCccd(cccd)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy điểm thi để xóa cho CCCD: " + cccd));
        
        entity.setIsDeleted(true); 
        scoreRepository.save(entity);
    }
}