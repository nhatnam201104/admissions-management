package com.example.managementadmissionwf.dto;

/**
 * Record for structured calculation step display in ScoreDetailDialog
 */
public record CalculationStep(
    int stepNumber,
    String description,
    String formula,
    double result,
    boolean isFinal,
    boolean isCapApplied,
    boolean isIncomplete
) {
    public CalculationStep(int stepNumber, String description, String formula, double result) {
        this(stepNumber, description, formula, result, false, false, false);
    }
    
    public CalculationStep(int stepNumber, String description, String formula, double result, boolean isFinal) {
        this(stepNumber, description, formula, result, isFinal, false, false);
    }
    
    public CalculationStep(int stepNumber, String description, String formula, double result, 
                           boolean isFinal, boolean isCapApplied) {
        this(stepNumber, description, formula, result, isFinal, isCapApplied, false);
    }
}