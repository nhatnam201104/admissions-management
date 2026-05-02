package com.example.thymeleaf_web.repository;

import com.example.thymeleaf_web.model.entity.NguyenVong;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NguyenVongRepository extends JpaRepository<NguyenVong, Integer> {

    @Query(value = "SELECT * FROM xt_nguyenvongxettuyen WHERE nn_cccd = :cccd AND is_deleted = false ORDER BY nv_tt ASC", nativeQuery = true)
    List<NguyenVong> findByCccdActive(@Param("cccd") String cccd);
}
