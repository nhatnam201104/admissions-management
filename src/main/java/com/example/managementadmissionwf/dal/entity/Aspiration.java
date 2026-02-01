package com.example.managementadmissionwf.dal.entity;

import lombok.Builder;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Entity
@Table(name = "complex_subject")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Aspiration {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @Column(name = "`index`", nullable = false)
    Integer index;

    @ManyToOne
    @JoinColumn(name = "student_id")
    Students student;

    @ManyToOne
    @JoinColumn(name = "major_id")
    MajorDetail majorDetail;

    Boolean isHire = false;

    LocalDate createdAt = LocalDate.now();
    LocalDate updatedAt = LocalDate.now();


    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDate.now();
    }
}
