package com.example.thymeleaf_web.repository;

import com.example.thymeleaf_web.model.entity.DiemThi;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DiemThiRepository extends JpaRepository<DiemThi, Integer> {

    @Query(value = "SELECT * FROM xt_diemthixettuyen WHERE cccd = :cccd AND is_deleted = false", nativeQuery = true)
    Optional<DiemThi> findByCccdActive(@Param("cccd") String cccd);
}
