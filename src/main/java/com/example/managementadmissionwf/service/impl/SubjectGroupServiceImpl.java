package com.example.managementadmissionwf.service.impl;

import com.example.managementadmissionwf.dal.entity.XtTohopMonthi;
import com.example.managementadmissionwf.dal.repository.SubjectGroupRepository;
import com.example.managementadmissionwf.dto.common.ImportResult;
import com.example.managementadmissionwf.dto.common.Paging;
import com.example.managementadmissionwf.dto.request.SubjectGroupRequest;
import com.example.managementadmissionwf.dto.response.SubjectGroupResponse;
import com.example.managementadmissionwf.mapper.SubjectGroupMapper;
import com.example.managementadmissionwf.service.SubjectGroupService;
import com.example.managementadmissionwf.util.ExcelUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class SubjectGroupServiceImpl implements SubjectGroupService {

    private final SubjectGroupRepository subjectGroupRepository;
    private final SubjectGroupMapper subjectGroupMapper;

    @Override
    public Paging<SubjectGroupResponse> search(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("id").descending());
        Page<XtTohopMonthi> entityPage;

        if (keyword != null && !keyword.trim().isEmpty()) {
            entityPage = subjectGroupRepository.search(keyword.trim(), pageable);
        } else {
            entityPage = subjectGroupRepository.findAll(pageable);
        }

        List<SubjectGroupResponse> responses = subjectGroupMapper.toResponseList(entityPage.getContent());

        return Paging.<SubjectGroupResponse>builder()
                .data(responses)
                .totalItems(entityPage.getTotalElements())
                .totalPages(entityPage.getTotalPages())
                .page(entityPage.getNumber() + 1)
                .limit(entityPage.getSize())
                .hasNext(entityPage.hasNext())
                .build();
    }

    @Override
    public SubjectGroupResponse findById(Integer id) {
        XtTohopMonthi entity = subjectGroupRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tổ hợp môn với ID: " + id));
        return subjectGroupMapper.toResponse(entity);
    }

    @Override
    @Transactional
    public SubjectGroupResponse create(SubjectGroupRequest request) {
        if (subjectGroupRepository.existsByMatohop(request.getMatohop())) {
            throw new RuntimeException("Mã tổ hợp đã tồn tại: " + request.getMatohop());
        }

        XtTohopMonthi entity = subjectGroupMapper.toEntity(request);
        entity = subjectGroupRepository.save(entity);
        return subjectGroupMapper.toResponse(entity);
    }

    @Override
    @Transactional
    public SubjectGroupResponse update(Integer id, SubjectGroupRequest request) {
        XtTohopMonthi entity = subjectGroupRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tổ hợp môn với ID: " + id));

        if (subjectGroupRepository.existsByMatohopAndIdNot(request.getMatohop(), id)) {
            throw new RuntimeException("Mã tổ hợp đã tồn tại: " + request.getMatohop());
        }

        subjectGroupMapper.updateEntity(entity, request);
        entity = subjectGroupRepository.save(entity);
        return subjectGroupMapper.toResponse(entity);
    }

    @Override
    @Transactional
    public void delete(Integer id) {
        XtTohopMonthi entity = subjectGroupRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tổ hợp môn với ID: " + id));

        entity.setIsDeleted(true);
        subjectGroupRepository.save(entity);
    }

    @Override
    public void exportExcel(OutputStream outputStream, String keyword) {
        try {
            List<XtTohopMonthi> entities;
            if (keyword != null && !keyword.trim().isEmpty()) {
                entities = subjectGroupRepository.search(keyword.trim(), Pageable.unpaged()).getContent();
            } else {
                entities = subjectGroupRepository.findAll();
            }

            List<SubjectGroupResponse> data = subjectGroupMapper.toResponseList(entities);
            ExcelUtil.exportExcel(data, SubjectGroupResponse.class, outputStream);
        } catch (Exception e) {
            log.error("Error exporting excel", e);
            throw new RuntimeException("Lỗi khi xuất file Excel: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public ImportResult<SubjectGroupResponse> importExcel(InputStream inputStream) {
        ImportResult<SubjectGroupResponse> result = new ImportResult<>();
        List<String> errors = new ArrayList<>();
        List<SubjectGroupResponse> validData = new ArrayList<>();

        try {
            List<SubjectGroupResponse> importedData = ExcelUtil.importExcel(inputStream, SubjectGroupResponse.class);
            result.setTotalRows(importedData.size());

            int rowIndex = 2; // Assuming row 1 is header
            for (SubjectGroupResponse response : importedData) {
                try {
                    // Validation
                    if (response.getMatohop() == null || response.getMatohop().trim().isEmpty()) {
                        errors.add("Dòng " + rowIndex + ": Mã tổ hợp không được để trống");
                        rowIndex++;
                        continue;
                    }
                    if (response.getTentohop() == null || response.getTentohop().trim().isEmpty()) {
                        errors.add("Dòng " + rowIndex + ": Tên tổ hợp không được để trống");
                        rowIndex++;
                        continue;
                    }
                    if (response.getMon1() == null || response.getMon2() == null || response.getMon3() == null) {
                        errors.add("Dòng " + rowIndex + ": Các môn học không được để trống");
                        rowIndex++;
                        continue;
                    }

                    // Processing
                    Optional<XtTohopMonthi> existingOpt = subjectGroupRepository.findByMatohop(response.getMatohop());
                    XtTohopMonthi entity;

                    if (existingOpt.isPresent()) {
                        // Update
                        entity = existingOpt.get();
                        entity.setMon1(response.getMon1());
                        entity.setMon2(response.getMon2());
                        entity.setMon3(response.getMon3());
                        entity.setTentohop(response.getTentohop());
                    } else {
                        // Create
                        entity = subjectGroupMapper.toEntityFromResponse(response);
                    }

                    subjectGroupRepository.save(entity);
                    validData.add(response);

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