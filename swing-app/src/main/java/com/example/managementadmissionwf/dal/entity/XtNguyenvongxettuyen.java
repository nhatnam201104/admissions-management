package com.example.managementadmissionwf.dal.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDate;

/**
 * Entity: xt_nguyenvongxettuyen
 * Danh sách nguyện vọng xét tuyển
 * Admission Processing - Admission System
 */
@Entity
@Table(name = "xt_nguyenvongxettuyen")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@SQLRestriction("is_deleted = false")
public class XtNguyenvongxettuyen {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @ManyToOne
    @JoinColumn(name = "nn_cccd", referencedColumnName = "cccd", insertable = false, updatable = false)
    XtThisinhxettuyen25 thisinh;

    @Column(name = "nn_cccd", nullable = false, length = 12)
    String nnCccd; // thí sinh

    @ManyToOne
    @JoinColumn(name = "nv_manganh", referencedColumnName = "manganh", insertable = false, updatable = false)
    XtNganh nganh;

    @Column(name = "nv_manganh", nullable = false, length = 50)
    String nvManganh; // ngành

    @Column(name = "nv_tt", nullable = false)
    Integer nvTt; // thứ tự NV

    // Điểm
    @Column(name = "diem_thxt")
    Double diemThxt; // tổng điểm 3 môn (mon1*hs1 + mon2*hs2 + mon3*hs3)

    @Column(name = "diem_utqd")
    Double diemUtqd; // điểm ưu tiên theo quy định

    @Column(name = "diem_cong")
    Double diemCong; // điểm cộng (IELTS, học sinh giỏi, etc.)

    @Column(name = "diem_xettuyen")
    Double diemXettuyen; // điểm cuối = diem_thxt + diem_utqd + diem_cong

    // Kết quả
    @Column(name = "nv_ketqua", length = 20)
    String nvKetqua; // TRUNG_TUYEN, TRUOT, CHO_XET

    @Column(nullable = false)
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