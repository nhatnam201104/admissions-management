package com.example.managementadmissionwf.dal.repository;

import com.example.managementadmissionwf.dal.entity.XtNganhTohop;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NganhTohopRepository extends JpaRepository<XtNganhTohop, Integer> {

    @Query("SELECT nt FROM XtNganhTohop nt WHERE nt.manganh = :manganh")
    Page<XtNganhTohop> findByManganh(@Param("manganh") String manganh, Pageable pageable);

    boolean existsByManganhAndMatohopAndIsDeletedFalse(String manganh, String matohop);
}