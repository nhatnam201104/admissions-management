package com.example.managementadmissionwf.dal.repository;

import com.example.managementadmissionwf.dal.entity.XtBangquydoi;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BangquydoiRepository extends JpaRepository<XtBangquydoi, Integer> {

    /**
     * Tìm các quy đổi theo phương thức
     */
    @Query("SELECT b FROM XtBangquydoi b WHERE b.dPhuongthuc = :phuongthuc")
    List<XtBangquydoi> findByDPhuongthuc(@Param("phuongthuc") String phuongthuc);

    /**
     * Tìm quy đổi theo phương thức và môn
     */
    @Query("SELECT b FROM XtBangquydoi b WHERE b.dPhuongthuc = :phuongthuc AND b.dMon = :mon")
    List<XtBangquydoi> findByDPhuongthucAndDMon(@Param("phuongthuc") String phuongthuc, @Param("mon") String mon);

}