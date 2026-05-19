package com.example.thymeleaf_web.service;

import com.example.thymeleaf_web.model.dto.CalculatorOutput;
import com.example.thymeleaf_web.model.dto.ScoreCalculatorForm;
import com.example.thymeleaf_web.model.dto.ScoreCalculatorResult;
import com.example.thymeleaf_web.model.entity.Nganh;

import java.util.List;

/**
 * Tính điểm xét tuyển dựa trên dữ liệu thí sinh tự nhập.
 * <p>
 * Khác với {@link ScoreLookupService} (lấy dữ liệu từ DB theo CCCD),
 * service này phục vụ trang {@code /tinh-diem} cho phép sinh viên thử
 * nghiệm các phương thức ĐGNL / VSAT / THPT mà không cần đăng nhập.
 */
public interface ScoreCalculatorService {

    /** Danh sách ngành đang mở để hiển thị trong combo. */
    List<Nganh> getActiveMajors();

    /** Thực thi tính điểm theo dữ liệu form (legacy — chọn ngành cụ thể). */
    ScoreCalculatorResult calculate(ScoreCalculatorForm form);

    /**
     * Tính điểm độc lập theo flow của trang reference: quy đổi từng môn
     * → tính tất cả tổ hợp khả thi → gợi ý ngành phù hợp.
     * Không yêu cầu chọn ngành trước.
     */
    CalculatorOutput calculateUniversal(ScoreCalculatorForm form);
}

