package com.example.managementadmissionwf.bus.impl;

import com.example.managementadmissionwf.bus.interfaces.MajorService;
import com.example.managementadmissionwf.dal.entity.XtNganh;
import com.example.managementadmissionwf.dal.entity.XtNganhTohop;
import com.example.managementadmissionwf.dal.repository.MajorRepository;
import com.example.managementadmissionwf.dal.repository.NganhTohopRepository;
import com.example.managementadmissionwf.dto.common.ImportResult;
import com.example.managementadmissionwf.dto.common.Paging;
import com.example.managementadmissionwf.dto.major.MajorDTO;
import com.example.managementadmissionwf.dto.major.MajorTohopDTO;
import com.example.managementadmissionwf.mapper.MajorMapper;
import com.example.managementadmissionwf.mapper.NganhTohopMapper;
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
public class MajorServiceImpl implements MajorService {

    private final MajorRepository majorRepository;
    private final NganhTohopRepository nganhTohopRepository;
    private final MajorMapper majorMapper;
    private final NganhTohopMapper nganhTohopMapper;

    @Override
    public Paging<MajorDTO> search(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("idnganh").descending());
        Page<XtNganh> entityPage;

        if (keyword != null && !keyword.trim().isEmpty()) {
            entityPage = majorRepository.search(keyword.trim(), pageable);
        } else {
            entityPage = majorRepository.findAll(pageable);
        }

        List<MajorDTO> responses = majorMapper.toResponseList(entityPage.getContent());

        return Paging.<MajorDTO>builder()
                .data(responses)
                .totalItems(entityPage.getTotalElements())
                .totalPages(entityPage.getTotalPages())
                .page(entityPage.getNumber() + 1)
                .limit(entityPage.getSize())
                .hasNext(entityPage.hasNext())
                .build();
    }

    @Override
    public MajorDTO getByMaNganh(String maNganh) {
        XtNganh entity = majorRepository.findByManganhAndIsDeletedFalse(maNganh)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy ngành với mã: " + maNganh));
        return majorMapper.toResponse(entity);
    }

    @Override
    @Transactional
    public MajorDTO create(MajorDTO dto) {
        if (majorRepository.existsByManganhAndIsDeletedFalse(dto.getMaNganh())) {
            throw new RuntimeException("Mã ngành đã tồn tại: " + dto.getMaNganh());
        }
        XtNganh entity = majorMapper.toEntity(dto);
        entity = majorRepository.save(entity);
        return majorMapper.toResponse(entity);
    }

    @Override
    @Transactional
    public MajorDTO update(String maNganh, MajorDTO dto) {
        XtNganh entity = majorRepository.findByManganhAndIsDeletedFalse(maNganh)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy ngành với mã: " + maNganh));

        if (majorRepository.existsByManganhAndIdnganhNotAndIsDeletedFalse(dto.getMaNganh(), entity.getIdnganh())) {
            throw new RuntimeException("Mã ngành đã tồn tại: " + dto.getMaNganh());
        }

        majorMapper.updateEntity(entity, dto);
        entity = majorRepository.save(entity);
        return majorMapper.toResponse(entity);
    }

    @Override
    @Transactional
    public void delete(String maNganh) {
        XtNganh entity = majorRepository.findByManganhAndIsDeletedFalse(maNganh)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy ngành với mã: " + maNganh));
        entity.setIsDeleted(true);
        majorRepository.save(entity);
    }

    @Override
    public Paging<MajorTohopDTO> getTohopByMaNganh(String maNganh, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("id").descending());
        Page<XtNganhTohop> entityPage = nganhTohopRepository.findByManganh(maNganh, pageable);
        List<MajorTohopDTO> responses = nganhTohopMapper.toResponseList(entityPage.getContent());

        return Paging.<MajorTohopDTO>builder()
                .data(responses)
                .totalItems(entityPage.getTotalElements())
                .totalPages(entityPage.getTotalPages())
                .page(entityPage.getNumber() + 1)
                .limit(entityPage.getSize())
                .hasNext(entityPage.hasNext())
                .build();
    }

    @Override
    @Transactional
    public MajorTohopDTO addTohop(MajorTohopDTO tohopDTO) {
        // VALIDATE DUPLICATE
        if (nganhTohopRepository.existsByManganhAndMatohopAndIsDeletedFalse(
                tohopDTO.getMaNganh(), tohopDTO.getMaToHop())) {
            throw new RuntimeException("Tổ hợp " + tohopDTO.getMaToHop() +
                    " đã tồn tại cho ngành " + tohopDTO.getMaNganh());
        }

        XtNganhTohop entity = nganhTohopMapper.toEntity(tohopDTO);
        entity = nganhTohopRepository.save(entity);
        return nganhTohopMapper.toResponse(entity);
    }

    @Override
    @Transactional
    public void removeTohop(Integer tohopId) {
        XtNganhTohop entity = nganhTohopRepository.findById(tohopId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy mapping tổ hợp với ID: " + tohopId));

        // SOFT DELETE thay vì physical delete
        entity.setIsDeleted(true);
        nganhTohopRepository.save(entity);
    }

    @Override
    public void exportExcel(OutputStream outputStream, String keyword) {
        try {
            Pageable unpaged = Pageable.unpaged();
            Page<XtNganh> page;
            if (keyword != null && !keyword.trim().isEmpty()) {
                page = majorRepository.search(keyword.trim(), unpaged);
            } else {
                page = majorRepository.findAll(unpaged);
            }
            List<MajorDTO> data = majorMapper.toResponseList(page.getContent());
            ExcelUtil.exportExcel(data, MajorDTO.class, outputStream);
        } catch (Exception e) {
            throw new RuntimeException("Không thể xuất file Excel: " + e.getMessage(), e);
        }
    }

    @Override
    // KHÔNG dùng @Transactional ở đây để hỗ trợ partial success
    public ImportResult<MajorDTO> importExcel(InputStream inputStream) {
        ImportResult<MajorDTO> result = new ImportResult<>();
        List<String> errors = new ArrayList<>();
        List<MajorDTO> validData = new ArrayList<>();

        try {
            List<MajorDTO> importedList = ExcelUtil.importExcel(inputStream, MajorDTO.class);
            result.setTotalRows(importedList.size());

            int rowNum = 2;
            for (MajorDTO dto : importedList) {
                try {
                    if (dto.getMaNganh() == null || dto.getMaNganh().trim().isEmpty()) {
                        errors.add("Dòng " + rowNum + ": Mã ngành không được trống");
                        rowNum++;
                        continue;
                    }

                    String cleanMaNganh = dto.getMaNganh().trim().toUpperCase();

                    XtNganh entity;
                    Optional<XtNganh> existingActive = majorRepository.findByManganhAndIsDeletedFalse(cleanMaNganh);

                    if (existingActive.isPresent()) {
                        // Cập nhật ngành đang active
                        entity = existingActive.get();
                        majorMapper.updateEntity(entity, dto);
                    } else {
                        // Kiểm tra ngành đã soft delete chưa
                        Optional<XtNganh> softDeleted = majorRepository.findByManganh(cleanMaNganh);
                        if (softDeleted.isPresent()) {
                            // ← RESTORE + UPDATE
                            entity = softDeleted.get();
                            entity.setIsDeleted(false);
                            majorMapper.updateEntity(entity, dto);
                        } else {
                            // Tạo mới
                            entity = majorMapper.toEntity(dto);
                            entity.setManganh(cleanMaNganh);
                        }
                    }

                    majorRepository.save(entity);
                    validData.add(dto);

                } catch (Exception ex) {
                    errors.add("Dòng " + rowNum + ": " + ex.getMessage());
                }
                rowNum++;
            }

            result.setSuccessCount(validData.size());
            result.setErrorCount(errors.size());
            result.setErrors(errors);
            result.setValidData(validData);

        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi đọc file Excel: " + e.getMessage(), e);
        }
        return result;
    }
}