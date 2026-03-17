package com.example.managementadmissionwf.dal.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

/**
 * Entity: xt_nganh
 * Danh sách ngành đào tạo
 * Master Data - Admission System
 */
@Entity
@Table(name = "xt_nganh")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class XtNganh {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer idnganh;
    
    @Column(nullable = false, length = 50)
    String manganh;
    
    @Column(nullable = false)
    String tennganh;
    
    @Column(name = "n_tohopgoc")
    String nTohopgoc; // tổ hợp gốc
    
    @Column(name = "n_chitieu")
    Integer nChitieu; // chỉ tiêu
    
    @Column(name = "n_diemsan")
    Double nDiemsan; // điểm sàn
    
    @Column(name = "n_diemtrungtuyen")
    Double nDiemtrungtuyen; // điểm chuẩn
    
    @Column(name = "n_tuyenthang")
    Boolean nTuyenthang; // có tuyển thẳng
    
    @Column(name = "n_dgnl")
    Boolean nDgnl; // có xét ĐGNL
    
    @Column(name = "n_thpt")
    Boolean nThpt; // xét điểm THPT
    
    @Column(name = "n_vsat")
    Boolean nVsat; // xét VSAT
    
    // Các cột thống kê
    @Column(name = "sl_xtt")
    Integer slXtt = 0; // số lượng tuyển thẳng
    
    @Column(name = "sl_dgnl")
    Integer slDgnl = 0; // số lượng ĐGNL
    
    @Column(name = "sl_vsat")
    Integer slVsat = 0; // số lượng VSAT
    
    @Column(name = "sl_thpt")
    Integer slThpt = 0; // số lượng THPT
    
    LocalDate createdAt = LocalDate.now();
    LocalDate updatedAt = LocalDate.now();
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDate.now();
    }
}