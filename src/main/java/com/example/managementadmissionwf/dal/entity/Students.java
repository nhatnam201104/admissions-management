package com.example.managementadmissionwf.dal.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.List;


@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Students {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne
    @JoinColumn(name = "user_id")
    Users user;

    String address;
    String school;

    Integer score = 0;
    Integer additionScore = 0;
    Integer totalAspiration = 0;
    LocalDate createdAt = LocalDate.now();
    LocalDate updatedAt = LocalDate.now();


    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDate.now();
    }

    @OneToMany(mappedBy = "student")
    private List<StudentSubject> studentSubjects;

    @OneToMany(mappedBy = "student")
    private List<Aspiration> aspirations;


}
