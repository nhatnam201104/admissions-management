package com.example.managementadmissionwf.bus.interfaces;

import java.util.List;

import com.example.managementadmissionwf.dto.wish.WishDTO;

public interface WishService {

    List<WishDTO> getAllWishes();

    List<WishDTO> getWishesByCandidate(String cccd);

    List<WishDTO> searchWishes(String keyword, String ketQua);

    WishDTO createWish(WishDTO dto);

    WishDTO updateWish(WishDTO dto);

    void deleteWish(Integer id);

    void calculateScore(WishDTO wish);

    void updateResult(Integer id, String ketQua);
}