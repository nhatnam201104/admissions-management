package com.example.thymeleaf_web.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

/**
 * Form input cho luồng tra cứu điểm xét tuyển ({@code /tra-cuu-diem}).
 * Validation:
 * <ul>
 *   <li>{@code cccd}: bắt buộc, đúng 12 chữ số.</li>
 *   <li>{@code ngaySinh}: bắt buộc và phải là ngày trong quá khứ.</li>
 * </ul>
 */
@Data
public class CccdForm {

    @NotBlank(message = "Vui lòng nhập số CCCD")
    @Pattern(regexp = "\\d{12}", message = "CCCD phải gồm đúng 12 chữ số")
    private String cccd;

    @NotNull(message = "Vui lòng chọn ngày sinh")
    @Past(message = "Ngày sinh phải là ngày trong quá khứ")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate ngaySinh;
}
