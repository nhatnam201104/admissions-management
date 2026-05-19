package com.example.managementadmissionwf.dal.repository;

import com.example.managementadmissionwf.dal.entity.XtNguyenvongxettuyen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NguyenVongRepository extends JpaRepository<XtNguyenvongxettuyen, Integer> {

    List<XtNguyenvongxettuyen> findByNnCccd(String cccd);

    List<XtNguyenvongxettuyen> findByNvKetqua(String nvKetqua);

    /**
     * Kiểm tra trùng nguyện vọng theo định nghĩa nghiệp vụ:
     * cùng CCCD + cùng ngành + cùng phương thức + cùng tổ hợp.
     * Tham số {@code excludeId} cho phép luồng update bỏ qua chính NV đang sửa.
     * Khi {@code thm} null, match với row có {@code ttThm} cũng null.
     */
    @Query("""
            SELECT COUNT(nv) > 0 FROM XtNguyenvongxettuyen nv
            WHERE nv.isDeleted = false
              AND nv.nnCccd = :cccd
              AND nv.nvManganh = :manganh
              AND nv.ttPhuongthuc = :phuongThuc
              AND ((:thm IS NULL AND nv.ttThm IS NULL) OR nv.ttThm = :thm)
              AND (:excludeId IS NULL OR nv.id <> :excludeId)
            """)
    boolean existsDuplicate(@Param("cccd") String cccd,
                            @Param("manganh") String manganh,
                            @Param("phuongThuc") String phuongThuc,
                            @Param("thm") String thm,
                            @Param("excludeId") Integer excludeId);

    @Query("""
            SELECT nv.nvManganh, COUNT(nv.id)
            FROM XtNguyenvongxettuyen nv
            WHERE nv.isDeleted = false AND nv.nvManganh IN :majorCodes
            GROUP BY nv.nvManganh
            """)
    List<Object[]> countActiveByMajorCodes(@Param("majorCodes") List<String> majorCodes);

    // ==================== Statistic queries ====================

    /**
     * Đếm tổng thí sinh đăng ký xét tuyển (DISTINCT cccd) - dùng cho dashboard
     * thay cho COUNT toàn bảng candidate.
     */
    @Query("SELECT COUNT(DISTINCT nv.nnCccd) FROM XtNguyenvongxettuyen nv WHERE nv.isDeleted = false")
    long countDistinctActiveCandidates();

    @Query("SELECT COUNT(nv) FROM XtNguyenvongxettuyen nv WHERE nv.isDeleted = false")
    long countAllActive();

    @Query("SELECT COUNT(DISTINCT nv.nnCccd) FROM XtNguyenvongxettuyen nv " +
            "WHERE nv.isDeleted = false AND nv.nvKetqua = 'TRUNG_TUYEN'")
    long countAdmittedStudents();

    @Query("SELECT COUNT(nv) FROM XtNguyenvongxettuyen nv " +
            "WHERE nv.isDeleted = false AND nv.nvKetqua = 'TRUOT'")
    long countRejected();

    @Query("SELECT AVG(nv.diemXettuyen) FROM XtNguyenvongxettuyen nv " +
            "WHERE nv.isDeleted = false AND nv.diemXettuyen IS NOT NULL")
    Double averageScore();

    @Query("SELECT MAX(nv.diemXettuyen) FROM XtNguyenvongxettuyen nv " +
            "WHERE nv.isDeleted = false AND nv.diemXettuyen IS NOT NULL")
    Double maxScore();

    @Query("SELECT MIN(nv.diemXettuyen) FROM XtNguyenvongxettuyen nv " +
            "WHERE nv.isDeleted = false AND nv.diemXettuyen IS NOT NULL")
    Double minScore();

    /**
     * Top ngành: tổng số TS, số trúng tuyển (theo NV), chỉ tiêu, điểm TB.
     * Dùng cho `StatisticServiceImpl.getMajorStatistics()`.
     * Trả về mảng: manganh, tennganh, totalDistinct, admittedAspirations, target, avgScore.
     */
    @Query("""
            SELECT n.manganh, n.tennganh,
                   COUNT(DISTINCT nv.nnCccd) AS totalStudents,
                   SUM(CASE WHEN nv.nvKetqua = 'TRUNG_TUYEN' THEN 1 ELSE 0 END) AS admitted,
                   n.nChitieu AS target,
                   AVG(nv.diemXettuyen) AS avgScore
            FROM XtNguyenvongxettuyen nv
            JOIN nv.nganh n
            WHERE nv.isDeleted = false
            GROUP BY n.manganh, n.tennganh, n.nChitieu
            ORDER BY totalStudents DESC
            """)
    List<Object[]> topMajorStatistics();

    /**
     * Đếm số thí sinh trúng tuyển (DISTINCT cccd) cho 1 ngành cụ thể.
     */
    @Query("SELECT COUNT(DISTINCT nv.nnCccd) FROM XtNguyenvongxettuyen nv " +
            "WHERE nv.isDeleted = false AND nv.nvManganh = :manganh AND nv.nvKetqua = 'TRUNG_TUYEN'")
    long countAdmittedStudentsByMajor(@Param("manganh") String manganh);

    /**
     * Thống kê theo phương thức xét tuyển.
     * Trả về: phuongThuc, total, admitted.
     */
    @Query("""
            SELECT nv.ttPhuongthuc AS phuongThuc,
                   COUNT(nv) AS total,
                   SUM(CASE WHEN nv.nvKetqua = 'TRUNG_TUYEN' THEN 1 ELSE 0 END) AS admitted
            FROM XtNguyenvongxettuyen nv
            WHERE nv.isDeleted = false AND nv.ttPhuongthuc IS NOT NULL
            GROUP BY nv.ttPhuongthuc
            ORDER BY total DESC
            """)
    List<Object[]> methodStatistics();

    /**
     * Phân bổ điểm xét tuyển theo 6 khoảng (đếm DISTINCT thí sinh).
     * Trả về: range label, count.
     */
    @Query("""
            SELECT
                CASE
                    WHEN nv.diemXettuyen >= 27 THEN '27+'
                    WHEN nv.diemXettuyen >= 24 THEN '24-26.9'
                    WHEN nv.diemXettuyen >= 21 THEN '21-23.9'
                    WHEN nv.diemXettuyen >= 18 THEN '18-20.9'
                    WHEN nv.diemXettuyen >= 15 THEN '15-17.9'
                    ELSE '0-14.9'
                END AS range,
                COUNT(DISTINCT nv.nnCccd) AS cnt
            FROM XtNguyenvongxettuyen nv
            WHERE nv.isDeleted = false AND nv.diemXettuyen IS NOT NULL
            GROUP BY
                CASE
                    WHEN nv.diemXettuyen >= 27 THEN '27+'
                    WHEN nv.diemXettuyen >= 24 THEN '24-26.9'
                    WHEN nv.diemXettuyen >= 21 THEN '21-23.9'
                    WHEN nv.diemXettuyen >= 18 THEN '18-20.9'
                    WHEN nv.diemXettuyen >= 15 THEN '15-17.9'
                    ELSE '0-14.9'
                END
            ORDER BY range DESC
            """)
    List<Object[]> scoreDistribution();

    @Query("SELECT COUNT(DISTINCT nv.nnCccd) FROM XtNguyenvongxettuyen nv " +
            "WHERE nv.isDeleted = false AND nv.diemXettuyen IS NOT NULL")
    long countDistinctScoredCandidates();

    /**
     * Ma trận trúng tuyển theo ngành × phương thức.
     * Trả về: manganh, tennganh, phuongThuc, totalApply, admitted.
     */
    @Query("""
            SELECT n.manganh, n.tennganh, nv.ttPhuongthuc,
                   COUNT(nv) AS totalApply,
                   SUM(CASE WHEN nv.nvKetqua = 'TRUNG_TUYEN' THEN 1 ELSE 0 END) AS admitted
            FROM XtNguyenvongxettuyen nv
            JOIN nv.nganh n
            WHERE nv.isDeleted = false AND nv.ttPhuongthuc IS NOT NULL
            GROUP BY n.manganh, n.tennganh, nv.ttPhuongthuc
            ORDER BY n.tennganh ASC, nv.ttPhuongthuc ASC
            """)
    List<Object[]> majorMethodMatrix();
}
