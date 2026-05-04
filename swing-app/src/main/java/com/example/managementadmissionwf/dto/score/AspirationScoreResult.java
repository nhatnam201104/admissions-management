package com.example.managementadmissionwf.dto.score;

public record AspirationScoreResult(
        String cccd,
        String manganh,
        String matohop,
        String phuongThuc,
        double diemGocMon1,
        double diemGocMon2,
        double diemGocMon3,
        Double diemSauQuyDoiMon1,
        Double diemSauQuyDoiMon2,
        Double diemSauQuyDoiMon3,
        Double hsmon1,
        Double hsmon2,
        Double hsmon3,
        double diemThxt,
        double diemCong,
        double diemUuTien,
        double diemXettuyen,
        double diemSan,
        boolean datDiemSan,
        String trangThai
) {
    public String trangThai() {
        return datDiemSan ? "CHO_XET" : "THIEU_DIEM";
    }
}
