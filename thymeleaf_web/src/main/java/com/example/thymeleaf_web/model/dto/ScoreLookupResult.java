package com.example.thymeleaf_web.model.dto;

import java.time.LocalDate;
import java.util.List;

public record ScoreLookupResult(
        String cccd,
        String soBaoDanh,
        String hoVaTen,
        LocalDate ngaySinh,
        String phuongThuc,
        Double toan, Double ly, Double hoa, Double sinh, Double su, Double dia, Double van,
        Double n1Thi, Double n1Cc, Double nl1, Double nk1, Double nk2,
        Double diemCc, Double diemUtxt, Double diemTong,
        List<AspirationDto> nguyenVongs
) {
    public boolean hasAnyAdmittedAspiration() {
        return nguyenVongs != null && nguyenVongs.stream().anyMatch(AspirationDto::isAdmitted);
    }
}
