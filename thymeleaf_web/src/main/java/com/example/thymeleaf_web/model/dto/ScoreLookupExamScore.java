package com.example.thymeleaf_web.model.dto;

/**
 * Điểm thi của thí sinh theo từng phương thức xét tuyển. Có thêm
 * <code>diemQuyDoiThang30</code> để hiển thị "điểm đã quy đổi về thang 30"
 * (yêu cầu rubric web mục 2 cho phương thức ĐGNL/VSAT).
 *
 * Quy đổi gồm:
 *  - DGNL: NL1 thang 1200 -> thang 30 (tỉ lệ 30/1200).
 *  - VSAT: tổng 3 môn cao nhất (mỗi môn quy đổi từ thang ~150 -> thang 10).
 *  - THPT/khác: trả về null.
 */
public record ScoreLookupExamScore(
        String phuongThuc,
        Double toan,
        Double ly,
        Double hoa,
        Double sinh,
        Double su,
        Double dia,
        Double van,
        Double n1Thi,
        Double n1Cc,
        Double nl1,
        Double nk1,
        Double nk2
) {

    /**
     * Hiển thị điểm quy đổi về thang 30 cho phương thức ĐGNL/VSAT.
     * Trả về null nếu phương thức không cần quy đổi hoặc thiếu dữ liệu.
     */
    public Double diemQuyDoiThang30() {
        if (phuongThuc == null) {
            return null;
        }
        return switch (phuongThuc.trim().toUpperCase()) {
            case "DGNL" -> nl1 != null ? round(nl1 * 30.0 / 1200.0) : null;
            case "VSAT" -> {
                Double sumThang30 = vsatTongQuyDoi();
                yield sumThang30 != null ? round(sumThang30) : null;
            }
            default -> null;
        };
    }

    private Double vsatTongQuyDoi() {
        // Lấy 3 môn có điểm cao nhất, quy đổi xấp xỉ về thang 10/môn rồi cộng tổng.
        Double[] cands = {toan, ly, hoa, sinh, su, dia, van};
        java.util.List<Double> nonNull = new java.util.ArrayList<>();
        for (Double v : cands) {
            if (v != null) {
                nonNull.add(v * 10.0 / 150.0); // ánh xạ tuyến tính 0..150 -> 0..10
            }
        }
        if (nonNull.size() < 3) {
            return null;
        }
        nonNull.sort(java.util.Comparator.reverseOrder());
        return nonNull.get(0) + nonNull.get(1) + nonNull.get(2);
    }

    private static double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
