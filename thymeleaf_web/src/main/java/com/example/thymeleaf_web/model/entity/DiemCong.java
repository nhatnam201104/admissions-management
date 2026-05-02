package com.example.thymeleaf_web.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "xt_diemcongxettuyen")
@Data
@NoArgsConstructor
public class DiemCong {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "cccd", nullable = false, length = 12)
    private String cccd;

    @Column(name = "diemCC")
    private Double diemCc;

    @Column(name = "diemUtxt")
    private Double diemUtxt;

    @Column(name = "diemTong")
    private Double diemTong;
}
