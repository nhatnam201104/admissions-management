package com.example.managementadmissionwf.dal.repository;

import com.example.managementadmissionwf.dal.entity.MajorDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MajorDetailRepository extends JpaRepository<MajorDetail, Integer> {
}