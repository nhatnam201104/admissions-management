package com.example.managementadmissionwf.dal.repository;

import com.example.managementadmissionwf.dal.entity.XtDiemthixettuyen;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface ScoreRepository extends JpaRepository<XtDiemthixettuyen, Long> {

    // Sửa câu Query để khớp với tham số truyền vào từ Service
    @Query("SELECT s FROM XtDiemthixettuyen s WHERE (s.isDeleted = false OR s.isDeleted IS NULL) " +
           "AND (:keyword IS NULL OR s.cccd LIKE :keyword OR s.sobaodanh LIKE :keyword) " +
           "AND (:phuongThuc IS NULL OR s.dPhuongthuc = :phuongThuc)")
    Page<XtDiemthixettuyen> searchScores(
        @Param("keyword") String keyword, 
        @Param("phuongThuc") String phuongThuc, 
        Pageable pageable
    );

    Optional<XtDiemthixettuyen> findByCccd(String cccd);

    boolean existsByCccd(String cccd);
}