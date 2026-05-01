package com.example.managementadmissionwf.dal.repository;

import com.example.managementadmissionwf.dal.entity.XtThisinhxettuyen25;

import jakarta.transaction.Transactional;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CandidateRepository extends JpaRepository<XtThisinhxettuyen25, Integer> {

    @Query("SELECT c FROM XtThisinhxettuyen25 c WHERE " +
           "(:keyword IS NULL OR LOWER(c.cccd) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(c.sobaodanh) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(c.hoVaTen) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " +
           "(:khuVuc IS NULL OR :khuVuc = 'Tất cả' OR c.khuVuc = :khuVuc) AND " +
           "(:doiTuong IS NULL OR :doiTuong = 'Tất cả' OR c.doiTuong = :doiTuong)")
    Page<XtThisinhxettuyen25> search(@Param("keyword") String keyword, 
                                     @Param("khuVuc") String khuVuc, 
                                     @Param("doiTuong") String doiTuong, 
                                     Pageable pageable);

    Optional<XtThisinhxettuyen25> findByCccd(String cccd);

    @Query(value = "SELECT * FROM xt_thisinhxettuyen25 WHERE cccd = :cccd", nativeQuery = true)
    Optional<XtThisinhxettuyen25> findByCccdIncludingDeleted(@Param("cccd") String cccd);
    
    boolean existsByCccd(String cccd);

    @Transactional
    @Modifying
    @Query("UPDATE XtThisinhxettuyen25 c SET c.isDeleted = true WHERE c.cccd = :cccd AND c.isDeleted = false")
    void softDeleteByCccd(@Param("cccd") String cccd);

    @Transactional
    @Modifying
    @Query(value = "UPDATE xt_diemthixettuyen SET is_deleted = true WHERE cccd = :cccd AND is_deleted = false", nativeQuery = true)
    void softDeleteScoresByCccd(@Param("cccd") String cccd);

    @Transactional
    @Modifying
    @Query(value = "UPDATE xt_diemcongxettuyen SET is_deleted = true WHERE cccd = :cccd AND is_deleted = false", nativeQuery = true)
    void softDeleteBonusScoresByCccd(@Param("cccd") String cccd);

    @Transactional
    @Modifying
    @Query(value = "UPDATE xt_nguyenvongxettuyen SET is_deleted = true WHERE nn_cccd = :cccd AND is_deleted = false", nativeQuery = true)
    void softDeleteAspirationsByCccd(@Param("cccd") String cccd);
    
    @Transactional
    @Modifying
    @Query(value = "UPDATE xt_thisinhxettuyen25 SET is_deleted = false WHERE cccd = :cccd", nativeQuery = true)
    void restoreSoftDeleteByCccd(@Param("cccd") String cccd);
    
    @Query(value = "SELECT * FROM xt_thisinhxettuyen25 WHERE sobaodanh = :sobaodanh LIMIT 1", nativeQuery = true)
    Optional<XtThisinhxettuyen25> findBySobaodanhIncludingDeleted(@Param("sobaodanh") String sobaodanh);
    
    @Query(value = "SELECT * FROM xt_thisinhxettuyen25 WHERE email = :email LIMIT 1", nativeQuery = true)
    Optional<XtThisinhxettuyen25> findByEmailIncludingDeleted(@Param("email") String email);

    @Query(value = "SELECT * FROM xt_thisinhxettuyen25 WHERE dien_thoai = :dienThoai LIMIT 1", nativeQuery = true)
    Optional<XtThisinhxettuyen25> findByDienThoaiIncludingDeleted(@Param("dienThoai") String dienThoai);
   

    
}
