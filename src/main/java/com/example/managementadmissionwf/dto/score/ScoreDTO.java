package com.example.managementadmissionwf.dto.score;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for Score Management
 * Maps to xt_diemthixettuyen + xt_diemcongxettuyen tables
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScoreDTO {
    // Candidate info
    private String cccd;
    private String sobaodanh;
    private String phuongThuc; // THPT, DGNL, VSAT
    
    // Subject scores
    private Double toan;    // TO
    private Double ly;      // LI
    private Double hoa;     // HO
    private Double sinh;    // SI
    private Double su;      // SU
    private Double dia;     // DI
    private Double van;     // VA
    
    // Foreign language
    private Double n1Thi;   // N1_THI
    private Double n1Cc;    // N1_CC
    
    // Other exams
    private Double nl1;     // NL1
    private Double nk1;     // NK1
    private Double nk2;     // NK2
    
    // Bonus points
    private Double diemCc;
    private Double diemUtxt;
    private Double diemTong;
    
    // Helper method to get N1_CC (max of thi and cc)
    public Double getN1CcCalculated() {
        if (n1Thi == null && n1Cc == null) {
            return 0.0;
        }
        if (n1Cc == null) {
            return n1Thi;
        }
        if (n1Thi == null) {
            return n1Cc;
        }
        return Math.max(n1Thi, n1Cc);
    }
    
    // Helper method to calculate total bonus points
    public Double calculateTotalBonus() {
        double total = 0.0;
        if (diemCc != null) total += diemCc;
        if (diemUtxt != null) total += diemUtxt;
        return total;
    }
}