package com.example.managementadmissionwf.bus.impl;

import com.example.managementadmissionwf.bus.interfaces.AdmissionResultService;
import com.example.managementadmissionwf.bus.interfaces.MajorService;
import com.example.managementadmissionwf.dal.entity.XtNganh;
import com.example.managementadmissionwf.dal.entity.XtNganhTohop;
import com.example.managementadmissionwf.dal.repository.MajorRepository;
import com.example.managementadmissionwf.dal.repository.NganhTohopRepository;
import com.example.managementadmissionwf.dto.admission.AdmissionResultDTO;
import com.example.managementadmissionwf.dto.common.ImportResult;
import com.example.managementadmissionwf.dto.common.Paging;
import com.example.managementadmissionwf.dto.major.MajorDTO;
import com.example.managementadmissionwf.dto.major.MajorTohopDTO;
import com.example.managementadmissionwf.mapper.MajorMapper;
import com.example.managementadmissionwf.mapper.NganhTohopMapper;
import com.example.managementadmissionwf.util.ExcelUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class MajorServiceImpl implements MajorService {

    private static final String RESULT_TRUNG_TUYEN = "TRUNG_TUYEN";
    private static final String METHOD_TUYEN_THANG = "TUYEN_THANG";
    private static final String METHOD_TUYEN_THANG_SHORT = "T.THẲNG";
    private static final String METHOD_XT_TT = "XT_TT";
    private static final String METHOD_DGNL = "DGNL";
    private static final String METHOD_VSAT = "VSAT";
    private static final String METHOD_THPT = "THPT";
    private static final String METHOD_XET_THPT = "XET_THPT";
    private static final Set<String> TUYEN_THANG_METHODS = Set.of(
            METHOD_TUYEN_THANG,
            METHOD_TUYEN_THANG_SHORT,
            METHOD_XT_TT
    );
    private static final Set<String> THPT_METHODS = Set.of(METHOD_THPT, METHOD_XET_THPT);

    private final MajorRepository majorRepository;
    private final NganhTohopRepository nganhTohopRepository;
    private final MajorMapper majorMapper;
    private final NganhTohopMapper nganhTohopMapper;
    private final AdmissionResultService admissionResultService;

    @Override
    public Paging<MajorDTO> search(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("idnganh").descending());
        Page<XtNganh> entityPage = (keyword != null && !keyword.trim().isEmpty())
                ? majorRepository.search(keyword.trim(), pageable)
                : majorRepository.findByIsDeletedFalse(pageable);

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

    private void updateMajorStatistics(XtNganh nganh) {
        List<AdmissionResultDTO> results = admissionResultService.getByMajor(nganh.getManganh());
        updateMajorStatistics(nganh, results);
    }

    private void updateMajorStatistics(XtNganh nganh, List<AdmissionResultDTO> results) {
        int slXtt = 0, slDgnl = 0, slVsat = 0, slThpt = 0;

        for (AdmissionResultDTO r : results) {
            if (!RESULT_TRUNG_TUYEN.equals(r.getKetQua())) continue;

            String pt = (r.getPhuongThuc() != null ? r.getPhuongThuc().toUpperCase().trim() : "");

            if (TUYEN_THANG_METHODS.contains(pt)) {
                slXtt++;
            } else if (METHOD_DGNL.equals(pt)) {
                slDgnl++;
            } else if (METHOD_VSAT.equals(pt)) {
                slVsat++;
            } else if (THPT_METHODS.contains(pt)) {
                slThpt++;
            }
        }

        nganh.setSlXtt(slXtt);
        nganh.setSlDgnl(slDgnl);
        nganh.setSlVsat(slVsat);
        nganh.setSlThpt(slThpt);
    }

    private Map<String, List<AdmissionResultDTO>> groupAdmissionResultsByMajor(List<AdmissionResultDTO> results) {
        Map<String, List<AdmissionResultDTO>> resultsByMajor = new HashMap<>();
        for (AdmissionResultDTO result : results) {
            addAdmissionResult(resultsByMajor, result.getManganh(), result);
            if (result.getTennganh() != null && !result.getTennganh().equals(result.getManganh())) {
                addAdmissionResult(resultsByMajor, result.getTennganh(), result);
            }
        }
        return resultsByMajor;
    }

    private void addAdmissionResult(Map<String, List<AdmissionResultDTO>> resultsByMajor,
                                    String majorKey,
                                    AdmissionResultDTO result) {
        if (majorKey == null || majorKey.isBlank()) return;
        resultsByMajor.computeIfAbsent(majorKey, key -> new ArrayList<>()).add(result);
    }

    private void validateDto(MajorDTO dto) {
        if (dto.getTohopGoc() == null || dto.getTohopGoc().trim().isEmpty()) {
            throw new RuntimeException("Tổ hợp gốc không được để trống");
        }
        boolean hasMethod = Boolean.TRUE.equals(dto.getTuyenThang()) ||
                Boolean.TRUE.equals(dto.getDgnl()) ||
                Boolean.TRUE.equals(dto.getThpt()) ||
                Boolean.TRUE.equals(dto.getVsat());
        if (!hasMethod) {
            throw new RuntimeException("Phải chọn ít nhất một phương thức xét tuyển");
        }
    }

    @Override
    @Transactional
    public MajorDTO create(MajorDTO dto) {
        if (majorRepository.existsByManganhAndIsDeletedFalse(dto.getMaNganh())) {
            throw new RuntimeException("Mã ngành đã tồn tại: " + dto.getMaNganh());
        }
        validateDto(dto);

        XtNganh entity = majorMapper.toEntity(dto);
        updateMajorStatistics(entity);

        entity = majorRepository.save(entity);
        return majorMapper.toResponse(entity);
    }

    @Override
    @Transactional
    public MajorDTO update(String maNganhCu, MajorDTO dto) {
        XtNganh entity = majorRepository.findByManganhAndIsDeletedFalse(maNganhCu)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy ngành với mã: " + maNganhCu));

        if (majorRepository.existsByManganhAndIdnganhNotAndIsDeletedFalse(dto.getMaNganh(), entity.getIdnganh())) {
            throw new RuntimeException("Mã ngành đã tồn tại: " + dto.getMaNganh());
        }
        validateDto(dto);

        majorMapper.updateEntity(entity, dto);
        updateMajorStatistics(entity);

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
        return Paging.<MajorTohopDTO>builder()
                .data(nganhTohopMapper.toResponseList(entityPage.getContent()))
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
        if (nganhTohopRepository.existsByManganhAndMatohopAndIsDeletedFalse(
                tohopDTO.getMaNganh(), tohopDTO.getMaToHop())) {
            throw new RuntimeException("Tổ hợp " + tohopDTO.getMaToHop() + " đã tồn tại cho ngành " + tohopDTO.getMaNganh());
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
        entity.setIsDeleted(true);
        nganhTohopRepository.save(entity);
    }

    @Override
    public void exportExcel(OutputStream outputStream, String keyword) {
        try {
            Pageable unpaged = Pageable.unpaged();
            Page<XtNganh> page = (keyword != null && !keyword.trim().isEmpty())
                    ? majorRepository.search(keyword.trim(), unpaged)
                    : majorRepository.findByIsDeletedFalse(unpaged);

            List<MajorDTO> data = majorMapper.toResponseList(page.getContent());
            ExcelUtil.exportExcel(data, MajorDTO.class, outputStream);
        } catch (Exception e) {
            throw new RuntimeException("Không thể xuất file Excel: " + e.getMessage(), e);
        }
    }

    @Override
    public ImportResult<MajorDTO> importExcel(InputStream inputStream) {
        ImportResult<MajorDTO> result = new ImportResult<>();
        List<String> errors = new ArrayList<>();
        List<MajorDTO> validData = new ArrayList<>();

        try {
            List<MajorDTO> importedList = ExcelUtil.importExcel(inputStream, MajorDTO.class);
            result.setTotalRows(importedList.size());

            int rowNum = 2;
            for (MajorDTO dto : importedList) {
                String cleanMaNganh = null;

                try {
                    if (dto.getMaNganh() == null || dto.getMaNganh().trim().isEmpty()) {
                        errors.add("Dòng " + rowNum + ": Mã ngành không được trống");
                        rowNum++;
                        continue;
                    }

                    dto.setTohopList(null);
                    dto.setIdNganh(null);
                    cleanMaNganh = dto.getMaNganh().trim().toUpperCase();

                    validateDto(dto);

                    XtNganh entity;

                    // Tìm ngành theo mã (bất kể is_deleted = true hay false)
                    Optional<XtNganh> optNganh = majorRepository.findByManganh(cleanMaNganh);

                    if (optNganh.isPresent()) {
                        entity = optNganh.get();
                        // Nếu ngành đang bị soft-delete thì restore nó
                        if (Boolean.TRUE.equals(entity.getIsDeleted())) {
                            log.info("Import - RESTORE ngành soft-deleted: {}", cleanMaNganh);
                            entity.setIsDeleted(false);
                        } else {
                            log.info("Import - UPDATE ngành tồn tại: {}", cleanMaNganh);
                        }
                        majorMapper.updateEntity(entity, dto);
                    } else {
                        // Tạo mới hoàn toàn
                        log.info("Import - CREATE ngành mới: {}", cleanMaNganh);
                        entity = majorMapper.toEntity(dto);
                        entity.setManganh(cleanMaNganh);
                        entity.setIsDeleted(false);
                    }

                    updateMajorStatistics(entity);
                    majorRepository.save(entity);
                    validData.add(dto);

                } catch (DataIntegrityViolationException e) {
                    errors.add("Dòng " + rowNum + ": Mã ngành '" + (cleanMaNganh != null ? cleanMaNganh : "unknown")
                            + "' đã tồn tại và không thể cập nhật (duplicate key)");
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

    @Override
    @Transactional
    public void refreshAllStatistics() {
        List<XtNganh> allMajors = majorRepository.findByIsDeletedFalse();
        Map<String, List<AdmissionResultDTO>> resultsByMajor =
                groupAdmissionResultsByMajor(admissionResultService.getAllResults());
        List<XtNganh> updatedMajors = new ArrayList<>();
        int updated = 0;
        for (XtNganh nganh : allMajors) {
            if (!Boolean.TRUE.equals(nganh.getIsDeleted())) {
                List<AdmissionResultDTO> majorResults = resultsByMajor.getOrDefault(
                        nganh.getManganh(),
                        Collections.emptyList()
                );
                updateMajorStatistics(nganh, majorResults);
                updatedMajors.add(nganh);
                updated++;
            }
        }
        majorRepository.saveAll(updatedMajors);
        log.info("Đã refresh thống kê sl_* cho {} ngành", updated);
    }
}
