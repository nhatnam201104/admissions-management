package com.example.managementadmissionwf.bus.interfaces;

import com.example.managementadmissionwf.dto.score.BonusScoreDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BonusScoreService {
    Page<BonusScoreDTO> getAllBonusScores(Pageable pageable);
    
    BonusScoreDTO getBonusScoreByCccd(String cccd);
    
    BonusScoreDTO createBonusScore(BonusScoreDTO dto);
    
    BonusScoreDTO updateBonusScore(BonusScoreDTO dto);
    
    void deleteBonusScore(String cccd);
}
