package com.example.managementadmissionwf.dal.repository;

import com.example.managementadmissionwf.dal.entity.XtBangquydoi;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConversionTableRepository extends JpaRepository<XtBangquydoi, Integer> {

        @Query("SELECT b FROM XtBangquydoi b WHERE " +
                        "(:phuongThuc IS NULL OR :phuongThuc = '' OR b.dPhuongthuc = :phuongThuc) AND " +
                        "(:toHop IS NULL OR :toHop = '' OR b.dTohop = :toHop) AND " +
                        "(:mon IS NULL OR :mon = '' OR b.dMon = :mon) AND " +
                        "(:keyword IS NULL OR :keyword = '' OR " +
                        "LOWER(b.dPhuongthuc) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
                        "LOWER(b.dTohop) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
                        "LOWER(b.dMon) LIKE LOWER(CONCAT('%', :keyword, '%')))")
        Page<XtBangquydoi> search(@Param("phuongThuc") String phuongThuc,
                        @Param("toHop") String toHop,
                        @Param("mon") String mon,
                        @Param("keyword") String keyword,
                        Pageable pageable);

        Optional<XtBangquydoi> findById(Integer id);

        @Query("SELECT b FROM XtBangquydoi b WHERE b.dPhuongthuc = :phuongThuc AND b.dMon = :mon AND b.dTohop = :toHop")
        Optional<XtBangquydoi> findByPhuongThucAndMonAndTohop(
                        @Param("phuongThuc") String phuongThuc,
                        @Param("mon") String mon,
                        @Param("toHop") String toHop);

        @Query("SELECT CASE WHEN COUNT(b) > 0 THEN true ELSE false END FROM XtBangquydoi b WHERE b.dPhuongthuc = :phuongThuc AND b.dMon = :mon AND b.dTohop = :toHop")
        boolean existsByPhuongThucAndMonAndTohop(
                        @Param("phuongThuc") String phuongThuc,
                        @Param("mon") String mon,
                        @Param("toHop") String toHop);

        @Query("SELECT CASE WHEN COUNT(b) > 0 THEN true ELSE false END FROM XtBangquydoi b WHERE b.dPhuongthuc = :phuongThuc AND b.dMon = :mon AND b.dTohop = :toHop AND b.id <> :id")
        boolean existsByPhuongThucAndMonAndTohopAndIdNot(
                        @Param("phuongThuc") String phuongThuc,
                        @Param("mon") String mon,
                        @Param("toHop") String toHop,
                        @Param("id") Integer id);

        @Query("SELECT DISTINCT b.dPhuongthuc FROM XtBangquydoi b ORDER BY b.dPhuongthuc")
        List<String> getAllPhuongThuc();

        @Query("SELECT DISTINCT b.dTohop FROM XtBangquydoi b WHERE b.dTohop IS NOT NULL ORDER BY b.dTohop")
        List<String> getAllToHop();

        @Query("SELECT DISTINCT b.dMon FROM XtBangquydoi b ORDER BY b.dMon")
        List<String> getAllMon();

        @Query("SELECT b FROM XtBangquydoi b WHERE b.dPhuongthuc = :phuongThuc AND b.dMon = :mon AND b.dTohop IS NULL")
        Optional<XtBangquydoi> findByPhuongThucAndMonAndTohopIsNull(
                        @Param("phuongThuc") String phuongThuc,
                        @Param("mon") String mon);
}