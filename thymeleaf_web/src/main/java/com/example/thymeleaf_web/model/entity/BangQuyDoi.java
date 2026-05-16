package com.example.thymeleaf_web.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "xt_bangquydoi")
@Data
@NoArgsConstructor
public class BangQuyDoi {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // Cột PK do swing-app/Hibernate auto-create với tên mặc định "id"
    // (xem XtBangquydoi.java). Trước đây ánh xạ "idqd" → SQLGrammarException.
    @Column(name = "id")
    private Integer id;


    @Column(name = "d_phuongthuc", nullable = false, length = 50)
    private String phuongThuc;

    @Column(name = "d_tohop", length = 10)
    private String toHop;

    @Column(name = "d_mon", nullable = false, length = 10)
    private String mon;

    @Column(name = "d_diema", nullable = false)
    private Double diemA;

    @Column(name = "d_diemb", nullable = false)
    private Double diemB;

    @Column(name = "d_diemc", nullable = false)
    private Double diemC;

    @Column(name = "d_diemd")
    private Double diemD;
}
