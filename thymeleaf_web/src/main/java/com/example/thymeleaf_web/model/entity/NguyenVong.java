package com.example.thymeleaf_web.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "xt_nguyenvongxettuyen")
@Data
@NoArgsConstructor
public class NguyenVong {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "nn_cccd", nullable = false, length = 12)
    private String nnCccd;

    @Column(name = "nv_manganh", nullable = false, length = 50)
    private String nvManganh;

    @Column(name = "nv_tt", nullable = false)
    private Integer nvTt;

    @Column(name = "diem_thxt")
    private Double diemThxt;

    @Column(name = "diem_utqd")
    private Double diemUtqd;

    @Column(name = "diem_cong")
    private Double diemCong;

    @Column(name = "diem_xettuyen")
    private Double diemXettuyen;

    @Column(name = "nv_ketqua", length = 20)
    private String nvKetqua;

    @Column(name = "tt_phuongthuc", length = 45)
    private String ttPhuongthuc;

    @Column(name = "tt_thm", length = 45)
    private String ttThm;

    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted = false;

    private LocalDate createdAt = LocalDate.now();

    private LocalDate updatedAt = LocalDate.now();

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDate.now();
        }
        updatedAt = LocalDate.now();
        if (isDeleted == null) {
            isDeleted = false;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDate.now();
        if (isDeleted == null) {
            isDeleted = false;
        }
    }
}
