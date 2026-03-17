package com.example.managementadmissionwf.dal.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

/**
 * Entity: xt_bangquydoi
 * Bảng quy đổi điểm
 * Maps certificate scores to subject scores
 * Admission Processing - Admission System
 */
@Entity
@Table(name = "xt_bangquydoi")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class XtBangquydoi {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;
    
    @Column(name = "d_phuongthuc", nullable = false, length = 50)
    String dPhuongthuc; // phương thức xét tuyển (THPT, DGNL, VSAT)
    
    @Column(name = "d_tohop", length = 10)
    String dTohop; // tổ hợp (A00, D01, etc.)
    
    @Column(name = "d_mon", nullable = false, length = 10)
    String dMon; // môn (TO, LI, N1, NL1, etc.)
    
    @Column(name = "d_diema", nullable = false)
    Double dDiema; // điểm A (điểm thấp nhất trong khoảng)
    
    @Column(name = "d_diemb", nullable = false)
    Double dDiemb; // điểm B (điểm cao nhất trong khoảng)
    
    @Column(name = "d_diemc", nullable = false)
    Double dDiemc; // điểm C (điểm quy đổi tương ứng)
    
    @Column(name = "d_diemd")
    Double dDiemd; // điểm D (điểm quy đổi tối đa - optional)
    
    LocalDate createdAt = LocalDate.now();
    LocalDate updatedAt = LocalDate.now();
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDate.now();
    }
}