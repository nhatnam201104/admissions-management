package com.example.managementadmissionwf.dal.repository;

import com.example.managementadmissionwf.dal.entity.Major;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MajorRepository extends JpaRepository<Major, Integer> {
}