package com.example.managementadmissionwf.dto;

import com.example.managementadmissionwf.annotation.ExcelColumn;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConversionTableDTO {

    private Integer id;

    @ExcelColumn(name = "Phương thức", index = 0)
    private String phuongThuc;

    @ExcelColumn(name = "Tổ hợp", index = 1)
    private String toHop;

    @ExcelColumn(name = "Môn", index = 2)
    private String mon;

    @ExcelColumn(name = "Điểm A (Min)", index = 3)
    private Double diemA;

    @ExcelColumn(name = "Điểm B (Max)", index = 4)
    private Double diemB;

    @ExcelColumn(name = "Điểm quy đổi (C)", index = 5)
    private Double diemC;

    @ExcelColumn(name = "Điểm quy đổi (D)", index = 6)
    private Double diemD;

    private Boolean isDeleted;

    private LocalDate createdAt;

    private LocalDate updatedAt;
}