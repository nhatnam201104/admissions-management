package com.example.managementadmissionwf.dal.repository;

import com.example.managementadmissionwf.dal.entity.XtDiemthixettuyen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ScoreRepository extends JpaRepository<XtDiemthixettuyen, Integer> {
    Optional<XtDiemthixettuyen> findByCccd(String cccd);
    boolean existsByCccd(String cccd);
}
