package com.example.thymeleaf_web.model.dto;

public record CalculationStep(
        int stepNumber,
        String description,
        String formula,
        Double result,
        boolean finalStep,
        boolean capApplied,
        boolean incomplete
) {
}
