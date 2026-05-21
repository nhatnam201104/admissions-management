package com.example.managementadmissionwf.bus.impl;

import com.example.managementadmissionwf.bus.interfaces.BonusScoreService;
import com.example.managementadmissionwf.dal.entity.XtThisinhxettuyen25;
import com.example.managementadmissionwf.dal.repository.CandidateRepository;
import com.example.managementadmissionwf.dto.candidate.CandidateDTO;
import com.example.managementadmissionwf.mapper.CandidateMapper;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;

/**
 * Test cho rule mới: thí sinh phải từ 17 tuổi trở lên.
 * UI đã chặn ở CandidateFormDialog, đây là lớp service-side cho luồng import.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("CandidateServiceImpl - age validation (≥17)")
class CandidateServiceImplAgeValidationTest {

    @Mock private CandidateRepository candidateRepository;
    @Mock private CandidateMapper candidateMapper;
    @Mock private Validator validator;
    @Mock private BonusScoreService bonusScoreService;

    @InjectMocks
    private CandidateServiceImpl service;

    private CandidateDTO baseDto(LocalDate dob) {
        CandidateDTO dto = new CandidateDTO();
        dto.setCccd("001234567890");
        dto.setSobaodanh("SBD001");
        dto.setHo("Nguyen");
        dto.setTen("Van A");
        dto.setNgaySinh(dob);
        return dto;
    }

    @BeforeEach
    void stubValidator() {
        // Không có constraint violations từ Bean Validation; rule tuổi là check riêng.
        lenient().when(validator.validate(any(CandidateDTO.class))).thenReturn(Collections.emptySet());
    }

    @Test
    @DisplayName("16 tuổi → reject")
    void rejects_when_ageBelow17() {
        LocalDate dob = LocalDate.now().minusYears(16);

        assertThatThrownBy(() -> service.createCandidate(baseDto(dob)))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("17 tuổi trở lên");
    }

    @Test
    @DisplayName("17 tuổi → accept (qua validate)")
    void accepts_when_age17() {
        LocalDate dob = LocalDate.now().minusYears(17);
        CandidateDTO dto = baseDto(dob);

        // Stub các bước sau validate để không chặn flow.
        lenient().when(candidateRepository.findBySobaodanhIncludingDeleted(any())).thenReturn(Optional.empty());
        lenient().when(candidateRepository.findByEmailIncludingDeleted(any())).thenReturn(Optional.empty());
        lenient().when(candidateRepository.findByDienThoaiIncludingDeleted(any())).thenReturn(Optional.empty());
        lenient().when(candidateRepository.findByCccdIncludingDeleted(any())).thenReturn(Optional.empty());
        XtThisinhxettuyen25 entity = XtThisinhxettuyen25.builder().cccd(dto.getCccd()).build();
        lenient().when(candidateMapper.toEntity(any())).thenReturn(entity);
        lenient().when(candidateRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        lenient().when(candidateMapper.toDTO(any())).thenReturn(dto);

        assertThatCode(() -> service.createCandidate(dto)).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("18 tuổi → accept")
    void accepts_when_age18() {
        LocalDate dob = LocalDate.now().minusYears(18);
        CandidateDTO dto = baseDto(dob);

        lenient().when(candidateRepository.findBySobaodanhIncludingDeleted(any())).thenReturn(Optional.empty());
        lenient().when(candidateRepository.findByEmailIncludingDeleted(any())).thenReturn(Optional.empty());
        lenient().when(candidateRepository.findByDienThoaiIncludingDeleted(any())).thenReturn(Optional.empty());
        lenient().when(candidateRepository.findByCccdIncludingDeleted(any())).thenReturn(Optional.empty());
        XtThisinhxettuyen25 entity = XtThisinhxettuyen25.builder().cccd(dto.getCccd()).build();
        lenient().when(candidateMapper.toEntity(any())).thenReturn(entity);
        lenient().when(candidateRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        lenient().when(candidateMapper.toDTO(any())).thenReturn(dto);

        assertThatCode(() -> service.createCandidate(dto)).doesNotThrowAnyException();
    }
}
