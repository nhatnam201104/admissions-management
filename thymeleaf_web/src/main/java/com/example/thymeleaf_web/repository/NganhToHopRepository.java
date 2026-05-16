package com.example.thymeleaf_web.repository;

import com.example.thymeleaf_web.model.entity.NganhToHop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NganhToHopRepository extends JpaRepository<NganhToHop, Integer> {

    List<NganhToHop> findByManganhOrderByMatohopAsc(String manganh);
}
