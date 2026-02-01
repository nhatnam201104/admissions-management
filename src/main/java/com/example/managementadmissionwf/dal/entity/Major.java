package com.example.managementadmissionwf.dal.entity;


import jakarta.persistence.*;
import lombok.*;


import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "major")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Major {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;
    private String description;

    private Integer hired = 0;
    private Integer targetQuantity = 0;

    @OneToMany(mappedBy = "major")
    private List<MajorDetail> majorDetails;
    LocalDate createdAt = LocalDate.now();
    LocalDate updatedAt = LocalDate.now();


    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDate.now();
    }
}