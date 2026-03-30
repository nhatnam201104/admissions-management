package com.example.managementadmissionwf.dto.response;

import com.example.managementadmissionwf.annotation.ExcelColumn;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SubjectGroupResponse {

    Integer id;

    @ExcelColumn(name = "Mã Tổ Hợp")
    String matohop;

    @ExcelColumn(name = "Môn 1")
    String mon1;

    @ExcelColumn(name = "Môn 2")
    String mon2;

    @ExcelColumn(name = "Môn 3")
    String mon3;

    @ExcelColumn(name = "Tên Tổ Hợp")
    String tentohop;
}
