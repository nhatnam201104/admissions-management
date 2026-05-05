package com.example.thymeleaf_web.model.dto;

import java.time.LocalDate;
import java.util.List;

public record ScoreLookupResult(
        String cccd,
        String soBaoDanh,
        String hoVaTen,
        LocalDate ngaySinh,
        List<ScoreLookupExamScore> diemThiList,
        Double diemCc, Double diemUtxt, Double diemTong,
        List<AspirationDto> nguyenVongs
) {
    public ScoreLookupResult {
        diemThiList = diemThiList != null ? diemThiList : List.of();
        nguyenVongs = nguyenVongs != null ? nguyenVongs : List.of();
    }

    public boolean hasAnyAdmittedAspiration() {
        return nguyenVongs != null && nguyenVongs.stream().anyMatch(AspirationDto::isAdmitted);
    }

    public ScoreLookupExamScore primaryDiemThi() {
        return diemThiList.isEmpty() ? null : diemThiList.get(0);
    }

    public String phuongThuc() {
        ScoreLookupExamScore diemThi = primaryDiemThi();
        return diemThi != null ? diemThi.phuongThuc() : null;
    }

    public Double toan() {
        ScoreLookupExamScore diemThi = primaryDiemThi();
        return diemThi != null ? diemThi.toan() : null;
    }

    public Double ly() {
        ScoreLookupExamScore diemThi = primaryDiemThi();
        return diemThi != null ? diemThi.ly() : null;
    }

    public Double hoa() {
        ScoreLookupExamScore diemThi = primaryDiemThi();
        return diemThi != null ? diemThi.hoa() : null;
    }

    public Double sinh() {
        ScoreLookupExamScore diemThi = primaryDiemThi();
        return diemThi != null ? diemThi.sinh() : null;
    }

    public Double su() {
        ScoreLookupExamScore diemThi = primaryDiemThi();
        return diemThi != null ? diemThi.su() : null;
    }

    public Double dia() {
        ScoreLookupExamScore diemThi = primaryDiemThi();
        return diemThi != null ? diemThi.dia() : null;
    }

    public Double van() {
        ScoreLookupExamScore diemThi = primaryDiemThi();
        return diemThi != null ? diemThi.van() : null;
    }

    public Double n1Thi() {
        ScoreLookupExamScore diemThi = primaryDiemThi();
        return diemThi != null ? diemThi.n1Thi() : null;
    }

    public Double n1Cc() {
        ScoreLookupExamScore diemThi = primaryDiemThi();
        return diemThi != null ? diemThi.n1Cc() : null;
    }

    public Double nl1() {
        ScoreLookupExamScore diemThi = primaryDiemThi();
        return diemThi != null ? diemThi.nl1() : null;
    }

    public Double nk1() {
        ScoreLookupExamScore diemThi = primaryDiemThi();
        return diemThi != null ? diemThi.nk1() : null;
    }

    public Double nk2() {
        ScoreLookupExamScore diemThi = primaryDiemThi();
        return diemThi != null ? diemThi.nk2() : null;
    }
}
