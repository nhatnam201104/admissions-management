package com.example.managementadmissionwf.dal.repository;

import com.example.managementadmissionwf.dal.entity.XtNganh;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MajorRepository extends JpaRepository<XtNganh, Integer> {

    @Query("SELECT n FROM XtNganh n WHERE n.isDeleted = false AND (" +
            "LOWER(n.manganh) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(n.tennganh) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<XtNganh> search(@Param("keyword") String keyword, Pageable pageable);

    Optional<XtNganh> findByManganhAndIsDeletedFalse(String manganh);

    boolean existsByManganhAndIsDeletedFalse(String manganh);

    boolean existsByManganhAndIdnganhNotAndIsDeletedFalse(String manganh, Integer idnganh);
}