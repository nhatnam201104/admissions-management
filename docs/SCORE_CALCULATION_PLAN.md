# KẾ HOẠCH HOÀN CHỈNH: QUẢN LÝ ĐIỂM TUYỂN SINH

**Ngày:** 2026-05-04
**Branch:** feature/47-fix-logic-score-management
**Ràng buộc:** KHÔNG thay đổi database schema

---

## TỔNG QUAN VẤN ĐỀ

### 1.1 Source hiện có

**Entity chính:**
- `XtDiemthixettuyen` - Điểm thi (1 record/thí sinh, unique CCCD)
- `XtBangquydoi` - Bảng quy đổi điểm (ĐGNL/SAT/IELTS → thang 30)
- `XtNganhTohop` - Hệ số tổ hợp (hsmon1/2/3 theo ngành+tổ hợp)
- `XtNguyenvongxettuyen` - Nguyện vọng (diemThxt, diemUtqd, diemCong, diemXettuyen)
- `XtDiemcongxettuyen` - Điểm cộng (diemCc, diemUtxt, diemTong)

### 1.2 Vấn đề nghiệp vụ cần giải quyết

| # | Vấn đề | Mức độ | Nguyên nhân gốc |
|---|--------|--------|----------------|
| 1 | Ô trống lưu thành 0.0 | CRITICAL | `getValue()` default về 0.0 khi empty |
| 2 | Không phân biệt null vs zero | CRITICAL | DTO `@Min(0)` không reject null, entity lưu Double |
| 3 | Một thí sinh chỉ một phương thức | HIGH | Thiết kế 1 điểm/thí sinh không đủ cho DGNL+THPT |
| 4 | Điểm không gắn nguyện vọng | HIGH | Diemthixettuyen đứng riêng, không FK tới nguyện vọng |
| 5 | Quy đổi điểm chưa áp dụng | HIGH | Bangquydoi table tồn tại nhưng ScoreService không dùng |
| 6 | Hệ số tổ hợp chưa áp dụng | HIGH | NganhTohop có hsmon nhưng tính điểm không nhân |
| 7 | Chưa kiểm tra đủ môn | MEDIUM | Không validate đủ 3 môn cho tổ hợp xét tuyển |

---

## PHẦN 1: SỬA LỖI NULL VS ZERO

### 1.1 Vấn đề hiện tại

```java
// ScoreFormDialog.java - DÒNG 426-446
private Double getValue(JFormattedTextField f) {
    String t = f.getText() == null ? "" : f.getText().trim();
    if (t.isEmpty()) return 0.0;  // ← LỖI: ô trống = 0.0
    // ...
}
```

**Hậu quả:**
- Thí sinh không có điểm Toán → lưu thành `toan = 0.0`
- Thí sinh có điểm Toán = 0 → cũng lưu `toan = 0.0`
- Không phân biệt được hai trường hợp

### 1.2 Giải pháp

**Thay đổi `ScoreDTO`:**
```java
// Thay @Min(0) bằng validate riêng cho phép null
@ExcelColumn(name = "Toán")
private Double toan;  // Nullable - null = chưa có điểm

// Validation riêng cho từng phương thức
public void validateForPhuongThuc(String phuongThuc) {
    if ("THPT".equals(phuongThuc)) {
        requireRange(toan, 0, 10, "Toán");
        requireRange(van, 0, 10, "Văn");
        // ...
    }
}
```

**Thay đổi `ScoreFormDialog`:**
```java
private Double getValue(JFormattedTextField f) {
    String t = f.getText() == null ? "" : f.getText().trim();
    if (t.isEmpty()) return null;  // ← ĐÚNG: null = chưa nhập
    // ...
}
```

**Thay đổi `ScoreServiceImpl`:**
```java
// validateRange cho phép null (không throw khi null)
private void validateRange(String fieldName, Double value, double min, double max) {
    if (value == null) return;  // null = chưa có điểm, hợp lệ
    if (value < min || value > max) {
        throw new RuntimeException(...);
    }
}
```

### 1.3 File cần sửa

| File | Thay đổi |
|------|----------|
| `ScoreDTO.java` | Bỏ @Min(0), đổi thành nullable Double |
| `ScoreFormDialog.java` | `getValue()` trả null khi empty |
| `ScoreServiceImpl.java` | `validateRange()` cho phép null |

---

## PHẦN 2: REFACTOR SCORE SAVING - GẮN NGUYỆN VỌNG

### 2.1 Vấn đề hiện tại

Khi lưu điểm, hệ thống:
1. Lưu vào `xt_diemthixettuyen` (độc lập, không FK)
2. KHÔNG cập nhật `xt_nguyenvongxettuyen`
3. KHÔNG tính điểm xét tuyển theo tổ hợp

→ Điểm lưu xong nhưng nguyện vọng vẫn có `diem_xettuyen = null`

### 2.2 Thiết kế mới

**Khi lưu điểm thành công, đồng thời:**
1. Load các nguyện vọng của thí sinh
2. Với mỗi nguyện vọng: tính điểm theo tổ hợp + hệ số
3. Cập nhật `diem_thxt`, `diem_xettuyen` cho từng nguyện vọng

```java
@Transactional
public ScoreDTO saveScore(ScoreDTO dto, boolean isUpdate) {
    // 1. Save điểm vào xt_diemthixettuyen
    ScoreDTO saved = isUpdate ? updateScore(dto) : createScore(dto);

    // 2. Load nguyện vọng của thí sinh
    List<XtNguyenvongxettuyen> aspirations =
        nguyenVongRepository.findByCccdActive(dto.getCccd());

    // 3. Với mỗi nguyện vọng, tính và cập nhật điểm
    for (XtNguyenvongxettuyen nv : aspirations) {
        calculateAndUpdateAspirationScore(nv, saved);
    }

    return saved;
}
```

### 2.3 Service mới: AspirationScoreService

```java
public interface AspirationScoreService {
    /**
     * Tính điểm cho một nguyện vọng dựa trên điểm thi
     */
    AspirationScoreResult calculateForAspiration(
        XtNguyenvongxettuyen aspiration,
        XtDiemthixettuyen score
    );
}
```

### 2.4 File cần thêm

| File | Mô tả |
|------|-------|
| `AspirationScoreService.java` | Interface tính điểm nguyện vọng |
| `AspirationScoreServiceImpl.java` | Implement đầy đủ pipeline 9 bước |
| `AspirationScoreResult.java` | DTO kết quả tính điểm |

### 2.5 File cần sửa

| File | Thay đổi |
|------|----------|
| `ScoreServiceImpl.java` | Gọi AspirationScoreService sau khi lưu điểm |
| `NguyenVongRepository.java` | Thêm `findByCccdActive()` |

---

## PHẦN 3: PIPELINE TÍNH ĐIỂM 9 BƯỚC

### 3.1 Sơ đồ luồng

```
┌─────────────────────────────────────────────────────────────────┐
│  BẮT ĐẦU: XtNguyenvongxettuyen + XtDiemthixettuyen            │
└─────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│  BƯỚC 1: Xác định tổ hợp của nguyện vọng                        │
│  - Từ XtNganhTohop theo (manganh, matohop)                    │
│  - Lấy thMon1/2/3, hsmon1/2/3                                   │
└─────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│  BƯỚC 2: Map mã môn → trường điểm trong entity                  │
│  - TO → toan, LI → ly, HO → hoa, SI → sinh, SU → su             │
│  - DI → dia, VA → van, N1 → n1Thi/n1Cc                          │
│  - NL1 → nl1, NK1 → nk1, NK2 → nk2                              │
└─────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│  BƯỚC 3: Lấy điểm gốc (Double, có thể null)                     │
│  - Lấy từ XtDiemthixettuyen theo cccd                          │
│  - Đánh dấu null = chưa có điểm môn này                         │
└─────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│  BƯỚC 4: QUY ĐỔI điểm (nếu cần)                                  │
│  - Tìm rule trong XtBangquydoi                                 │
│  - Áp dụng khi phương thức ≠ THPT hoặc môn ngoại ngữ             │
│  - Ví dụ: SAT 1380 → 18/30 (theo bảng quy đổi)                   │
└─────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│  BƯỚC 5: ÁP DỤNG HỆ SỐ                                          │
│  - Nhân hsmon cho từng môn (hsMon1/2/3)                        │
│  - Ví dụ: Toán×2.0, Lý×1.0, Hóa×1.0                            │
└─────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│  BƯỚC 6: TÍNH ĐIỂM THXT (tổng 3 môn sau hệ số)                   │
│  - Không hệ số 2: diemMon1 + diemMon2 + diemMon3                │
│  - Có hệ số 2: (M×2 + M2 + M3) × 3/4                            │
│  - Max = 30 điểm                                                │
└─────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│  BƯỚC 7: CỘNG ĐIỂM ƯU TIÊN                                     │
│  - Từ XtDiemcongxettuyen (diemUtxt + diemCc = diemTong)        │
│  - Điều chỉnh nếu tổng ≥ 22.5 (công thức giảm)                   │
└─────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│  BƯỚC 8: TÍNH ĐIỂM XÉT TUYỂN CUỐI CÙNG                          │
│  - diemXettuyen = diemThxt + diemTong                            │
└─────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│  BƯỚC 9: SO SÁNH VỚI ĐIỂM SÀN                                   │
│  - diemSan từ XtNganh.nDiemsan                                  │
│  - datDiemSan = diemXettuyen >= diemSan                         │
└─────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│  LƯU: Cập nhật xt_nguyenvongxettuyen                             │
│  - diem_thxt = diemThxt                                          │
│  - diem_xettuyen = diemXettuyen                                  │
│  - nv_ketqua = datDiemSan ? "CHO_XET" : "TRUOT"                 │
└─────────────────────────────────────────────────────────────────┘
```

### 3.2 Chi tiết Bước 4: Quy đổi điểm

```java
private Double convertScore(Double original, String phuongThuc, String toHop, String mon) {
    if (original == null) return null;

    Optional<XtBangquydoi> rule = conversionRepo
        .findByPhuongThucAndMonAndTohop(phuongThuc, mon, toHop);

    if (rule.isEmpty()) {
        rule = conversionRepo.findByPhuongThucAndMonAndTohop(phuongThuc, mon, null);
    }

    if (rule.isPresent()) {
        XtBangquydoi r = rule.get();
        if (original >= r.getDDiema() && original <= r.getDDiemb()) {
            double ratio = (original - r.getDDiema()) / (r.getDDiemb() - r.getDDiema());
            return r.getDDiemc() + ratio * (r.getDDiemd() - r.getDDiemc());
        }
    }

    return original; // Không có rule → giữ nguyên
}
```

### 3.3 Chi tiết Bước 5: Áp dụng hệ số

```java
private Double applyCoefficient(Double score, Double coefficient) {
    if (score == null || coefficient == null) return score;
    if (coefficient == 1.0) return score;
    return score * coefficient;
}

// Kiểm tra có hệ số 2 không
private boolean hasCoefficient2(Double hs1, Double hs2, Double hs3) {
    return (hs1 != null && hs1 == 2.0)
        || (hs2 != null && hs2 == 2.0)
        || (hs3 != null && hs3 == 2.0);
}
```

### 3.4 Chi tiết Bước 7: Điểm ưu tiên

```java
private Double calculatePriorityScore(Double diemTong, Double diemThxt) {
    if (diemTong == null || diemTong == 0.0) return 0.0;

    // Điều chỉnh nếu tổng điểm ≥ 22.5
    if (diemThxt >= 22.5) {
        return ((30.0 - diemThxt) / 7.5) * diemTong;
    }
    return diemTong;
}
```

---

## PHẦN 4: VALIDATION NGHIỆP VỤ

### 4.1 Validate đủ môn theo tổ hợp

```java
private void validateSubjectCompletion(XtDiemthixettuyen score, XtNganhTohop tohop) {
    List<String> missingSubjects = new ArrayList<>();

    if (tohop.getThMon1() != null && getScoreField(score, tohop.getThMon1()) == null) {
        missingSubjects.add(tohop.getThMon1());
    }
    if (tohop.getThMon2() != null && getScoreField(score, tohop.getThMon2()) == null) {
        missingSubjects.add(tohop.getThMon2());
    }
    if (tohop.getThMon3() != null && getScoreField(score, tohop.getThMon3()) == null) {
        missingSubjects.add(tohop.getThMon3());
    }

    if (!missingSubjects.isEmpty()) {
        throw new RuntimeException("Thiếu điểm các môn: " + missingSubjects);
    }
}

private Double getScoreField(XtDiemthixettuyen score, String monCode) {
    return switch (monCode) {
        case "TO" -> score.getTo();
        case "LI" -> score.getLi();
        case "HO" -> score.getHo();
        case "SI" -> score.getSi();
        case "SU" -> score.getSu();
        case "DI" -> score.getDi();
        case "VA" -> score.getVa();
        case "N1" -> score.getN1Thi(); // hoặc n1Cc tùy logic
        case "NL1" -> score.getNl1();
        case "NK1" -> score.getNk1();
        case "NK2" -> score.getNk2();
        default -> null;
    };
}
```

### 4.2 Validate theo phương thức

```java
private void validateForPhuongThuc(XtDiemthixettuyen score, String phuongThuc) {
    switch (phuongThuc) {
        case "THPT":
            // Toán, Lý, Hóa hoặc Văn, Toán, Anh tùy tổ hợp
            // Điểm 0-10
            break;
        case "DGNL":
            // NL1: 0-150 hoặc 0-1200
            // NK1, NK2: 0-100
            // Thông báo sẽ quy đổi về thang 30
            break;
        case "VSAT":
            // Tùy quy định trường
            break;
    }
}
```

---

## PHẦN 5: REFACTOR SCOREFORMDIALOG - DYNAMIC FIELDS

### 5.1 Nguyên tắc UX mới

**Cho người dùng chọn phương thức TRƯỚC → hiện fields tương ứng SAU**

```
Bước 1: Chọn phương thức xét tuyển (bắt buộc)
Bước 2: Nhập điểm theo phương thức đã chọn
```

### 5.2 Cấu trúc UI mới

```java
// 3 Panel riêng biệt, chỉ hiện 1 tại thời điểm
private JPanel createMethodSelectionPanel() {
    JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    
    // Radio buttons cho 3 phương thức
    JRadioButton rbTHPT = new JRadioButton("THPT");
    JRadioButton rbDGNL = new JRadioButton("DGNL");
    JRadioButton rbVSAT = new JRadioButton("VSAT");
    
    ButtonGroup bg = new ButtonGroup();
    bg.add(rbTHPT);
    bg.add(rbDGNL);
    bg.add(rbVSAT);
    rbTHPT.setSelected(true);  // Default
    
    // Khi chọn, hiện panel tương ứng
    rbTHPT.addItemListener(e -> showMethodPanel("THPT"));
    rbDGNL.addItemListener(e -> showMethodPanel("DGNL"));
    rbVSAT.addItemListener(e -> showMethodPanel("VSAT"));
    
    return panel;
}

private void showMethodPanel(String phuongThuc) {
    cardLayout.show(cardPanel, phuongThuc);
}
```

### 5.3 3 Panel cho 3 phương thức

```java
// Panel THPT - 7 môn văn hóa + Ngoại ngữ
private JPanel createTHPTPanel() {
    JPanel p = basePanel();
    // Toán(0-10), Lý(0-10), Hóa(0-10), Sinh(0-10), Sử(0-10), Địa(0-10), Văn(0-10)
    // N1 Thi(0-10), N1 CC(0-10)
    // Validation: Toán bắt buộc, ít nhất 3 môn có điểm
    return p;
}

// Panel DGNL - Đánh giá năng lực
private JPanel createDGNLPanel() {
    JPanel p = basePanel();
    // NL1 (Ngữ văn L1): 0-150 hoặc 0-1200 tùy đề thi
    // NK1, NK2 (Năng lực L1, L2): 0-100
    // Ghi chú: "Điểm sẽ quy đổi về thang 30 theo bảng quy đổi"
    return p;
}

// Panel VSAT - Bài kiểm tra riêng của trường
private JPanel createVSATPanel() {
    JPanel p = basePanel();
    // Tùy quy định từng trường:
    // - Điểm bài thi riêng: tên bài, điểm, thang điểm
    // - Có thể gồm: viết, vấn đáp, thực hành
    // - Thường thang 10 hoặc thang 30
    return p;
}
```

### 5.4 Phương thức VSAT - Nghiên cứu thực tế

**Nguồn:** Nghiên cứu từ 18+ trường đại học VN sử dụng V-SAT 2025-2026

#### 5.4.1 V-SAT là gì?

| Thuật ngữ | Mô tả |
|-----------|-------|
| **V-SAT** | Computer-based competency test của V-SAT Center |
| **V-ACT** | Aptitude test của VNU-HCM |
| **TSA** | Thinking Skills Assessment |

**Các môn thi V-SAT 2025:** Toán, Lý, Hóa, Sinh, Sử, Địa, Anh, Văn (8 môn)
**Raw scores:** Math 144/150, English 144.5/150...

#### 5.4.2 Các trường sử dụng V-SAT

- University of Banking TP.HCM, Saigon University
- Open University HCMC, Can Tho University
- Van Lang University, HUTECH, Thai Nguyen University
- VÀ 12 trường khác...

**Nguồn:** [baochinhphu.vn](https://baochinhphu.vn/18-truong-dai-hoc-su-dung-ky-thi-v-sat-cho-mua-tuyen-sinh-nam-2025-102241107154838.htm)

#### 5.4.3 Quy đổi điểm V-SAT

```
V-SAT Raw Score (thang 150)
    → V-SAT Admission Scale (thang 450)
    → Thang 30 của trường (theo công thức riêng)
```

**Công thức ví dụ:**
```java
// V-SAT to thang 30
double diemV-sat = rawScore * 30.0 / 150.0;
```

**Nguồn:** [v-sat.edu.vn](https://v-sat.edu.vn/ky-thi-v-sat/tinh-diem-dai-hoc-v-sat)

#### 5.4.4 Công thức kết hợp điểm (2026)

| Trường | Công thức | Ghi chú |
|--------|-----------|---------|
| **VNU-HCM (IT)** | 47.5% V-ACT + 47.5% THPT + 5% Học bạ | Điểm quy đổi về thang chung |
| **HUST** | Profile = Thinking Skills + Achievement + Bonus | Critical Thinking = TSA×40/60 |
| **HUTECH** | ≥24 cho phương thức năng lực | Math≥8.0, 3 môn≥24 |

**Quy tắc điểm cộng:** ≤10% thang điểm (≤3 điểm trên thang 30)

**Nguồn:** [vietnam.vn](https://vietnam.vn/en/tuyen-sinh-dh-2026-luu-y-xet-tuyen-ket-hop-vao-dh-quoc-gia-tp-hcm)

#### 5.4.5 Thiết kế form VSAT cho hệ thống

**Hệ thống hỗ trợ 2 loại VSAT:**

```java
public enum VSATExamType {
    TRUONG_TU_CHON("Bài thi riêng của trường"),
    VSAT_CENTER("V-SAT Center");

    public final String moTa;
}
```

**Panel cho Bài thi riêng:**
```java
private JPanel createVSATExamPanel() {
    JPanel p = basePanel();
    addRow(p, 0, "Tên bài thi:", txtExamName, null, null);
    addRow(p, 2, "Điểm:", txtExamScore, "Thang:", txtExamMaxScore);
    return p;
}
```

**Panel cho V-SAT Center:**
```java
private JPanel createVSATCenterPanel() {
    JPanel p = basePanel();
    addRow(p, 0, "Môn thi:", cboVSATSubject, null, null);
    addRow(p, 2, "Điểm raw:", txtVSATScore, "(thang 150)", null);
    lblDiemQuyDoi = new JLabel("Điểm quy đổi: -");
    return p;
}
```

#### 5.4.6 Validation cho VSAT

```java
private void validateVSAT() {
    if (vsatType == VSATExamType.TRUONG_TU_CHON) {
        Double diem = getValue(txtExamScore);
        Double max = parseDouble(txtExamMaxScore.getText());
        if (diem != null && max != null && (diem < 0 || diem > max)) {
            throw new RuntimeException("Điểm phải từ 0 đến " + max);
        }
    } else {
        Double diem = getValue(txtVSATScore);
        if (diem != null && (diem < 0 || diem > 150)) {
            throw new RuntimeException("Điểm V-SAT phải từ 0 đến 150");
        }
    }
}
```

### 5.5 Validation theo phương thức

```java
private void validateScoresForMethod(String phuongThuc) {
    switch (phuongThuc) {
        case "THPT":
            // Toán bắt buộc (môn chính)
            requireScore(toan, 0, 10, true, "Toán");
            // Ít nhất 2 trong 6 môn còn lại có điểm
            int filledCount = countNonNull(toan, van, anh, ly, hoa, sinh, su, dia);
            if (filledCount < 3) {
                throw new RuntimeException("THPT cần ít nhất 3 môn có điểm");
            }
            break;
        case "DGNL":
            // NL1 bắt buộc
            requireScore(nl1, 0, 1200, true, "NL1");
            // NK1, NK2 tùy tổ hợp (có thể null)
            if (nk1 != null) requireScore(nk1, 0, 100, false, "NK1");
            if (nk2 != null) requireScore(nk2, 0, 100, false, "NK2");
            break;
        case "VSAT":
            // Kiểm tra từng bài thi
            for (int i = 0; i < numberOfVSATExams; i++) {
                String name = txtVSATNames[i].getText();
                Double score = getValue(txtVSATScores[i]);
                Double max = parseDouble(txtVSATMaxScores[i].getText());
                if (name.isEmpty()) continue;
                requireScore(score, 0, max, true, name);
            }
            break;
    }
}
```

### 5.6 Điểm cộng - Hiển thị khi cần

```java
// Tab bonus score vẫn độc lập, chỉ hiện khi thí sinh đã có điểm cộng
private JPanel createBonusScoreInfo() {
    JPanel p = basePanel();
    // Nếu có bonus score: hiện thông tin
    // Điểm CC (chứng chỉ): hiển thị nếu có
    // Điểm UTXT (ưu tiên): hiển thị KV/UT đã tính
    // Tổng điểm cộng: diemTong
    return p;
}
```

---

## PHẦN 6: FILE CẦN THÊM/SỬA

### 6.1 File thêm mới

| File | Thay đổi |
|------|----------|
| `AspirationScoreService.java` | Interface mới |
| `AspirationScoreServiceImpl.java` | Implement pipeline 9 bước |
| `AspirationScoreResult.java` | DTO kết quả |

### 6.2 File sửa

| File | Thay đổi |
|------|----------|
| `ScoreDTO.java` | Nullable fields, bỏ @Min(0) |
| `ScoreFormDialog.java` | Tab preview, null handling |
| `ScoreServiceImpl.java` | Gọi AspirationScoreService sau save |
| `NguyenVongRepository.java` | Thêm findByCccdActive() |
| `ScoreMapper.java` | Map null không thành 0 |

---

## PHẦN 7: THỨ TỰ IMPLEMENT

```
Bước 1: Fix Null vs Zero (ScoreDTO + ScoreFormDialog + ScoreServiceImpl)
   ↓
Bước 2: Thêm AspirationScoreService với pipeline 9 bước
   ↓
Bước 3: Tích hợp vào ScoreServiceImpl.saveScore()
   ↓
Bước 4: Thêm Tab preview vào ScoreFormDialog
   ↓
Bước 5: Test và verify
```

---

## PHẦN 8: TEST CASE

### 8.1 Null vs Zero
| Trường hợp | Input | Lưu DB |
|-----------|------|--------|
| Không nhập Toán | "" | null |
| Nhập Toán = 0 | "0" | 0.0 |
| Nhập Toán = 8.5 | "8.5" | 8.5 |

### 8.2 Tính điểm nguyện vọng
| Trường hợp | Input | Kết quả |
|-----------|------|---------|
| A00: Toán 9, Lý 7, Hóa 9 | hs: 2,1,1 | (9×2+7+9)×0.75 = 25.5 |
| D01: Toán 8, Văn 7, Anh 9 | hs: 1,1,1 | 8+7+9 = 24 |
| Thiếu môn | Toán 9, Hóa 9 (không có Lý) | Throw "Thiếu điểm Lý" |

### 8.3 Quy đổi DGNL
| Input | Bảng quy đổi | Kết quả |
|-------|-------------|---------|
| NL1 = 100/150 | 0-150 → 0-30 | 20.0 |
| SAT = 1380 | 1200-1600 → 16-30 | 18.0 |
| IELTS 6.5 | 5.0-9.0 → 6.5-10 | 8.5 |

---

## PHẦN 9: XÁC NHẬN

**Các vấn đề đã lên kế hoạch:**
1. ✅ Null vs Zero - Phân biệt "chưa có điểm" (null) và "điểm = 0"
2. ✅ Gắn nguyện vọng - Tự động tính điểm xét tuyển khi lưu điểm
3. ✅ Pipeline 9 bước - Quy đổi → Hệ số → Điểm XT → Ưu tiên → So sánh sàn
4. ✅ Validation đủ môn - Kiểm tra 3 môn theo tổ hợp
5. ✅ Preview điểm - Tab mới trong ScoreFormDialog

**Chờ bạn duyệt để implement.**
