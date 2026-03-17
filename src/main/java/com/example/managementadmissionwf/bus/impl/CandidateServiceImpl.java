package com.example.managementadmissionwf.bus.impl;

import com.example.managementadmissionwf.bus.interfaces.CandidateService;
import com.example.managementadmissionwf.dto.candidate.CandidateDTO;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service Implementation for Candidate Management
 * Uses mock data (Java List)
 */
@Service
public class CandidateServiceImpl implements CandidateService {
    
    private final List<CandidateDTO> mockData = new ArrayList<>();
    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    
    public CandidateServiceImpl() {
        initializeMockData();
    }
    
    private void initializeMockData() {
        try {
            mockData.add(CandidateDTO.builder()
                    .cccd("001234567890")
                    .sobaodanh("BD001001")
                    .ho("Nguyễn")
                    .ten("Văn An")
                    .ngaySinh(sdf.parse("2005-05-15"))
                    .dienThoai("0912345678")
                    .email("an.nguyen@example.com")
                    .gioiTinh("Nam")
                    .noiSinh("Hà Nội")
                    .doiTuong("Không")
                    .khuVuc("KV1")
                    .build());
            
            mockData.add(CandidateDTO.builder()
                    .cccd("001234567891")
                    .sobaodanh("BD001002")
                    .ho("Trần")
                    .ten("Thị Bình")
                    .ngaySinh(sdf.parse("2005-08-20"))
                    .dienThoai("0923456789")
                    .email("binh.tran@example.com")
                    .gioiTinh("Nữ")
                    .noiSinh("TP Hồ Chí Minh")
                    .doiTuong("KV1")
                    .khuVuc("KV1")
                    .build());
            
            mockData.add(CandidateDTO.builder()
                    .cccd("001234567892")
                    .sobaodanh("BD001003")
                    .ho("Lê")
                    .ten("Minh Cường")
                    .ngaySinh(sdf.parse("2005-03-10"))
                    .dienThoai("0934567890")
                    .email("cuong.le@example.com")
                    .gioiTinh("Nam")
                    .noiSinh("Đà Nẵng")
                    .doiTuong("KV2-NT")
                    .khuVuc("KV2")
                    .build());
            
            mockData.add(CandidateDTO.builder()
                    .cccd("001234567893")
                    .sobaodanh("BD001004")
                    .ho("Phạm")
                    .ten("Thảo Dung")
                    .ngaySinh(sdf.parse("2005-11-25"))
                    .dienThoai("0945678901")
                    .email("dung.pham@example.com")
                    .gioiTinh("Nữ")
                    .noiSinh("Hải Phòng")
                    .doiTuong("Con thương binh")
                    .khuVuc("KV1")
                    .build());
            
            mockData.add(CandidateDTO.builder()
                    .cccd("001234567894")
                    .sobaodanh("BD001005")
                    .ho("Hoàng")
                    .ten("Văn Em")
                    .ngaySinh(sdf.parse("2005-07-05"))
                    .dienThoai("0956789012")
                    .email("em.hoang@example.com")
                    .gioiTinh("Nam")
                    .noiSinh("Cần Thơ")
                    .doiTuong("Không")
                    .khuVuc("KV3")
                    .build());
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public List<CandidateDTO> getAllCandidates() {
        return new ArrayList<>(mockData);
    }
    
    @Override
    public List<CandidateDTO> searchCandidates(String keyword, String khuVuc, String doiTuong) {
        return mockData.stream()
                .filter(candidate -> {
                    boolean matches = true;
                    
                    if (keyword != null && !keyword.isEmpty()) {
                        String searchTerm = keyword.toLowerCase();
                        matches = matches && (
                            candidate.getCccd().toLowerCase().contains(searchTerm) ||
                            candidate.getSobaodanh().toLowerCase().contains(searchTerm) ||
                            candidate.getHoTen().toLowerCase().contains(searchTerm)
                        );
                    }
                    
                    if (khuVuc != null && !khuVuc.equals("Tất cả")) {
                        matches = matches && khuVuc.equals(candidate.getKhuVuc());
                    }
                    
                    if (doiTuong != null && !doiTuong.equals("Tất cả")) {
                        matches = matches && doiTuong.equals(candidate.getDoiTuong());
                    }
                    
                    return matches;
                })
                .collect(Collectors.toList());
    }
    
    @Override
    public CandidateDTO getCandidateByCccd(String cccd) {
        return mockData.stream()
                .filter(candidate -> candidate.getCccd().equals(cccd))
                .findFirst()
                .orElse(null);
    }
    
    @Override
    public CandidateDTO createCandidate(CandidateDTO dto) {
        // Check if CCCD already exists
        if (getCandidateByCccd(dto.getCccd()) != null) {
            throw new IllegalArgumentException("CCCD đã tồn tại: " + dto.getCccd());
        }
        mockData.add(dto);
        return dto;
    }
    
    @Override
    public CandidateDTO updateCandidate(CandidateDTO dto) {
        CandidateDTO existing = getCandidateByCccd(dto.getCccd());
        if (existing == null) {
            throw new IllegalArgumentException("Không tìm thấy thí sinh với CCCD: " + dto.getCccd());
        }
        
        // Update fields
        existing.setSobaodanh(dto.getSobaodanh());
        existing.setHo(dto.getHo());
        existing.setTen(dto.getTen());
        existing.setNgaySinh(dto.getNgaySinh());
        existing.setDienThoai(dto.getDienThoai());
        existing.setEmail(dto.getEmail());
        existing.setGioiTinh(dto.getGioiTinh());
        existing.setNoiSinh(dto.getNoiSinh());
        existing.setDoiTuong(dto.getDoiTuong());
        existing.setKhuVuc(dto.getKhuVuc());
        
        return existing;
    }
    
    @Override
    public void deleteCandidate(String cccd) {
        mockData.removeIf(candidate -> candidate.getCccd().equals(cccd));
    }
}