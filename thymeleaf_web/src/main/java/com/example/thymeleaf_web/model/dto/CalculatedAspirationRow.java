package com.example.thymeleaf_web.model.dto;

import java.util.List;

/**
 * Một dòng kết quả tính điểm xét tuyển theo một tổ hợp cụ thể.
 *
 * @param matohop       mã tổ hợp (A00, A01, D01...)
 * @param subjects      các môn của tổ hợp với điểm gốc, điểm quy đổi
 * @param diemThxt      Điểm tổ hợp xét tuyển (đã quy đổi về thang 30)
 * @param diemUuTienQd  Điểm ưu tiên đã quy đổi (áp dụng công thức ngưỡng 22.5)
 * @param diemCong      Điểm cộng (môn / chứng chỉ) tham gia vào ĐXT
 * @param diemXetTuyen  Điểm xét tuyển cuối cùng
 * @param capApplied    Có áp dụng giảm ưu tiên (ĐTHXT ≥ 22.5) hay không
 * @param formula       Công thức rút gọn để hiển thị
 * @param dat_diemSan   So với điểm sàn ngành: TRUE = đạt
 * @param dat_diemTT    So với điểm trúng tuyển ngành: TRUE = đạt (null nếu chưa công bố)
 */
public record CalculatedAspirationRow(
        String matohop,
        List<ConvertedScoreRow> subjects,
        Double diemThxt,
        Double diemUuTienQd,
        Double diemCong,
        Double diemXetTuyen,
        boolean capApplied,
        String formula,
        Boolean dat_diemSan,
        Boolean dat_diemTT
) {
    public CalculatedAspirationRow {
        subjects = subjects != null ? subjects : List.of();
    }
}
