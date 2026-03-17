package com.example.managementadmissionwf.dal.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

/**
 * Entity: xt_nganh_tohop
 * Mapping Ngành ↔ Tổ hợp môn
 * Master Data - Admission System
 */
@Entity
@Table(name = "xt_nganh_tohop")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class XtNganhTohop {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;
    
    @ManyToOne
    @JoinColumn(name = "manganh", referencedColumnName = "manganh", insertable = false, updatable = false)
    XtNganh nganh;
    
    @Column(name = "manganh", nullable = false, length = 50)
    String manganh; // mã ngành
    
    @ManyToOne
    @JoinColumn(name = "matohop", referencedColumnName = "matohop", insertable = false, updatable = false)
    XtTohopMonthi tohopMonthi;
    
    @Column(name = "matohop", nullable = false, length = 10)
    String matohop; // mã tổ hợp
    
    // Môn 1
    @Column(name = "th_mon1", nullable = false, length = 5)
    String thMon1; // môn
    
    @Column(name = "hsmon1", nullable = false)
    Double hsmon1; // hệ số
    
    // Môn 2
    @Column(name = "th_mon2", nullable = false, length = 5)
    String thMon2; // môn
    
    @Column(name = "hsmon2", nullable = false)
    Double hsmon2; // hệ số
    
    // Môn 3
    @Column(name = "th_mon3", nullable = false, length = 5)
    String thMon3; // môn
    
    @Column(name = "hsmon3", nullable = false)
    Double hsmon3; // hệ số
    
    LocalDate createdAt = LocalDate.now();
    LocalDate updatedAt = LocalDate.now();
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDate.now();
    }
}