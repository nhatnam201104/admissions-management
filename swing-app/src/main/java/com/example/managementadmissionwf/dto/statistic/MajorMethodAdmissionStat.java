package com.example.managementadmissionwf.dto.statistic;

/**
 * Số lượng trúng tuyển từng phương thức theo ngành.
 * Phục vụ rubric mục 6 (Win Form): "Danh sách số lượng trúng tuyển từng phương
 * thức theo ngành".
 *
 * @param manganh        mã ngành
 * @param tennganh       tên ngành
 * @param phuongThuc     THPT/DGNL/VSAT/TUYEN_THANG/...
 * @param totalQuota     tổng chỉ tiêu của ngành (lấy từ {@code xt_nganh.n_chitieu})
 * @param methodQuota    chỉ tiêu của riêng phương thức trong ngành (lấy từ
 *                       {@code xt_nganh.sl_*}); có thể bằng 0 nếu chưa nhập
 * @param admitted       số nguyện vọng trúng tuyển (TRUNG_TUYEN)
 * @param totalApply     số nguyện vọng đăng ký
 */
public record MajorMethodAdmissionStat(
        String manganh,
        String tennganh,
        String phuongThuc,
        int totalQuota,
        int methodQuota,
        long admitted,
        long totalApply
) {
    /**
     * Tỷ lệ % chỉ tiêu phương thức được lấp đầy bằng số trúng tuyển. Nếu
     * methodQuota = 0 thì fallback dùng tổng chỉ tiêu.
     */
    public double fillRate() {
        int divisor = methodQuota > 0 ? methodQuota : totalQuota;
        return divisor > 0 ? Math.round(((double) admitted / divisor) * 1000.0) / 10.0 : 0.0;
    }

    /**
     * Tỷ lệ chọi của phương thức = totalApply / methodQuota (×100).
     */
    public double competitionRate() {
        int divisor = methodQuota > 0 ? methodQuota : totalQuota;
        return divisor > 0 ? Math.round(((double) totalApply / divisor) * 100.0) / 100.0 : 0.0;
    }
}
