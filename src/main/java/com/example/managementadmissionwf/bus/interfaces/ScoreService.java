package com.example.managementadmissionwf.bus.interfaces;

import com.example.managementadmissionwf.dto.score.ScoreDTO;

import java.util.List;

/**
 * Service Interface for Score Management
 */
public interface ScoreService {
    List<ScoreDTO> getAllScores();
    
    ScoreDTO getScoreByCccd(String cccd);
    
    ScoreDTO createScore(ScoreDTO dto);
    
    ScoreDTO updateScore(ScoreDTO dto);
    
    void deleteScore(String cccd);
    
    Double calculateTotalScore(ScoreDTO dto);
}