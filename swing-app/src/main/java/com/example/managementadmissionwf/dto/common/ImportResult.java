package com.example.managementadmissionwf.dto.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImportResult<T> {
    private int totalRows;
    private int successCount;
    private int errorCount;

    @Builder.Default
    private List<String> errors = new ArrayList<>();

    @Builder.Default
    private List<T> validData = new ArrayList<>();

    public void addError(String error) {
        if (this.errors == null) {
            this.errors = new ArrayList<>();
        }
        this.errors.add(error);
        this.errorCount++;
    }

    public void addValidData(T data) {
        if (this.validData == null) {
            this.validData = new ArrayList<>();
        }
        this.validData.add(data);
        this.successCount++;
    }
}
