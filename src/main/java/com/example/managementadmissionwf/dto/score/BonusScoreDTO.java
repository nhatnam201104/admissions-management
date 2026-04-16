package com.example.managementadmissionwf.dto.score;


import com.example.managementadmissionwf.annotation.*;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
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
    @NotBlank(message = "CCCD không được để trống")
    @Pattern(regexp = "^\\d{12}$", message = "CCCD phải đúng 12 chữ số")
    private String cccd;

    @ExcelColumn(name = "Điểm CC")
    @Min(value = 0, message = "Điểm cộng không được âm")
    private Double diemCc;

    @ExcelColumn(name = "Điểm UTXT")
    @Min(value = 0, message = "Điểm UTXT không được âm")
    private Double diemUtxt;

    @ExcelColumn(name = "Tổng điểm cộng")
    private Double diemTong;
}