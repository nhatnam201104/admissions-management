package com.example.managementadmissionwf.bus.interfaces;

import com.example.managementadmissionwf.dto.admission.AdmissionResultDTO;
import java.util.List;

public interface AdmissionResultService {
    List<AdmissionResultDTO> getAllResults();
    List<AdmissionResultDTO> getByResult(String ketQua);
    List<AdmissionResultDTO> getByMajor(String manganh);
    List<AdmissionResultDTO> search(String keyword, String ketQua, String manganh);
    void updateResult(Integer id, String ketQua);
    void exportToExcel(List<AdmissionResultDTO> results);
    void exportToPDF(List<AdmissionResultDTO> results);
}