package com.example.managementadmissionwf.dto.admission;

import com.example.managementadmissionwf.annotation.ExcelColumn;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO map từ Excel cho bảng nguyện vọng. Sau khi import, service sẽ tự gọi
 * {@code AspirationScoreService.calculateForAspiration} để tính điểm.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AspirationImportDTO {

    @ExcelColumn(name = "CCCD")
    private String cccd;

    @ExcelColumn(name = "Mã ngành")
    private String maNganh;

    @ExcelColumn(name = "NV")
    private Integer nvTt;

    @ExcelColumn(name = "Phương thức")
    private String phuongThuc;

    @ExcelColumn(name = "Tổ hợp")
    private String toHop;
}
