package com.example.managementadmissionwf.dto.score;


import com.example.managementadmissionwf.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BonusScoreDTO {
    @ExcelColumn(name = "CCCD")
    private String cccd;

    @ExcelColumn(name = "Điểm CC")
    private Double diemCc;

    @ExcelColumn(name = "Điểm UTXT")
    private Double diemUtxt;

    @ExcelColumn(name = "Tổng điểm cộng")
    private Double diemTong;
}