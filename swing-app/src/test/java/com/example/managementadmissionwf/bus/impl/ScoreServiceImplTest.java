package com.example.managementadmissionwf.bus.impl;

import com.example.managementadmissionwf.bus.interfaces.AspirationScoreService;
import com.example.managementadmissionwf.dal.entity.XtDiemthixettuyen;
import com.example.managementadmissionwf.dal.entity.XtThisinhxettuyen25;
import com.example.managementadmissionwf.dal.repository.CandidateRepository;
import com.example.managementadmissionwf.dal.repository.ScoreRepository;
import com.example.managementadmissionwf.dto.common.ImportResult;
import com.example.managementadmissionwf.dto.score.ScoreDTO;
import com.example.managementadmissionwf.mapper.ScoreMapper;
import com.example.managementadmissionwf.util.ExcelUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ScoreServiceImpl")
class ScoreServiceImplTest {

    @Mock private ScoreRepository scoreRepository;
    @Mock private ScoreMapper scoreMapper;
    @Mock private CandidateRepository candidateRepository;
    @Mock private AspirationScoreService aspirationScoreService;

    @InjectMocks
    private ScoreServiceImpl scoreService;

    private static final String CCCD = "012345678901";
    private static final String SBD = "SBD001";

    private ScoreDTO baseDto(String method) {
        ScoreDTO dto = new ScoreDTO();
        dto.setCccd(CCCD);
        dto.setSobaodanh(SBD);
        dto.setPhuongThuc(method);
        return dto;
    }

    private XtDiemthixettuyen baseEntity() {
        return XtDiemthixettuyen.builder()
                .id(1).cccd(CCCD).sobaodanh(SBD).dPhuongthuc("THPT")
                .isDeleted(false).build();
    }

    // ======================== GET ALL SCORES ========================

    @Nested
    @DisplayName("getAllScores")
    class GetAllScores {

        @Test
        @DisplayName("should return paginated list of active candidate scores")
        void shouldReturnPaginatedScores() {
            Pageable pageable = PageRequest.of(0, 10);
            XtDiemthixettuyen entity = baseEntity();
            ScoreDTO dto = baseDto("THPT");

            when(scoreRepository.findAllWithActiveCandidate(pageable))
                    .thenReturn(new PageImpl<>(List.of(entity)));
            when(scoreMapper.toDto(entity)).thenReturn(dto);

            Page<ScoreDTO> result = scoreService.getAllScores(pageable);

            assertThat(result).hasSize(1);
            assertThat(result.getContent().getFirst().getCccd()).isEqualTo(CCCD);
        }

        @Test
        @DisplayName("should return empty page when no scores")
        void shouldReturnEmptyPage() {
            Pageable pageable = PageRequest.of(0, 10);
            when(scoreRepository.findAllWithActiveCandidate(pageable))
                    .thenReturn(new PageImpl<>(Collections.emptyList()));

            assertThat(scoreService.getAllScores(pageable)).isEmpty();
        }
    }

    // ======================== SEARCH SCORES ========================

    @Nested
    @DisplayName("searchScores")
    class SearchScores {

        @Test
        @DisplayName("should search by keyword wrapped with %")
        void shouldSearchByKeyword() {
            Pageable pageable = PageRequest.of(0, 10);
            when(scoreRepository.searchScores("%keyword%", null, pageable))
                    .thenReturn(new PageImpl<>(Collections.emptyList()));

            scoreService.searchScores("keyword", null, pageable);

            verify(scoreRepository).searchScores("%keyword%", null, pageable);
        }

        @Test
        @DisplayName("should trim keyword before searching")
        void shouldTrimKeyword() {
            Pageable pageable = PageRequest.of(0, 10);
            when(scoreRepository.searchScores("%abc%", null, pageable))
                    .thenReturn(new PageImpl<>(Collections.emptyList()));
            scoreService.searchScores("  abc  ", null, pageable);

            verify(scoreRepository).searchScores("%abc%", null, pageable);
        }

        @ParameterizedTest
        @DisplayName("should pass null keyword when blank")
        @NullAndEmptySource
        void shouldPassNullKeywordWhenBlank(String keyword) {
            Pageable pageable = PageRequest.of(0, 10);
            when(scoreRepository.searchScores(null, null, pageable))
                    .thenReturn(new PageImpl<>(Collections.emptyList()));
            scoreService.searchScores(keyword, null, pageable);

            verify(scoreRepository).searchScores(null, null, pageable);
        }

        @ParameterizedTest
        @DisplayName("should pass null keyword when whitespace-only")
        @ValueSource(strings = {"  ", "\t", "   "})
        void shouldPassNullKeywordWhenWhitespace(String keyword) {
            Pageable pageable = PageRequest.of(0, 10);
            when(scoreRepository.searchScores(null, null, pageable))
                    .thenReturn(new PageImpl<>(Collections.emptyList()));
            scoreService.searchScores(keyword, null, pageable);

            verify(scoreRepository).searchScores(null, null, pageable);
        }

        @Test
        @DisplayName("should filter by phuongThuc")
        void shouldFilterByPhuongThuc() {
            Pageable pageable = PageRequest.of(0, 10);
            when(scoreRepository.searchScores(null, "THPT", pageable))
                    .thenReturn(new PageImpl<>(Collections.emptyList()));
            scoreService.searchScores(null, "THPT", pageable);

            verify(scoreRepository).searchScores(null, "THPT", pageable);
        }

        @Test
        @DisplayName("should pass null phuongThuc when 'Tất cả'")
        void shouldPassNullWhenAll() {
            Pageable pageable = PageRequest.of(0, 10);
            when(scoreRepository.searchScores(null, null, pageable))
                    .thenReturn(new PageImpl<>(Collections.emptyList()));
            scoreService.searchScores(null, "Tất cả", pageable);

            verify(scoreRepository).searchScores(null, null, pageable);
        }

        @Test
        @DisplayName("should pass null phuongThuc when null")
        void shouldPassNullWhenNull() {
            Pageable pageable = PageRequest.of(0, 10);
            when(scoreRepository.searchScores(null, null, pageable))
                    .thenReturn(new PageImpl<>(Collections.emptyList()));
            scoreService.searchScores(null, null, pageable);

            verify(scoreRepository).searchScores(null, null, pageable);
        }
    }

    // ======================== GET BY CCCD ========================

    @Nested
    @DisplayName("getScoreByCccd")
    class GetScoreByCccd {

        @Test
        @DisplayName("should return DTO when CCCD exists")
        void shouldReturnDto_whenExists() {
            XtDiemthixettuyen entity = baseEntity();
            ScoreDTO dto = baseDto("THPT");

            when(scoreRepository.findByCccdAndDPhuongthuc(CCCD, "THPT")).thenReturn(Optional.of(entity));
            when(scoreMapper.toDto(entity)).thenReturn(dto);

            assertThat(scoreService.getScoreByCccd(CCCD)).isEqualTo(dto);
        }

        @Test
        @DisplayName("should throw when CCCD not found")
        void shouldThrow_whenNotFound() {
            when(scoreRepository.findByCccdAndDPhuongthuc(CCCD, "THPT")).thenReturn(Optional.empty());

            assertThatThrownBy(() -> scoreService.getScoreByCccd(CCCD))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining(CCCD);
        }

        @Test
        @DisplayName("should trim CCCD before lookup")
        void shouldTrimCccd() {
            when(scoreRepository.findByCccdAndDPhuongthuc(CCCD, "THPT")).thenReturn(Optional.empty());

            assertThatThrownBy(() -> scoreService.getScoreByCccd("  " + CCCD + "  "))
                    .isInstanceOf(RuntimeException.class);

            verify(scoreRepository).findByCccdAndDPhuongthuc(CCCD, "THPT");
        }
    }

    // ======================== EXISTS HELPERS ========================

    @Nested
    @DisplayName("exists helpers")
    class ExistsHelpers {

        @Test
        @DisplayName("existsByCccd delegates to repository")
        void existsByCccd_delegates() {
            when(scoreRepository.existsByCccdAndIsDeletedFalse(CCCD)).thenReturn(true);
            assertThat(scoreService.existsByCccd(CCCD)).isTrue();
        }

        @Test
        @DisplayName("existsCandidateByCccd trims input")
        void existsCandidateByCccd_trims() {
            scoreService.existsCandidateByCccd("  " + CCCD + "  ");
            verify(candidateRepository).existsByCccd(CCCD);
        }

        @Test
        @DisplayName("getCandidateByCccd trims input and returns candidate")
        void getCandidateByCccd_returnsCandidate() {
            XtThisinhxettuyen25 candidate = XtThisinhxettuyen25.builder()
                    .cccd(CCCD).sobaodanh(SBD).build();

            when(scoreRepository.findCandidateByCccd(CCCD)).thenReturn(candidate);

            assertThat(scoreService.getCandidateByCccd("  " + CCCD + "  ")).isEqualTo(candidate);
        }
    }

    // ======================== VALIDATION (shared by create/update) ========================

    @Nested
    @DisplayName("validateScore — shared by create and update")
    class ValidateScore {

        // --- CCCD validation ---

        @Test
        @DisplayName("should reject null CCCD")
        void rejectNullCccd() {
            assertThatThrownBy(() -> scoreService.createScore(new ScoreDTO()))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("CCCD không được để trống");
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {"   ", "\t"})
        @DisplayName("should reject blank CCCD")
        void rejectBlankCccd(String cccd) {
            ScoreDTO dto = new ScoreDTO();
            dto.setCccd(cccd);
            assertThatThrownBy(() -> scoreService.createScore(dto))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("CCCD không được để trống");
        }

        @Test
        @DisplayName("should reject CCCD not 12 digits")
        void rejectCccdNot12Digits() {
            ScoreDTO dto = baseDto("THPT");
            dto.setCccd("12345");

            assertThatThrownBy(() -> scoreService.createScore(dto))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("12 chữ số");
        }

        @Test
        @DisplayName("should reject CCCD with letters")
        void rejectCccdWithLetters() {
            ScoreDTO dto = baseDto("THPT");
            dto.setCccd("abcdefghijk1");

            assertThatThrownBy(() -> scoreService.createScore(dto))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("12 chữ số");
        }

        @Test
        @DisplayName("should reject when candidate does not exist")
        void rejectCandidateNotExist() {
            ScoreDTO dto = baseDto("THPT");
            when(candidateRepository.existsByCccd(CCCD)).thenReturn(false);

            assertThatThrownBy(() -> scoreService.createScore(dto))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("CCCD không tồn tại");
        }

        // --- Phuong thuc validation ---

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {"   "})
        @DisplayName("should reject blank phuongThuc")
        void rejectBlankPhuongThuc(String pt) {
            ScoreDTO dto = new ScoreDTO();
            dto.setCccd(CCCD);
            dto.setSobaodanh(SBD);
            dto.setPhuongThuc(pt);
            when(candidateRepository.existsByCccd(CCCD)).thenReturn(true);

            assertThatThrownBy(() -> scoreService.createScore(dto))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Phương thức không được để trống");
        }

        @Test
        @DisplayName("should reject invalid phuongThuc value")
        void rejectInvalidPhuongThuc() {
            ScoreDTO dto = new ScoreDTO();
            dto.setCccd(CCCD);
            dto.setSobaodanh(SBD);
            dto.setPhuongThuc("INVALID");
            when(candidateRepository.existsByCccd(CCCD)).thenReturn(true);

            assertThatThrownBy(() -> scoreService.createScore(dto))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("THPT, DGNL hoặc VSAT");
        }

        @ParameterizedTest
        @ValueSource(strings = {"thpt", "DGNL", " VSAT "})
        @DisplayName("should normalize phuongThuc to uppercase")
        void normalizePhuongThuc(String input) {
            ScoreDTO dto = new ScoreDTO();
            dto.setCccd(CCCD);
            dto.setSobaodanh(SBD);
            dto.setPhuongThuc(input);
            XtDiemthixettuyen entity = baseEntity();
            when(candidateRepository.existsByCccd(CCCD)).thenReturn(true);
            when(scoreRepository.existsByCccdAndDPhuongthuc(eq(CCCD), anyString())).thenReturn(false);
            when(scoreMapper.toEntity(any())).thenReturn(entity);
            when(scoreRepository.save(any())).thenReturn(entity);
            when(scoreMapper.toDto(any())).thenReturn(dto);

            scoreService.createScore(dto);

            assertThat(dto.getPhuongThuc()).isEqualTo(input.trim().toUpperCase());
        }

        // --- Sobao danh validation ---

        @Test
        @DisplayName("should reject null sobaodanh")
        void rejectNullSobaodanh() {
            ScoreDTO dto = new ScoreDTO();
            dto.setCccd(CCCD);
            dto.setPhuongThuc("THPT");
            when(candidateRepository.existsByCccd(CCCD)).thenReturn(true);

            assertThatThrownBy(() -> scoreService.createScore(dto))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Số báo danh không được để trống");
        }

        @Test
        @DisplayName("should reject blank sobaodanh")
        void rejectBlankSobaodanh() {
            ScoreDTO dto = new ScoreDTO();
            dto.setCccd(CCCD);
            dto.setPhuongThuc("THPT");
            dto.setSobaodanh("  ");
            when(candidateRepository.existsByCccd(CCCD)).thenReturn(true);

            assertThatThrownBy(() -> scoreService.createScore(dto))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Số báo danh không được để trống");
        }

        // --- Score range: THPT ---

        @ParameterizedTest(name = "THPT: {0} is valid")
        @CsvSource({"0", "5.5", "10"})
        @DisplayName("should accept THPT scores in [0, 10]")
        void acceptThptScoreRange(double score) {
            ScoreDTO dto = baseDto("THPT");
            dto.setToan(score);
            XtDiemthixettuyen entity = baseEntity();
            when(candidateRepository.existsByCccd(CCCD)).thenReturn(true);
            when(scoreRepository.existsByCccdAndDPhuongthuc(CCCD, "THPT")).thenReturn(false);
            when(scoreMapper.toEntity(any())).thenReturn(entity);
            when(scoreRepository.save(any())).thenReturn(entity);
            when(scoreMapper.toDto(any())).thenReturn(dto);

            assertThatCode(() -> scoreService.createScore(dto)).doesNotThrowAnyException();
        }

        @ParameterizedTest(name = "THPT: {0} should fail")
        @CsvSource({"-0.01", "10.01", "100"})
        @DisplayName("should reject THPT scores outside [0, 10]")
        void rejectThptScoreRange(double score) {
            ScoreDTO dto = baseDto("THPT");
            dto.setToan(score);
            when(candidateRepository.existsByCccd(CCCD)).thenReturn(true);

            assertThatThrownBy(() -> scoreService.createScore(dto))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Toán");
        }

        @Test
        @DisplayName("should reject THPT N1_CC > 10")
        void rejectThptN1CcOver10() {
            ScoreDTO dto = baseDto("THPT");
            dto.setN1Cc(10.5);
            when(candidateRepository.existsByCccd(CCCD)).thenReturn(true);

            assertThatThrownBy(() -> scoreService.createScore(dto))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("N1 CC");
        }

        // --- Score range: VSAT ---

        @ParameterizedTest(name = "VSAT: {0} is valid")
        @CsvSource({"0", "75", "150"})
        @DisplayName("should accept VSAT scores in [0, 150]")
        void acceptVsatScoreRange(double score) {
            ScoreDTO dto = baseDto("VSAT");
            dto.setToan(score);
            XtDiemthixettuyen entity = baseEntity();
            when(candidateRepository.existsByCccd(CCCD)).thenReturn(true);
            when(scoreRepository.existsByCccdAndDPhuongthuc(CCCD, "VSAT")).thenReturn(false);
            when(scoreMapper.toEntity(any())).thenReturn(entity);
            when(scoreRepository.save(any())).thenReturn(entity);
            when(scoreMapper.toDto(any())).thenReturn(dto);

            assertThatCode(() -> scoreService.createScore(dto)).doesNotThrowAnyException();
        }

        @Test
        @DisplayName("should reject VSAT score > 150")
        void rejectVsatOver150() {
            ScoreDTO dto = baseDto("VSAT");
            dto.setToan(150.01);
            when(candidateRepository.existsByCccd(CCCD)).thenReturn(true);

            assertThatThrownBy(() -> scoreService.createScore(dto))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("150");
        }

        @Test
        @DisplayName("should reject VSAT score < 0")
        void rejectVsatNegative() {
            ScoreDTO dto = baseDto("VSAT");
            dto.setVan(-1.0);
            when(candidateRepository.existsByCccd(CCCD)).thenReturn(true);

            assertThatThrownBy(() -> scoreService.createScore(dto))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Văn");
        }

        // --- Score range: DGNL ---

        @Test
        @DisplayName("should accept DGNL NL1 up to 1200")
        void acceptDgnlNl1UpTo1200() {
            ScoreDTO dto = baseDto("DGNL");
            dto.setNl1(1200.0);
            XtDiemthixettuyen entity = baseEntity();
            when(candidateRepository.existsByCccd(CCCD)).thenReturn(true);
            when(scoreRepository.existsByCccdAndDPhuongthuc(CCCD, "DGNL")).thenReturn(false);
            when(scoreMapper.toEntity(any())).thenReturn(entity);
            when(scoreRepository.save(any())).thenReturn(entity);
            when(scoreMapper.toDto(any())).thenReturn(dto);

            assertThatCode(() -> scoreService.createScore(dto)).doesNotThrowAnyException();
        }

        @Test
        @DisplayName("should reject DGNL NL1 > 1200")
        void rejectDgnlNl1Over1200() {
            ScoreDTO dto = baseDto("DGNL");
            dto.setNl1(1201.0);
            when(candidateRepository.existsByCccd(CCCD)).thenReturn(true);

            assertThatThrownBy(() -> scoreService.createScore(dto))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("1200");
        }

        @ParameterizedTest(name = "DGNL NK: {0} is valid")
        @CsvSource({"0", "50", "100"})
        @DisplayName("should accept DGNL NK scores in [0, 100]")
        void acceptDgnlNkRange(double score) {
            ScoreDTO dto = baseDto("DGNL");
            dto.setNk1(score);
            dto.setNk2(score);
            XtDiemthixettuyen entity = baseEntity();
            when(candidateRepository.existsByCccd(CCCD)).thenReturn(true);
            when(scoreRepository.existsByCccdAndDPhuongthuc(CCCD, "DGNL")).thenReturn(false);
            when(scoreMapper.toEntity(any())).thenReturn(entity);
            when(scoreRepository.save(any())).thenReturn(entity);
            when(scoreMapper.toDto(any())).thenReturn(dto);

            assertThatCode(() -> scoreService.createScore(dto)).doesNotThrowAnyException();
        }

        @Test
        @DisplayName("should reject DGNL NK > 100")
        void rejectDgnlNkOver100() {
            ScoreDTO dto = baseDto("DGNL");
            dto.setNk1(100.01);
            when(candidateRepository.existsByCccd(CCCD)).thenReturn(true);

            assertThatThrownBy(() -> scoreService.createScore(dto))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("100");
        }

        // --- Null scores are allowed ---

        @Test
        @DisplayName("should accept null subject scores (optional fields)")
        void acceptNullScores() {
            ScoreDTO dto = baseDto("THPT");
            dto.setToan(null);
            dto.setLy(null);
            dto.setN1Thi(null);
            XtDiemthixettuyen entity = baseEntity();
            when(candidateRepository.existsByCccd(CCCD)).thenReturn(true);
            when(scoreRepository.existsByCccdAndDPhuongthuc(CCCD, "THPT")).thenReturn(false);
            when(scoreMapper.toEntity(any())).thenReturn(entity);
            when(scoreRepository.save(any())).thenReturn(entity);
            when(scoreMapper.toDto(any())).thenReturn(dto);

            assertThatCode(() -> scoreService.createScore(dto)).doesNotThrowAnyException();
        }
    }

    // ======================== CREATE ========================

    @Nested
    @DisplayName("createScore")
    class CreateScore {

        @Test
        @DisplayName("should create and save score successfully")
        void shouldCreateSuccessfully() {
            ScoreDTO dto = baseDto("THPT");
            XtDiemthixettuyen entity = baseEntity();
            ScoreDTO savedDto = baseDto("THPT");

            when(candidateRepository.existsByCccd(CCCD)).thenReturn(true);
            when(scoreRepository.existsByCccdAndDPhuongthuc(CCCD, "THPT")).thenReturn(false);
            when(scoreMapper.toEntity(dto)).thenReturn(entity);
            when(scoreRepository.save(entity)).thenReturn(entity);
            when(scoreMapper.toDto(entity)).thenReturn(savedDto);

            ScoreDTO result = scoreService.createScore(dto);

            assertThat(result).isEqualTo(savedDto);
            assertThat(entity.getIsDeleted()).isFalse();
            verify(aspirationScoreService).calculateAllForCccd(CCCD);
        }

        @Test
        @DisplayName("should reject duplicate CCCD + phuongThuc")
        void rejectDuplicate() {
            ScoreDTO dto = baseDto("THPT");
            when(candidateRepository.existsByCccd(CCCD)).thenReturn(true);
            when(scoreRepository.existsByCccdAndDPhuongthuc(CCCD, "THPT")).thenReturn(true);

            assertThatThrownBy(() -> scoreService.createScore(dto))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("đã có điểm cho phương thức: THPT");
            verify(scoreRepository, never()).save(any());
        }

        @Test
        @DisplayName("should trim CCCD before creating")
        void shouldTrimCccd() {
            ScoreDTO dto = baseDto("THPT");
            dto.setCccd("  " + CCCD + "  ");
            XtDiemthixettuyen entity = baseEntity();

            when(candidateRepository.existsByCccd(CCCD)).thenReturn(true);
            when(scoreRepository.existsByCccdAndDPhuongthuc(CCCD, "THPT")).thenReturn(false);
            when(scoreMapper.toEntity(any())).thenReturn(entity);
            when(scoreRepository.save(any())).thenReturn(entity);
            when(scoreMapper.toDto(any())).thenReturn(dto);

            scoreService.createScore(dto);

            assertThat(dto.getCccd()).isEqualTo(CCCD);
        }

        @Test
        @DisplayName("should continue even if aspiration recalculation fails")
        void shouldContinueOnAspirationFail() {
            ScoreDTO dto = baseDto("THPT");
            XtDiemthixettuyen entity = baseEntity();

            when(candidateRepository.existsByCccd(CCCD)).thenReturn(true);
            when(scoreRepository.existsByCccdAndDPhuongthuc(CCCD, "THPT")).thenReturn(false);
            when(scoreMapper.toEntity(any())).thenReturn(entity);
            when(scoreRepository.save(any())).thenReturn(entity);
            when(scoreMapper.toDto(any())).thenReturn(dto);
            doThrow(new RuntimeException("async fail"))
                    .when(aspirationScoreService).calculateAllForCccd(CCCD);

            assertThatCode(() -> scoreService.createScore(dto)).doesNotThrowAnyException();
        }
    }

    // ======================== UPDATE ========================

    @Nested
    @DisplayName("updateScore")
    class UpdateScore {

        @Test
        @DisplayName("should update existing score")
        void shouldUpdateExisting() {
            ScoreDTO dto = baseDto("THPT");
            XtDiemthixettuyen existing = baseEntity();

            when(candidateRepository.existsByCccd(CCCD)).thenReturn(true);
            when(scoreRepository.findByCccdAndDPhuongthuc(CCCD, "THPT"))
                    .thenReturn(Optional.of(existing));
            when(scoreRepository.save(existing)).thenReturn(existing);
            when(scoreMapper.toDto(existing)).thenReturn(dto);

            scoreService.updateScore(dto);

            verify(scoreMapper).updateEntityFromDto(dto, existing);
            verify(aspirationScoreService).calculateAllForCccd(CCCD);
        }

        @Test
        @DisplayName("should throw when score not found")
        void shouldThrowWhenNotFound() {
            ScoreDTO dto = baseDto("THPT");
            when(candidateRepository.existsByCccd(CCCD)).thenReturn(true);
            when(scoreRepository.findByCccdAndDPhuongthuc(CCCD, "THPT"))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() -> scoreService.updateScore(dto))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Không tìm thấy điểm thi cho CCCD: " + CCCD);
        }

        @Test
        @DisplayName("should handle DataIntegrityViolationException")
        void shouldHandleIntegrityViolation() {
            ScoreDTO dto = baseDto("THPT");
            XtDiemthixettuyen existing = baseEntity();

            when(candidateRepository.existsByCccd(CCCD)).thenReturn(true);
            when(scoreRepository.findByCccdAndDPhuongthuc(CCCD, "THPT"))
                    .thenReturn(Optional.of(existing));
            when(scoreRepository.save(existing))
                    .thenThrow(new DataIntegrityViolationException("dup"));

            assertThatThrownBy(() -> scoreService.updateScore(dto))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Cập nhật thất bại");
        }

        @Test
        @DisplayName("should run same validation as create")
        void shouldValidateLikeCreate() {
            ScoreDTO dto = baseDto("THPT");
            dto.setToan(15.0);

            when(candidateRepository.existsByCccd(CCCD)).thenReturn(true);

            assertThatThrownBy(() -> scoreService.updateScore(dto))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Toán");
        }
    }

    // ======================== DELETE ========================

    @Nested
    @DisplayName("deleteScore")
    class DeleteScore {

        @Test
        @DisplayName("should soft delete existing score")
        void shouldSoftDelete() {
            XtDiemthixettuyen entity = baseEntity();

            when(scoreRepository.findByCccdAndDPhuongthuc(CCCD, "THPT")).thenReturn(Optional.of(entity));
            when(scoreRepository.save(entity)).thenReturn(entity);

            scoreService.deleteScore(CCCD, "THPT");

            assertThat(entity.getIsDeleted()).isTrue();
        }

        @Test
        @DisplayName("should throw when CCCD not found")
        void shouldThrowWhenNotFound() {
            when(scoreRepository.findByCccdAndDPhuongthuc(CCCD, "THPT")).thenReturn(Optional.empty());

            assertThatThrownBy(() -> scoreService.deleteScore(CCCD, "THPT"))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining(CCCD);
        }

        @Test
        @DisplayName("should trim CCCD before deletion")
        void shouldTrimCccd() {
            when(scoreRepository.findByCccdAndDPhuongthuc(CCCD, "THPT")).thenReturn(Optional.empty());

            assertThatThrownBy(() -> scoreService.deleteScore("  " + CCCD + "  ", "THPT"))
                    .isInstanceOf(RuntimeException.class);

            verify(scoreRepository).findByCccdAndDPhuongthuc(CCCD, "THPT");
        }
    }

    // ======================== IMPORT EXCEL ========================

    @Nested
    @DisplayName("importExcel")
    class ImportExcel {

        @Test
        @DisplayName("should create new score for valid row")
        void shouldCreateForValidRow() throws Exception {
            ScoreDTO dto = baseDto("THPT");
            XtDiemthixettuyen entity = baseEntity();

            try (MockedStatic<ExcelUtil> excel = mockStatic(ExcelUtil.class)) {
                excel.when(() -> ExcelUtil.importExcel(any(), eq(ScoreDTO.class)))
                        .thenReturn(List.of(dto));

                when(candidateRepository.existsByCccd(CCCD)).thenReturn(true);
                when(scoreRepository.existsByCccdAndDPhuongthuc(CCCD, "THPT")).thenReturn(false);
                when(scoreMapper.toEntity(any())).thenReturn(entity);
                when(scoreRepository.save(entity)).thenReturn(entity);
                when(scoreMapper.toDto(entity)).thenReturn(dto);

                ImportResult<ScoreDTO> result = scoreService.importExcel(
                        new ByteArrayInputStream(new byte[0]));

                assertThat(result.getSuccessCount()).isEqualTo(1);
                assertThat(result.getErrorCount()).isZero();
            }
        }

        @Test
        @DisplayName("should update when CCCD+method already exists")
        void shouldUpdateWhenExists() throws Exception {
            ScoreDTO dto = baseDto("THPT");
            XtDiemthixettuyen existing = baseEntity();

            try (MockedStatic<ExcelUtil> excel = mockStatic(ExcelUtil.class)) {
                excel.when(() -> ExcelUtil.importExcel(any(), eq(ScoreDTO.class)))
                        .thenReturn(List.of(dto));

                when(candidateRepository.existsByCccd(CCCD)).thenReturn(true);
                when(scoreRepository.existsByCccdAndDPhuongthuc(CCCD, "THPT")).thenReturn(true);
                when(scoreRepository.findByCccdAndDPhuongthuc(CCCD, "THPT"))
                        .thenReturn(Optional.of(existing));
                when(scoreRepository.save(existing)).thenReturn(existing);
                when(scoreMapper.toDto(existing)).thenReturn(dto);

                ImportResult<ScoreDTO> result = scoreService.importExcel(
                        new ByteArrayInputStream(new byte[0]));

                assertThat(result.getSuccessCount()).isEqualTo(1);
                verify(scoreMapper).updateEntityFromDto(dto, existing);
            }
        }

        @Test
        @DisplayName("should collect error for invalid row and continue")
        void shouldCollectErrorAndContinue() throws Exception {
            ScoreDTO bad = new ScoreDTO();
            bad.setCccd("BAD");
            bad.setSobaodanh("S");

            try (MockedStatic<ExcelUtil> excel = mockStatic(ExcelUtil.class)) {
                excel.when(() -> ExcelUtil.importExcel(any(), eq(ScoreDTO.class)))
                        .thenReturn(List.of(bad));

                ImportResult<ScoreDTO> result = scoreService.importExcel(
                        new ByteArrayInputStream(new byte[0]));

                assertThat(result.getErrorCount()).isEqualTo(1);
                assertThat(result.getErrors().getFirst()).startsWith("Dòng 2");
                assertThat(result.getSuccessCount()).isZero();
            }
        }

        @Test
        @DisplayName("should uppercase phuongThuc during import")
        void shouldUppercasePhuongThuc() throws Exception {
            ScoreDTO dto = baseDto("thpt");
            XtDiemthixettuyen entity = baseEntity();

            try (MockedStatic<ExcelUtil> excel = mockStatic(ExcelUtil.class)) {
                excel.when(() -> ExcelUtil.importExcel(any(), eq(ScoreDTO.class)))
                        .thenReturn(List.of(dto));

                when(candidateRepository.existsByCccd(CCCD)).thenReturn(true);
                when(scoreRepository.existsByCccdAndDPhuongthuc(CCCD, "THPT")).thenReturn(false);
                when(scoreMapper.toEntity(any())).thenReturn(entity);
                when(scoreRepository.save(entity)).thenReturn(entity);
                when(scoreMapper.toDto(entity)).thenReturn(dto);

                scoreService.importExcel(new ByteArrayInputStream(new byte[0]));

                assertThat(dto.getPhuongThuc()).isEqualTo("THPT");
            }
        }

        @Test
        @DisplayName("should auto-fill sobaodanh from candidate when blank")
        void shouldAutoFillSbd() throws Exception {
            ScoreDTO dto = new ScoreDTO();
            dto.setCccd(CCCD);
            dto.setSobaodanh("");
            dto.setPhuongThuc("THPT");
            XtThisinhxettuyen25 candidate = XtThisinhxettuyen25.builder()
                    .cccd(CCCD).sobaodanh("AUTO001").build();
            XtDiemthixettuyen entity = baseEntity();

            try (MockedStatic<ExcelUtil> excel = mockStatic(ExcelUtil.class)) {
                excel.when(() -> ExcelUtil.importExcel(any(), eq(ScoreDTO.class)))
                        .thenReturn(List.of(dto));

                when(scoreRepository.findCandidateByCccd(CCCD)).thenReturn(candidate);
                when(candidateRepository.existsByCccd(CCCD)).thenReturn(true);
                when(scoreRepository.existsByCccdAndDPhuongthuc(CCCD, "THPT")).thenReturn(false);
                when(scoreMapper.toEntity(any())).thenReturn(entity);
                when(scoreRepository.save(entity)).thenReturn(entity);
                when(scoreMapper.toDto(entity)).thenReturn(dto);

                scoreService.importExcel(new ByteArrayInputStream(new byte[0]));

                assertThat(dto.getSobaodanh()).isEqualTo("AUTO001");
            }
        }

        @Test
        @DisplayName("should not auto-fill when sobaodanh already has value")
        void shouldNotAutoFillWhenSbdPresent() throws Exception {
            ScoreDTO dto = baseDto("THPT");
            XtDiemthixettuyen entity = baseEntity();

            try (MockedStatic<ExcelUtil> excel = mockStatic(ExcelUtil.class)) {
                excel.when(() -> ExcelUtil.importExcel(any(), eq(ScoreDTO.class)))
                        .thenReturn(List.of(dto));

                when(candidateRepository.existsByCccd(CCCD)).thenReturn(true);
                when(scoreRepository.existsByCccdAndDPhuongthuc(CCCD, "THPT")).thenReturn(false);
                when(scoreMapper.toEntity(any())).thenReturn(entity);
                when(scoreRepository.save(entity)).thenReturn(entity);
                when(scoreMapper.toDto(entity)).thenReturn(dto);

                scoreService.importExcel(new ByteArrayInputStream(new byte[0]));

                assertThat(dto.getSobaodanh()).isEqualTo(SBD);
            }
        }

        @Test
        @DisplayName("should handle multiple rows with mixed results")
        void shouldHandleMixedRows() throws Exception {
            ScoreDTO valid = baseDto("THPT");
            ScoreDTO invalid = new ScoreDTO();
            invalid.setCccd("BAD");

            XtDiemthixettuyen entity = baseEntity();
            XtThisinhxettuyen25 candidate = XtThisinhxettuyen25.builder()
                    .cccd(CCCD).sobaodanh(SBD).build();

            try (MockedStatic<ExcelUtil> excel = mockStatic(ExcelUtil.class)) {
                excel.when(() -> ExcelUtil.importExcel(any(), eq(ScoreDTO.class)))
                        .thenReturn(List.of(valid, invalid));

                when(scoreRepository.findCandidateByCccd(CCCD)).thenReturn(candidate);
                when(candidateRepository.existsByCccd(CCCD)).thenReturn(true);
                when(scoreRepository.existsByCccdAndDPhuongthuc(CCCD, "THPT")).thenReturn(false);
                when(scoreMapper.toEntity(any())).thenReturn(entity);
                when(scoreRepository.save(entity)).thenReturn(entity);
                when(scoreMapper.toDto(entity)).thenReturn(valid);

                ImportResult<ScoreDTO> result = scoreService.importExcel(
                        new ByteArrayInputStream(new byte[0]));

                assertThat(result.getTotalRows()).isEqualTo(2);
                assertThat(result.getSuccessCount()).isEqualTo(1);
                assertThat(result.getErrorCount()).isEqualTo(1);
            }
        }

        @Test
        @DisplayName("should wrap Excel read error in RuntimeException")
        void shouldWrapExcelReadError() throws Exception {
            try (MockedStatic<ExcelUtil> excel = mockStatic(ExcelUtil.class)) {
                excel.when(() -> ExcelUtil.importExcel(any(), eq(ScoreDTO.class)))
                        .thenThrow(new RuntimeException("file corrupt"));

                assertThatThrownBy(() -> scoreService.importExcel(
                                new ByteArrayInputStream(new byte[0])))
                        .isInstanceOf(RuntimeException.class)
                        .hasMessageContaining("Lỗi khi đọc file Excel");
            }
        }

        @Test
        @DisplayName("should accept score=0 as boundary")
        void shouldAcceptZeroScore() throws Exception {
            ScoreDTO dto = baseDto("THPT");
            dto.setToan(0.0);
            dto.setLy(0.0);
            dto.setHoa(0.0);
            XtDiemthixettuyen entity = baseEntity();

            try (MockedStatic<ExcelUtil> excel = mockStatic(ExcelUtil.class)) {
                excel.when(() -> ExcelUtil.importExcel(any(), eq(ScoreDTO.class)))
                        .thenReturn(List.of(dto));

                when(candidateRepository.existsByCccd(CCCD)).thenReturn(true);
                when(scoreRepository.existsByCccdAndDPhuongthuc(CCCD, "THPT")).thenReturn(false);
                when(scoreMapper.toEntity(any())).thenReturn(entity);
                when(scoreRepository.save(entity)).thenReturn(entity);
                when(scoreMapper.toDto(entity)).thenReturn(dto);

                ImportResult<ScoreDTO> result = scoreService.importExcel(
                        new ByteArrayInputStream(new byte[0]));

                assertThat(result.getSuccessCount()).isEqualTo(1);
            }
        }
    }

    // ======================== EXPORT EXCEL ========================

    @Nested
    @DisplayName("exportExcel")
    class ExportExcel {

        @Test
        @DisplayName("should export scores to Excel")
        void shouldExportScores() throws Exception {
            XtDiemthixettuyen entity = baseEntity();
            ScoreDTO dto = baseDto("THPT");

            try (MockedStatic<ExcelUtil> excel = mockStatic(ExcelUtil.class);
                 ByteArrayOutputStream out = new ByteArrayOutputStream()) {

                when(scoreRepository.searchScores(null, null, Pageable.unpaged()))
                        .thenReturn(new PageImpl<>(List.of(entity)));
                when(scoreMapper.toDto(entity)).thenReturn(dto);
                excel.when(() -> ExcelUtil.exportExcel(anyList(), eq(ScoreDTO.class), any(OutputStream.class)))
                        .then(invocation -> null);

                scoreService.exportExcel(out, null, null);

                excel.verify(() -> ExcelUtil.exportExcel(
                        argThat(list -> list.size() == 1), eq(ScoreDTO.class), eq(out)));
            }
        }

        @Test
        @DisplayName("should apply keyword filter when exporting")
        void shouldFilterByKeywordOnExport() throws Exception {
            try (MockedStatic<ExcelUtil> excel = mockStatic(ExcelUtil.class);
                 ByteArrayOutputStream out = new ByteArrayOutputStream()) {

                when(scoreRepository.searchScores("%abc%", null, Pageable.unpaged()))
                        .thenReturn(new PageImpl<>(Collections.emptyList()));
                excel.when(() -> ExcelUtil.exportExcel(anyList(), eq(ScoreDTO.class), any(OutputStream.class)))
                        .then(invocation -> null);

                scoreService.exportExcel(out, "abc", null);

                verify(scoreRepository).searchScores("%abc%", null, Pageable.unpaged());
            }
        }

        @Test
        @DisplayName("should apply phuongThuc filter when exporting")
        void shouldFilterByMethodOnExport() throws Exception {
            try (MockedStatic<ExcelUtil> excel = mockStatic(ExcelUtil.class);
                 ByteArrayOutputStream out = new ByteArrayOutputStream()) {

                when(scoreRepository.searchScores(null, "DGNL", Pageable.unpaged()))
                        .thenReturn(new PageImpl<>(Collections.emptyList()));
                excel.when(() -> ExcelUtil.exportExcel(anyList(), eq(ScoreDTO.class), any(OutputStream.class)))
                        .then(invocation -> null);

                scoreService.exportExcel(out, null, "DGNL");

                verify(scoreRepository).searchScores(null, "DGNL", Pageable.unpaged());
            }
        }

        @Test
        @DisplayName("should wrap export errors in RuntimeException")
        void shouldWrapExportError() throws Exception {
            try (MockedStatic<ExcelUtil> excel = mockStatic(ExcelUtil.class);
                 ByteArrayOutputStream out = new ByteArrayOutputStream()) {

                when(scoreRepository.searchScores(any(), any(), any(Pageable.class)))
                        .thenThrow(new RuntimeException("db down"));

                assertThatThrownBy(() -> scoreService.exportExcel(out, null, null))
                        .isInstanceOf(RuntimeException.class)
                        .hasMessageContaining("Lỗi khi xuất file Excel");
            }
        }
    }
}
