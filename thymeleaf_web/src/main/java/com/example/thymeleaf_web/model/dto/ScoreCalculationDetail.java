package com.example.thymeleaf_web.model.dto;

import java.util.List;

public record ScoreCalculationDetail(
        String phuongThuc,
        String toHop,
        List<ConvertedScoreRow> convertedScores,
        List<CalculationStep> steps,
        boolean incomplete
) {
    private static final ScoreCalculationDetail EMPTY =
            new ScoreCalculationDetail(null, null, List.of(), List.of(), false);

    public ScoreCalculationDetail {
        convertedScores = convertedScores != null ? convertedScores : List.of();
        steps = steps != null ? steps : List.of();
    }

    public static ScoreCalculationDetail empty() {
        return EMPTY;
    }

    public boolean hasDetails() {
        return !convertedScores.isEmpty() || !steps.isEmpty();
    }
}
