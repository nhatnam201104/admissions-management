package com.example.managementadmissionwf.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SubjectGroupRequest {

    Integer id;

    @NotBlank(message = "Mã tổ hợp không được để trống")
    @Size(max = 10, message = "Mã tổ hợp không được vượt quá 10 ký tự")
    String matohop;

    @NotBlank(message = "Môn 1 không được để trống")
    @Size(max = 5, message = "Môn 1 không được vượt quá 5 ký tự")
    String mon1;

    @NotBlank(message = "Môn 2 không được để trống")
    @Size(max = 5, message = "Môn 2 không được vượt quá 5 ký tự")
    String mon2;

    @NotBlank(message = "Môn 3 không được để trống")
    @Size(max = 5, message = "Môn 3 không được vượt quá 5 ký tự")
    String mon3;

    @NotBlank(message = "Tên tổ hợp không được để trống")
    String tentohop;
}
