package com.example.managementadmissionwf.dal.repository;

import com.example.managementadmissionwf.dal.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long>, JpaSpecificationExecutor<Student> {
    

    Optional<Student> findByStudentCode(String studentCode);
    

    Optional<Student> findByEmail(String email);
    

    List<Student> findByStatus(Student.StudentStatus status);
    

    List<Student> findByAdmissionYear(Integer admissionYear);
    

    boolean existsByStudentCode(String studentCode);


    boolean existsByEmail(String email);
    

    List<Student> findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(
        String firstName, String lastName);
    

    long countByStatus(Student.StudentStatus status);
}
