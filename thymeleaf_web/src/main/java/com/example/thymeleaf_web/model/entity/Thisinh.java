package com.example.thymeleaf_web.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "xt_thisinhxettuyen25")
@Data
@NoArgsConstructor
public class Thisinh {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "cccd", nullable = false, length = 12)
    private String cccd;

    @Column(name = "sobaodanh", nullable = false, length = 10)
    private String sobaodanh;

    @Column(nullable = false, length = 50)
    private String ho;

    @Column(nullable = false, length = 50)
    private String ten;

    @Column(name = "ngay_sinh", nullable = false)
    private LocalDate ngaySinh;

    @Column(name = "dien_thoai", length = 15)
    private String dienThoai;

    @Column(name = "email", length = 100)
    private String email;

    @Column(name = "ho_va_ten")
    private String hoVaTen;
}
