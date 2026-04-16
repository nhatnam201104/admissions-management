package com.example.managementadmissionwf.bus.interfaces;

import com.example.managementadmissionwf.dto.score.ScoreDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.annotation.Validated;

@Validated
public interface ScoreService {
    Page<ScoreDTO> getAllScores(Pageable pageable);

    Page<ScoreDTO> searchScores(String keyword, String phuongThuc, Pageable pageable);

    ScoreDTO getScoreByCccd(@NotBlank(message = "CCCD không được để trống") String cccd);
    
    ScoreDTO createScore(@Valid ScoreDTO dto);
    
    ScoreDTO updateScore(@Valid ScoreDTO dto);
    
    void deleteScore(@NotBlank(message = "CCCD không được để trống") String cccd);
}