package com.example.managementadmissionwf.dto.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Paging<T> {
    private int page;
    private int limit;
    private int totalPages;
    private long totalItems;
    private boolean hasNext;
    private List<T> data;
}
