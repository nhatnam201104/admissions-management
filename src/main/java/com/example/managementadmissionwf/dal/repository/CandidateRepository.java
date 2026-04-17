package com.example.managementadmissionwf.dal.repository;

import com.example.managementadmissionwf.dal.entity.XtThisinhxettuyen25;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
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

    @Modifying
    @Query("UPDATE XtThisinhxettuyen25 c SET c.isDeleted = true WHERE c.cccd = :cccd AND c.isDeleted = false")
    void softDeleteByCccd(@Param("cccd") String cccd);
}
