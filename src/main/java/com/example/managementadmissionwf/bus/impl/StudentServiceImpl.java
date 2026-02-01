package com.example.managementadmissionwf.bus.impl;

import com.example.managementadmissionwf.bus.interfaces.StudentService;
import com.example.managementadmissionwf.dal.repository.StudentRepository;
import com.example.managementadmissionwf.dto.StudentDTO;

import com.example.managementadmissionwf.mapper.StudentMapper;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@RequiredArgsConstructor
@Transactional
public class StudentServiceImpl implements StudentService {

    private final StudentRepository studentRepository;
    private final StudentMapper studentMapper;
    private final Validator validator;


    @Override
    public StudentDTO createStudent(StudentDTO studentDTO) {
        return null;
    }

    @Override
    public StudentDTO updateStudent(StudentDTO studentDTO) {
        return null;
    }

    @Override
    public List<StudentDTO> getAllStudents() {
        return List.of();
    }

    @Override
    public StudentDTO getStudentById(Integer id) {
        return null;
    }


}
