package com.example.managementadmissionwf.bus.interfaces;

import com.example.managementadmissionwf.dto.admission.AdmissionResultDTO;
import java.util.List;
import java.util.Map;

public interface AdmissionResultService {
    List<AdmissionResultDTO> getAllResults();
    List<AdmissionResultDTO> getByResult(String ketQua);
    List<AdmissionResultDTO> getByMajor(String manganh);
    List<AdmissionResultDTO> search(String keyword, String ketQua, String manganh, String phuongThuc);
    void updateResult(Integer id, String ketQua);
    void exportToExcel(List<AdmissionResultDTO> results);
    void exportToPDF(List<AdmissionResultDTO> results);
    
    // Chi tiết điểm
    String getManganhById(Integer id);
    /**
     * Lấy chi tiết điểm theo ID nguyện vọng
     * @param id ID của nguyện vọng (XtNguyenvongxettuyen.id)
     * @return Map chứa thông tin chi tiết điểm
     */
    Map<String, Object> getScoreDetails(Integer id);
    
    // Xét tuyển tự động
    int handleAutomaticAdmission();
}
