package com.example.thymeleaf_web.model.dto;

/**
 * Một nguyện vọng xét tuyển đã được tính toán.
 *
 * @param finalAdmitted    {@code true} nếu đây là nguyện vọng thí sinh chính thức
 *                         được nhận vào (theo quy chế: chỉ 1 nguyện vọng có
 *                         thứ tự ưu tiên cao nhất trong các NV đủ điểm).
 *                         Các NV khác có {@code ketQua = TRUNG_TUYEN} nhưng
 *                         {@code finalAdmitted = false} sẽ bị đánh dấu
 *                         "không xét tiếp".
 */
public record AspirationDto(
        int order,
        String maNganh,
        String tenNganh,
        String phuongThuc,
        String toHop,
        Double diemThxt,
        Double diemUtqd,
        Double diemCong,
        Double diemXettuyen,
        String ketQua,
        boolean finalAdmitted,
        ScoreCalculationDetail calculationDetail
) {
    public AspirationDto {
        calculationDetail = calculationDetail != null ? calculationDetail : ScoreCalculationDetail.empty();
    }

    /**
     * NV được hệ thống tính là trúng tuyển (đủ điểm). Có thể chưa phải là
     * NV được nhận chính thức nếu thí sinh đã trúng NV ưu tiên cao hơn.
     */
    public boolean isAdmitted() {
        return "TRUNG_TUYEN".equals(ketQua);
    }

    /**
     * NV vừa đủ điểm vừa không được nhận chính thức (đã có NV ưu tiên cao
     * hơn được chọn). Hiển thị là "Đủ điểm — Không xét tiếp".
     */
    public boolean isOverridden() {
        return isAdmitted() && !finalAdmitted;
    }

    public String getKetQuaDisplay() {
        if (finalAdmitted) {
            return "Trúng tuyển";
        }
        if (isAdmitted()) {
            return "Đủ điểm — Không xét tiếp";
        }
        return switch (ketQua != null ? ketQua : "") {
            case "TRUOT" -> "Trượt";
            case "CHO_XET" -> "Chờ xét";
            case "THIEU_DIEM" -> "Thiếu điểm";
            default -> "Chưa có kết quả";
        };
    }

    public String phuongThucDisplay() {
        return phuongThuc != null && !phuongThuc.isBlank() ? phuongThuc.trim() : "-";
    }

    public String toHopDisplay() {
        return toHop != null && !toHop.isBlank() ? toHop.trim() : "-";
    }
}
