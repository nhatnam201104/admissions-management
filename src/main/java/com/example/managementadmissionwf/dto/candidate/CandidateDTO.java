package com.example.managementadmissionwf.dto.candidate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * DTO for Candidate Management
 * Maps to xt_thisinhxettuyen25 table
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CandidateDTO {
    private String cccd;
    private String sobaodanh;
    private String ho;
    private String ten;
    private Date ngaySinh;
    private String dienThoai;
    private String email;
    private String gioiTinh;
    private String noiSinh;
    private String doiTuong;
    private String khuVuc;
    
    // Helper method to get full name
    public String getHoTen() {
        return ho + " " + ten;
    }
}