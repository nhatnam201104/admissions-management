package com.example.managementadmissionwf.bus.impl;

import com.example.managementadmissionwf.bus.interfaces.AspirationScoreService;
import com.example.managementadmissionwf.dal.entity.XtNguyenvongxettuyen;
import com.example.managementadmissionwf.dal.repository.CandidateRepository;
import com.example.managementadmissionwf.dal.repository.MajorRepository;
import com.example.managementadmissionwf.dal.repository.NguyenVongRepository;
import com.example.managementadmissionwf.dto.admission.AspirationImportDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service helper riêng để mỗi dòng import được wrap trong một transaction
 * REQUIRES_NEW. Nếu đặt method này nằm chung class với
 * {@link AdmissionResultServiceImpl}, gọi qua {@code this.method()} sẽ bypass
 * Spring proxy → annotation @Transactional không có tác dụng và toàn bộ
 * batch bị rollback chỉ vì 1 dòng lỗi.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AspirationImportHelper {

    private final NguyenVongRepository nguyenVongRepository;
    private final CandidateRepository candidateRepository;
    private final MajorRepository majorRepository;
    private final AspirationScoreService aspirationScoreService;

    /**
     * Tạo 1 nguyện vọng + tính điểm. Chạy trong transaction RIÊNG để row khác
     * không bị ảnh hưởng.
     *
     * @return null nếu thành công, hoặc message lỗi nếu skip.
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public String saveAspirationRow(AspirationImportDTO dto) {
        String cccd = dto.getCccd() == null ? "" : dto.getCccd().trim();
        String maNganh = dto.getMaNganh() == null ? "" : dto.getMaNganh().trim();
        if (cccd.isBlank() || maNganh.isBlank() || dto.getNvTt() == null) {
            return "Thiếu CCCD/Mã ngành/Số NV";
        }
        if (!candidateRepository.existsByCccd(cccd)) {
            return "CCCD không tồn tại trong DB: " + cccd;
        }
        if (majorRepository.findByManganh(maNganh).isEmpty()) {
            return "Mã ngành không tồn tại trong DB: " + maNganh;
        }

        boolean exists = nguyenVongRepository.findByNnCccd(cccd).stream()
                .anyMatch(nv -> dto.getNvTt().equals(nv.getNvTt()));
        if (exists) {
            return "NV " + dto.getNvTt() + " của CCCD " + cccd + " đã tồn tại";
        }

        String phuongThuc = dto.getPhuongThuc() != null ? dto.getPhuongThuc() : "THPT";
        String toHop = dto.getToHop();
        if (nguyenVongRepository.existsDuplicate(cccd, maNganh, phuongThuc, toHop, null)) {
            String tohopMsg = toHop != null ? " + tổ hợp " + toHop : "";
            return "NV trùng (CCCD " + cccd + " + ngành " + maNganh + " + phương thức " + phuongThuc + tohopMsg + ") đã tồn tại";
        }

        XtNguyenvongxettuyen aspiration = XtNguyenvongxettuyen.builder()
                .nnCccd(cccd)
                .nvManganh(maNganh)
                .nvTt(dto.getNvTt())
                .ttPhuongthuc(phuongThuc)
                .ttThm(toHop)
                .nvKetqua("CHO_XET")
                .build();
        aspiration = nguyenVongRepository.save(aspiration);

        // Tính điểm xét tuyển — bọc try-catch riêng để khi failure không rollback
        // việc tạo NV (NV vẫn được lưu với kết quả CHO_XET).
        try {
            aspirationScoreService.calculateForAspiration(aspiration);
        } catch (Exception calcEx) {
            log.warn("Không tính được điểm cho NV {} (CCCD={}, ngành={}): {}",
                    aspiration.getId(), cccd, maNganh, calcEx.getMessage());
        }
        return null;
    }
}
