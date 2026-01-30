package com.example.managementadmissionwf.bus.impl;

import com.example.managementadmissionwf.bus.interfaces.StudentService;
import com.example.managementadmissionwf.dal.entity.Student;
import com.example.managementadmissionwf.dal.repository.StudentRepository;
import com.example.managementadmissionwf.dto.StudentDTO;
import com.example.managementadmissionwf.exception.BusinessException;
import com.example.managementadmissionwf.exception.ResourceNotFoundException;
import com.example.managementadmissionwf.mapper.StudentMapper;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Year;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class StudentServiceImpl implements StudentService {
    
    private final StudentRepository studentRepository;
    private final StudentMapper studentMapper;
    private final Validator validator;
    
    @Override
    public StudentDTO createStudent(StudentDTO studentDTO) {
        // Validate DTO
        validateStudentDTO(studentDTO);
        
        // Check for duplicate student code
        if (studentRepository.existsByStudentCode(studentDTO.getStudentCode())) {
            throw new BusinessException("Student code already exists: " + studentDTO.getStudentCode());
        }
        
        // Check for duplicate email
        if (studentRepository.existsByEmail(studentDTO.getEmail())) {
            throw new BusinessException("Email already exists: " + studentDTO.getEmail());
        }
        
        // Set default status to PENDING if not provided
        if (studentDTO.getStatus() == null) {
            studentDTO.setStatus(StudentDTO.StudentStatus.PENDING);
        }
        
        // Set admission year to current year if not provided
        if (studentDTO.getAdmissionYear() == null) {
            studentDTO.setAdmissionYear(Year.now().getValue());
        }
        
        // Convert DTO to Entity and save
        Student entity = studentMapper.toEntity(studentDTO);
        Student savedEntity = studentRepository.save(entity);
        
        return studentMapper.toDto(savedEntity);
    }
    
    @Override
    public StudentDTO updateStudent(Long id, StudentDTO studentDTO) {
        // Validate DTO
        validateStudentDTO(studentDTO);
        
        // Check if student exists
        Student existingEntity = studentRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Student", id));
        
        // Prevent changing student code
        if (!existingEntity.getStudentCode().equals(studentDTO.getStudentCode())) {
            throw new BusinessException("Cannot change student code");
        }
        
        // Check for duplicate email if email changed
        if (!existingEntity.getEmail().equals(studentDTO.getEmail())) {
            if (studentRepository.existsByEmail(studentDTO.getEmail())) {
                throw new BusinessException("Email already exists: " + studentDTO.getEmail());
            }
        }
        
        // Update entity from DTO
        studentMapper.updateEntityFromDto(studentDTO, existingEntity);
        Student savedEntity = studentRepository.save(existingEntity);
        
        return studentMapper.toDto(savedEntity);
    }
    
    @Override
    public void deleteStudent(Long id) {
        // Check if student exists
        Student entity = studentRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Student", id));
        
        // Prevent deletion if student is ENROLLED or GRADUATED
        if (entity.getStatus() == Student.StudentStatus.ENROLLED || 
            entity.getStatus() == Student.StudentStatus.GRADUATED) {
            throw new BusinessException("Cannot delete enrolled or graduated student");
        }
        
        studentRepository.deleteById(id);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<StudentDTO> getStudentById(Long id) {
        return studentRepository.findById(id)
            .map(studentMapper::toDto);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<StudentDTO> getStudentByCode(String studentCode) {
        return studentRepository.findByStudentCode(studentCode)
            .map(studentMapper::toDto);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<StudentDTO> getAllStudents() {
        return studentMapper.toDtoList(studentRepository.findAll());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<StudentDTO> getStudentsByStatus(StudentDTO.StudentStatus status) {
        Student.StudentStatus entityStatus = Student.StudentStatus.valueOf(status.name());
        return studentMapper.toDtoList(studentRepository.findByStatus(entityStatus));
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<StudentDTO> getStudentsByAdmissionYear(Integer admissionYear) {
        return studentMapper.toDtoList(studentRepository.findByAdmissionYear(admissionYear));
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<StudentDTO> searchStudents(String searchTerm) {
        return studentMapper.toDtoList(
            studentRepository.findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(
                searchTerm, searchTerm
            )
        );
    }
    
    @Override
    public StudentDTO approveStudent(Long id) {
        Student entity = studentRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Student", id));
        
        // Check if student is in PENDING status
        if (entity.getStatus() != Student.StudentStatus.PENDING) {
            throw new BusinessException("Cannot approve student. Student must be in PENDING status");
        }
        
        // Update status to APPROVED
        entity.setStatus(Student.StudentStatus.APPROVED);
        Student savedEntity = studentRepository.save(entity);
        
        return studentMapper.toDto(savedEntity);
    }
    
    @Override
    public StudentDTO rejectStudent(Long id) {
        Student entity = studentRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Student", id));
        
        // Check if student is in PENDING status
        if (entity.getStatus() != Student.StudentStatus.PENDING) {
            throw new BusinessException("Cannot reject student. Student must be in PENDING status");
        }
        
        // Update status to REJECTED
        entity.setStatus(Student.StudentStatus.REJECTED);
        Student savedEntity = studentRepository.save(entity);
        
        return studentMapper.toDto(savedEntity);
    }
    
    @Override
    public StudentDTO enrollStudent(Long id) {
        Student entity = studentRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Student", id));
        
        // Check if student is in APPROVED status
        if (entity.getStatus() != Student.StudentStatus.APPROVED) {
            throw new BusinessException("Cannot enroll student. Student must be in APPROVED status");
        }
        
        // Update status to ENROLLED
        entity.setStatus(Student.StudentStatus.ENROLLED);
        Student savedEntity = studentRepository.save(entity);
        
        return studentMapper.toDto(savedEntity);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Map<StudentDTO.StudentStatus, Long> getStudentStatistics() {
        Map<StudentDTO.StudentStatus, Long> statistics = new EnumMap<>(StudentDTO.StudentStatus.class);
        
        for (Student.StudentStatus status : Student.StudentStatus.values()) {
            long count = studentRepository.countByStatus(status);
            statistics.put(
                StudentDTO.StudentStatus.valueOf(status.name()),
                count
            );
        }
        
        return statistics;
    }
    

    private void validateStudentDTO(StudentDTO studentDTO) {
        Set<ConstraintViolation<StudentDTO>> violations = validator.validate(studentDTO);
        
        if (!violations.isEmpty()) {
            String errorMessages = violations.stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining(", "));
            throw new BusinessException("Validation failed: " + errorMessages);
        }
    }
}
