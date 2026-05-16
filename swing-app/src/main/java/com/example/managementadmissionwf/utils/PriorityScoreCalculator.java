package com.example.managementadmissionwf.utils;

import com.example.managementadmissionwf.dal.entity.XtThisinhxettuyen25;

import java.util.Map;

/**
 * Tính tự động điểm ưu tiên theo Quy chế tuyển sinh 2023+ của Bộ GD&ĐT.
 *
 * <p><b>Khu vực</b> (theo Phụ lục III - Thông tư 08/2022/TT-BGDĐT, sửa đổi
 * 06/2023/TT-BGDĐT)
 * <ul>
 *   <li>KV1 = 0.75</li>
 *   <li>KV2-NT = 0.50</li>
 *   <li>KV2 = 0.25</li>
 *   <li>KV3 = 0.00</li>
 * </ul>
 *
 * <p><b>Đối tượng ưu tiên</b> (theo Phụ lục IV)
 * <ul>
 *   <li>UT1 = 2.00 (đối tượng 01-04: con liệt sĩ, thương binh hạng 1...)</li>
 *   <li>UT2 = 1.00 (đối tượng 05-07: con thương binh hạng 2, hộ nghèo, dân tộc thiểu số...)</li>
 *   <li>KT1, KT2, KT3 = 0.00 (không thuộc diện ưu tiên đối tượng)</li>
 * </ul>
 *
 * <p>Tổng điểm ưu tiên = điểm khu vực + điểm đối tượng. Quy chế 2023 còn có
 * <i>cap</i>: nếu điểm 3 môn ≥ 22.5 thì điểm ưu tiên giảm dần theo công thức
 * (30 - tổng điểm) / 7.5 × mức ưu tiên — phần đó đã được xử lý ở
 * <code>AdmissionResultServiceImpl.calculateDUT</code>.
 *
 * <p>Class này chỉ trả về <i>mức ưu tiên gốc</i> (chưa cap), tương ứng với
 * cột <code>diemUtxt</code> trong bảng <code>xt_diemcongxettuyen</code>.
 *
 * <p>Tham khảo: <a href="https://moet.gov.vn">moet.gov.vn</a>,
 * <a href="https://thi.tuyensinh247.com">thi.tuyensinh247.com</a>.
 */
public final class PriorityScoreCalculator {

    private PriorityScoreCalculator() {
    }

    /** Mức điểm cộng theo khu vực (0–0.75). */
    public static final Map<String, Double> KHU_VUC_POINTS = Map.of(
            "KV1", 0.75,
            "KV2-NT", 0.50,
            "KV2", 0.25,
            "KV3", 0.00
    );

    /** Mức điểm cộng theo đối tượng (0–2.00). */
    public static final Map<String, Double> DOI_TUONG_POINTS = Map.of(
            "UT1", 2.00,
            "UT2", 1.00,
            "KT1", 0.00,
            "KT2", 0.00,
            "KT3", 0.00
    );

    /**
     * Tính điểm ưu tiên gốc (chưa cap) cho 1 thí sinh dựa vào khu vực + đối
     * tượng. Trả về 0.0 nếu cả 2 trường đều không khớp danh mục.
     */
    public static double calculateForCandidate(XtThisinhxettuyen25 candidate) {
        if (candidate == null) {
            return 0.0;
        }
        return calculate(candidate.getKhuVuc(), candidate.getDoiTuong());
    }

    /** Tính điểm ưu tiên gốc dựa trên mã khu vực và mã đối tượng. */
    public static double calculate(String khuVuc, String doiTuong) {
        return pointsForKhuVuc(khuVuc) + pointsForDoiTuong(doiTuong);
    }

    public static double pointsForKhuVuc(String khuVuc) {
        if (khuVuc == null) {
            return 0.0;
        }
        return KHU_VUC_POINTS.getOrDefault(khuVuc.trim().toUpperCase(), 0.0);
    }

    public static double pointsForDoiTuong(String doiTuong) {
        if (doiTuong == null) {
            return 0.0;
        }
        return DOI_TUONG_POINTS.getOrDefault(doiTuong.trim().toUpperCase(), 0.0);
    }

    /**
     * Áp dụng cap "Quy chế 2023": nếu (điểm 3 môn + điểm cộng) ≥ 22.5 thì
     * mức ưu tiên giảm theo công thức (30 - thxt - cong) / 7.5 × mucGoc.
     */
    public static double applyCap(double mucGoc, double diemThxt, double diemCong) {
        if (mucGoc <= 0) {
            return 0.0;
        }
        if (diemThxt + diemCong < 22.5) {
            return mucGoc;
        }
        double heSo = (30.0 - diemThxt - diemCong) / 7.5;
        if (heSo <= 0) {
            return 0.0;
        }
        return heSo * mucGoc;
    }
}
