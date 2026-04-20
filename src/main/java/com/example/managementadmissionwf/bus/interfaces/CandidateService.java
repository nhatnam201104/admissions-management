package com.example.managementadmissionwf.bus.interfaces;

import com.example.managementadmissionwf.dto.candidate.CandidateDTO;
import com.example.managementadmissionwf.dto.common.ImportResult;
import com.example.managementadmissionwf.dto.common.Paging;

import jakarta.validation.Valid;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

/**
 * Service Interface for Candidate Management
 */
public interface CandidateService {
	Paging<CandidateDTO> searchCandidates(String keyword, String khuVuc, String doiTuong, int page, int size);
    
    CandidateDTO getCandidateByCccd(String cccd);
    
    CandidateDTO createCandidate(@Valid CandidateDTO dto);
    
    CandidateDTO updateCandidate(@Valid CandidateDTO dto);
    
    void deleteCandidate(String cccd);

    void exportExcel(OutputStream outputStream, String keyword);

    ImportResult<CandidateDTO> importExcel(InputStream inputStream);
    
    boolean existsByCccdIncludingDeleted(String cccd);
}