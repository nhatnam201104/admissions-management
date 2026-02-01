package com.example.managementadmissionwf.dal.repository;

import com.example.managementadmissionwf.dal.entity.ComplexSubject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ComplexSubjectRepository extends JpaRepository<ComplexSubject, Integer> {
}