package com.example.thymeleaf_web.model.dto;

/**
 * Kết quả tính cho 1 tổ hợp môn — hiển thị ở cột phải dạng card
 * (giống reference): mã tổ hợp, mô tả 3 môn, và điểm xét tuyển /30.
 *
 * @param matohop      mã tổ hợp (A00, A01, ...)
 * @param subjectsText "Toán - Lý - Hóa" (mô tả ngắn)
 * @param diemThxt     điểm tổ hợp xét tuyển (thang 30, đã áp hệ số)
 * @param fullScore    {@code true} nếu cả 3 môn đều có điểm; {@code false}
 *                     khi thiếu môn → UI hiện trạng "—"
 */
public record TohopResult(
        String matohop,
        String subjectsText,
        Double diemThxt,
        boolean fullScore
) {}
