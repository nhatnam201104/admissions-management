package com.example.thymeleaf_web.repository;

import com.example.thymeleaf_web.model.entity.Nganh;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NganhRepository extends JpaRepository<Nganh, Integer> {

    @Query(value = "SELECT * FROM xt_nganh WHERE is_deleted = false", nativeQuery = true)
    List<Nganh> findAllActive();
}
