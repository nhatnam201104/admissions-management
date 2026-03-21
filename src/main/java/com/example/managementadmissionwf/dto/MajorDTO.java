package com.example.managementadmissionwf.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MajorDTO {

    private Integer idNganh; 

    @NotBlank(message = "Mã ngành không được để trống")
    private String maNganh;

    @NotBlank(message = "Tên ngành không được để trống")
    private String tenNganh;

    private String nTohopGoc;

    @NotNull(message = "Chỉ tiêu không được để trống")
    @Min(value = 0, message = "Chỉ tiêu không được là số âm")
    private Integer nChiTieu;

    @PositiveOrZero(message = "Điểm sàn phải lớn hơn hoặc bằng 0")
    private Double nDiemSan;

    @PositiveOrZero(message = "Điểm trúng tuyển phải lớn hơn hoặc bằng 0")
    private Double nDiemTrungTuyen;

    @NotNull(message = "Vui lòng xác nhận trạng thái xét tuyển thẳng")
    private Boolean nTuyenThang;

    private Boolean nDgnl;
    
    private Boolean nThpt;
    
    private Boolean nVsat;

    @Min(value = 0, message = "Số lượng không hợp lệ")
    private Integer slXtt;

    @Min(value = 0, message = "Số lượng không hợp lệ")
    private Integer slDgnl;

    @Min(value = 0, message = "Số lượng không hợp lệ")
    private Integer slVsat;

    @Min(value = 0, message = "Số lượng không hợp lệ")
    private Integer slThpt;

    @Valid
    private List<MajorTohopDTO> tohopList;
}