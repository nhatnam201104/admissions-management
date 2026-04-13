package com.example.managementadmissionwf.dto.candidate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

import com.example.managementadmissionwf.annotation.ExcelColumn;

/**
 * DTO for Candidate Management
 * Maps to xt_thisinhxettuyen25 table
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CandidateDTO {
	
	@ExcelColumn(name = "CCCD")
    private String cccd;
    
    @ExcelColumn(name = "SBD")
    private String sobaodanh;
    
    @ExcelColumn(name = "Họ")
    private String ho;
    
    @ExcelColumn(name = "Tên")
    private String ten;
    
    @ExcelColumn(name = "Ngày Sinh")
    private Date ngaySinh;
    
    @ExcelColumn(name = "SĐT")
    private String dienThoai;
    
    @ExcelColumn(name = "Email")
    private String email;
    
    @ExcelColumn(name = "Giới Tính")
    private String gioiTinh;
    
    @ExcelColumn(name = "Nơi Sinh")
    private String noiSinh;
    
    @ExcelColumn(name = "Đối Tượng")
    private String doiTuong;
    
    @ExcelColumn(name = "Khu Vực")
    private String khuVuc;
    
    // Helper method to get full name
    public String getHoTen() {
        return ho + " " + ten;
    }
}