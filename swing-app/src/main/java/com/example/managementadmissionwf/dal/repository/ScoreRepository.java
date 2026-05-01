package com.example.managementadmissionwf.dal.repository;

import com.example.managementadmissionwf.dal.entity.XtDiemthixettuyen;
import com.example.managementadmissionwf.dal.entity.XtThisinhxettuyen25;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
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
