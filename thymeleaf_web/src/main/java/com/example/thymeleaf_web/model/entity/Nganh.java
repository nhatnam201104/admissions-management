package com.example.thymeleaf_web.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "xt_nganh")
@Data
@NoArgsConstructor
public class Nganh {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idnganh;

    @Column(nullable = false, length = 50)
    private String manganh;

    @Column(nullable = false)
    private String tennganh;

    @Column(name = "n_diemtrungtuyen")
    private Double nDiemtrungtuyen;

    @Column(name = "n_diemsan")
    private Double nDiemsan;

    @Column(name = "n_chitieu")
    private Integer nChitieu;
}
