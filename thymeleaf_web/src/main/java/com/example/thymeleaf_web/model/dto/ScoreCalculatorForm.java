package com.example.thymeleaf_web.model.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Form đầu vào cho trang tính điểm xét tuyển ({@code /tinh-diem}).
 * <p>
 * Chứa cả các trường cho phương thức ĐGNL (chỉ nhập điểm 1200) và
 * phương thức VSAT/THPT (nhập điểm theo từng môn). Trường
 * {@code phuongThuc} sẽ quyết định service áp dụng nhóm nào.
 * <p>
 * Validation Bean (jakarta) chỉ áp cho các trường có ngưỡng cố định
 * (NK, Anh CC, ĐGNL...). Ngưỡng phụ thuộc phương thức (môn 7 môn
 * VSAT/THPT 0-150 hay 0-10) được validate động trong
 * {@code ScoreCalculatorController} vì cùng 1 field tái sử dụng cho cả
 * 2 phương thức.
 */
@Data
@NoArgsConstructor
public class ScoreCalculatorForm {

    /** DGNL | VSAT | THPT */
    private String phuongThuc;

    /** Mã ngành xét tuyển. */
    private String manganh;

    /** Mức điểm ưu tiên đối tượng (0; 0.25; 0.5; 0.75; 1; 1.5; 2). */
    @DecimalMin(value = "0.0", message = "Ưu tiên đối tượng không được âm")
    @DecimalMax(value = "2.0", message = "Ưu tiên đối tượng tối đa 2.00")
    private Double doiTuongUuTien;

    /** Mức điểm ưu tiên khu vực (0; 0.25; 0.5; 0.75). */
    @DecimalMin(value = "0.0", message = "Ưu tiên khu vực không được âm")
    @DecimalMax(value = "0.75", message = "Ưu tiên khu vực tối đa 0.75")
    private Double khuVucUuTien;

    /** Điểm cộng do thí sinh tự khai (chứng chỉ, khuyến khích...). */
    @DecimalMin(value = "0.0", message = "Điểm cộng không được âm")
    @DecimalMax(value = "5.0", message = "Điểm cộng tối đa 5.00")
    private Double diemCong;

    // ===== Phương thức ĐGNL =====

    /** Điểm thi ĐGNL thang 1200. */
    @DecimalMin(value = "0.0", message = "Điểm ĐGNL không được âm")
    @DecimalMax(value = "1200.0", message = "Điểm ĐGNL tối đa 1200")
    private Double diemDgnl;

    // ===== Phương thức VSAT / THPT =====
    // Range 0-150 (rộng nhất) — controller sẽ siết về 0-10 nếu phương
    // thức là THPT.

    @DecimalMin(value = "0.0", message = "Điểm Toán không được âm")
    @DecimalMax(value = "150.0", message = "Điểm Toán tối đa 150")
    private Double diemToan;

    @DecimalMin(value = "0.0", message = "Điểm Vật lý không được âm")
    @DecimalMax(value = "150.0", message = "Điểm Vật lý tối đa 150")
    private Double diemLy;

    @DecimalMin(value = "0.0", message = "Điểm Hóa học không được âm")
    @DecimalMax(value = "150.0", message = "Điểm Hóa học tối đa 150")
    private Double diemHoa;

    @DecimalMin(value = "0.0", message = "Điểm Sinh học không được âm")
    @DecimalMax(value = "150.0", message = "Điểm Sinh học tối đa 150")
    private Double diemSinh;

    @DecimalMin(value = "0.0", message = "Điểm Lịch sử không được âm")
    @DecimalMax(value = "150.0", message = "Điểm Lịch sử tối đa 150")
    private Double diemSu;

    @DecimalMin(value = "0.0", message = "Điểm Địa lý không được âm")
    @DecimalMax(value = "150.0", message = "Điểm Địa lý tối đa 150")
    private Double diemDia;

    @DecimalMin(value = "0.0", message = "Điểm Ngữ văn không được âm")
    @DecimalMax(value = "150.0", message = "Điểm Ngữ văn tối đa 150")
    private Double diemVan;

    /** Điểm thi tiếng Anh (THPT thang 10, VSAT thang 150). */
    @DecimalMin(value = "0.0", message = "Điểm Tiếng Anh không được âm")
    @DecimalMax(value = "150.0", message = "Điểm Tiếng Anh tối đa 150")
    private Double diemAnh;

    /** Điểm tiếng Anh quy đổi từ chứng chỉ (đã ở thang 10). */
    @DecimalMin(value = "0.0", message = "Điểm Anh CC không được âm")
    @DecimalMax(value = "10.0", message = "Điểm Anh CC tối đa 10")
    private Double diemAnhCc;

    @DecimalMin(value = "0.0", message = "Điểm năng khiếu 1 không được âm")
    @DecimalMax(value = "10.0", message = "Điểm năng khiếu 1 tối đa 10")
    private Double diemNk1;

    @DecimalMin(value = "0.0", message = "Điểm năng khiếu 2 không được âm")
    @DecimalMax(value = "10.0", message = "Điểm năng khiếu 2 tối đa 10")
    private Double diemNk2;

    /** Mã môn được cộng điểm (TO/LI/HO/SI/SU/DI/VA/N1/NK1/NK2). */
    private String monCongDiem;

    /** Mức cộng cho môn đã chọn (cộng vào điểm môn, sau khi quy đổi). */
    @DecimalMin(value = "0.0", message = "Mức cộng môn không được âm")
    @DecimalMax(value = "3.0", message = "Mức cộng môn tối đa 3.00")
    private Double mucCongMon;
}
