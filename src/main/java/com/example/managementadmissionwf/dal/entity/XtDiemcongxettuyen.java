package com.example.managementadmissionwf.dal.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

/**
 * Entity: xt_diemcongxettuyen
 * Điểm cộng thêm cho thí sinh
 * Admission Processing - Admission System
 */
@Entity
@Table(name = "xt_diemcongxettuyen")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class XtDiemcongxettuyen {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;
    
    @OneToOne
    @JoinColumn(name = "cccd", referencedColumnName = "cccd", insertable = false, updatable = false)
    XtThisinhxettuyen25 thisinh;
    
    @Column(name = "cccd", nullable = false, unique = true, length = 12)
    String cccd; // thí sinh
    
    @Column(name = "diemCC")
    Double diemCc; // điểm chứng chỉ (IELTS, SAT, etc.)
    
    @Column(name = "diemUtxt")
    Double diemUtxt; // điểm ưu tiên đặc biệt (HSG, thể thao, etc.)
    
    @Column(name = "diemTong")
    Double diemTong; // tổng điểm cộng = diemCC + diemUtxt
    
    LocalDate createdAt = LocalDate.now();
    LocalDate updatedAt = LocalDate.now();
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDate.now();
    }
    
    // Compute total bonus points
    @PrePersist
    protected void onCreate() {
        if (diemTong == null) {
            diemTong = (diemCc != null ? diemCc : 0) + (diemUtxt != null ? diemUtxt : 0);
        }
    }
}