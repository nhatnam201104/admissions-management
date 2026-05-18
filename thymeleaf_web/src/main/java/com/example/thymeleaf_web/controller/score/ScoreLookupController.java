package com.example.thymeleaf_web.controller.score;

import com.example.thymeleaf_web.exception.ResourceNotFoundException;
import com.example.thymeleaf_web.model.dto.CccdForm;
import com.example.thymeleaf_web.model.dto.ScoreLookupResult;
import com.example.thymeleaf_web.service.ScoreLookupService;
import jakarta.servlet.http.HttpSession;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/tra-cuu-diem")
@RequiredArgsConstructor
@Slf4j
public class ScoreLookupController {

    private static final String SESSION_VALIDATED_CCCD = "SCORE_LOOKUP_VALIDATED_CCCD";

    private final ScoreLookupService scoreLookupService;

    @GetMapping
    public String showLookupForm(@ModelAttribute("cccdForm") CccdForm cccdForm) {
        return "score/lookup";
    }

    @PostMapping
    public String processLookup(@Valid @ModelAttribute("cccdForm") CccdForm cccdForm,
                                BindingResult bindingResult,
                                HttpSession session,
                                Model model) {
        // Validation thất bại (CCCD không phải 12 số, ngày sinh thiếu/sai
        // định dạng, ngày sinh không trong quá khứ...) → render lại form
        // với thông báo lỗi field-level. Không redirect để giữ giá trị
        // người dùng đã nhập, dễ chỉnh sửa.
        if (bindingResult.hasErrors()) {
            return "score/lookup";
        }

        String cccd = cccdForm.getCccd().trim();
        var result = scoreLookupService.lookupByCccdAndNgaySinh(cccd, cccdForm.getNgaySinh());

        if (result.isEmpty()) {
            session.removeAttribute(SESSION_VALIDATED_CCCD);
            // Báo lỗi global trên form (không gắn vào field cụ thể vì
            // không xác định được field nào sai trong cặp CCCD/ngày sinh).
            bindingResult.reject("lookup.notFound",
                    "Không tìm thấy thí sinh khớp với CCCD và ngày sinh đã nhập. Vui lòng kiểm tra lại.");
            return "score/lookup";
        }

        session.setAttribute(SESSION_VALIDATED_CCCD, cccd);
        return "redirect:/tra-cuu-diem/ket-qua";
    }

    @GetMapping("/ket-qua")
    public String showResult(Model model,
                             HttpSession session,
                             RedirectAttributes redirectAttributes) {
        String cccd = (String) session.getAttribute(SESSION_VALIDATED_CCCD);
        if (cccd == null || cccd.isBlank()) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Vui lòng xác thực CCCD và ngày sinh trước khi xem kết quả.");
            return "redirect:/tra-cuu-diem";
        }

        ScoreLookupResult result = scoreLookupService.lookupByCccd(cccd)
                .orElseThrow(() -> new ResourceNotFoundException("Thí sinh", cccd));
        model.addAttribute("result", result);
        return "score/result";
    }
}
