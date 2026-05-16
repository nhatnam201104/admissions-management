package com.example.thymeleaf_web.repository;

import com.example.thymeleaf_web.model.entity.BangQuyDoi;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BangQuyDoiRepository extends JpaRepository<BangQuyDoi, Integer> {

    List<BangQuyDoi> findByPhuongThucIgnoreCaseAndMonIgnoreCaseOrderByDiemAAsc(String phuongThuc, String mon);
}
