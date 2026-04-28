package com.example.managementadmissionwf.dal.repository;

import com.example.managementadmissionwf.dal.entity.XtThisinhxettuyen25;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CandidateRepository extends JpaRepository<XtThisinhxettuyen25, Integer> {

    boolean existsByCccd(String cccd);

}
