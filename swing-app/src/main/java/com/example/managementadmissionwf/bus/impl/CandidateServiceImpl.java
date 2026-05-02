package com.example.managementadmissionwf.bus.impl;

import com.example.managementadmissionwf.bus.interfaces.CandidateService;
import com.example.managementadmissionwf.dal.entity.XtThisinhxettuyen25;
import com.example.managementadmissionwf.dal.repository.CandidateRepository;
import com.example.managementadmissionwf.dto.candidate.CandidateDTO;
import com.example.managementadmissionwf.dto.common.ImportResult;
import com.example.managementadmissionwf.dto.common.Paging;
import com.example.managementadmissionwf.mapper.CandidateMapper;
import com.example.managementadmissionwf.util.ExcelUtil;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
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
import java.util.Set;
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
    private final Validator validator;
    
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
        normalizeAndValidate(dto);
    	
        Optional<XtThisinhxettuyen25> sbdCheck = candidateRepository.findBySobaodanhIncludingDeleted(dto.getSobaodanh());
        if (sbdCheck.isPresent()) {
            XtThisinhxettuyen25 duplicateEntity = sbdCheck.get();
            if (!duplicateEntity.getCccd().equals(dto.getCccd())) { 
                if (duplicateEntity.getIsDeleted()) {
                    throw new RuntimeException("Số báo danh này thuộc về một thí sinh đã bị xóa. Vui lòng sử dụng SBD khác!");
                } else {
                    throw new RuntimeException("Số báo danh này đã được sử dụng cho một thí sinh khác!");
                }
            }
        }
        
        if (dto.getEmail() != null) {
            Optional<XtThisinhxettuyen25> emailCheck = candidateRepository.findByEmailIncludingDeleted(dto.getEmail());
            if (emailCheck.isPresent()) {
                XtThisinhxettuyen25 dup = emailCheck.get();
                if (!dup.getCccd().equals(dto.getCccd())) {
                    if (dup.getIsDeleted() != null && dup.getIsDeleted()) {
                        throw new RuntimeException("Địa chỉ Email này thuộc về một thí sinh đã bị xóa!");
                    } else {
                        throw new RuntimeException("Địa chỉ Email này đã được đăng ký cho người khác!");
                    }
                }
            }
        }

        if (dto.getDienThoai() != null) {
            Optional<XtThisinhxettuyen25> phoneCheck = candidateRepository.findByDienThoaiIncludingDeleted(dto.getDienThoai());
            if (phoneCheck.isPresent()) {
                XtThisinhxettuyen25 dup = phoneCheck.get();
                if (!dup.getCccd().equals(dto.getCccd())) {
                    if (dup.getIsDeleted() != null && dup.getIsDeleted()) {
                        throw new RuntimeException("Số điện thoại này thuộc về một thí sinh đã bị xóa!");
                    } else {
                        throw new RuntimeException("Số điện thoại này đã tồn tại trong hệ thống!");
                    }
                }
            }
        }
    	Optional<XtThisinhxettuyen25> existingOpt = candidateRepository.findByCccdIncludingDeleted(dto.getCccd());
    	XtThisinhxettuyen25 entity;
    	
    	if (existingOpt.isPresent()) {
            entity = existingOpt.get();
            if (entity.getIsDeleted() == null || entity.getIsDeleted() == false) {
                throw new RuntimeException("CCCD này đã tồn tại trong hệ thống: " + dto.getCccd());
            }
            candidateRepository.restoreSoftDeleteByCccd(entity.getCccd());
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
        normalizeAndValidate(dto);

        XtThisinhxettuyen25 entity = candidateRepository.findByCccd(dto.getCccd())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thí sinh với CCCD: " + dto.getCccd()));
          
        if (dto.getSobaodanh() != null && !dto.getSobaodanh().equals(entity.getSobaodanh())) {
            Optional<XtThisinhxettuyen25> sbdCheck = candidateRepository.findBySobaodanhIncludingDeleted(dto.getSobaodanh());
            if (sbdCheck.isPresent()) {
                XtThisinhxettuyen25 duplicateEntity = sbdCheck.get();
                if (duplicateEntity.getIsDeleted()) {
                    throw new RuntimeException("Số báo danh này thuộc về một thí sinh đã bị xóa. Vui lòng sử dụng SBD khác!");
                } else {
                    throw new RuntimeException("Số báo danh này đã được sử dụng cho một thí sinh khác!");
                }
            }
        }
        
        if (dto.getEmail() != null && !dto.getEmail().equals(entity.getEmail())) {
            Optional<XtThisinhxettuyen25> emailCheck = candidateRepository.findByEmailIncludingDeleted(dto.getEmail());
            if (emailCheck.isPresent()) {
                XtThisinhxettuyen25 dup = emailCheck.get();
                if (dup.getIsDeleted() != null && dup.getIsDeleted()) {
                    throw new RuntimeException("Địa chỉ Email này thuộc về một thí sinh đã bị xóa!");
                } else {
                    throw new RuntimeException("Địa chỉ Email này đã được đăng ký cho người khác!");
                }
            }
        }
        
        if (dto.getDienThoai() != null && !dto.getDienThoai().equals(entity.getDienThoai())) {
            Optional<XtThisinhxettuyen25> phoneCheck = candidateRepository.findByDienThoaiIncludingDeleted(dto.getDienThoai());
            if (phoneCheck.isPresent()) {
                XtThisinhxettuyen25 dup = phoneCheck.get();
                if (dup.getIsDeleted() != null && dup.getIsDeleted()) {
                    throw new RuntimeException("Số điện thoại này thuộc về một thí sinh đã bị xóa!");
                } else {
                    throw new RuntimeException("Số điện thoại này đã tồn tại trong hệ thống!");
                }
            }
        }

        candidateMapper.updateEntity(entity, dto);
        
        entity.setEmail(dto.getEmail());
        entity.setDienThoai(dto.getDienThoai());
        entity.setHoVaTen(dto.getHo() + " " + dto.getTen());
        
        entity = candidateRepository.save(entity);
        return candidateMapper.toDTO(entity);
    }
    
    @Override
    @Transactional
    public void deleteCandidate(String cccd) {
        String cleanCccd = normalizeCccd(cccd);

        if (!candidateRepository.existsByCccd(cleanCccd)) {
            throw new RuntimeException("Không tìm thấy thí sinh với CCCD: " + cleanCccd);
        }
        candidateRepository.softDeleteScoresByCccd(cleanCccd);
        candidateRepository.softDeleteBonusScoresByCccd(cleanCccd);
        candidateRepository.softDeleteAspirationsByCccd(cleanCccd);
        candidateRepository.softDeleteByCccd(cleanCccd);
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
    
    @Transactional
    @Override
    public ImportResult<CandidateDTO> importExcel(InputStream inputStream) {
        ImportResult<CandidateDTO> result = new ImportResult<>();
        List<String> errors = new ArrayList<>();
        List<CandidateDTO> validData = new ArrayList<>();

        try {
            List<CandidateDTO> importedData = ExcelUtil.importExcel(inputStream, CandidateDTO.class);
            int rowIndex = 2;
            for (CandidateDTO dto : importedData) {
                try {
                    if (dto.getCccd() == null || dto.getCccd().trim().isEmpty()) {
                        errors.add("Dòng " + rowIndex + ": CCCD trống");
                        rowIndex++; continue;
                    }

                    this.createCandidate(dto);
                    validData.add(dto);

                } catch (Exception e) {
                    errors.add("Dòng " + rowIndex + ": " + e.getMessage());
                }
                rowIndex++;
            }

            result.setSuccessCount(validData.size());
            result.setErrorCount(errors.size());
            result.setErrors(errors);
            result.setValidData(validData);
            result.setTotalRows(importedData.size());
            
        } catch (Exception e) {
            log.error("Error importing excel", e);
            throw new RuntimeException("Lỗi khi đọc file Excel: " + e.getMessage());
        }

        return result;
    }
    
    @Override
    public boolean existsByCccdIncludingDeleted(String cccd) {
        return candidateRepository.findByCccdIncludingDeleted(normalizeCccd(cccd)).isPresent();
    }

    private void normalizeAndValidate(CandidateDTO dto) {
        if (dto == null) {
            throw new RuntimeException("Candidate data must not be null");
        }

        dto.setCccd(trimToNull(dto.getCccd()));
        dto.setSobaodanh(trimToNull(dto.getSobaodanh()));
        dto.setHo(trimToNull(dto.getHo()));
        dto.setTen(trimToNull(dto.getTen()));
        dto.setDienThoai(trimToNull(dto.getDienThoai()));
        dto.setEmail(trimToNull(dto.getEmail()));
        dto.setGioiTinh(trimToNull(dto.getGioiTinh()));
        dto.setNoiSinh(trimToNull(dto.getNoiSinh()));
        dto.setDoiTuong(trimToNull(dto.getDoiTuong()));
        dto.setKhuVuc(trimToNull(dto.getKhuVuc()));

        Set<ConstraintViolation<CandidateDTO>> violations = validator.validate(dto);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
    }

    private String normalizeCccd(String cccd) {
        String cleanCccd = trimToNull(cccd);
        if (cleanCccd == null) {
            throw new RuntimeException("CCCD must not be blank");
        }
        return cleanCccd;
    }

    private String trimToNull(String value) {
        if (value == null) {
            return null;
        }

        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
