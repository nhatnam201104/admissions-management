package com.example.managementadmissionwf.dto.score;

import com.example.managementadmissionwf.annotation.*;

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
public class ScoreDTO {
    @ExcelColumn(name = "CCCD")
    @NotBlank(message = "CCCD không được để trống")
    @Pattern(regexp = "^\\d{12}$", message = "CCCD phải bao gồm đúng 12 chữ số")
    private String cccd;

    @ExcelColumn(name = "Số báo danh")
    @NotBlank(message = "Số báo danh không được để trống")
    private String sobaodanh;

    @ExcelColumn(name = "Phương thức")
    @NotBlank(message = "Phương thức không được để trống")
    private String phuongThuc;

    @ExcelColumn(name = "Toán")
    private Double toan;

    @ExcelColumn(name = "Lý")
    private Double ly;

    @ExcelColumn(name = "Hóa")
    private Double hoa;

    @ExcelColumn(name = "Sinh")
    private Double sinh;

    @ExcelColumn(name = "Sử")
    private Double su;

    @ExcelColumn(name = "Địa")
    private Double dia;

    @ExcelColumn(name = "Văn")
    private Double van;

    @ExcelColumn(name = "Ngoại ngữ (Thi)")
    private Double n1Thi;
    @ExcelColumn(name = "Ngoại ngữ (CC)")
    private Double n1Cc;

    @ExcelColumn(name = "NL1")
    private Double nl1;
    @ExcelColumn(name = "NK1")
    private Double nk1;
    @ExcelColumn(name = "NK2")
    private Double nk2;

    public Double getN1CcCalculated() {
        if (n1Thi == null && n1Cc == null) return null;
        if (n1Cc == null) return n1Thi;
        if (n1Thi == null) return n1Cc;
        return Math.max(n1Thi, n1Cc);
    }
}
