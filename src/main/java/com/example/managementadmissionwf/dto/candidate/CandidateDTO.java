package com.example.managementadmissionwf.dto.candidate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

import com.example.managementadmissionwf.annotation.ExcelColumn;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;

/**
 * DTO for Candidate Management
 * Maps to xt_thisinhxettuyen25 table
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CandidateDTO {
	
	@NotBlank(message = "CCCD không được để trống")
    @Pattern(regexp = "^\\d{12}$", message = "CCCD phải bao gồm đúng 12 chữ số")
    @ExcelColumn(name = "CCCD")
    private String cccd;
    
    @NotBlank(message = "Số báo danh không được để trống")
    @ExcelColumn(name = "SBD")
    private String sobaodanh;
    
    @NotBlank(message = "Họ không được để trống")
    @Pattern(regexp = "^[\\p{L}\\s]+$", message = "Họ không được chứa số hoặc ký tự đặc biệt")
    @ExcelColumn(name = "Họ")
    private String ho;
    
    @NotBlank(message = "Tên không được để trống")
    @Pattern(regexp = "^[\\p{L}\\s]+$", message = "Tên không được chứa số hoặc ký tự đặc biệt")
    @ExcelColumn(name = "Tên")
    private String ten;
    
    @NotNull(message = "Ngày sinh không được để trống")
    @Past(message = "Ngày sinh phải là một ngày trong quá khứ")
    @ExcelColumn(name = "Ngày Sinh")
    private LocalDate ngaySinh;
    
    @Pattern(regexp = "^(0\\d{9})?$", message = "Số điện thoại phải có 10 chữ số và bắt đầu bằng số 0")
    @ExcelColumn(name = "SĐT")
    private String dienThoai;
    
    @Email(message = "Email không đúng định dạng")
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