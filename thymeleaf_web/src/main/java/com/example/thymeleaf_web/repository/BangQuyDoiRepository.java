package com.example.thymeleaf_web.repository;

import com.example.thymeleaf_web.model.entity.BangQuyDoi;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository cho bảng quy đổi điểm ({@code xt_bangquydoi}).
 * <p>
 * Các method được thiết kế để khớp với pipeline tính điểm trong
 * {@code AspirationScoreServiceImpl} của module swing-app: cùng cách
 * khớp rule theo (phương thức, môn, tổ hợp) hoặc rule chung khi không
 * có tổ hợp riêng.
 */
@Repository
public interface BangQuyDoiRepository extends JpaRepository<BangQuyDoi, Integer> {

    List<BangQuyDoi> findByPhuongThucIgnoreCaseAndMonIgnoreCaseOrderByDiemAAsc(String phuongThuc, String mon);

    /**
     * Tra cứu rule theo (phuongThuc, mon, tohop). Khớp môn và tổ hợp đồng thời
     * (không phân biệt hoa-thường).
     */
    @Query("SELECT b FROM BangQuyDoi b "
            + "WHERE UPPER(b.phuongThuc) = UPPER(:phuongThuc) "
            + "AND UPPER(b.mon) = UPPER(:mon) "
            + "AND UPPER(b.toHop) = UPPER(:toHop) "
            + "ORDER BY b.diemA ASC")
    List<BangQuyDoi> findByPhuongThucMonAndToHop(@Param("phuongThuc") String phuongThuc,
                                                 @Param("mon") String mon,
                                                 @Param("toHop") String toHop);

    /**
     * Rule môn dùng chung cho mọi tổ hợp ({@code tohop IS NULL} hoặc rỗng).
     */
    @Query("SELECT b FROM BangQuyDoi b "
            + "WHERE UPPER(b.phuongThuc) = UPPER(:phuongThuc) "
            + "AND UPPER(b.mon) = UPPER(:mon) "
            + "AND (b.toHop IS NULL OR b.toHop = '') "
            + "ORDER BY b.diemA ASC")
    List<BangQuyDoi> findByPhuongThucMonAndToHopIsNull(@Param("phuongThuc") String phuongThuc,
                                                       @Param("mon") String mon);

    /**
     * Bảng quy đổi DGNL theo tổ hợp gốc của ngành ({@code mon IS NULL}).
     * Dùng cho phương thức ĐGNL: rule không gắn với một môn cụ thể, mà gắn
     * theo tổ hợp gốc (A00, A01, B00...) để chuyển NL1 sang thang 30.
     */
    @Query("SELECT b FROM BangQuyDoi b "
            + "WHERE UPPER(b.phuongThuc) = UPPER(:phuongThuc) "
            + "AND b.mon IS NULL "
            + "AND UPPER(b.toHop) = UPPER(:toHop) "
            + "ORDER BY b.diemA ASC")
    List<BangQuyDoi> findByPhuongThucMonIsNullAndToHop(@Param("phuongThuc") String phuongThuc,
                                                       @Param("toHop") String toHop);
}
