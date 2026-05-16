package com.example.thymeleaf_web.model.dto;

public record ConvertedScoreRow(
        String subjectCode,
        String subjectName,
        Double rawScore,
        Double convertedScore,
        String formula,
        boolean converted
) {
}
