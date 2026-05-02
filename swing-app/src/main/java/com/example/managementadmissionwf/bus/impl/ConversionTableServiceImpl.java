package com.example.managementadmissionwf.bus.impl;

import com.example.managementadmissionwf.bus.interfaces.ConversionTableService;
import com.example.managementadmissionwf.dal.entity.XtBangquydoi;
import com.example.managementadmissionwf.dal.repository.ConversionTableRepository;
import com.example.managementadmissionwf.dto.ConversionTableDTO;
import com.example.managementadmissionwf.dto.common.ImportResult;
import com.example.managementadmissionwf.dto.common.Paging;
import com.example.managementadmissionwf.mapper.ConversionTableMapper;
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

@Service
@RequiredArgsConstructor
@Slf4j
public class ConversionTableServiceImpl implements ConversionTableService {

    private final ConversionTableRepository conversionTableRepository;
    private final ConversionTableMapper conversionTableMapper;

    @Override
    public Paging<ConversionTableDTO> search(String phuongThuc, String toHop, String mon, String keyword, int page,
            int size) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("id").descending());
        Page<XtBangquydoi> entityPage = conversionTableRepository.search(
                phuongThuc, toHop, mon, keyword, pageable);

        List<ConversionTableDTO> responses = conversionTableMapper.toResponseList(entityPage.getContent());
        return Paging.<ConversionTableDTO>builder()
                .data(responses)
                .totalItems(entityPage.getTotalElements())
                .totalPages(entityPage.getTotalPages())
                .page(entityPage.getNumber() + 1)
                .limit(entityPage.getSize())
                .hasNext(entityPage.hasNext())
                .build();
    }

    @Override
    public ConversionTableDTO getById(Integer id) {
        XtBangquydoi entity = conversionTableRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bảng quy đổi với ID: " + id));
        return conversionTableMapper.toResponse(entity);
    }

    @Override
    @Transactional
    public ConversionTableDTO create(ConversionTableDTO dto) {
        // Check duplicate - chỉ kiểm tra khi tổ hợp được chỉ định
        if (dto.getToHop() != null && !dto.getToHop().isEmpty()) {
            if (conversionTableRepository.existsByPhuongThucAndMonAndTohop(
                    dto.getPhuongThuc(), dto.getMon(), dto.getToHop())) {
                throw new RuntimeException("Bảng quy đổi đã tồn tại cho: " +
                        dto.getPhuongThuc() + " - " + dto.getMon() + " - " + dto.getToHop());
            }
        }

        XtBangquydoi entity = conversionTableMapper.toEntity(dto);
        entity = conversionTableRepository.save(entity);
        return conversionTableMapper.toResponse(entity);
    }

    @Override
    @Transactional
    public ConversionTableDTO update(Integer id, ConversionTableDTO dto) {
        XtBangquydoi entity = conversionTableRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bảng quy đổi với ID: " + id));

        // Check duplicate - chỉ kiểm tra khi tổ hợp được chỉ định
        if (dto.getToHop() != null && !dto.getToHop().isEmpty()) {
            if (conversionTableRepository.existsByPhuongThucAndMonAndTohopAndIdNot(
                    dto.getPhuongThuc(), dto.getMon(), dto.getToHop(), id)) {
                throw new RuntimeException("Bảng quy đổi đã tồn tại cho: " +
                        dto.getPhuongThuc() + " - " + dto.getMon() + " - " + dto.getToHop());
            }
        }

        conversionTableMapper.updateEntity(entity, dto);
        entity = conversionTableRepository.save(entity);
        return conversionTableMapper.toResponse(entity);
    }

    @Override
    @Transactional
    public void delete(Integer id) {
        conversionTableRepository.deleteById(id);
    }

    @Override
    public List<String> getAllPhuongThuc() {
        return conversionTableRepository.getAllPhuongThuc();
    }

    @Override
    public List<String> getAllToHop() {
        return conversionTableRepository.getAllToHop();
    }

    @Override
    public List<String> getAllMon() {
        return conversionTableRepository.getAllMon();
    }

    @Override
    public void exportExcel(OutputStream outputStream, String phuongThuc, String toHop, String mon, String keyword) {
        try {
            Pageable unpaged = Pageable.unpaged();
            Page<XtBangquydoi> page = conversionTableRepository.search(
                    phuongThuc, toHop, mon, keyword, unpaged);

            List<ConversionTableDTO> data = conversionTableMapper.toResponseList(page.getContent());
            ExcelUtil.exportExcel(data, ConversionTableDTO.class, outputStream);
        } catch (Exception e) {
            throw new RuntimeException("Không thể xuất file Excel: " + e.getMessage(), e);
        }
    }

    @Override
    public ImportResult<ConversionTableDTO> importExcel(InputStream inputStream) {
        ImportResult<ConversionTableDTO> result = new ImportResult<>();
        List<String> errors = new ArrayList<>();
        List<ConversionTableDTO> validData = new ArrayList<>();

        try {
            List<ConversionTableDTO> importedList = ExcelUtil.importExcel(inputStream, ConversionTableDTO.class);
            result.setTotalRows(importedList.size());

            int rowNum = 2;
            for (ConversionTableDTO dto : importedList) {
                try {
                    // Validate required fields
                    if (dto.getPhuongThuc() == null || dto.getPhuongThuc().trim().isEmpty()) {
                        errors.add("Dòng " + rowNum + ": Phương thức không được trống");
                        rowNum++;
                        continue;
                    }
                    if (dto.getMon() == null || dto.getMon().trim().isEmpty()) {
                        errors.add("Dòng " + rowNum + ": Môn không được trống");
                        rowNum++;
                        continue;
                    }

                    // Check if exists
                    XtBangquydoi entity;
                    boolean isUpdate = false;

                    if (dto.getToHop() != null && !dto.getToHop().isEmpty()) {
                        var existing = conversionTableRepository
                                .findByPhuongThucAndMonAndTohop(
                                        dto.getPhuongThuc(), dto.getMon(), dto.getToHop());
                        if (existing.isPresent()) {
                            entity = existing.get();
                            isUpdate = true;
                        } else {
                            entity = conversionTableMapper.toEntity(dto);
                        }
                    } else {
                        entity = conversionTableMapper.toEntity(dto);
                    }

                    conversionTableMapper.updateEntity(entity, dto);
                    conversionTableRepository.save(entity);
                    validData.add(dto);

                    if (isUpdate) {
                        log.info("Import - UPDATE conversion table: {} - {} - {}",
                                dto.getPhuongThuc(), dto.getMon(), dto.getToHop());
                    } else {
                        log.info("Import - CREATE conversion table: {} - {} - {}",
                                dto.getPhuongThuc(), dto.getMon(), dto.getToHop());
                    }

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