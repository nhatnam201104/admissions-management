package com.example.managementadmissionwf.dto.major;

//import com.example.managementadmissionwf.util.ExcelColumn;
import com.example.managementadmissionwf.annotation.ExcelColumn;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MajorTohopDTO {

    private Integer id;

    @NotBlank(message = "Mã ngành không được để trống")
    @ExcelColumn(name = "Mã ngành", index = 1)
    private String maNganh;

    @NotBlank(message = "Mã tổ hợp không được để trống")
    @ExcelColumn(name = "Mã tổ hợp", index = 2)
    private String maToHop;

    @NotBlank(message = "Môn 1 không được để trống")
    @ExcelColumn(name = "Môn 1", index = 3)
    private String thMon1;

    @NotNull(message = "Hệ số môn 1 không được để trống")
    @Positive(message = "Hệ số môn 1 phải lớn hơn 0")
    @ExcelColumn(name = "HS Môn 1", index = 4)
    private Double hsMon1;

    @NotBlank(message = "Môn 2 không được để trống")
    @ExcelColumn(name = "Môn 2", index = 5)
    private String thMon2;

    @NotNull(message = "Hệ số môn 2 không được để trống")
    @Positive(message = "Hệ số môn 2 phải lớn hơn 0")
    @ExcelColumn(name = "HS Môn 2", index = 6)
    private Double hsMon2;

    @NotBlank(message = "Môn 3 không được để trống")
    @ExcelColumn(name = "Môn 3", index = 7)
    private String thMon3;

    @NotNull(message = "Hệ số môn 3 không được để trống")
    @Positive(message = "Hệ số môn 3 phải lớn hơn 0")
    @ExcelColumn(name = "HS Môn 3", index = 8)
    private Double hsMon3;
}