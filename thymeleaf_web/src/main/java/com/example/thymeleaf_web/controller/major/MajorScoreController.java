package com.example.thymeleaf_web.controller.major;

import com.example.thymeleaf_web.service.ScoreLookupService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/diem-chuan")
@RequiredArgsConstructor
@Slf4j
public class MajorScoreController {

    private final ScoreLookupService scoreLookupService;

    @GetMapping
    public String showMajorScores(Model model) {
        model.addAttribute("nganhs", scoreLookupService.getAllActiveNganh());
        return "major/scores";
    }
}
