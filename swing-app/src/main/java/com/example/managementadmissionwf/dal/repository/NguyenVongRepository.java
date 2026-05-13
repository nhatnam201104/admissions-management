package com.example.managementadmissionwf.dal.repository;

import com.example.managementadmissionwf.dal.entity.XtNguyenvongxettuyen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NguyenVongRepository extends JpaRepository<XtNguyenvongxettuyen, Integer> {

    List<XtNguyenvongxettuyen> findByNnCccd(String cccd);
    
    List<XtNguyenvongxettuyen> findByNvKetqua(String nvKetqua);
}
