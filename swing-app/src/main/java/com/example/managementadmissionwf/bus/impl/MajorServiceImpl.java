package com.example.managementadmissionwf.bus.impl;

import com.example.managementadmissionwf.bus.interfaces.AdmissionResultService;
import com.example.managementadmissionwf.bus.interfaces.MajorService;
import com.example.managementadmissionwf.dal.entity.XtNganh;
import com.example.managementadmissionwf.dal.entity.XtNganhTohop;
import com.example.managementadmissionwf.dal.entity.XtTohopMonthi;
import com.example.managementadmissionwf.dal.repository.MajorRepository;
import com.example.managementadmissionwf.dal.repository.NganhTohopRepository;
import com.example.managementadmissionwf.dal.repository.NguyenVongRepository;
import com.example.managementadmissionwf.dal.repository.SubjectGroupRepository;
import com.example.managementadmissionwf.dto.admission.AdmissionResultDTO;
import com.example.managementadmissionwf.dto.common.ImportResult;
import com.example.managementadmissionwf.dto.common.Paging;
import com.example.managementadmissionwf.dto.major.MajorDTO;
import com.example.managementadmissionwf.dto.major.MajorTohopDTO;
import com.example.managementadmissionwf.mapper.MajorMapper;
import com.example.managementadmissionwf.mapper.NganhTohopMapper;
import com.example.managementadmissionwf.utils.ExcelUtil;

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
            METHOD_XT_TT);
    private static final Set<String> THPT_METHODS = Set.of(METHOD_THPT, METHOD_XET_THPT);

    private final MajorRepository majorRepository;
    private final NganhTohopRepository nganhTohopRepository;
    private final SubjectGroupRepository subjectGroupRepository;
    private final MajorMapper majorMapper;
    private final NganhTohopMapper nganhTohopMapper;
    private final AdmissionResultService admissionResultService;
    private final NguyenVongRepository nguyenVongRepository;

    @Override
    public Paging<MajorDTO> search(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("idnganh").descending());
        Page<XtNganh> entityPage = (keyword != null && !keyword.trim().isEmpty())
                ? majorRepository.search(keyword.trim(), pageable)
                : majorRepository.findByIsDeletedFalse(pageable);

        List<MajorDTO> responses = majorMapper.toResponseList(entityPage.getContent());
        attachTotalAspirations(responses);
        return Paging.<MajorDTO>builder()
                .data(responses)
                .totalItems(entityPage.getTotalElements())
                .totalPages(entityPage.getTotalPages())
                .page(entityPage.getNumber() + 1)
                .limit(entityPage.getSize())
                .hasNext(entityPage.hasNext())
                .build();
    }

    private void attachTotalAspirations(List<MajorDTO> majors) {
        List<String> majorCodes = majors.stream()
                .map(MajorDTO::getMaNganh)
                .filter(code -> code != null && !code.isBlank())
                .toList();
        if (majorCodes.isEmpty()) {
            return;
        }

        Map<String, Long> counts = new HashMap<>();
        for (Object[] row : nguyenVongRepository.countActiveByMajorCodes(majorCodes)) {
            counts.put((String) row[0], ((Number) row[1]).longValue());
        }
        majors.forEach(major -> major.setTotalAspirations(
                counts.getOrDefault(major.getMaNganh(), 0L)));
    }

    @Override
    public MajorDTO getByMaNganh(String maNganh) {
        XtNganh entity = majorRepository.findByManganhAndIsDeletedFalse(maNganh)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy ngành với mã: " + maNganh));
        MajorDTO dto = majorMapper.toResponse(entity);
        // Load tổ hợp xét tuyển — mapper không tự load vì XtNganh không
        // có quan hệ @OneToMany. Trước đây trả null → MajorDetailDialog
        // hiển thị bảng tổ hợp rỗng.
        List<XtNganhTohop> links = nganhTohopRepository.findByManganh(entity.getManganh());
        dto.setTohopList(nganhTohopMapper.toResponseList(links));
        return dto;
    }

    private void updateMajorStatistics(XtNganh nganh) {
        List<AdmissionResultDTO> results = admissionResultService.getByMajor(nganh.getManganh());
        updateMajorStatistics(nganh, results);
    }

    private void updateMajorStatistics(XtNganh nganh, List<AdmissionResultDTO> results) {
        int slXtt = 0, slDgnl = 0, slVsat = 0, slThpt = 0;

        for (AdmissionResultDTO r : results) {
            if (!RESULT_TRUNG_TUYEN.equals(r.getKetQua()))
                continue;

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
        if (majorKey == null || majorKey.isBlank())
            return;
        resultsByMajor.computeIfAbsent(majorKey, key -> new ArrayList<>()).add(result);
    }

    private void validateDto(MajorDTO dto) {
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

        entity = majorRepository.save(entity);
        return majorMapper.toResponse(entity);
    }

    @Override
    @Transactional
    public void delete(String maNganh) {
        XtNganh entity = majorRepository.findByManganhAndIsDeletedFalse(maNganh)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy ngành với mã: " + maNganh));

        // Rename với prefix + timestamp để tránh duplicate khi tạo mới cùng mã
        String newManganh = "DELETED_" + maNganh + "_" + System.currentTimeMillis();
        entity.setManganh(newManganh);
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
            throw new RuntimeException(
                    "Tổ hợp " + tohopDTO.getMaToHop() + " đã tồn tại cho ngành " + tohopDTO.getMaNganh());
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
                    // Sau updateMajorStatistics, sl_* bị đè bằng số trúng
                    // tuyển thực tế (= 0 khi mới import). Khôi phục lại giá
                    // trị từ Excel — đây là chỉ tiêu phân phối phương thức,
                    // không phải số trúng tuyển. updateMajorStatistics chỉ
                    // hữu ích khi gọi từ refreshAllStatistics() sau khi xét.
                    if (dto.getSlXtt() != null) entity.setSlXtt(dto.getSlXtt());
                    if (dto.getSlDgnl() != null) entity.setSlDgnl(dto.getSlDgnl());
                    if (dto.getSlVsat() != null) entity.setSlVsat(dto.getSlVsat());
                    if (dto.getSlThpt() != null) entity.setSlThpt(dto.getSlThpt());
                    majorRepository.save(entity);
                    autoLinkSubjectGroups(entity);
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
        Map<String, List<AdmissionResultDTO>> resultsByMajor = groupAdmissionResultsByMajor(
                admissionResultService.getAllResults());
        List<XtNganh> updatedMajors = new ArrayList<>();
        int updated = 0;
        for (XtNganh nganh : allMajors) {
            if (!Boolean.TRUE.equals(nganh.getIsDeleted())) {
                List<AdmissionResultDTO> majorResults = resultsByMajor.getOrDefault(
                        nganh.getManganh(),
                        Collections.emptyList());
                updateMajorStatistics(nganh, majorResults);
                updatedMajors.add(nganh);
                updated++;
            }
        }
        majorRepository.saveAll(updatedMajors);
        log.info("Đã refresh thống kê sl_* cho {} ngành", updated);
    }

    /**
     * Tự động tạo liên kết {@code xt_nganh_tohop} cho ngành dựa trên tổ hợp
     * gốc ({@code n_tohopgoc}). Được gọi sau khi import 1 ngành từ Excel:
     * vì file majors.xlsx không chứa thông tin tổ hợp, app phải suy ra dựa
     * trên nhóm khối (cùng ký tự đầu của mã tổ hợp gốc):
     * <ul>
     *   <li>"A00" → liên kết với A00, A01, A02 (nếu tồn tại trong xt_tohop_monthi)</li>
     *   <li>"B00" → B00, B03, B08, ...</li>
     *   <li>"C00" → C00, C01, C02, ...</li>
     *   <li>"D01" → D01, D07, D08, ...</li>
     *   <li>"M00" → M00, M01, ... (riêng vì không cùng prefix với D)</li>
     * </ul>
     * Hệ số mặc định 1.0 cho cả 3 môn. Bỏ qua các tổ hợp đã liên kết.
     */
    private void autoLinkSubjectGroups(XtNganh nganh) {
        String tohopGoc = nganh.getNTohopgoc();
        if (tohopGoc == null || tohopGoc.isBlank()) {
            log.debug("Bỏ qua auto-link cho ngành {} vì n_tohopgoc trống", nganh.getManganh());
            return;
        }
        String prefix = String.valueOf(tohopGoc.charAt(0)).toUpperCase();

        // Lấy tất cả tổ hợp cùng nhóm khối từ xt_tohop_monthi
        List<XtTohopMonthi> candidates = subjectGroupRepository.findAll().stream()
                .filter(t -> t.getMatohop() != null
                        && t.getMatohop().toUpperCase().startsWith(prefix))
                .toList();

        if (candidates.isEmpty()) {
            // Nếu không có tổ hợp cùng prefix, tối thiểu liên kết với chính tổ hợp gốc
            subjectGroupRepository.findByMatohop(tohopGoc)
                    .ifPresent(t -> linkOne(nganh.getManganh(), t));
            return;
        }

        int created = 0;
        for (XtTohopMonthi t : candidates) {
            if (linkOne(nganh.getManganh(), t)) {
                created++;
            }
        }
        log.info("Auto-link {} tổ hợp cho ngành {} (prefix={})",
                created, nganh.getManganh(), prefix);
    }

    /** Tạo 1 bản ghi xt_nganh_tohop nếu chưa tồn tại. Trả về true nếu insert. */
    private boolean linkOne(String manganh, XtTohopMonthi tohop) {
        if (nganhTohopRepository.existsByManganhAndMatohopAndIsDeletedFalse(
                manganh, tohop.getMatohop())) {
            return false;
        }
        XtNganhTohop link = XtNganhTohop.builder()
                .manganh(manganh)
                .matohop(tohop.getMatohop())
                .thMon1(tohop.getMon1())
                .hsmon1(1.0)
                .thMon2(tohop.getMon2())
                .hsmon2(1.0)
                .thMon3(tohop.getMon3())
                .hsmon3(1.0)
                .isDeleted(false)
                .build();
        nganhTohopRepository.save(link);
        return true;
    }
}
