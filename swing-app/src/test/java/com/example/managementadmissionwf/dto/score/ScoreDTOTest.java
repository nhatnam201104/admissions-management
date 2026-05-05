package com.example.managementadmissionwf.dto.score;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

@DisplayName("ScoreDTO - Unit Tests")
class ScoreDTOTest {

    @Nested
    @DisplayName("getN1CcCalculated")
    class GetN1CcCalculated {

        @Test
        @DisplayName("should return n1Thi when n1Cc is null")
        void shouldReturnN1Thi_whenN1CcIsNull() {
            ScoreDTO dto = ScoreDTO.builder().n1Thi(8.0).build();

            assertThat(dto.getN1CcCalculated()).isEqualTo(8.0);
        }

        @Test
        @DisplayName("should return n1Cc when n1Thi is null")
        void shouldReturnN1Cc_whenN1ThiIsNull() {
            ScoreDTO dto = ScoreDTO.builder().n1Cc(7.0).build();

            assertThat(dto.getN1CcCalculated()).isEqualTo(7.0);
        }

        @Test
        @DisplayName("should return max of n1Thi and n1Cc when both present")
        void shouldReturnMax_whenBothPresent() {
            ScoreDTO dto = ScoreDTO.builder().n1Thi(6.0).n1Cc(8.0).build();

            assertThat(dto.getN1CcCalculated()).isEqualTo(8.0);
        }

        @Test
        @DisplayName("should return n1Thi when both are equal")
        void shouldReturnN1Thi_whenBothEqual() {
            ScoreDTO dto = ScoreDTO.builder().n1Thi(7.5).n1Cc(7.5).build();

            assertThat(dto.getN1CcCalculated()).isEqualTo(7.5);
        }

        @Test
        @DisplayName("should return null when both n1Thi and n1Cc are null")
        void shouldReturnNull_whenBothNull() {
            ScoreDTO dto = ScoreDTO.builder().build();

            assertThat(dto.getN1CcCalculated()).isNull();
        }
    }

    @Nested
    @DisplayName("builder pattern")
    class BuilderPattern {

        @Test
        @DisplayName("should build DTO with all fields")
        void shouldBuildDtoWithAllFields() {
            ScoreDTO dto = ScoreDTO.builder()
                    .cccd("001234567890")
                    .sobaodanh("SBD001")
                    .phuongThuc("THPT")
                    .toan(8.5).ly(7.0).hoa(9.0)
                    .sinh(6.5).su(8.0).dia(7.5).van(8.0)
                    .n1Thi(7.5).n1Cc(8.0)
                    .nl1(950.0).nk1(80.0).nk2(75.0)
                    .build();

            assertThat(dto.getCccd()).isEqualTo("001234567890");
            assertThat(dto.getSobaodanh()).isEqualTo("SBD001");
            assertThat(dto.getPhuongThuc()).isEqualTo("THPT");
            assertThat(dto.getToan()).isEqualTo(8.5);
            assertThat(dto.getNl1()).isEqualTo(950.0);
            assertThat(dto.getNk1()).isEqualTo(80.0);
            assertThat(dto.getNk2()).isEqualTo(75.0);
        }

        @Test
        @DisplayName("should create empty DTO with no-args constructor")
        void shouldCreateEmptyDto_withNoArgsConstructor() {
            ScoreDTO dto = new ScoreDTO();

            assertThat(dto.getCccd()).isNull();
            assertThat(dto.getToan()).isNull();
            assertThat(dto.getPhuongThuc()).isNull();
        }
    }

    @Nested
    @DisplayName("equals and hashCode")
    class EqualsAndHashCode {

        @Test
        @DisplayName("should consider two DTOs with same values as equal")
        void shouldConsiderEqual_whenSameValues() {
            ScoreDTO dto1 = ScoreDTO.builder().cccd("001234567890").toan(8.0).build();
            ScoreDTO dto2 = ScoreDTO.builder().cccd("001234567890").toan(8.0).build();

            assertThat(dto1).isEqualTo(dto2);
            assertThat(dto1.hashCode()).isEqualTo(dto2.hashCode());
        }

        @Test
        @DisplayName("should consider two DTOs with different values as not equal")
        void shouldConsiderNotEqual_whenDifferentValues() {
            ScoreDTO dto1 = ScoreDTO.builder().cccd("001234567890").toan(8.0).build();
            ScoreDTO dto2 = ScoreDTO.builder().cccd("001234567891").toan(8.0).build();

            assertThat(dto1).isNotEqualTo(dto2);
        }
    }
}
