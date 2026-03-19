package com.example.managementadmissionwf.bus.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.managementadmissionwf.bus.interfaces.WishService;
import com.example.managementadmissionwf.dto.wish.WishDTO;

import jakarta.annotation.PostConstruct;

@Service
public class WishServiceImpl implements WishService {

    private List<WishDTO> data = new ArrayList<>();

    @PostConstruct
    public void init() {
        // MOCK DATA
        data.add(WishDTO.builder()
                .id(1)
                .nnCccd("079204001234")
                .hoTenThiSinh("Nguyễn Văn A")
                .nvManganh("7480201")
                .tenNganh("CNTT")
                .nvTt(1)
                .diemUtqd(0.5)
                .diemCong(1.0)
                .nvKetqua("CHO_XET")
                .build());

        data.forEach(this::calculateScore);
    }

    @Override
    public List<WishDTO> getAllWishes() {
        return data;
    }

    @Override
    public List<WishDTO> getWishesByCandidate(String cccd) {
        return data.stream()
                .filter(w -> w.getNnCccd().equals(cccd))
                .collect(Collectors.toList());
    }

    @Override
    public List<WishDTO> searchWishes(String keyword, String ketQua) {
        return data.stream()
                .filter(w -> keyword.isEmpty()
                        || w.getNnCccd().contains(keyword)
                        || w.getHoTenThiSinh().toLowerCase().contains(keyword.toLowerCase()))
                .filter(w -> ketQua.equals("Tất cả") || w.getNvKetqua().equals(ketQua))
                .collect(Collectors.toList());
    }

    @Override
    public WishDTO createWish(WishDTO dto) {
        // VALIDATE TRÙNG CCCD + NGÀNH
        boolean exists = data.stream()
                .anyMatch(w -> w.getNnCccd().equals(dto.getNnCccd())
                        && w.getNvManganh().equals(dto.getNvManganh()));

        if (exists) throw new RuntimeException("Nguyện vọng đã tồn tại!");

        dto.setId(data.size() + 1);
        calculateScore(dto);
        dto.setNvKetqua("CHO_XET");

        data.add(dto);
        return dto;
    }

    @Override
    public WishDTO updateWish(WishDTO dto) {
        WishDTO old = data.stream()
                .filter(w -> w.getId().equals(dto.getId()))
                .findFirst()
                .orElseThrow();

        old.setNvTt(dto.getNvTt());
        old.setDiemCong(dto.getDiemCong());
        old.setDiemUtqd(dto.getDiemUtqd());

        calculateScore(old);
        return old;
    }

    @Override
    public void deleteWish(Integer id) {
        data.removeIf(w -> w.getId().equals(id));
    }

    @Override
    public void calculateScore(WishDTO w) {
        // MOCK logic từ XtNganhTohop + XtDiemthixettuyen
        double diemMon = 24.0; // giả lập
        w.setDiemThxt(diemMon);

        double tong = diemMon + safe(w.getDiemUtqd()) + safe(w.getDiemCong());
        w.setDiemXettuyen(tong);
    }

    @Override
    public void updateResult(Integer id, String ketQua) {
        data.stream()
                .filter(w -> w.getId().equals(id))
                .findFirst()
                .ifPresent(w -> w.setNvKetqua(ketQua));
    }

    private double safe(Double d) {
        return d == null ? 0 : d;
    }
}