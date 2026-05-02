package com.example.thymeleaf_web.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "xt_diemthixettuyen")
@Data
@NoArgsConstructor
public class DiemThi {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "cccd", nullable = false, length = 12)
    private String cccd;

    @Column(name = "sobaodanh", nullable = false, length = 10)
    private String sobaodanh;

    @Column(name = "d_phuongthuc", length = 50)
    private String phuongThuc;

    @Column(name = "`TO`")
    private Double toan;

    @Column(name = "LI")
    private Double ly;

    @Column(name = "HO")
    private Double hoa;

    @Column(name = "SI")
    private Double sinh;

    @Column(name = "SU")
    private Double su;

    @Column(name = "DI")
    private Double dia;

    @Column(name = "VA")
    private Double van;

    @Column(name = "N1_THI")
    private Double n1Thi;

    @Column(name = "N1_CC")
    private Double n1Cc;

    @Column(name = "NL1")
    private Double nl1;

    @Column(name = "NK1")
    private Double nk1;

    @Column(name = "NK2")
    private Double nk2;
}
