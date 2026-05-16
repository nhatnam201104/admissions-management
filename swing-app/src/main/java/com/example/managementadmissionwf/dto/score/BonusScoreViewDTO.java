package com.example.managementadmissionwf.dto.score;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BonusScoreViewDTO {
    private String cccd;
    private String hoTen;
    private Integer nvTt;
    private String maNganh;
    private String tenNganh;
    private String phuongThuc;
    private String toHop;
    private Double diemCc;
    private Double diemUtxt;
    private Double diemTong;
}
