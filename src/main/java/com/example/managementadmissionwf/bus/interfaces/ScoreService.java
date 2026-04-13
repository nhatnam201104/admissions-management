package com.example.managementadmissionwf.bus.interfaces;

import com.example.managementadmissionwf.dto.score.ScoreDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ScoreService {
    Page<ScoreDTO> getAllScores(Pageable pageable);
    
    ScoreDTO getScoreByCccd(String cccd);
    
    ScoreDTO createScore(ScoreDTO dto);
    
    ScoreDTO updateScore(ScoreDTO dto);
    
    void deleteScore(String cccd);
}