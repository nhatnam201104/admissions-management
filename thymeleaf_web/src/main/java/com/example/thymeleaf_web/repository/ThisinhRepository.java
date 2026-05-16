package com.example.thymeleaf_web.repository;

import com.example.thymeleaf_web.model.entity.Thisinh;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface ThisinhRepository extends JpaRepository<Thisinh, Integer> {

    @Query(value = "SELECT * FROM xt_thisinhxettuyen25 WHERE cccd = :cccd AND is_deleted = false", nativeQuery = true)
    Optional<Thisinh> findByCccdActive(@Param("cccd") String cccd);

    @Query(value = """
            SELECT *
            FROM xt_thisinhxettuyen25
            WHERE cccd = :cccd
              AND ngay_sinh = :ngaySinh
              AND is_deleted = false
            """, nativeQuery = true)
    Optional<Thisinh> findByCccdAndNgaySinhActive(@Param("cccd") String cccd,
                                                  @Param("ngaySinh") LocalDate ngaySinh);
}
