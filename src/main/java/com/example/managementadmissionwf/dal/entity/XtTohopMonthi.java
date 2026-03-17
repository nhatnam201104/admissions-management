package com.example.managementadmissionwf.dal.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

/**
 * Entity: xt_tohop_monthi
 * Danh sách tổ hợp môn
 * Master Data - Admission System
 */
@Entity
@Table(name = "xt_tohop_monthi")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class XtTohopMonthi {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;
    
    @Column(name = "matohop", nullable = false, length = 10)
    String matohop; // mã tổ hợp (A00, D01...)
    
    @Column(nullable = false, length = 5)
    String mon1; // môn 1
    
    @Column(nullable = false, length = 5)
    String mon2; // môn 2
    
    @Column(nullable = false, length = 5)
    String mon3; // môn 3
    
    @Column(name = "tentohop", nullable = false)
    String tentohop; // tên tổ hợp
    
    LocalDate createdAt = LocalDate.now();
    LocalDate updatedAt = LocalDate.now();
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDate.now();
    }
}