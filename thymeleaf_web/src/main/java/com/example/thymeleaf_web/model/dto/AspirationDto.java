package com.example.thymeleaf_web.model.dto;

public record AspirationDto(
        int order,
        String maNganh,
        String tenNganh,
        Double diemThxt,
        Double diemUtqd,
        Double diemCong,
        Double diemXettuyen,
        String ketQua
) {
    public boolean isAdmitted() {
        return "TRUNG_TUYEN".equals(ketQua);
    }

    public String getKetQuaDisplay() {
        return switch (ketQua != null ? ketQua : "") {
            case "TRUNG_TUYEN" -> "Trúng tuyển";
            case "TRUOT" -> "Trượt";
            case "CHO_XET" -> "Chờ xét";
            default -> "Chưa có kết quả";
        };
    }
}
