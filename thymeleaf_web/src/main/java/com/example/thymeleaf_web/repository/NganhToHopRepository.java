package com.example.thymeleaf_web.repository;

import com.example.thymeleaf_web.model.entity.NganhToHop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NganhToHopRepository extends JpaRepository<NganhToHop, Integer> {

    List<NganhToHop> findByManganhOrderByMatohopAsc(String manganh);

    /**
     * Lấy tất cả các tổ hợp distinct đang được dùng (1 dòng cho mỗi matohop —
     * dùng làm "thư viện tổ hợp" cho công cụ quy đổi điểm độc lập, không
     * gắn với ngành nào). MIN(id) để chọn 1 dòng đại diện.
     */
    @Query("SELECT n FROM NganhToHop n WHERE n.id IN ("
            + "  SELECT MIN(n2.id) FROM NganhToHop n2 GROUP BY n2.matohop"
            + ") ORDER BY n.matohop ASC")
    List<NganhToHop> findDistinctMatohop();
}

