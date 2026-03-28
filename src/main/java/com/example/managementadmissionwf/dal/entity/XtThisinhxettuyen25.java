package com.example.managementadmissionwf.dal.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDate;

/**
 * Entity: xt_thisinhxettuyen25
 * Thông tin thí sinh
 * Candidate Data - Admission System
 * CCCD = primary identity
 */
@Entity
@Table(name = "xt_thisinhxettuyen25")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@SQLRestriction("is_deleted = false")
public class XtThisinhxettuyen25 {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;
    
    @Column(name = "cccd", nullable = false, unique = true, length = 12)
    String cccd; // CCCD - primary identity
    
    @Column(name = "sobaodanh", nullable = false, unique = true, length = 10)
    String sobaodanh; // số báo danh
    
    @Column(nullable = false, length = 50)
    String ho; // họ
    
    @Column(nullable = false, length = 50)
    String ten; // tên
    
    @Column(name = "ngay_sinh", nullable = false)
    LocalDate ngaySinh; // ngày sinh
    
    @Column(name = "dien_thoai", length = 15)
    String dienThoai; // phone
    
    @Column(nullable = false, length = 100)
    String email; // email
    
    @Column(name = "gioi_tinh", length = 10)
    String gioiTinh; // gender
    
    @Column(name = "noi_sinh", length = 255)
    String noiSinh; // birthplace
    
    @Column(name = "doi_tuong", length = 50)
    String doiTuong; // đối tượng ưu tiên
    
    @Column(name = "khu_vuc", length = 50)
    String khuVuc; // khu vực
    
    @Column(name = "ho_va_ten")
    String hoVaTen; // Họ và tên đầy đủ (computed field)
    
    @Column(nullable = false)
    @Builder.Default
    Boolean isDeleted = false;

    LocalDate createdAt = LocalDate.now();
    LocalDate updatedAt = LocalDate.now();
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDate.now();
    }
    
    // Get full name helper
    public String getHoVaTen() {
        if (hoVaTen != null) {
            return hoVaTen;
        }
        return ho + " " + ten;
    }
}