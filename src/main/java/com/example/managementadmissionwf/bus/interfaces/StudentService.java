package com.example.managementadmissionwf.bus.interfaces;

import com.example.managementadmissionwf.dto.StudentDTO;

import java.util.List;
import java.util.Optional;


public interface StudentService {
    

    StudentDTO createStudent(StudentDTO studentDTO);
    

    StudentDTO updateStudent(Long id, StudentDTO studentDTO);

    void deleteStudent(Long id);
    

    Optional<StudentDTO> getStudentById(Long id);
    

    Optional<StudentDTO> getStudentByCode(String studentCode);
    

    List<StudentDTO> getAllStudents();
    

    List<StudentDTO> getStudentsByStatus(StudentDTO.StudentStatus status);

    List<StudentDTO> getStudentsByAdmissionYear(Integer admissionYear);

    List<StudentDTO> searchStudents(String searchTerm);
    

    StudentDTO approveStudent(Long id);
    

    StudentDTO rejectStudent(Long id);
    

    StudentDTO enrollStudent(Long id);

    java.util.Map<StudentDTO.StudentStatus, Long> getStudentStatistics();
}
