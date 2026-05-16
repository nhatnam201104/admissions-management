package com.example.managementadmissionwf.bus.interfaces;

import com.example.managementadmissionwf.dto.common.ImportResult;
import com.example.managementadmissionwf.dto.score.BonusScoreDTO;
import com.example.managementadmissionwf.dto.score.BonusScoreViewDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.annotation.Validated;

import java.io.InputStream;

@Validated
public interface BonusScoreService {
    Page<BonusScoreDTO> getAllBonusScores(Pageable pageable);

    Page<BonusScoreViewDTO> searchBonusScoreViews(String keyword, Pageable pageable);

    BonusScoreDTO getBonusScoreByCccd(@NotBlank(message = "CCCD không được để trống")
                                      @Pattern(regexp = "^\\d{12}$", message = "CCCD phải đúng 12 chữ số")
                                      String cccd);

    BonusScoreDTO createBonusScore(@Valid BonusScoreDTO dto);

    BonusScoreDTO updateBonusScore(@Valid BonusScoreDTO dto);

    void deleteBonusScore(@NotBlank(message = "CCCD không được để trống") String cccd);

    /** Import điểm cộng từ Excel (header theo {@code @ExcelColumn}). */
    ImportResult<BonusScoreDTO> importExcel(InputStream inputStream);

    /** Recompute lại điểm ưu tiên cho toàn bộ thí sinh theo Quy chế tuyển sinh. */
    int recomputeAllPriorityPoints();
}
