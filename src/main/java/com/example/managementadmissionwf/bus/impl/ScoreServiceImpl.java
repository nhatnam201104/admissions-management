package com.example.managementadmissionwf.bus.impl;

import com.example.managementadmissionwf.bus.interfaces.ScoreService;
import com.example.managementadmissionwf.dal.entity.XtDiemthixettuyen;
import com.example.managementadmissionwf.dal.repository.ScoreRepository;
import com.example.managementadmissionwf.dto.score.ScoreDTO;
import com.example.managementadmissionwf.mapper.ScoreMapper;
import lombok.RequiredArgsConstructor;
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
        // Chỉ lấy những bản ghi chưa bị xóa (isDeleted = false)
        return scoreRepository.findAll(pageable).map(scoreMapper::toDto);
    }

    /**
     * Phương thức tìm kiếm mới:
     * Hỗ trợ tìm theo từ khóa (CCCD/SBD) và lọc theo Phương thức xét tuyển.
     */
    @Override
    @Transactional(readOnly = true)
    public Page<ScoreDTO> searchScores(String keyword, String phuongThuc, Pageable pageable) {
        String searchKey = (keyword == null || keyword.trim().isEmpty()) ? null : "%" + keyword.trim() + "%";
    
        return scoreRepository.searchScores(searchKey, phuongThuc, pageable)
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
        if (scoreRepository.existsByCccd(dto.getCccd())) {
            throw new RuntimeException("Điểm thi cho CCCD này đã tồn tại!");
        }
        XtDiemthixettuyen entity = scoreMapper.toEntity(dto);
        // Đảm bảo bản ghi mới không ở trạng thái đã xóa
        entity.setIsDeleted(false);
        XtDiemthixettuyen savedEntity = scoreRepository.save(entity);
        return scoreMapper.toDto(savedEntity);
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
        
        // Thực hiện Soft Delete
        entity.setIsDeleted(true); 
        scoreRepository.save(entity);
    }
}