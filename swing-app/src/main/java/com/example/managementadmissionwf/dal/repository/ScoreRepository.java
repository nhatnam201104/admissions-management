package com.example.managementadmissionwf.dal.repository;

import com.example.managementadmissionwf.dal.entity.XtDiemthixettuyen;
import com.example.managementadmissionwf.dal.entity.XtThisinhxettuyen25;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ScoreRepository extends JpaRepository<XtDiemthixettuyen, Integer> {
    @Query(
        value = """
            SELECT s
            FROM XtDiemthixettuyen s
            WHERE EXISTS (
                SELECT 1
                FROM XtThisinhxettuyen25 c
                WHERE c.cccd = s.cccd
                  AND c.isDeleted = false
            )
        """,
        countQuery = """
            SELECT COUNT(s)
            FROM XtDiemthixettuyen s
            WHERE EXISTS (
                SELECT 1
                FROM XtThisinhxettuyen25 c
                WHERE c.cccd = s.cccd
                  AND c.isDeleted = false
            )
        """
    )
    Page<XtDiemthixettuyen> findAllWithActiveCandidate(Pageable pageable);

    @Query("SELECT s FROM XtDiemthixettuyen s WHERE " +
           "EXISTS (SELECT 1 FROM XtThisinhxettuyen25 c WHERE c.cccd = s.cccd AND c.isDeleted = false) " +
           "AND " +
           "(:keyword IS NULL OR s.cccd LIKE :keyword OR s.sobaodanh LIKE :keyword) " +
           "AND (:phuongThuc IS NULL OR s.dPhuongthuc = :phuongThuc)")
    Page<XtDiemthixettuyen> searchScores(
        @Param("keyword") String keyword,
        @Param("phuongThuc") String phuongThuc,
        Pageable pageable
    );

    @Query(value = "SELECT * FROM xt_diemthixettuyen WHERE cccd = :cccd LIMIT 1", nativeQuery = true)
    XtDiemthixettuyen findByCccdIncludeDeleted(@Param("cccd") String cccd);

    // Tìm tất cả điểm thi của một thí sinh (hỗ trợ nhiều phương thức)
    List<XtDiemthixettuyen> findAllByCccd(String cccd);

    // Tìm điểm thi theo CCCD và phương thức
    @Query("SELECT s FROM XtDiemthixettuyen s WHERE s.cccd = :cccd AND s.dPhuongthuc = :phuongThuc")
    Optional<XtDiemthixettuyen> findByCccdAndDPhuongthuc(@Param("cccd") String cccd, @Param("phuongThuc") String phuongThuc);

    // Tìm điểm thi đầu tiên của một thí sinh (legacy support)
    Optional<XtDiemthixettuyen> findFirstByCccd(String cccd);

    // Kiểm tra tồn tại với CCCD và phương thức
    @Query("SELECT CASE WHEN COUNT(s) > 0 THEN true ELSE false END " +
           "FROM XtDiemthixettuyen s WHERE s.cccd = :cccd AND s.dPhuongthuc = :phuongThuc")
    boolean existsByCccdAndDPhuongthuc(@Param("cccd") String cccd, @Param("phuongThuc") String phuongThuc);

    Optional<XtDiemthixettuyen> findByCccd(String cccd);

    @Query(value = """
        SELECT *
        FROM xt_thisinhxettuyen25
        WHERE cccd = :cccd AND is_deleted = false
        LIMIT 1
    """, nativeQuery = true)
    XtThisinhxettuyen25 findCandidateByCccd(@Param("cccd") String cccd);

    boolean existsByCccd(String cccd);
    boolean existsByCccdAndIsDeletedFalse(String cccd);
}
