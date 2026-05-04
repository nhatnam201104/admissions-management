package com.example.thymeleaf_web.controller.aspiration;

import com.example.thymeleaf_web.exception.BusinessException;
import com.example.thymeleaf_web.model.dto.AspirationRegistrationForm;
import com.example.thymeleaf_web.model.dto.CccdForm;
import com.example.thymeleaf_web.service.AspirationRegistrationService;
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
@RequestMapping("/dang-ky-nguyen-vong")
@RequiredArgsConstructor
@Slf4j
public class AspirationRegistrationController {

    private final AspirationRegistrationService aspirationRegistrationService;

    @GetMapping
    public String showLookupForm(@ModelAttribute("cccdForm") CccdForm cccdForm) {
        return "aspiration/register";
    }

    @PostMapping("/tra-cuu")
    public String lookupCandidate(@Valid @ModelAttribute("cccdForm") CccdForm cccdForm,
                                  BindingResult bindingResult,
                                  RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "aspiration/register";
        }

        String cccd = cccdForm.getCccd();
        if (aspirationRegistrationService.findCandidate(cccd).isEmpty()) {
            bindingResult.reject("candidate.notFound",
                    "Không tìm thấy thông tin thí sinh với CCCD đã nhập");
            return "aspiration/register";
        }

        redirectAttributes.addAttribute("cccd", cccd);
        return "redirect:/dang-ky-nguyen-vong/bieu-mau";
    }

    @GetMapping("/bieu-mau")
    public String showRegistrationForm(@RequestParam("cccd") String cccd,
                                       Model model,
                                       RedirectAttributes redirectAttributes) {
        try {
            AspirationRegistrationForm form = aspirationRegistrationService.buildRegistrationForm(cccd);
            populateRegistrationModel(model, form);
            return "aspiration/register";
        } catch (BusinessException ex) {
            log.warn("Cannot open aspiration registration form: {}", ex.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:/dang-ky-nguyen-vong";
        }
    }

    @PostMapping
    public String submitRegistration(@Valid @ModelAttribute("registrationForm") AspirationRegistrationForm form,
                                     BindingResult bindingResult,
                                     Model model,
                                     RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            populateRegistrationModel(model, form);
            return "aspiration/register";
        }

        try {
            aspirationRegistrationService.register(form);
            redirectAttributes.addFlashAttribute("successMessage", "Đã lưu đăng ký nguyện vọng thành công");
            redirectAttributes.addAttribute("cccd", form.getCccd());
            return "redirect:/dang-ky-nguyen-vong/bieu-mau";
        } catch (BusinessException ex) {
            bindingResult.reject("registration.invalid", ex.getMessage());
            populateRegistrationModel(model, form);
            return "aspiration/register";
        }
    }

    private void populateRegistrationModel(Model model, AspirationRegistrationForm form) {
        form.ensureMinimumRows();
        model.addAttribute("registrationForm", form);
        model.addAttribute("candidate", aspirationRegistrationService.findCandidate(form.getCccd()).orElse(null));
        model.addAttribute("nganhs", aspirationRegistrationService.getActiveMajors());
        model.addAttribute("maxAspirations", AspirationRegistrationForm.MAX_ASPIRATIONS);
        if (!model.containsAttribute("cccdForm")) {
            model.addAttribute("cccdForm", new CccdForm());
        }
    }
}
