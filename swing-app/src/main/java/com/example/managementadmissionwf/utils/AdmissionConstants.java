package com.example.managementadmissionwf.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * Hằng số và ma trận cho hệ thống xét tuyển
 */
public final class AdmissionConstants {

    private AdmissionConstants() {}

    /**
     * Ma trận lưu mức độ lệch giữa tổ hợp gốc và tổ hợp thực tế.
     * Format: Map<Tổ hợp gốc, Map<Tổ hợp thực tế, Mức độ lệch>>
     */
    private static final Map<String, Map<String, Double>> DEVIATION_MATRIX = new HashMap<>();

    static {
        // Hàng 1: Tổ hợp gốc A00
        Map<String, Double> a00Row = new HashMap<>();
        a00Row.put("A00", 0.0);
        a00Row.put("A01", -0.69);
        a00Row.put("B00", -1.21);
        a00Row.put("C00", 2.32);
        a00Row.put("C01", 0.94);
        a00Row.put("D01", -0.68);
        a00Row.put("D07", -1.62);
        DEVIATION_MATRIX.put("A00", a00Row);

        // Hàng 2: Tổ hợp gốc A01
        Map<String, Double> a01Row = new HashMap<>();
        a01Row.put("A00", 0.69);
        a01Row.put("A01", 0.0);
        a01Row.put("B00", -0.52);
        a01Row.put("C00", 3.01);
        a01Row.put("C01", 1.63);
        a01Row.put("D01", 0.01);
        a01Row.put("D07", -0.93);
        DEVIATION_MATRIX.put("A01", a01Row);

        // Hàng 3: Tổ hợp gốc B00
        Map<String, Double> b00Row = new HashMap<>();
        b00Row.put("A00", 1.21);
        b00Row.put("A01", 0.52);
        b00Row.put("B00", 0.0);
        b00Row.put("C00", 3.53);
        b00Row.put("C01", 2.15);
        b00Row.put("D01", 0.53);
        b00Row.put("D07", -0.41);
        DEVIATION_MATRIX.put("B00", b00Row);

        // Hàng 4: Tổ hợp gốc C00
        Map<String, Double> c00Row = new HashMap<>();
        c00Row.put("A00", -2.32);
        c00Row.put("A01", -3.01);
        c00Row.put("B00", -3.53);
        c00Row.put("C00", 0.0);
        c00Row.put("C01", -1.38);
        c00Row.put("D01", -3.00);
        c00Row.put("D07", -3.94);
        DEVIATION_MATRIX.put("C00", c00Row);

        // Hàng 5: Tổ hợp gốc C01
        Map<String, Double> c01Row = new HashMap<>();
        c01Row.put("A00", -0.94);
        c01Row.put("A01", -1.63);
        c01Row.put("B00", -2.15);
        c01Row.put("C00", 1.38);
        c01Row.put("C01", 0.0);
        c01Row.put("D01", -1.62);
        c01Row.put("D07", -2.56);
        DEVIATION_MATRIX.put("C01", c01Row);

        // Hàng 6: Tổ hợp gốc D01
        Map<String, Double> d01Row = new HashMap<>();
        d01Row.put("A00", 0.68);
        d01Row.put("A01", -0.01);
        d01Row.put("B00", -0.53);
        d01Row.put("C00", 3.00);
        d01Row.put("C01", 1.62);
        d01Row.put("D01", 0.0);
        d01Row.put("D07", -0.94);
        DEVIATION_MATRIX.put("D01", d01Row);
    }

    /**
     * Lấy mức độ lệch giữa tổ hợp gốc và tổ hợp thực tế.
     * 
     * @param baseGroup Tổ hợp gốc (ví dụ: "A00") - Lấy từ n_tohopgoc của xt_nganh
     * @param actualGroup Tổ hợp thực tế (ví dụ: "A01") - Lấy từ nguyện vọng
     * @return Mức điểm chênh lệch (Double), mặc định 0.0 nếu không tìm thấy
     */
    public static double getDeviationScore(String baseGroup, String actualGroup) {
        if (baseGroup == null || actualGroup == null) {
            return 0.0;
        }

        String baseGroupUpper = baseGroup.trim().toUpperCase();
        String actualGroupUpper = actualGroup.trim().toUpperCase();

        Map<String, Double> row = DEVIATION_MATRIX.get(baseGroupUpper);
        if (row != null) {
            return row.getOrDefault(actualGroupUpper, 0.0);
        }
        
        return 0.0;
    }

}