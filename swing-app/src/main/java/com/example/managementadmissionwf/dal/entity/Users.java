package com.example.managementadmissionwf.dal.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SQLRestriction("is_deleted = false")
public class Users {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;
    @Column(nullable = false)
    String fullname;
    @Column(unique = true, nullable = false)
    String email;
    @Column(nullable = false, unique = true)
    String username;
    @Column(nullable = false)
    String password;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    RoleUser role;

    @Column(nullable = false)
    @Builder.Default
    Boolean isDeleted = false;

    @Builder.Default
    LocalDate createdAt = LocalDate.now();
    @Builder.Default
    LocalDate updatedAt = LocalDate.now();

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDate.now();
        updatedAt = LocalDate.now();
    }
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDate.now();
    }

}
