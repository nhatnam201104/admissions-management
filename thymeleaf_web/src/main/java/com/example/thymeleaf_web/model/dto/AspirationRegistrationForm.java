package com.example.thymeleaf_web.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class AspirationRegistrationForm {

    public static final int MIN_VISIBLE_ROWS = 3;
    public static final int MAX_ASPIRATIONS = 10;

    @NotBlank(message = "Vui lòng nhập số CCCD")
    @Pattern(regexp = "\\d{12}", message = "CCCD phải gồm đúng 12 chữ số")
    private String cccd;

    private List<AspirationChoiceForm> aspirations = new ArrayList<>();

    public List<String> selectedMajorCodes() {
        if (aspirations == null) {
            return List.of();
        }
        return aspirations.stream()
                .map(AspirationChoiceForm::getMaNganh)
                .filter(value -> value != null && !value.isBlank())
                .map(String::trim)
                .toList();
    }

    public void ensureMinimumRows() {
        if (aspirations == null) {
            aspirations = new ArrayList<>();
        }
        while (aspirations.size() < MIN_VISIBLE_ROWS) {
            aspirations.add(new AspirationChoiceForm());
        }
    }
}
