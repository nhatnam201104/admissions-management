package com.example.managementadmissionwf.bus.interfaces;


import com.example.managementadmissionwf.dto.StudentDTO;

import java.util.List;

public interface StudentService {

    public StudentDTO createStudent(StudentDTO studentDTO);

    public StudentDTO updateStudent(StudentDTO studentDTO);

    public List<StudentDTO> getAllStudents();

    public StudentDTO getStudentById(Integer id);

}
