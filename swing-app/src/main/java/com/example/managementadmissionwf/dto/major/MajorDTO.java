package com.example.managementadmissionwf.dto.major;

import com.example.managementadmissionwf.annotation.ExcelColumn;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MajorDTO {

    private Integer idNganh;

    @ExcelColumn(name = "Mã ngành", index = 0)
    private String maNganh;

    @ExcelColumn(name = "Tên ngành", index = 1)
    private String tenNganh;

    @ExcelColumn(name = "Tổ hợp gốc", index = 2)
    private String tohopGoc;

    @ExcelColumn(name = "Chỉ tiêu", index = 3)
    private Integer chiTieu;

    @ExcelColumn(name = "Điểm sàn", index = 4)
    private Double diemSan;

    @ExcelColumn(name = "Điểm chuẩn", index = 5)
    private Double diemTrungTuyen;

    @ExcelColumn(name = "Tuyển thẳng", index = 6)
    private Boolean tuyenThang;

    @ExcelColumn(name = "ĐGNL", index = 7)
    private Boolean dgnl;

    @ExcelColumn(name = "THPT", index = 8)
    private Boolean thpt;

    @ExcelColumn(name = "VSAT", index = 9)
    private Boolean vsat;

    // Số lượng tuyển sinh theo từng phương thức (invariant: tổng = chỉ tiêu).
    @ExcelColumn(name = "SL Tuyển thẳng", index = 10)
    private Integer slXtt;

    @ExcelColumn(name = "SL ĐGNL", index = 11)
    private Integer slDgnl;

    @ExcelColumn(name = "SL VSAT", index = 12)
    private Integer slVsat;

    @ExcelColumn(name = "SL THPT", index = 13)
    private Integer slThpt;

    private Long totalAspirations;

    @Valid
    private List<MajorTohopDTO> tohopList;
}
