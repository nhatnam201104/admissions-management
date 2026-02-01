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
public class ComplexSubject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    String name; // A00, A01...

    @ManyToOne
    @JoinColumn(name = "subject_id_1")
    Subject subject1;

    @ManyToOne
    @JoinColumn(name = "subject_id_2")
    Subject subject2;

    @ManyToOne
    @JoinColumn(name = "subject_id_3")
    Subject subject3;

    LocalDate createdAt = LocalDate.now();
    LocalDate updatedAt = LocalDate.now();


    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDate.now();
    }
}
