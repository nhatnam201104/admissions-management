package com.example.managementadmissionwf.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * StudentDTO - Data Transfer Object for Student
 * 
 * Layer: DTO (Data Transfer Object)
 * Responsibility: Transfer data between BUS and UI layers
 * No JPA annotations, only validation annotations
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentDTO {
    
    private Long id;
    
    @NotBlank(message = "First name is required")
    @Size(max = 100, message = "First name must not exceed 100 characters")
    private String firstName;
    
    @NotBlank(message = "Last name is required")
    @Size(max = 100, message = "Last name must not exceed 100 characters")
    private String lastName;
    
    @NotBlank(message = "Student code is required")
    @Size(max = 50, message = "Student code must not exceed 50 characters")
    private String studentCode;
    
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Size(max = 100, message = "Email must not exceed 100 characters")
    private String email;
    
    @Size(max = 20, message = "Phone number must not exceed 20 characters")
    @Pattern(regexp = "^[+]?[0-9]{10,15}$", message = "Invalid phone number format")
    private String phoneNumber;
    
    private LocalDate dateOfBirth;
    
    @NotBlank(message = "Gender is required")
    @Size(max = 10, message = "Gender must not exceed 10 characters")
    private String gender;
    
    @Size(max = 255, message = "Address must not exceed 255 characters")
    private String address;
    
    @NotNull(message = "Status is required")
    private StudentStatus status;
    
    private Integer admissionYear;
    
    private LocalDate createdAt;
    
    private LocalDate updatedAt;
    
    /**
     * Helper method to get full name
     */
    public String getFullName() {
        return firstName + " " + lastName;
    }
    
    /**
     * Student Status Enum
     * Matches the Entity enum but defined here for DTO layer independence
     */
    public enum StudentStatus {
        PENDING,
        APPROVED,
        REJECTED,
        ENROLLED,
        GRADUATED
    }
}
