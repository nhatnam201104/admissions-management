package com.example.managementadmissionwf.dal.repository;

import com.example.managementadmissionwf.dal.entity.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SubjectRepository extends JpaRepository<Subject, Integer> {
}