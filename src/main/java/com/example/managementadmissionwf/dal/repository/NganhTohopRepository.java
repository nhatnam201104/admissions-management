package com.example.managementadmissionwf.dal.repository;

import com.example.managementadmissionwf.dal.entity.XtNganhTohop;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NganhTohopRepository extends JpaRepository<XtNganhTohop, Integer> {

    Page<XtNganhTohop> findByManganh(String manganh, Pageable pageable);
}