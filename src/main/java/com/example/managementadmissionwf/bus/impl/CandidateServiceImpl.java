package com.example.managementadmissionwf.bus.impl;

import com.example.managementadmissionwf.bus.interfaces.CandidateService;
import com.example.managementadmissionwf.dal.entity.XtThisinhxettuyen25;
import com.example.managementadmissionwf.dal.repository.CandidateRepository;
import com.example.managementadmissionwf.dto.candidate.CandidateDTO;
import com.example.managementadmissionwf.dto.common.ImportResult;
import com.example.managementadmissionwf.dto.common.Paging;
import com.example.managementadmissionwf.mapper.CandidateMapper;
import com.example.managementadmissionwf.util.ExcelUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
/**
 * Service Implementation for Candidate Management
 * Uses mock data (Java List)
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Validated
public class CandidateServiceImpl implements CandidateService {
    
	private final CandidateRepository candidateRepository;
    private final CandidateMapper candidateMapper;
    
    @Override
    public Paging<CandidateDTO> searchCandidates(String keyword, String khuVuc, String doiTuong, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("id").descending());
        
        String kw = (keyword != null && !keyword.trim().isEmpty()) ? keyword.trim() : null;
        String kv = (khuVuc != null && !khuVuc.trim().isEmpty() && !khuVuc.equals("Tất cả")) ? khuVuc.trim() : null;
        String dt = (doiTuong != null && !doiTuong.trim().isEmpty() && !doiTuong.equals("Tất cả")) ? doiTuong.trim() : null;

        Page<XtThisinhxettuyen25> entityPage = candidateRepository.search(kw, kv, dt, pageable);
        List<CandidateDTO> dtoList = candidateMapper.toDTOList(entityPage.getContent());
        
        return Paging.<CandidateDTO>builder()
                .data(dtoList)
                .totalItems(entityPage.getTotalElements())
                .totalPages(entityPage.getTotalPages())
                .page(entityPage.getNumber() + 1)
                .limit(entityPage.getSize())
                .hasNext(entityPage.hasNext())
                .build();
    }
    
    @Override
    public CandidateDTO getCandidateByCccd(String cccd) {
        return candidateRepository.findByCccd(cccd)
                .map(candidateMapper::toDTO)
                .orElse(null);
    }
    
    @Override
    @Transactional
    public CandidateDTO createCandidate(CandidateDTO dto) {
    	Optional<XtThisinhxettuyen25> existingOpt = candidateRepository.findByCccdIncludingDeleted(dto.getCccd());
    	XtThisinhxettuyen25 entity;
    	
    	if (existingOpt.isPresent()) {
            entity = existingOpt.get();
            if (!entity.getIsDeleted()) {
                throw new RuntimeException("CCCD đã tồn tại: " + dto.getCccd());
            }
            
            candidateMapper.updateEntity(entity, dto);
            entity.setIsDeleted(false);
        } else {     
            entity = candidateMapper.toEntity(dto);
        }
    	
    	entity.setHoVaTen(dto.getHo() + " " + dto.getTen());
        entity = candidateRepository.save(entity);
        return candidateMapper.toDTO(entity);
    }
    
    @Override
    @Transactional
    public CandidateDTO updateCandidate(CandidateDTO dto) {
        XtThisinhxettuyen25 entity = candidateRepository.findByCccd(dto.getCccd())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thí sinh với CCCD: " + dto.getCccd()));
        
        candidateMapper.updateEntity(entity, dto);
        entity.setHoVaTen(dto.getHo() + " " + dto.getTen());
        
        entity = candidateRepository.save(entity);
        return candidateMapper.toDTO(entity);
    }
    
    @Override
    @Transactional
    public void deleteCandidate(String cccd) {
        if (!candidateRepository.existsByCccd(cccd)) {
            throw new RuntimeException("Không tìm thấy thí sinh với CCCD: " + cccd);
        }
        candidateRepository.softDeleteByCccd(cccd);
    }

    @Override
    public void exportExcel(OutputStream outputStream, String keyword) {
        try {
            String kw = (keyword != null && !keyword.trim().isEmpty()) ? keyword.trim() : null;
            
            List<XtThisinhxettuyen25> entities = candidateRepository.search(kw, null, null, Pageable.unpaged()).getContent();
            List<CandidateDTO> data = candidateMapper.toDTOList(entities);
            ExcelUtil.exportExcel(data, CandidateDTO.class, outputStream);
        } catch (Exception e) {
            log.error("Error exporting excel", e);
            throw new RuntimeException("Lỗi khi xuất file Excel: " + e.getMessage());
        }
    }

    @Override
    public ImportResult<CandidateDTO> importExcel(InputStream inputStream) {
        ImportResult<CandidateDTO> result = new ImportResult<>();
        List<String> errors = new ArrayList<>();
        List<CandidateDTO> validData = new ArrayList<>();

        try {
            List<CandidateDTO> importedData = ExcelUtil.importExcel(inputStream, CandidateDTO.class);
            result.setTotalRows(importedData.size());

            int rowIndex = 2;
            for (CandidateDTO dto : importedData) {
                try {
                    if (dto.getCccd() == null || dto.getCccd().trim().isEmpty()) {
                        errors.add("Dòng " + rowIndex + ": CCCD không được để trống");
                        rowIndex++;
                        continue;
                    }
                    if (dto.getHo() == null || dto.getTen() == null) {
                        errors.add("Dòng " + rowIndex + ": Họ và tên không được để trống");
                        rowIndex++;
                        continue;
                    }

                    Optional<XtThisinhxettuyen25> existingOpt = candidateRepository.findByCccdIncludingDeleted(dto.getCccd());
                    XtThisinhxettuyen25 entity;

                    if (existingOpt.isPresent()) {
                        entity = existingOpt.get();  
                        candidateMapper.updateEntity(entity, dto);
                        entity.setIsDeleted(false); 
                    } else {
                        entity = candidateMapper.toEntity(dto);
                    }
                    
                    entity.setHoVaTen(dto.getHo() + " " + dto.getTen());
                    candidateRepository.save(entity);
                    validData.add(dto);

                } catch (Exception e) {
                    errors.add("Dòng " + rowIndex + ": Lỗi xử lý - " + e.getMessage());
                }
                rowIndex++;
            }

            result.setSuccessCount(validData.size());
            result.setErrorCount(errors.size());
            result.setErrors(errors);
            result.setValidData(validData);

        } catch (Exception e) {
            log.error("Error importing excel", e);
            throw new RuntimeException("Lỗi khi đọc file Excel: " + e.getMessage());
        }

        return result;
    }
}