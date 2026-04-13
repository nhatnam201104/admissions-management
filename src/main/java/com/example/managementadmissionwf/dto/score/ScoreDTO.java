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
public class ScoreDTO {
    @ExcelColumn(name = "CCCD")
    private String cccd;

    @ExcelColumn(name = "Số báo danh")
    private String sobaodanh;

    @ExcelColumn(name = "Phương thức")
    private String phuongThuc; // THPT, DGNL, VSAT
    
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
        if (n1Thi == null && n1Cc == null) return 0.0;
        if (n1Cc == null) return n1Thi;
        if (n1Thi == null) return n1Cc;
        return Math.max(n1Thi, n1Cc);
    }
}