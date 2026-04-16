package com.example.managementadmissionwf.bus.interfaces;

import com.example.managementadmissionwf.dto.score.BonusScoreDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.annotation.Validated;

@Validated
public interface BonusScoreService {
    Page<BonusScoreDTO> getAllBonusScores(Pageable pageable);
    
    BonusScoreDTO getBonusScoreByCccd(@NotBlank(message = "CCCD không được để trống") 
                                     @Pattern(regexp = "^\\d{12}$", message = "CCCD phải đúng 12 chữ số") 
                                     String cccd);
    
    BonusScoreDTO createBonusScore(@Valid BonusScoreDTO dto);
    
    BonusScoreDTO updateBonusScore(@Valid BonusScoreDTO dto);
    
    void deleteBonusScore(@NotBlank(message = "CCCD không được để trống") String cccd);
}