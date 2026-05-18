package com.example.thymeleaf_web.model.dto;

import java.util.List;

/**
 * Kết quả tính điểm xét tuyển hiển thị trên trang {@code /tinh-diem}.
 * <p>
 * Một thí sinh có thể được tính điểm cho nhiều tổ hợp môn khác nhau
 * (đặc biệt với phương thức THPT/VSAT), nên kết quả gồm danh sách
 * {@link CalculatedAspirationRow}. Đối với phương thức ĐGNL chỉ có
 * 1 dòng (theo tổ hợp gốc của ngành).
 *
 * @param phuongThuc       phương thức được tính (DGNL/VSAT/THPT)
 * @param tenNganh         tên ngành xét tuyển
 * @param maNganh          mã ngành
 * @param diemSan          điểm sàn của ngành (nếu có)
 * @param diemTrungTuyen   điểm trúng tuyển của ngành (nếu có)
 * @param doiTuongUuTien   mức cộng đối tượng đã chọn
 * @param khuVucUuTien     mức cộng khu vực đã chọn
 * @param mucUuTien        tổng mức ưu tiên (đối tượng + khu vực)
 * @param diemCong         điểm cộng tự khai
 * @param convertedScores  bảng quy đổi (DGNL → 30, VSAT → 10, THPT giữ nguyên)
 * @param rows             các dòng kết quả theo tổ hợp xét tuyển
 */
public record ScoreCalculatorResult(
        String phuongThuc,
        String tenNganh,
        String maNganh,
        Double diemSan,
        Double diemTrungTuyen,
        Double doiTuongUuTien,
        Double khuVucUuTien,
        Double mucUuTien,
        Double diemCong,
        List<ConvertedScoreRow> convertedScores,
        List<CalculatedAspirationRow> rows
) {
    public ScoreCalculatorResult {
        convertedScores = convertedScores != null ? convertedScores : List.of();
        rows = rows != null ? rows : List.of();
    }
}
