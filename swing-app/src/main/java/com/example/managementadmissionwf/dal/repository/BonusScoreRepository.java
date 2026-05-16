package com.example.managementadmissionwf.dal.repository;

import com.example.managementadmissionwf.dal.entity.XtDiemcongxettuyen;
import com.example.managementadmissionwf.dto.score.BonusScoreViewDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BonusScoreRepository extends JpaRepository<XtDiemcongxettuyen, Integer> {
    Optional<XtDiemcongxettuyen> findByCccd(String cccd);
    boolean existsByCccd(String cccd);

    @Query(
            value = """
                    SELECT new com.example.managementadmissionwf.dto.score.BonusScoreViewDTO(
                        dc.cccd,
                        COALESCE(ts.hoVaTen, CONCAT(COALESCE(ts.ho, ''), ' ', COALESCE(ts.ten, ''))),
                        nv.nvTt,
                        nv.nvManganh,
                        n.tennganh,
                        nv.ttPhuongthuc,
                        nv.ttThm,
                        dc.diemCc,
                        dc.diemUtxt,
                        COALESCE(dc.diemTong, COALESCE(dc.diemCc, 0.0) + COALESCE(dc.diemUtxt, 0.0))
                    )
                    FROM XtDiemcongxettuyen dc
                    LEFT JOIN XtThisinhxettuyen25 ts ON ts.cccd = dc.cccd AND ts.isDeleted = false
                    LEFT JOIN XtNguyenvongxettuyen nv ON nv.nnCccd = dc.cccd AND nv.isDeleted = false
                    LEFT JOIN XtNganh n ON n.manganh = nv.nvManganh AND n.isDeleted = false
                    WHERE dc.isDeleted = false
                      AND (
                        :keyword IS NULL
                        OR LOWER(dc.cccd) LIKE LOWER(CONCAT('%', :keyword, '%'))
                        OR LOWER(COALESCE(ts.hoVaTen, CONCAT(COALESCE(ts.ho, ''), ' ', COALESCE(ts.ten, ''))))
                           LIKE LOWER(CONCAT('%', :keyword, '%'))
                      )
                    ORDER BY dc.cccd ASC, nv.nvTt ASC
                    """,
            countQuery = """
                    SELECT COUNT(dc.id)
                    FROM XtDiemcongxettuyen dc
                    LEFT JOIN XtThisinhxettuyen25 ts ON ts.cccd = dc.cccd AND ts.isDeleted = false
                    LEFT JOIN XtNguyenvongxettuyen nv ON nv.nnCccd = dc.cccd AND nv.isDeleted = false
                    WHERE dc.isDeleted = false
                      AND (
                        :keyword IS NULL
                        OR LOWER(dc.cccd) LIKE LOWER(CONCAT('%', :keyword, '%'))
                        OR LOWER(COALESCE(ts.hoVaTen, CONCAT(COALESCE(ts.ho, ''), ' ', COALESCE(ts.ten, ''))))
                           LIKE LOWER(CONCAT('%', :keyword, '%'))
                      )
                    """
    )
    Page<BonusScoreViewDTO> searchDisplayRows(@Param("keyword") String keyword, Pageable pageable);
}
