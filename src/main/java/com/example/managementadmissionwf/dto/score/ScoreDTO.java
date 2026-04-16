package com.example.managementadmissionwf.dto.score;

import com.example.managementadmissionwf.annotation.*;

import jakarta.validation.constraints.Max;
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
    @Min(value = 0, message = "Điểm không được âm")
    @Max(value = 10, message = "Điểm không được vượt quá 10")
    private Double toan;
    
    @ExcelColumn(name = "Lý")
    @Min(value = 0, message = "Điểm không được âm")
    @Max(value = 10, message = "Điểm không được vượt quá 10")
    private Double ly;

    @ExcelColumn(name = "Hóa")
    @Min(value = 0, message = "Điểm không được âm")
    @Max(value = 10, message = "Điểm không được vượt quá 10")
    private Double hoa;

    @ExcelColumn(name = "Sinh")
    @Min(value = 0, message = "Điểm không được âm")
    @Max(value = 10, message = "Điểm không được vượt quá 10")
    private Double sinh;

    @ExcelColumn(name = "Sử")
    @Min(value = 0, message = "Điểm không được âm")
    @Max(value = 10, message = "Điểm không được vượt quá 10")
    private Double su;

    @ExcelColumn(name = "Địa")
    @Min(value = 0, message = "Điểm không được âm")
    @Max(value = 10, message = "Điểm không được vượt quá 10")
    private Double dia;

    @ExcelColumn(name = "Văn")
    @Min(value = 0, message = "Điểm không được âm")
    @Max(value = 10, message = "Điểm không được vượt quá 10")
    private Double van;
    
    @ExcelColumn(name = "Ngoại ngữ (Thi)")
    @Min(value = 0, message = "Điểm không được âm")
    @Max(value = 10, message = "Điểm không được vượt quá 10")
    private Double n1Thi;
    @ExcelColumn(name = "Ngoại ngữ (CC)")
    @Min(value = 0, message = "Điểm không được âm")
    @Max(value = 10, message = "Điểm không được vượt quá 10")
    private Double n1Cc;
    
    @ExcelColumn(name = "NL1")
    @Min(0) @Max(1200)
    private Double nl1;
    @ExcelColumn(name = "NK1")
    @Min(0) @Max(100) 
    private Double nk1;
    @ExcelColumn(name = "NK2")
    private Double nk2;
    
    public Double getN1CcCalculated() {
        if (n1Thi == null && n1Cc == null) return 0.0;
        if (n1Cc == null) return n1Thi;
        if (n1Thi == null) return n1Cc;
        return Math.max(n1Thi, n1Cc);
    }
}