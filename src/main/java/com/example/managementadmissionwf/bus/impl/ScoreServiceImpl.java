package com.example.managementadmissionwf.bus.impl;

import com.example.managementadmissionwf.bus.interfaces.ScoreService;
import com.example.managementadmissionwf.dto.score.ScoreDTO;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service Implementation for Score Management
 * Uses mock data (Java List)
 */
@Service
public class ScoreServiceImpl implements ScoreService {
    
    private final List<ScoreDTO> mockData = new ArrayList<>();
    
    public ScoreServiceImpl() {
        initializeMockData();
    }
    
    private void initializeMockData() {
        // Mock scores for candidates
        mockData.add(ScoreDTO.builder()
                .cccd("001234567890")
                .sobaodanh("BD001001")
                .phuongThuc("THPT")
                .toan(8.5)
                .ly(7.5)
                .hoa(8.0)
                .sinh(null)
                .su(null)
                .dia(null)
                .van(7.0)
                .n1Thi(6.5)
                .n1Cc(null)
                .nl1(null)
                .nk1(null)
                .nk2(null)
                .diemCc(0.0)
                .diemUtxt(0.0)
                .diemTong(0.0)
                .build());
        
        mockData.add(ScoreDTO.builder()
                .cccd("001234567891")
                .sobaodanh("BD001002")
                .phuongThuc("THPT")
                .toan(9.0)
                .ly(null)
                .hoa(null)
                .sinh(8.5)
                .su(null)
                .dia(null)
                .van(8.0)
                .n1Thi(7.5)
                .n1Cc(null)
                .nl1(null)
                .nk1(null)
                .nk2(null)
                .diemCc(0.5)
                .diemUtxt(0.0)
                .diemTong(0.5)
                .build());
        
        mockData.add(ScoreDTO.builder()
                .cccd("001234567892")
                .sobaodanh("BD001003")
                .phuongThuc("DGNL")
                .toan(null)
                .ly(null)
                .hoa(null)
                .sinh(null)
                .su(null)
                .dia(null)
                .van(null)
                .n1Thi(null)
                .n1Cc(7.0)
                .nl1(8.0)
                .nk1(null)
                .nk2(null)
                .diemCc(0.0)
                .diemUtxt(1.0)
                .diemTong(1.0)
                .build());
        
        mockData.add(ScoreDTO.builder()
                .cccd("001234567893")
                .sobaodanh("BD001004")
                .phuongThuc("THPT")
                .toan(7.0)
                .ly(6.5)
                .hoa(7.0)
                .sinh(null)
                .su(null)
                .dia(null)
                .van(6.0)
                .n1Thi(5.5)
                .n1Cc(null)
                .nl1(null)
                .nk1(null)
                .nk2(null)
                .diemCc(0.0)
                .diemUtxt(2.0)
                .diemTong(2.0)
                .build());
        
        mockData.add(ScoreDTO.builder()
                .cccd("001234567894")
                .sobaodanh("BD001005")
                .phuongThuc("VSAT")
                .toan(null)
                .ly(null)
                .hoa(null)
                .sinh(null)
                .su(null)
                .dia(null)
                .van(null)
                .n1Thi(null)
                .n1Cc(null)
                .nl1(null)
                .nk1(7.5)
                .nk2(6.5)
                .diemCc(1.0)
                .diemUtxt(0.0)
                .diemTong(1.0)
                .build());
    }
    
    @Override
    public List<ScoreDTO> getAllScores() {
        return new ArrayList<>(mockData);
    }
    
    @Override
    public ScoreDTO getScoreByCccd(String cccd) {
        return mockData.stream()
                .filter(score -> score.getCccd().equals(cccd))
                .findFirst()
                .orElse(null);
    }
    
    @Override
    public ScoreDTO createScore(ScoreDTO dto) {
        // Check if CCCD already exists
        if (getScoreByCccd(dto.getCccd()) != null) {
            throw new IllegalArgumentException("Điểm của thí sinh đã tồn tại: " + dto.getCccd());
        }
        mockData.add(dto);
        return dto;
    }
    
    @Override
    public ScoreDTO updateScore(ScoreDTO dto) {
        ScoreDTO existing = getScoreByCccd(dto.getCccd());
        if (existing == null) {
            throw new IllegalArgumentException("Không tìm thấy điểm của thí sinh với CCCD: " + dto.getCccd());
        }
        
        // Update fields
        existing.setSobaodanh(dto.getSobaodanh());
        existing.setPhuongThuc(dto.getPhuongThuc());
        existing.setToan(dto.getToan());
        existing.setLy(dto.getLy());
        existing.setHoa(dto.getHoa());
        existing.setSinh(dto.getSinh());
        existing.setSu(dto.getSu());
        existing.setDia(dto.getDia());
        existing.setVan(dto.getVan());
        existing.setN1Thi(dto.getN1Thi());
        existing.setN1Cc(dto.getN1Cc());
        existing.setNl1(dto.getNl1());
        existing.setNk1(dto.getNk1());
        existing.setNk2(dto.getNk2());
        existing.setDiemCc(dto.getDiemCc());
        existing.setDiemUtxt(dto.getDiemUtxt());
        existing.setDiemTong(dto.getDiemTong());
        
        return existing;
    }
    
    @Override
    public void deleteScore(String cccd) {
        mockData.removeIf(score -> score.getCccd().equals(cccd));
    }
    
    @Override
    public Double calculateTotalScore(ScoreDTO dto) {
        // Simple calculation - just sum of all non-null scores
        double total = 0.0;
        
        if (dto.getToan() != null) total += dto.getToan();
        if (dto.getLy() != null) total += dto.getLy();
        if (dto.getHoa() != null) total += dto.getHoa();
        if (dto.getSinh() != null) total += dto.getSinh();
        if (dto.getSu() != null) total += dto.getSu();
        if (dto.getDia() != null) total += dto.getDia();
        if (dto.getVan() != null) total += dto.getVan();
        if (dto.getN1CcCalculated() != null) total += dto.getN1CcCalculated();
        if (dto.getNl1() != null) total += dto.getNl1();
        if (dto.getNk1() != null) total += dto.getNk1();
        if (dto.getNk2() != null) total += dto.getNk2();
        
        // Add bonus points
        if (dto.getDiemCc() != null) total += dto.getDiemCc();
        if (dto.getDiemUtxt() != null) total += dto.getDiemUtxt();
        
        return total;
    }
}