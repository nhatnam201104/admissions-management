package com.example.managementadmissionwf.dal.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MajorDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @ManyToOne
    @JoinColumn(name = "major_id")
    Major major;

    @ManyToOne
    @JoinColumn(name = "complex_subject_id")
    ComplexSubject complexSubject;

    @Column(nullable = false)
    Integer totalScore;

    LocalDate createdAt = LocalDate.now();
    LocalDate updatedAt = LocalDate.now();


    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDate.now();
    }
}
