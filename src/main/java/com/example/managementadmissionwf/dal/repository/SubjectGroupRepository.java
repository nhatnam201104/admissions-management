package com.example.managementadmissionwf.dal.repository;

import com.example.managementadmissionwf.dal.entity.XtTohopMonthi;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SubjectGroupRepository extends JpaRepository<XtTohopMonthi, Integer> {

    @Query("SELECT s FROM XtTohopMonthi s WHERE " +
            "LOWER(s.matohop) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(s.tentohop) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(s.mon1) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(s.mon2) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(s.mon3) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<XtTohopMonthi> search(@Param("keyword") String keyword, Pageable pageable);

    Optional<XtTohopMonthi> findByMatohop(String matohop);

    boolean existsByMatohop(String matohop);

    boolean existsByMatohopAndIdNot(String matohop, Integer id);
}
