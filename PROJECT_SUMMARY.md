# Hệ thống Quản lý Xét Tuyển - Tài liệu Kỹ thuật

> Document dùng cho: `handle_admission_result` và `statistic`

---

## 1. Tổng quan hệ thống

### 1.1 Cấu trúc dự án

| Module          | Mô tả                       | Stack                            |
| --------------- | --------------------------- | -------------------------------- |
| `swing-app`     | Ứng dụng Desktop Java Swing | Spring Boot 4.0.2, JPA, MySQL    |
| `thymeleaf_web` | Ứng dụng Web                | Spring Boot 4.0.6, Thymeleaf, H2 |

### 1.2 Kiến trúc 3-Layer

```
┌─────────────────────────────────────┐
│         UI Layer (Swing/Web)        │
│  Frames, Panels, Controllers      │
└─────────────────────────────────────┘
                  ↓
┌─────────────────────────────────────┐
│      Business Layer (Services)      │
│  AdmissionResultService, etc.      │
└─────────────────────────────────────┘
                  ↓
┌─────────────────────────────────────┐
│    Data Access Layer (Repositories) │
│  JPA Repositories + MySQL          │
└─────────────────────────────────────┘
```

---

## 2. Database Schema (Xt\* Entities)

### 2.1 MASTER DATA - Danh mục

#### `xt_nganh` - Ngành đào tạo

| Column             | Type        | Mô tả                |
| ------------------ | ----------- | -------------------- |
| `idnganh`          | INT (PK)    | ID ngành             |
| `manganh`          | VARCHAR(50) | Mã ngành (unique)    |
| `tennganh`         | VARCHAR     | Tên ngành            |
| `n_tohopgoc`       | VARCHAR     | Tổ hợp gốc           |
| `n_chitieu`        | INT         | Chỉ tiêu tuyển sinh  |
| `n_diemsan`        | DOUBLE      | Điểm sàn             |
| `n_diemtrungtuyen` | DOUBLE      | Điểm chuẩn           |
| `n_tuyenthang`     | BOOLEAN     | Có tuyển thẳng       |
| `n_dgnl`           | BOOLEAN     | Có xét ĐGNL          |
| `n_thpt`           | BOOLEAN     | Xét điểm THPT        |
| `n_vsat`           | BOOLEAN     | Xét VSAT             |
| `sl_xtt`           | INT         | Số lượng tuyển thẳng |
| `sl_dgnl`          | INT         | Số lượng ĐGNL        |
| `sl_vsat`          | INT         | Số lượng VSAT        |
| `sl_thpt`          | INT         | Số lượng THPT        |

#### `xt_tohop_monthi` - Tổ hợp môn

| Column     | Type        | Mô tả                 |
| ---------- | ----------- | --------------------- |
| `id`       | INT (PK)    | ID tổ hợp             |
| `matohop`  | VARCHAR(10) | Mã (A00, D01...)      |
| `mon1`     | VARCHAR(50) | Môn 1 (TO, LI, HO...) |
| `mon2`     | VARCHAR(50) | Môn 2                 |
| `mon3`     | VARCHAR(50) | Môn 3                 |
| `tentohop` | VARCHAR     | Tên tổ hợp            |

#### `xt_nganh_tohop` - Mapping Ngành ↔ Tổ hợp

| Column    | Type        | Mô tả                |
| --------- | ----------- | -------------------- |
| `id`      | INT (PK)    | ID mapping           |
| `manganh` | VARCHAR(50) | FK → xt_nganh        |
| `matohop` | VARCHAR(50) | FK → xt_tohop_monthi |
| `th_mon1` | VARCHAR(50) | Tên môn 1            |
| `hsmon1`  | DOUBLE      | Hệ số môn 1          |
| `th_mon2` | VARCHAR(50) | Tên môn 2            |
| `hsmon2`  | DOUBLE      | Hệ số môn 2          |
| `th_mon3` | VARCHAR(50) | Tên môn 3            |
| `hsmon3`  | DOUBLE      | Hệ số môn 3          |

### 2.2 CANDIDATE DATA - Dữ liệu thí sinh

#### `xt_thisinhxettuyen25` - Thông tin thí sinh

| Column       | Type         | Mô tả                      |
| ------------ | ------------ | -------------------------- |
| `id`         | INT (PK)     | ID                         |
| `cccd`       | VARCHAR(12)  | CCCD (unique, PK identity) |
| `sobaodanh`  | VARCHAR(10)  | Số báo danh (unique)       |
| `ho`         | VARCHAR(50)  | Họ                         |
| `ten`        | VARCHAR(50)  | Tên                        |
| `ngay_sinh`  | DATE         | Ngày sinh                  |
| `dien_thoai` | VARCHAR(15)  | Điện thoại                 |
| `email`      | VARCHAR(100) | Email                      |
| `gioi_tinh`  | VARCHAR(10)  | Giới tính                  |
| `noi_sinh`   | VARCHAR(255) | Nơi sinh                   |
| `doi_tuong`  | VARCHAR(50)  | Đối tượng ưu tiên          |
| `khu_vuc`    | VARCHAR(50)  | Khu vực                    |
| `ho_va_ten`  | VARCHAR      | Họ và tên đầy đủ           |

#### `xt_diemthixettuyen` - Điểm thi (theo phương thức)

| Column         | Type        | Mô tả                        |
| -------------- | ----------- | ---------------------------- |
| `id`           | INT (PK)    | ID                           |
| `cccd`         | VARCHAR(12) | FK → xt_thisinhxettuyen25    |
| `sobaodanh`    | VARCHAR(10) | SBD                          |
| `d_phuongthuc` | VARCHAR(50) | Phương thức (THPT/DGNL/VSAT) |
| `TO`           | DOUBLE      | Điểm Toán                    |
| `LI`           | DOUBLE      | Điểm Lý                      |
| `HO`           | DOUBLE      | Điểm Hóa                     |
| `SI`           | DOUBLE      | Điểm Sinh                    |
| `SU`           | DOUBLE      | Điểm Sử                      |
| `DI`           | DOUBLE      | Điểm Địa                     |
| `VA`           | DOUBLE      | Điểm Văn                     |
| `N1_THI`       | DOUBLE      | Điểm thi Ngoại ngữ           |
| `N1_CC`        | DOUBLE      | Điểm chứng chỉ Ngoại ngữ     |
| `NL1`          | DOUBLE      | Ngữ văn L1 (ĐGNL)            |
| `NK1`          | DOUBLE      | Năng lực L1 (ĐGNL)           |
| `NK2`          | DOUBLE      | Năng lực L2 (ĐGNL)           |

#### `xt_diemcongxettuyen` - Điểm cộng thêm

| Column     | Type        | Mô tả                              |
| ---------- | ----------- | ---------------------------------- |
| `id`       | INT (PK)    | ID                                 |
| `cccd`     | VARCHAR(12) | FK → xt_thisinhxettuyen25 (unique) |
| `diemCC`   | DOUBLE      | Điểm chứng chỉ (IELTS, SAT...)     |
| `diemUtxt` | DOUBLE      | Điểm ưu tiên đặc biệt              |
| `diemTong` | DOUBLE      | Tổng = diemCC + diemUtxt           |

### 2.3 ADMISSION PROCESSING - Xử lý xét tuyển

#### `xt_nguyenvongxettuyen` - Nguyện vọng xét tuyển

| Column          | Type        | Mô tả                                         |
| --------------- | ----------- | --------------------------------------------- |
| `id`            | INT (PK)    | ID                                            |
| `nn_cccd`       | VARCHAR(45) | FK → xt_thisinhxettuyen25.cccd                |
| `nv_manganh`    | VARCHAR(50) | FK → xt_nganh.manganh                         |
| `nv_tt`         | INT         | Thứ tự nguyện vọng                            |
| `diem_thxt`     | DOUBLE      | Điểm 3 môn = mon1*hs1 + mon2*hs2 + mon3\*hs3  |
| `diem_utqd`     | DOUBLE      | Điểm ưu tiên theo quy định                    |
| `diem_cong`     | DOUBLE      | Điểm cộng (from xt_diemcongxettuyen)          |
| `diem_xettuyen` | DOUBLE      | Điểm cuối = diem_thxt + diem_utqd + diem_cong |
| `nv_ketqua`     | VARCHAR(20) | **TRUNG_TUYEN / TRUOT / CHO_XET**             |
| `nv_keys`       | VARCHAR(45) | Composite key                                 |
| `tt_phuongthuc` | VARCHAR(45) | Phương thức xét tuyển được chọn               |
| `tt_thm`        | VARCHAR(45) | Thứ tự ưu tiên/học bổng                       |

#### `xt_bangquydoi` - Bảng quy đổi điểm

| Column         | Type        | Mô tả                  |
| -------------- | ----------- | ---------------------- |
| `id`           | INT (PK)    | ID                     |
| `d_phuongthuc` | VARCHAR(45) | THPT, DGNL, VSAT       |
| `d_tohop`      | VARCHAR(45) | A00, D01...            |
| `d_mon`        | VARCHAR(45) | TO, LI, N1...          |
| `d_diema`      | DOUBLE      | Điểm A (thấp nhất)     |
| `d_diemb`      | DOUBLE      | Điểm B (cao nhất)      |
| `d_diemc`      | DOUBLE      | Điểm quy đổi tương ứng |
| `d_diemd`      | DOUBLE      | Điểm quy đổi tối đa    |

---

## 3. Entity Relationships

```
xt_thisinhxettuyen25 (1)──(N) xt_diemthixettuyen
        │                            │
        │ (1:1)                      │
        ↓                            ↓
xt_diemcongxettuyen    xt_nguyenvongxettuyen
                                 │
                                 │ (N:1)
                                 ↓
                            xt_nganh
                                 │
                                 │ (1:N)
                                 ↓
                           xt_nganh_tohop
                                 │
                                 │ (N:1)
                                 ↓
                           xt_tohop_monthi
```

---

## 4. Luồng xử lý Admission Result

### 4.1 Dữ liệu đầu vào

- Thí sinh đã có điểm thi (`xt_diemthixettuyen`)
- Thí sinh có thể có điểm cộng (`xt_diemcongxettuyen`)
- Thí sinh đăng ký nguyện vọng (`xt_nguyenvongxettuyen`)
- Ngành có chỉ tiêu và điểm chuẩn (`xt_nganh`)

### 4.2 Quy trình xử lý

```
1. Lấy danh sách nguyện vọng (xt_nguyenvongxettuyen)
   ↓
2. Join thông tin thí sinh (xt_thisinhxettuyen25)
   ↓
3. Join thông tin ngành (xt_nganh)
   ↓
4. Tính điểm xét tuyển:
   - diem_xettuyen = diem_thxt + diem_utqd + diem_cong
   - diem_thxt = Σ(điểm môn × hệ số) từ xt_diemthixettuyen
   - diem_utqd = from xt_diemcongxettuyen.diemUtxt
   - diem_cong = from xt_diemcongxettuyen.diemTong
   ↓
5. So sánh với điểm chuẩn:
   - IF diem_xettuyen >= n_diemtrungtuyen → TRUNG_TUYEN
   - ELSE → TRUOT
   ↓
6. Cập nhật nv_ketqua vào xt_nguyenvongxettuyen
```

### 4.3 Kết quả xét tuyển (nv_ketqua)

| Giá trị       | Mô tả                     |
| ------------- | ------------------------- |
| `TRUNG_TUYEN` | Trúng tuyển               |
| `TRUOT`       | Không trúng tuyển         |
| `CHO_XET`     | Chờ xét (chưa có kết quả) |

### 4.4 Phương thức xét tuyển (d_phuongthuc / tt_phuongthuc)

| Giá trị       | Mô tả                 |
| ------------- | --------------------- |
| `THPT`        | Xét điểm thi THPT     |
| `DGNL`        | Xét Điểm Ghi Nhận Lực |
| `VSAT`        | Xét Vòng Sơ Tuyển     |
| `Tuyển thẳng` | Tuyển thẳng           |

---

## 5. Metrics cho Statistic

### 5.1 Các chỉ số cần thống kê

| STT | Metric              | Mô tả                                 | Cách tính                                      |
| --- | ------------------- | ------------------------------------- | ---------------------------------------------- |
| 1   | Tổng số thí sinh    | Số lượng thí sinh đăng ký             | COUNT(DISTINCT cccd) FROM xt_thisinhxettuyen25 |
| 2   | Tổng số nguyện vọng | Số lượng nguyện vọng                  | COUNT(\*) FROM xt_nguyenvongxettuyen           |
| 3   | Số trúng tuyển      | Thí sinh có nv_ketqua = 'TRUNG_TUYEN' | COUNT WHERE nv_ketqua = 'TRUNG_TUYEN'          |
| 4   | Tỷ lệ trúng tuyển   | % trúng tuyển                         | (TRUNG_TUYEN / Tổng) × 100                     |
| 5   | Điểm trung bình     | Điểm TB xét tuyển                     | AVG(diem_xettuyen)                             |
| 6   | Điểm cao nhất       | Điểm max                              | MAX(diem_xettuyen)                             |
| 7   | Điểm thấp nhất      | Điểm min                              | MIN(diem_xettuyen)                             |

### 5.2 Thống kê theo ngành

```sql
SELECT
    n.tennganh,
    COUNT(nv.id) as total_applications,
    SUM(CASE WHEN nv.nv_ketqua = 'TRUNG_TUYEN' THEN 1 ELSE 0 END) as admitted,
    n.n_chitieu as target,
    AVG(nv.diem_xettuyen) as avg_score
FROM xt_nguyenvongxettuyen nv
JOIN xt_nganh n ON nv.nv_manganh = n.manganh
GROUP BY n.tennganh, n.n_chitieu
```

### 5.3 Thống kê theo phương thức

```sql
SELECT
    tt_phuongthuc,
    COUNT(*) as total,
    SUM(CASE WHEN nv_ketqua = 'TRUNG_TUYEN' THEN 1 ELSE 0 END) as admitted
FROM xt_nguyenvongxettuyen
GROUP BY tt_phuongthuc
```

### 5.4 Thống kê điểm phân bố

```sql
SELECT
    CASE
        WHEN diem_xettuyen >= 30 THEN '30+'
        WHEN diem_xettuyen >= 25 THEN '25-29.9'
        WHEN diem_xettuyen >= 20 THEN '20-24.9'
        WHEN diem_xettuyen >= 15 THEN '15-19.9'
        ELSE '<15'
    END as score_range,
    COUNT(*) as total
FROM xt_nguyenvongxettuyen
GROUP BY score_range
ORDER BY score_range DESC
```

---

## 6. DTO Classes (Data Transfer Objects)

### 6.1 AdmissionResultDTO

```java
@Data @Builder
public class AdmissionResultDTO {
    private Integer id;
    private String cccd;           // CCCD thí sinh
    private String hoTen;          // Họ và tên
    private String sobaodanh;      // Số báo danh
    private String manganh;        // Mã ngành
    private String tennganh;       // Tên ngành
    private Integer nvTt;         // Thứ tự nguyện vọng
    private Double diemXettuyen;   // Điểm xét tuyển
    private String ketQua;          // TRUNG_TUYEN/TRUOT/CHO_XET
    private String phuongThuc;     // THPT/DGNL/VSAT
    private LocalDate ngayXet;     // Ngày xét
}
```

### 6.2 Các DTO khác

| DTO             | Mô tả              |
| --------------- | ------------------ |
| `ScoreDTO`      | Điểm thi thí sinh  |
| `BonusScoreDTO` | Điểm cộng thêm     |
| `CandidateDTO`  | Thông tin thí sinh |
| `MajorDTO`      | Thông tin ngành    |

---

## 7. Service Interfaces

### 7.1 AdmissionResultService

```java
public interface AdmissionResultService {
    List<AdmissionResultDTO> getAllResults();           // Lấy tất cả kết quả
    List<AdmissionResultDTO> getByResult(String ketQua); // Lọc theo kết quả
    List<AdmissionResultDTO> getByMajor(String manganh); // Lọc theo ngành
    List<AdmissionResultDTO> search(String keyword, String ketQua, String manganh); // Tìm kiếm
    void updateResult(Integer id, String ketQua);       // Cập nhật kết quả
    void exportToExcel(List<AdmissionResultDTO> results); // Xuất Excel
    void exportToPDF(List<AdmissionResultDTO> results);   // Xuất PDF
}
```

### 7.2 AspirationScoreService

```java
public interface AspirationScoreService {
    // Tính điểm cho nguyện vọng
    // Chọn tổ hợp tốt nhất
    // Áp dụng điểm cộng
}
```

---

## 8. Repositories

### 8.1 Các Repository chính

| Repository             | Entity               | Methods quan trọng                   |
| ---------------------- | -------------------- | ------------------------------------ |
| `CandidateRepository`  | XtThisinhxettuyen25  | findByCccd, search, softDeleteByCccd |
| `NguyenVongRepository` | XtNguyenvongxettuyen | findByNnCccd                         |
| `MajorRepository`      | XtNganh              | findByManganh, search                |
| `ScoreRepository`      | XtDiemthixettuyen    | -                                    |
| `BonusScoreRepository` | XtDiemcongxettuyen   | findByCccd                           |

---

## 9. UI Components

### 9.1 AdmissionPanel

- Hiển thị danh sách kết quả xét tuyển
- Filter theo: Kết quả, Ngành, Phương thức
- Toolbar: Update, Export Excel, Export PDF, Print
- Thống kê: Tổng trúng tuyển, Tỷ lệ trúng tuyển

### 9.2 StatisticPanel

- Placeholder cho thống kê & báo cáo
- Sẽ chứa: Biểu đồ, Báo cáo tỷ lệ, Phân bố điểm

### 9.3 AdmissionResultController

```java
@Component
public class AdmissionResultController {
    void loadResults();           // Tải dữ liệu
    void search(keyword, ketQua, nganh);  // Tìm kiếm
    void updateResult();          // Cập nhật kết quả
    void handleExportExcel();      // Xuất Excel
    void handleExportPDF();       // Xuất PDF
}
```

---

## 10. Sample Queries cho handle_admission_result

### 10.1 Lấy kết quả xét tuyển đầy đủ

```sql
SELECT
    nv.id,
    ts.cccd,
    ts.ho_va_ten as hoTen,
    ts.sobaodanh,
    n.manganh,
    n.tennganh,
    nv.nv_tt,
    nv.diem_xettuyen,
    nv.nv_ketqua,
    nv.tt_phuongthuc,
    nv.created_at
FROM xt_nguyenvongxettuyen nv
JOIN xt_thisinhxettuyen25 ts ON nv.nn_cccd = ts.cccd
JOIN xt_nganh n ON nv.nv_manganh = n.manganh
WHERE nv.is_deleted = false
ORDER BY nv.nv_tt, n.tennganh
```

### 10.2 Cập nhật kết quả hàng loạt

```sql
UPDATE xt_nguyenvongxettuyen nv
JOIN xt_nganh n ON nv.nv_manganh = n.manganh
SET nv.nv_ketqua = CASE
    WHEN nv.diem_xettuyen >= n.n_diemtrungtuyen THEN 'TRUNG_TUYEN'
    ELSE 'TRUOT'
END
WHERE nv.nv_ketqua = 'CHO_XET'
```

### 10.3 Lấy thống kê tổng quan

```sql
SELECT
    COUNT(*) as total_aspirations,
    SUM(CASE WHEN nv_ketqua = 'TRUNG_TUYEN' THEN 1 ELSE 0 END) as admitted,
    SUM(CASE WHEN nv_ketqua = 'TRUOT' THEN 1 ELSE 0 END) as rejected,
    AVG(diem_xettuyen) as avg_score,
    MAX(diem_xettuyen) as max_score,
    MIN(diem_xettuyen) as min_score
FROM xt_nguyenvongxettuyen
WHERE is_deleted = false
```

---

## 11. Notes

### 11.1 Điểm xét tuyển

```
diem_xettuyen = diem_thxt + diem_utqd + diem_cong

Trong đó:
- diem_thxt = Σ(mon_i × hs_i) với i = 1, 2, 3
- diem_utqd = điểm ưu tiên theo quy định (khu vực, đối tượng)
- diem_cong = điểm cộng (chứng chỉ, HSG, thể thao...)
```

### 11.2 Điểm cộng tối đa

| Loại                      | Tối đa  |
| ------------------------- | ------- |
| Chứng chỉ (IELTS, SAT...) | 2.0     |
| Học sinh giỏi             | 1.5     |
| Thể thao                  | 2.0     |
| **Tổng cộng**             | **4.0** |

### 11.3 Điểm ưu tiên khu vực

| Khu vực | Điểm |
| ------- | ---- |
| KV1     | 0.75 |
| KV2     | 0.5  |
| KV2-NT  | 0.25 |
| KV3     | 0    |

---

_Document version: 1.0_  
_Created: 2026-05-05_  
_For: handle_admission_result & statistic functions_
