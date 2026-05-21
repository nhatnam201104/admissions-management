package com.example.managementadmissionwf.utils;

import com.example.managementadmissionwf.dal.entity.XtThisinhxettuyen25;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Tính tự động điểm ưu tiên theo Quy chế tuyển sinh 2023+ của Bộ GD&ĐT
 * (Thông tư 08/2022/TT-BGDĐT, sửa đổi 06/2023/TT-BGDĐT) - tham chiếu danh mục
 * UEF.
 *
 * <p><b>Khu vực</b>
 * <ul>
 *   <li>KV1 = 0.75</li>
 *   <li>KV2-NT = 0.50</li>
 *   <li>KV2 = 0.25</li>
 *   <li>KV3 = 0.00</li>
 * </ul>
 *
 * <p><b>Đối tượng ưu tiên</b>
 * <ul>
 *   <li>UT1 = 2.00 (nhóm đối tượng 01-04 theo quy chế)</li>
 *   <li>UT2 = 1.00 (nhóm đối tượng 05-07 theo quy chế)</li>
 *   <li>"Không" = 0.00 (không thuộc diện ưu tiên)</li>
 * </ul>
 *
 * <p>Tổng điểm ưu tiên = điểm khu vực + điểm đối tượng. Cap 22.5 đã xử lý ở
 * {@link #applyCap(double, double, double)} và
 * {@code AdmissionResultServiceImpl.calculateDUT}.
 *
 * <p>Class này chỉ trả về <i>mức ưu tiên gốc</i> (chưa cap), tương ứng với
 * cột <code>diemUtxt</code> trong bảng <code>xt_diemcongxettuyen</code>.
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
            "Không", 0.00,
            "UT1", 2.00,
            "UT2", 1.00
    );

    /** Danh sách mã đối tượng cho dropdown (theo thứ tự hiển thị). */
    public static final List<String> DOI_TUONG_OPTIONS = List.of("Không", "UT1", "UT2");

    /** Danh sách mã khu vực cho dropdown (theo thứ tự hiển thị). */
    public static final List<String> KHU_VUC_OPTIONS = List.of("KV1", "KV2-NT", "KV2", "KV3");

    private static final DecimalFormat LABEL_FORMAT =
            new DecimalFormat("0.##", DecimalFormatSymbols.getInstance(Locale.US));

    /**
     * Render label hiển thị cho dropdown: {@code "UT1 (+2đ)"}, {@code "Không (0đ)"}.
     * Map dùng để tra điểm có thể là {@link #DOI_TUONG_POINTS} hoặc {@link #KHU_VUC_POINTS}.
     */
    public static String displayLabel(String code, Map<String, Double> points) {
        if (code == null) {
            return "";
        }
        double p = points.getOrDefault(code, 0.0);
        if (p <= 0.0) {
            return code + " (0đ)";
        }
        return code + " (+" + LABEL_FORMAT.format(p) + "đ)";
    }

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
        return DOI_TUONG_POINTS.getOrDefault(doiTuong.trim(), 0.0);
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
