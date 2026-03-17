package com.example.managementadmissionwf.bus.interfaces;

import com.example.managementadmissionwf.dto.candidate.CandidateDTO;

import java.util.List;

/**
 * Service Interface for Candidate Management
 */
public interface CandidateService {
    List<CandidateDTO> getAllCandidates();
    
    List<CandidateDTO> searchCandidates(String keyword, String khuVuc, String doiTuong);
    
    CandidateDTO getCandidateByCccd(String cccd);
    
    CandidateDTO createCandidate(CandidateDTO dto);
    
    CandidateDTO updateCandidate(CandidateDTO dto);
    
    void deleteCandidate(String cccd);
}