package com.example.managementadmissionwf.dto.admission;

import lombok.*;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdmissionResultDTO {
    private Integer id;
    private String cccd;
    private String hoTen;
    private String sobaodanh;
    private String manganh;
    private String tennganh;
    private Integer nvTt;
    private Double diemXettuyen;
    private String ketQua;
    private String phuongThuc;
    private LocalDate ngayXet;
    
    // Thêm trường mới cho bảng
    private String tohop;      // Tổ hợp môn xét tuyển
    private Double diemChuan;  // Điểm chuẩn ngành
    private String lyDo;       // Lý do rớt/trúng (VD: "Đạt quota")
}
