package com.example.managementadmissionwf.bus.impl;

import com.example.managementadmissionwf.bus.interfaces.AdmissionResultService;
import com.example.managementadmissionwf.dto.admission.AdmissionResultDTO;
import org.springframework.stereotype.Service;
import jakarta.annotation.PostConstruct;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdmissionResultServiceImpl implements AdmissionResultService {

    private List<AdmissionResultDTO> data = new ArrayList<>();

    @PostConstruct
    public void init() {
        data.add(AdmissionResultDTO.builder()
                .id(1).cccd("001234567890").hoTen("Nguyễn Văn An").sobaodanh("SBD001")
                .manganh("7480201").tennganh("Công nghệ thông tin").nvTt(1)
                .diemXettuyen(27.5).ketQua("TRUNG_TUYEN").phuongThuc("THPT")
                .ngayXet(LocalDate.now()).build());

        data.add(AdmissionResultDTO.builder()
                .id(2).cccd("001234567891").hoTen("Trần Thị Bình").sobaodanh("SBD002")
                .manganh("7460101").tennganh("Toán tin").nvTt(2)
                .diemXettuyen(26.0).ketQua("TRUNG_TUYEN").phuongThuc("DGNL")
                .ngayXet(LocalDate.now()).build());

        data.add(AdmissionResultDTO.builder()
                .id(3).cccd("001234567892").hoTen("Lê Minh Cường").sobaodanh("SBD003")
                .manganh("7480103").tennganh("Kỹ thuật phần mềm").nvTt(1)
                .diemXettuyen(21.5).ketQua("TRUOT").phuongThuc("THPT")
                .ngayXet(LocalDate.now()).build());
    }

    @Override
    public List<AdmissionResultDTO> getAllResults() {
        return data;
    }

    @Override
    public List<AdmissionResultDTO> getByResult(String ketQua) {
        return data.stream()
                .filter(r -> r.getKetQua().equals(ketQua))
                .collect(Collectors.toList());
    }

    @Override
    public List<AdmissionResultDTO> getByMajor(String manganh) {
        return data.stream()
                .filter(r -> r.getManganh().equals(manganh) || r.getTennganh().equals(manganh))
                .collect(Collectors.toList());
    }

    @Override
    public List<AdmissionResultDTO> search(String keyword, String ketQua, String manganh) {
        return data.stream()
                // Lọc theo từ khóa
                .filter(r -> keyword == null || keyword.isEmpty()
                        || r.getCccd().contains(keyword)
                        || r.getSobaodanh().contains(keyword)
                        || r.getHoTen().toLowerCase().contains(keyword.toLowerCase()))
                // Lọc theo kết quả
                .filter(r -> ketQua == null || ketQua.equals("Tất cả") || r.getKetQua().equals(ketQua))
                // Lọc theo ngành (có thể chọn theo tên hoặc mã ngành)
                .filter(r -> manganh == null || manganh.equals("Tất cả") 
                        || r.getTennganh().equals(manganh) 
                        || r.getManganh().equals(manganh))
                .collect(Collectors.toList());
    }

    @Override
    public void updateResult(Integer id, String ketQua) {
        data.stream()
                .filter(r -> r.getId().equals(id))
                .findFirst()
                .ifPresent(r -> r.setKetQua(ketQua));
    }

    @Override
    public void exportToExcel(List<AdmissionResultDTO> results) {
        //gọi thư viện Apache POI
        System.out.println("Đang xuất " + results.size() + " dòng ra file Excel...");
    }

    @Override
    public void exportToPDF(List<AdmissionResultDTO> results) {
        //gọi thư viện iText
        System.out.println("Đang xuất " + results.size() + " dòng ra file PDF...");
    }
}