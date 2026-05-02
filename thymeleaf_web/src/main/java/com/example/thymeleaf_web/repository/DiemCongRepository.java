package com.example.thymeleaf_web.repository;

import com.example.thymeleaf_web.model.entity.DiemCong;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DiemCongRepository extends JpaRepository<DiemCong, Integer> {

    @Query(value = "SELECT * FROM xt_diemcongxettuyen WHERE cccd = :cccd AND is_deleted = false", nativeQuery = true)
    Optional<DiemCong> findByCccdActive(@Param("cccd") String cccd);
}
