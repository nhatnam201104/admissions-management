package com.example.managementadmissionwf.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class MajorTohopDTO {
    private Integer Id;
    @NotBlank(message = "Mã ngành không được để trống")
    private String maNganh;

    @NotBlank(message = "Mã tổ hợp không được để trống")
    private String maToHop;

    @NotBlank(message = "Môn 1 không được để trống")
    private String thMon1;

    @NotNull(message = "Hệ số môn 1 không được để trống")
    @Positive(message = "Hệ số môn 1 phải lớn hơn 0")
    private Double hsMon1;

    @NotBlank(message = "Môn 2 không được để trống")
    private String thMon2;

    @NotNull(message = "Hệ số môn 2 không được để trống")
    @Positive(message = "Hệ số môn 2 phải lớn hơn 0")
    private Double hsMon2;

    @NotBlank(message = "Môn 3 không được để trống")
    private String thMon3;

    @NotNull(message = "Hệ số môn 3 không được để trống")
    @Positive(message = "Hệ số môn 3 phải lớn hơn 0")
    private Double hsMon3;
}
