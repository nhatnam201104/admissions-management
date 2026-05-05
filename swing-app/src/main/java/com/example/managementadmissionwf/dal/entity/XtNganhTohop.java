package com.example.managementadmissionwf.dal.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDate;

/**
 * Entity: xt_nganh_tohop
 * Mapping Ngành ↔ Tổ hợp môn
 */
@Entity
@Table(name = "xt_nganh_tohop")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@SQLRestriction("is_deleted = false") // ← THÊM
public class XtNganhTohop {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @ManyToOne
    @JoinColumn(name = "manganh", referencedColumnName = "manganh", insertable = false, updatable = false)
    XtNganh nganh;

    @Column(name = "manganh", nullable = false, length = 50)
    String manganh;

    @ManyToOne
    @JoinColumn(name = "matohop", referencedColumnName = "matohop", insertable = false, updatable = false)
    XtTohopMonthi tohopMonthi;

    @Column(name = "matohop", nullable = false, length = 50)
    String matohop;

    // Môn 1
    @Column(name = "th_mon1", nullable = false, length = 50)
    String thMon1;
    @Column(name = "hsmon1", nullable = false)
    Double hsmon1;

    // Môn 2
    @Column(name = "th_mon2", nullable = false, length = 50)
    String thMon2;
    @Column(name = "hsmon2", nullable = false)
    Double hsmon2;

    // Môn 3
    @Column(name = "th_mon3", nullable = false, length = 50)
    String thMon3;
    @Column(name = "hsmon3", nullable = false)
    Double hsmon3;

    @Column(name = "is_deleted", nullable = false)
    @Builder.Default
    Boolean isDeleted = false;
    @Builder.Default
    LocalDate createdAt = LocalDate.now();
    @Builder.Default
    LocalDate updatedAt = LocalDate.now();

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDate.now();
    }
}