package com.example.managementadmissionwf.ui.panel.wish;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.managementadmissionwf.bus.interfaces.WishService;
import com.example.managementadmissionwf.dto.wish.WishDTO;

@Component
public class WishController {

    @Autowired
    private WishService service;

    public List<WishDTO> loadAllWishes() {
        return service.getAllWishes();
    }

    public List<WishDTO> searchWishes(String keyword, String ketQua) {
        return service.searchWishes(keyword, ketQua);
    }

    public void addWish(WishDTO dto) {
        service.createWish(dto);
    }

    public void editWish(WishDTO dto) {
        service.updateWish(dto);
    }

    public void deleteWish(Integer id) {
        service.deleteWish(id);
    }

    public void calculateScore() {
        service.getAllWishes().forEach(service::calculateScore);
    }

    public void updateResult(Integer id, String ketQua) {
        service.updateResult(id, ketQua);
    }
}