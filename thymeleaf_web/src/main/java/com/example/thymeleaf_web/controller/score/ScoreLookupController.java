package com.example.thymeleaf_web.controller.score;

import com.example.thymeleaf_web.exception.ResourceNotFoundException;
import com.example.thymeleaf_web.model.dto.CccdForm;
import com.example.thymeleaf_web.model.dto.ScoreLookupResult;
import com.example.thymeleaf_web.service.ScoreLookupService;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/tra-cuu-diem")
@RequiredArgsConstructor
@Slf4j
public class ScoreLookupController {

    private final ScoreLookupService scoreLookupService;

    @GetMapping
    public String showLookupForm(@ModelAttribute("cccdForm") CccdForm cccdForm) {
        return "score/lookup";
    }

    @PostMapping
    public String processLookup(@Valid @ModelAttribute("cccdForm") CccdForm cccdForm,
                                BindingResult bindingResult,
                                RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "score/lookup";
        }

        String cccd = cccdForm.getCccd();
        var result = scoreLookupService.lookupByCccd(cccd);

        if (result.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Không tìm thấy thông tin thí sinh với CCCD: " + maskCccd(cccd));
            return "redirect:/tra-cuu-diem";
        }

        redirectAttributes.addAttribute("cccd", cccd);
        return "redirect:/tra-cuu-diem/ket-qua";
    }

    @GetMapping("/ket-qua")
    public String showResult(@RequestParam("cccd") String cccd, Model model) {
        ScoreLookupResult result = scoreLookupService.lookupByCccd(cccd)
                .orElseThrow(() -> new ResourceNotFoundException("Thí sinh", cccd));
        model.addAttribute("result", result);
        return "score/result";
    }

    private String maskCccd(String cccd) {
        if (cccd == null || cccd.length() < 4) return cccd;
        return "********" + cccd.substring(cccd.length() - 4);
    }
}
