package com.example.managementadmissionwf.bus.interfaces;

import com.example.managementadmissionwf.dal.entity.XtThisinhxettuyen25;
import com.example.managementadmissionwf.dto.common.ImportResult;
import com.example.managementadmissionwf.dto.score.ScoreDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.annotation.Validated;

import java.io.InputStream;
import java.io.OutputStream;

@Validated
public interface ScoreService {
    Page<ScoreDTO> getAllScores(Pageable pageable);

    Page<ScoreDTO> searchScores(String keyword, String phuongThuc, Pageable pageable);

    ScoreDTO getScoreByCccd(@NotBlank(message = "CCCD không được để trống") String cccd);
    
    ScoreDTO getScoreByCccdAndPhuongThuc(@NotBlank(message = "CCCD không được để trống") String cccd, 
            @NotBlank(message = "Phương thức không được để trống") String phuongThuc);
    
    ScoreDTO createScore(@Valid ScoreDTO dto);
    
    ScoreDTO updateScore(@Valid ScoreDTO dto);
    
    void deleteScore(@NotBlank(message = "CCCD không được để trống") String cccd, 
            @NotBlank(message = "Phương thức không được để trống") String phuongThuc);

    boolean existsByCccd(String cccd);
    boolean existsCandidateByCccd(String cccd);
    XtThisinhxettuyen25 getCandidateByCccd(String cccd);

    void exportExcel(OutputStream outputStream, String keyword, String phuongThuc);

    ImportResult<ScoreDTO> importExcel(InputStream inputStream);
}
