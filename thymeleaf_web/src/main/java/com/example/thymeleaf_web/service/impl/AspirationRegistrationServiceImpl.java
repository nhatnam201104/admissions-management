package com.example.thymeleaf_web.service.impl;

import com.example.thymeleaf_web.exception.BusinessException;
import com.example.thymeleaf_web.model.dto.AspirationChoiceForm;
import com.example.thymeleaf_web.model.dto.AspirationRegistrationForm;
import com.example.thymeleaf_web.model.dto.CandidateInfoDto;
import com.example.thymeleaf_web.model.entity.Nganh;
import com.example.thymeleaf_web.model.entity.NguyenVong;
import com.example.thymeleaf_web.model.entity.Thisinh;
import com.example.thymeleaf_web.repository.NganhRepository;
import com.example.thymeleaf_web.repository.NguyenVongRepository;
import com.example.thymeleaf_web.repository.ThisinhRepository;
import com.example.thymeleaf_web.service.AspirationRegistrationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AspirationRegistrationServiceImpl implements AspirationRegistrationService {

    private static final String PENDING_RESULT = "CHO_XET";

    private final ThisinhRepository thisinhRepository;
    private final NganhRepository nganhRepository;
    private final NguyenVongRepository nguyenVongRepository;

    @Override
    @Transactional(readOnly = true)
    public Optional<CandidateInfoDto> findCandidate(String cccd) {
        String normalizedCccd = normalizeCccd(cccd);
        if (!isValidCccd(normalizedCccd)) {
            return Optional.empty();
        }
        return thisinhRepository.findByCccdActive(normalizedCccd).map(this::toCandidateInfo);
    }

    @Override
    @Transactional(readOnly = true)
    public AspirationRegistrationForm buildRegistrationForm(String cccd) {
        String normalizedCccd = requireValidCccd(cccd);
        requireCandidate(normalizedCccd);

        AspirationRegistrationForm form = new AspirationRegistrationForm();
        form.setCccd(normalizedCccd);
        form.setAspirations(nguyenVongRepository.findByCccdActive(normalizedCccd).stream()
                .map(nguyenVong -> new AspirationChoiceForm(nguyenVong.getNvManganh()))
                .collect(Collectors.toCollection(ArrayList::new)));
        form.ensureMinimumRows();
        return form;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Nganh> getActiveMajors() {
        return nganhRepository.findAllActive().stream()
                .sorted((a, b) -> a.getManganh().compareToIgnoreCase(b.getManganh()))
                .toList();
    }

    @Override
    @Transactional
    public void register(AspirationRegistrationForm form) {
        String cccd = requireValidCccd(form.getCccd());
        requireCandidate(cccd);

        List<String> selectedMajorCodes = sanitizeSelectedMajorCodes(form);
        validateSelectedMajorCodes(selectedMajorCodes);

        Map<String, Nganh> activeMajorMap = getActiveMajors().stream()
                .collect(Collectors.toMap(Nganh::getManganh, Function.identity(), (a, b) -> a, LinkedHashMap::new));
        validateMajorsExist(selectedMajorCodes, activeMajorMap);

        List<NguyenVong> existingAspirations = nguyenVongRepository.findByCccdIncludingDeleted(cccd);
        Map<String, ArrayDeque<NguyenVong>> reusableByMajor = groupReusableAspirations(existingAspirations);

        LocalDate today = LocalDate.now();
        Set<Integer> usedIds = new HashSet<>();
        List<NguyenVong> changes = new ArrayList<>();

        for (int index = 0; index < selectedMajorCodes.size(); index++) {
            String maNganh = selectedMajorCodes.get(index);
            NguyenVong aspiration = takeReusableAspiration(reusableByMajor, maNganh);
            boolean isNew = aspiration.getId() == null;

            aspiration.setNnCccd(cccd);
            aspiration.setNvManganh(maNganh);
            aspiration.setNvTt(index + 1);
            aspiration.setIsDeleted(false);
            aspiration.setUpdatedAt(today);

            if (isNew) {
                aspiration.setCreatedAt(today);
                aspiration.setNvKetqua(PENDING_RESULT);
            } else if (aspiration.getNvKetqua() == null) {
                aspiration.setNvKetqua(PENDING_RESULT);
            }

            changes.add(aspiration);
            if (aspiration.getId() != null) {
                usedIds.add(aspiration.getId());
            }
        }

        existingAspirations.stream()
                .filter(aspiration -> aspiration.getId() != null)
                .filter(aspiration -> !usedIds.contains(aspiration.getId()))
                .filter(aspiration -> !Boolean.TRUE.equals(aspiration.getIsDeleted()))
                .forEach(aspiration -> {
                    aspiration.setIsDeleted(true);
                    aspiration.setUpdatedAt(today);
                    changes.add(aspiration);
                });

        nguyenVongRepository.saveAll(changes);
    }

    private CandidateInfoDto toCandidateInfo(Thisinh thisinh) {
        return new CandidateInfoDto(
                thisinh.getCccd(),
                thisinh.getSobaodanh(),
                displayName(thisinh),
                thisinh.getNgaySinh()
        );
    }

    private String displayName(Thisinh thisinh) {
        if (thisinh.getHoVaTen() != null && !thisinh.getHoVaTen().isBlank()) {
            return thisinh.getHoVaTen();
        }
        return (thisinh.getHo() + " " + thisinh.getTen()).trim();
    }

    private String normalizeCccd(String cccd) {
        return cccd == null ? "" : cccd.trim();
    }

    private String requireValidCccd(String cccd) {
        String normalizedCccd = normalizeCccd(cccd);
        if (!isValidCccd(normalizedCccd)) {
            throw new BusinessException("CCCD phải gồm đúng 12 chữ số");
        }
        return normalizedCccd;
    }

    private boolean isValidCccd(String cccd) {
        return cccd != null && cccd.matches("\\d{12}");
    }

    private Thisinh requireCandidate(String cccd) {
        return thisinhRepository.findByCccdActive(cccd)
                .orElseThrow(() -> new BusinessException("Không tìm thấy thông tin thí sinh với CCCD đã nhập"));
    }

    private List<String> sanitizeSelectedMajorCodes(AspirationRegistrationForm form) {
        return form.selectedMajorCodes().stream()
                .map(String::trim)
                .toList();
    }

    private void validateSelectedMajorCodes(List<String> selectedMajorCodes) {
        if (selectedMajorCodes.isEmpty()) {
            throw new BusinessException("Vui lòng chọn ít nhất 1 nguyện vọng");
        }
        if (selectedMajorCodes.size() > AspirationRegistrationForm.MAX_ASPIRATIONS) {
            throw new BusinessException("Mỗi thí sinh chỉ được đăng ký tối đa "
                    + AspirationRegistrationForm.MAX_ASPIRATIONS + " nguyện vọng");
        }

        Set<String> uniqueCodes = new HashSet<>();
        for (String maNganh : selectedMajorCodes) {
            if (!uniqueCodes.add(maNganh.toLowerCase())) {
                throw new BusinessException("Không được đăng ký trùng ngành trong danh sách nguyện vọng");
            }
        }
    }

    private void validateMajorsExist(List<String> selectedMajorCodes, Map<String, Nganh> activeMajorMap) {
        for (String maNganh : selectedMajorCodes) {
            if (!activeMajorMap.containsKey(maNganh)) {
                throw new BusinessException("Ngành " + maNganh + " không tồn tại hoặc đã ngừng xét tuyển");
            }
        }
    }

    private Map<String, ArrayDeque<NguyenVong>> groupReusableAspirations(List<NguyenVong> existingAspirations) {
        Map<String, ArrayDeque<NguyenVong>> reusableByMajor = new HashMap<>();
        for (NguyenVong aspiration : existingAspirations) {
            reusableByMajor
                    .computeIfAbsent(aspiration.getNvManganh(), ignored -> new ArrayDeque<>())
                    .addLast(aspiration);
        }
        return reusableByMajor;
    }

    private NguyenVong takeReusableAspiration(Map<String, ArrayDeque<NguyenVong>> reusableByMajor, String maNganh) {
        ArrayDeque<NguyenVong> reusable = reusableByMajor.get(maNganh);
        if (reusable == null || reusable.isEmpty()) {
            return new NguyenVong();
        }
        return reusable.removeFirst();
    }
}
