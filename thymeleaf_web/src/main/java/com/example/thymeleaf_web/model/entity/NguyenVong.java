package com.example.thymeleaf_web.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

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
}
