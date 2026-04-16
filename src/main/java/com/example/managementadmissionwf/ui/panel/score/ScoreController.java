package com.example.managementadmissionwf.ui.panel.score;

import com.example.managementadmissionwf.bus.interfaces.BonusScoreService;
import com.example.managementadmissionwf.bus.interfaces.ScoreService;
import com.example.managementadmissionwf.dto.score.BonusScoreDTO;
import com.example.managementadmissionwf.dto.score.ScoreDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
public class ScoreController {

    @Autowired
    private ScoreService scoreService;

    @Autowired
    private BonusScoreService bonusScoreService;

    // QUẢN LÝ ĐIỂM THI
    public Page<ScoreDTO> getScores(String keyword, String phuongThuc, int page, int size) {
        String pt = "Tất cả".equals(phuongThuc) ? null : phuongThuc;
        Pageable pageable = PageRequest.of(page, size);
        return scoreService.searchScores(keyword, pt, pageable);
    }

    public void saveScore(ScoreDTO dto, boolean isUpdate) throws Exception {
        if (isUpdate) {
            scoreService.updateScore(dto);
        } else {
            scoreService.createScore(dto);
        }
    }

    public void deleteScore(String cccd) throws Exception {
        scoreService.deleteScore(cccd);
    }

    // QUẢN LÝ ĐIỂM ƯU TIÊN
    public Page<BonusScoreDTO> getBonusScores(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return bonusScoreService.getAllBonusScores(pageable);
    }
    public BonusScoreDTO getBonusScoreByCccd(String cccd) {
        if (cccd == null || cccd.trim().isEmpty()) {
            throw new RuntimeException("CCCD không hợp lệ");
        }

        return bonusScoreService.getBonusScoreByCccd(cccd.trim());
    }

    public void saveBonusScore(BonusScoreDTO dto, boolean isUpdate) throws Exception {
        if (isUpdate) {
            bonusScoreService.updateBonusScore(dto);
        } else {
            bonusScoreService.createBonusScore(dto);
        }
    }

    public void deleteBonusScore(String cccd) throws Exception {
        bonusScoreService.deleteBonusScore(cccd);
    }
}