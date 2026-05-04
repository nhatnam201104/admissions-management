package com.example.thymeleaf_web.model.dto;

import java.time.LocalDate;

public record CandidateInfoDto(
        String cccd,
        String soBaoDanh,
        String hoVaTen,
        LocalDate ngaySinh
) {
}
