package com.example.managementadmissionwf.dal.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDate;

/**
 * Entity: xt_diemthixettuyen
 * Điểm thi của thí sinh (theo phương thức xét tuyển)
 * Mỗi thí sinh có thể có nhiều record cho các phương thức khác nhau (THPT, DGNL, VSAT)
 * Candidate Data - Admission System
 */
@Entity
@Table(name = "xt_diemthixettuyen", uniqueConstraints = {
    @UniqueConstraint(name = "cccd_phuongthuc_unique", columnNames = {"cccd", "d_phuongthuc"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@SQLRestriction("is_deleted = false")
public class XtDiemthixettuyen {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @OneToOne
    @JoinColumn(name = "cccd", referencedColumnName = "cccd", insertable = false, updatable = false)
    XtThisinhxettuyen25 thisinh;

    @Column(name = "cccd", nullable = false, length = 12)
    String cccd; // thí sinh

    @Column(name = "sobaodanh", nullable = false,  length = 10)
    String sobaodanh; // SBD - unique vì mỗi thí sinh có 1 SBD duy nhất

    @Column(name = "d_phuongthuc", nullable = false, length = 50)
    String dPhuongthuc; // phương thức xét tuyển (THPT, DGNL, VSAT)

    // Điểm các môn
    @Column(name = "`TO`")
    Double to; // Toán

    @Column(name = "LI")
    Double li; // Lý

    @Column(name = "HO")
    Double ho; // Hóa

    @Column(name = "SI")
    Double si; // Sinh

    @Column(name = "SU")
    Double su; // Sử

    @Column(name = "DI")
    Double di; // Địa

    @Column(name = "VA")
    Double va; // Văn

    // Ngoại ngữ
    @Column(name = "N1_THI")
    Double n1Thi; // Điểm N1 thi

    @Column(name = "N1_CC")
    Double n1Cc; // Điểm N1 chứng chỉ (max(thi, quy đổi))

    // Các bài thi khác
    @Column(name = "NL1")
    Double nl1; // Ngữ văn L1 (ĐGNL)

    @Column(name = "NK1")
    Double nk1; // Năng lực L1 (ĐGNL)

    @Column(name = "NK2")
    Double nk2; // Năng lực L2 (ĐGNL)

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