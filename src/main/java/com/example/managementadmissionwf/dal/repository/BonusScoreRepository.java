package com.example.managementadmissionwf.dal.repository;

import com.example.managementadmissionwf.dal.entity.XtDiemcongxettuyen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BonusScoreRepository extends JpaRepository<XtDiemcongxettuyen, Integer> {
    Optional<XtDiemcongxettuyen> findByCccd(String cccd);
    boolean existsByCccd(String cccd);
}
