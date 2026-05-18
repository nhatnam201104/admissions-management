package com.example.thymeleaf_web.model.dto;

import java.util.List;

/**
 * Kết quả tổng hợp cho công cụ quy đổi điểm + gợi ý ngành.
 * Chứa 3 phần (ánh xạ với reference UI):
 *  - {@code convertedScores}: điểm quy đổi từng môn (badge cạnh slider)
 *  - {@code tohopResults}: tất cả tổ hợp khả thi với điểm xét tuyển /30
 *  - {@code suggestions}: ngành phù hợp xếp theo điểm + level
 *
 * @param phuongThuc      DGNL | VSAT | THPT
 * @param convertedScores list điểm môn đã quy đổi (thang 10)
 * @param tohopResults    list điểm các tổ hợp (thang 30)
 * @param suggestions     gợi ý ngành phù hợp (đã sort)
 * @param totalMajors     tổng số ngành active (cho counter "X/Y ngành đạt")
 * @param qualifiedCount  số ngành đạt điểm sàn (level=VUA_SUC)
 * @param diemDgnlConverted (DGNL only) điểm DGNL đã quy đổi /30
 */
public record CalculatorOutput(
        String phuongThuc,
        List<ConvertedScoreRow> convertedScores,
        List<TohopResult> tohopResults,
        List<MajorSuggestion> suggestions,
        int totalMajors,
        int qualifiedCount,
        Double diemDgnlConverted
) {
    public CalculatorOutput {
        convertedScores = convertedScores != null ? convertedScores : List.of();
        tohopResults = tohopResults != null ? tohopResults : List.of();
        suggestions = suggestions != null ? suggestions : List.of();
    }
}
