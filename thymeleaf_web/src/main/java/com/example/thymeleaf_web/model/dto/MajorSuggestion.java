package com.example.thymeleaf_web.model.dto;

/**
 * Gợi ý ngành phù hợp với điểm xét tuyển của thí sinh, hiển thị ở
 * panel "Gợi ý ngành phù hợp" (giống reference). Mỗi item là một
 * (ngành, tổ hợp tốt nhất) — chỉ giữ tổ hợp cho điểm cao nhất.
 *
 * @param manganh      mã ngành
 * @param tennganh     tên ngành
 * @param matohop      tổ hợp đạt điểm cao nhất cho ngành này
 * @param diemXt       điểm xét tuyển tốt nhất (thang 30)
 * @param diemSan      điểm sàn của ngành (nullable nếu chưa công bố)
 * @param level        "VUA_SUC" (>= sàn), "THU_THACH" (thiếu < 2đ),
 *                     "KHO" (thiếu >= 2đ), "CHUA_CONG_BO" (sàn null)
 */
public record MajorSuggestion(
        String manganh,
        String tennganh,
        String matohop,
        Double diemXt,
        Double diemSan,
        String level
) {

    public String levelLabel() {
        return switch (level) {
            case "VUA_SUC" -> "Vừa sức";
            case "THU_THACH" -> "Thử thách";
            case "KHO" -> "Khó";
            default -> "Chưa rõ";
        };
    }

    public String levelBadgeClass() {
        return switch (level) {
            case "VUA_SUC" -> "badge-vua-suc";
            case "THU_THACH" -> "badge-thu-thach";
            case "KHO" -> "badge-kho";
            default -> "badge-chua-ro";
        };
    }
}
