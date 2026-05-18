package com.example.thymeleaf_web.controller.score;

import com.example.thymeleaf_web.model.dto.CalculatorOutput;
import com.example.thymeleaf_web.model.dto.ScoreCalculatorForm;
import com.example.thymeleaf_web.model.dto.ScoreCalculatorResult;
import com.example.thymeleaf_web.service.ScoreCalculatorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Controller cho trang tính điểm xét tuyển ({@code /tinh-diem}).
 * <p>
 * Hai flow song song:
 * <ul>
 *   <li>Universal calculator (mặc định, giống reference):
 *       quy đổi từng môn → tất cả tổ hợp khả thi → gợi ý ngành theo điểm sàn.
 *       Không yêu cầu chọn ngành trước.</li>
 *   <li>Legacy per-major calculator: giữ lại endpoint POST cũ cho ai cần
 *       tính cụ thể 1 ngành đã chọn.</li>
 * </ul>
 */
@Controller
@RequestMapping("/tinh-diem")
@RequiredArgsConstructor
@Slf4j
public class ScoreCalculatorController {

    /** Ngưỡng điểm môn theo phương thức (max). */
    private static final double MAX_SCORE_THPT = 10.0;
    private static final double MAX_SCORE_VSAT = 150.0;

    private final ScoreCalculatorService scoreCalculatorService;

    @GetMapping
    public String showForm(@RequestParam(value = "phuongThuc", required = false) String phuongThuc,
                           @ModelAttribute("calculatorForm") ScoreCalculatorForm form,
                           Model model) {
        if (form.getPhuongThuc() == null || form.getPhuongThuc().isBlank()) {
            form.setPhuongThuc(phuongThuc != null ? phuongThuc : "VSAT");
        }
        if (form.getDoiTuongUuTien() == null) {
            form.setDoiTuongUuTien(0.0);
        }
        if (form.getKhuVucUuTien() == null) {
            form.setKhuVucUuTien(0.0);
        }
        if (form.getDiemCong() == null) {
            form.setDiemCong(0.0);
        }
        populateModel(model, form, null);
        return "score/calculator";
    }

    /**
     * Endpoint AJAX cho universal calculator: nhận form, trả về JSON
     * {@link CalculatorOutput} để client cập nhật cột phải mà không cần
     * reload trang. Phục vụ trải nghiệm slider live giống reference.
     */
    @PostMapping(value = "/api/calculate")
    @ResponseBody
    public CalculatorOutput calculateApi(@ModelAttribute ScoreCalculatorForm form) {
        if (form.getPhuongThuc() == null || form.getPhuongThuc().isBlank()) {
            form.setPhuongThuc("VSAT");
        }
        return scoreCalculatorService.calculateUniversal(form);
    }

    /** Legacy: tính điểm theo ngành cụ thể (giữ tương thích). */
    @PostMapping
    public String calculate(@Valid @ModelAttribute("calculatorForm") ScoreCalculatorForm form,
                            BindingResult bindingResult,
                            Model model) {
        if (form.getPhuongThuc() == null || form.getPhuongThuc().isBlank()) {
            form.setPhuongThuc("THPT");
        }

        validateMethodSpecificRanges(form, bindingResult);

        if (form.getManganh() == null || form.getManganh().isBlank()) {
            bindingResult.rejectValue("manganh", "manganh.required",
                    "Vui lòng chọn ngành xét tuyển");
        }

        if (bindingResult.hasErrors()) {
            populateModel(model, form, null);
            return "score/calculator";
        }

        ScoreCalculatorResult result = scoreCalculatorService.calculate(form);
        if (result.rows().isEmpty()) {
            bindingResult.reject("calculation.empty",
                    "Không thể tính điểm: ngành chưa cấu hình tổ hợp hoặc bạn chưa nhập đủ điểm môn.");
        }
        populateModel(model, form, result);
        return "score/calculator";
    }

    private void validateMethodSpecificRanges(ScoreCalculatorForm form, BindingResult br) {
        if ("DGNL".equalsIgnoreCase(form.getPhuongThuc())) {
            return;
        }
        double max = "VSAT".equalsIgnoreCase(form.getPhuongThuc())
                ? MAX_SCORE_VSAT
                : MAX_SCORE_THPT;
        String scaleLabel = max == MAX_SCORE_VSAT ? "150" : "10";

        Map<String, Function<ScoreCalculatorForm, Double>> subjectMap = new LinkedHashMap<>();
        subjectMap.put("diemToan", ScoreCalculatorForm::getDiemToan);
        subjectMap.put("diemLy", ScoreCalculatorForm::getDiemLy);
        subjectMap.put("diemHoa", ScoreCalculatorForm::getDiemHoa);
        subjectMap.put("diemSinh", ScoreCalculatorForm::getDiemSinh);
        subjectMap.put("diemSu", ScoreCalculatorForm::getDiemSu);
        subjectMap.put("diemDia", ScoreCalculatorForm::getDiemDia);
        subjectMap.put("diemVan", ScoreCalculatorForm::getDiemVan);
        subjectMap.put("diemAnh", ScoreCalculatorForm::getDiemAnh);

        subjectMap.forEach((field, getter) -> {
            Double value = getter.apply(form);
            if (value != null && value > max) {
                br.rejectValue(field, "subject.max",
                        "Điểm môn này tối đa " + scaleLabel + " ở phương thức "
                                + form.getPhuongThuc().toUpperCase());
            }
        });
    }

    private void populateModel(Model model, ScoreCalculatorForm form, ScoreCalculatorResult result) {
        model.addAttribute("calculatorForm", form);
        model.addAttribute("nganhs", scoreCalculatorService.getActiveMajors());
        model.addAttribute("result", result);
        model.addAttribute("convertedMap", buildConvertedMap(result));
    }

    private Map<String, Double> buildConvertedMap(ScoreCalculatorResult result) {
        Map<String, Double> map = new LinkedHashMap<>();
        if (result == null) {
            return map;
        }
        result.convertedScores().forEach(row -> {
            if (row.subjectCode() != null && row.convertedScore() != null) {
                map.putIfAbsent(row.subjectCode(), row.convertedScore());
            }
        });
        return map;
    }
}

