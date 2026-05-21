package com.example.managementadmissionwf.bus.impl;

import com.example.managementadmissionwf.dal.entity.XtDiemcongxettuyen;
import com.example.managementadmissionwf.dal.entity.XtThisinhxettuyen25;
import com.example.managementadmissionwf.dal.repository.BonusScoreRepository;
import com.example.managementadmissionwf.dal.repository.CandidateRepository;
import com.example.managementadmissionwf.mapper.BonusScoreMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("BonusScoreServiceImpl.upsertForCandidate")
class BonusScoreServiceImplTest {

    @Mock private BonusScoreRepository bonusScoreRepository;
    @Mock private BonusScoreMapper bonusScoreMapper;
    @Mock private CandidateRepository candidateRepository;

    @InjectMocks
    private BonusScoreServiceImpl service;

    private static final String CCCD = "001234567890";

    private XtThisinhxettuyen25 candidate(String khuVuc, String doiTuong) {
        return XtThisinhxettuyen25.builder()
                .cccd(CCCD)
                .khuVuc(khuVuc)
                .doiTuong(doiTuong)
                .build();
    }

    @Test
    @DisplayName("upsert khi chưa có row → tạo mới với diemCc=0, diemUtxt đúng công thức")
    void upsert_createsNewRow_whenAbsent() {
        when(candidateRepository.findByCccd(CCCD)).thenReturn(Optional.of(candidate("KV1", "UT1")));
        when(bonusScoreRepository.findByCccd(CCCD)).thenReturn(Optional.empty());
        when(bonusScoreRepository.save(any(XtDiemcongxettuyen.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        service.upsertForCandidate(CCCD);

        ArgumentCaptor<XtDiemcongxettuyen> captor = ArgumentCaptor.forClass(XtDiemcongxettuyen.class);
        org.mockito.Mockito.verify(bonusScoreRepository).save(captor.capture());
        XtDiemcongxettuyen saved = captor.getValue();
        assertThat(saved.getCccd()).isEqualTo(CCCD);
        assertThat(saved.getDiemCc()).isEqualTo(0.0);
        assertThat(saved.getDiemUtxt()).isEqualTo(2.75);
    }

    @Test
    @DisplayName("upsert khi đã có row → giữ diemCc cũ, recompute diemUtxt")
    void upsert_keepsExistingDiemCc_recomputesUtxt() {
        XtDiemcongxettuyen existing = XtDiemcongxettuyen.builder()
                .id(7).cccd(CCCD).diemCc(1.5).diemUtxt(0.0).isDeleted(false).build();
        when(candidateRepository.findByCccd(CCCD)).thenReturn(Optional.of(candidate("KV2-NT", "UT2")));
        when(bonusScoreRepository.findByCccd(CCCD)).thenReturn(Optional.of(existing));
        when(bonusScoreRepository.save(any(XtDiemcongxettuyen.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        service.upsertForCandidate(CCCD);

        ArgumentCaptor<XtDiemcongxettuyen> captor = ArgumentCaptor.forClass(XtDiemcongxettuyen.class);
        org.mockito.Mockito.verify(bonusScoreRepository).save(captor.capture());
        XtDiemcongxettuyen saved = captor.getValue();
        assertThat(saved.getDiemCc()).isEqualTo(1.5);
        assertThat(saved.getDiemUtxt()).isEqualTo(1.5); // KV2-NT 0.5 + UT2 1.0
    }

    @Test
    @DisplayName("upsert idempotent: gọi 2 lần với cùng state → cùng kết quả")
    void upsert_isIdempotent() {
        XtDiemcongxettuyen existing = XtDiemcongxettuyen.builder()
                .id(7).cccd(CCCD).diemCc(0.0).diemUtxt(0.75).isDeleted(false).build();
        when(candidateRepository.findByCccd(CCCD)).thenReturn(Optional.of(candidate("KV1", "Không")));
        when(bonusScoreRepository.findByCccd(CCCD)).thenReturn(Optional.of(existing));
        when(bonusScoreRepository.save(any(XtDiemcongxettuyen.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        service.upsertForCandidate(CCCD);
        service.upsertForCandidate(CCCD);

        assertThat(existing.getDiemUtxt()).isEqualTo(0.75);
        assertThat(existing.getDiemCc()).isEqualTo(0.0);
    }

    @Test
    @DisplayName("upsert ném lỗi khi không có thí sinh tương ứng")
    void upsert_throws_whenCandidateMissing() {
        when(candidateRepository.findByCccd(CCCD)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.upsertForCandidate(CCCD))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining(CCCD);
    }
}
