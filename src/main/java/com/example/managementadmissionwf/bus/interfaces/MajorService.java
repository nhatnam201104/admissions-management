package com.example.managementadmissionwf.bus.interfaces;

import java.util.List;

import com.example.managementadmissionwf.dto.MajorDTO;

public interface MajorService {
    
    List<MajorDTO> getAllMajors();
    MajorDTO getMajorByCode(String maNganh);

    List<MajorDTO> searchMajors(String keyword);
    MajorDTO createMajor(MajorDTO dto);

    MajorDTO updateMajor(MajorDTO dto);
    
    void deleteMajor(Integer id);

    void addSubjectGroup(String maNganh, String maToHop);

    void removeSubjectGroup(Integer id);
}
