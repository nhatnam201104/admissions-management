# Quản lý Nguyện vọng & Xét tuyển

## 1. Entity chính

### XtNguyenvongxettuyen (Bảng nguyện vọng)

| Field           | Kiểu    | Mô tả                                         |
| --------------- | ------- | --------------------------------------------- |
| `id`            | Integer | PK tự động                                    |
| `nn_cccd`       | String  | CCCD thí sinh (FK)                            |
| `nv_manganh`    | String  | Mã ngành đăng ký (FK)                         |
| `nv_tt`         | Integer | Thứ tự nguyện vọng (1, 2, 3...)               |
| `diem_thxt`     | Double  | Tổng điểm 3 môn (đã nhân hệ số)               |
| `diem_utqd`     | Double  | Điểm ưu tiên (khu vực, đối tượng)             |
| `diem_cong`     | Double  | Điểm cộng (IELTS, HSG...)                     |
| `diem_xettuyen` | Double  | Điểm cuối = diem_thxt + diem_utqd + diem_cong |
| `nv_ketqua`     | String  | Kết quả: `TRUNG_TUYEN`, `TRUOT`, `CHO_XET`    |
| `tt_phuongthuc` | String  | Phương thức xét tuyển (THPT, DGNL, VSAT)      |

### XtNganh (Bảng ngành)

| Field                        | Kiểu    | Mô tả                  |
| ---------------------------- | ------- | ---------------------- |
| `manganh`                    | String  | Mã ngành (PK)          |
| `tennganh`                   | String  | Tên ngành              |
| `n_chitieu`                  | Integer | Chỉ tiêu tuyển sinh    |
| `n_diemsan`                  | Double  | Điểm sàn               |
| `n_diemtrungtuyen`           | Double  | **Điểm chuẩn** để xét  |
| `n_thpt`, `n_dgnl`, `n_vsat` | Boolean | Có xét phương thức nào |

### XtDiemthixettuyen (Bảng điểm thi)

| Field                                    | Kiểu   | Mô tả                       |
| ---------------------------------------- | ------ | --------------------------- |
| `cccd`                                   | String | CCCD thí sinh               |
| `d_phuongthuc`                           | String | Phương thức (THPT, DGNL...) |
| `TO`, `LI`, `HO`, `SI`, `SU`, `DI`, `VA` | Double | Điểm các môn (thang 10)     |

### XtNganhTohop (Mapping ngành-tổ hợp)

| Field         | Kiểu   | Mô tả                   |
| ------------- | ------ | ----------------------- |
| `manganh`     | String | Mã ngành                |
| `matohop`     | String | Mã tổ hợp (A00, A01...) |
| `th_mon1/2/3` | String | Mã môn 1/2/3            |
| `hsmon1/2/3`  | Double | Hệ số môn 1/2/3         |

---

## 2. Logic xét tuyển

### Điều kiện xét tuyển

1. **Điểm chuẩn tồn tại**: `nDiemtrungtuyen != null`
2. **Điểm xét tuyển**: `diem_xettuyen >= nDiemtrungtuyen`
3. **Còn chỉ tiêu**: số đã trúng tuyển < `nChitieu`

### Thuật toán xét tuyển

```
1. Lấy tất cả nguyện vọng của ngành X
2. Tính điểm cho mỗi NV (nếu chưa có)
3. Sort theo diem_xettuyen DESC, rồi theo nv_tt ASC
4. Duyệt từng NV:
   a. Nếu thí sinh đã đậu NV khác → TRUOT
   b. Nếu diem_xettuyen < diem_chuan → TRUOT
   c. Nếu đã đủ chỉ tiêu → TRUOT
   d. Ngược lại → TRUNG_TUYEN, mark thí sinh đã đậu
5. Lưu tất cả kết quả
```

### Trạng thái kết quả

| Trạng thái    | Ý nghĩa                                   |
| ------------- | ----------------------------------------- |
| `TRUNG_TUYEN` | Đậu - nằm trong chỉ tiêu và >= điểm chuẩn |
| `TRUOT`       | Rớt - không đạt điều kiện                 |
| `CHO_XET`     | Chưa xét - ngành chưa có điểm chuẩn       |

---

## 3. Service: AdmissionService

### Methods

#### `runAdmissionAll()`

- Xét tuyển **toàn bộ** nguyện vọng
- Dùng khi cần reset lại tất cả

#### `runAdmissionPending()`

- Xét tuyển **chỉ những NV đang chờ** (`CHO_XET`)
- Dùng khi đã có điểm chuẩn mới

#### `calculateScore(aspiration)`

- Tính điểm xét tuyển cho 1 nguyện vọng
- Lấy điểm thi → lấy tổ hợp → nhân hệ số → cộng điểm ưu tiên/cộng

#### `getScoreDetails(cccd, manganh)`

- Trả về chi tiết điểm cho modal hiển thị
- Gồm: thông tin thí sinh, điểm tổ hợp, điểm chuẩn ngành

---

## 4. UI: AdmissionPanel

### Layout

```
+-- Toolbar --------------------------------+
| [Tiêu đề]              [Nút Xét] [Nút Chi tiết] |
+---------------------------------------------+

+-- Table nguyện vọng ----------------------------+
| CCCD | Họ tên | Ngành | NV | Điểm | KQ | ... |
|----------------------------------------------|
| 001  | Nguyễn A | CNTT   | 1  | 27.8 | Đậu | [Xem] |
| 001  | Nguyễn A | KTPM   | 2  | 26.5 | Rớt | [Xem] |
+----------------------------------------------+
```

### Buttons

1. **"Xét tuyển toàn bộ"** → `runAdmissionAll()`
2. **"Xét tuyển chờ xét"** → `runAdmissionPending()`
3. **"Chi tiết điểm"** → Mở modal

### Modal chi tiết điểm

```
+-- Chi tiết điểm -----------------------------+
| Thí sinh: Nguyễn An                          |
| Ngày sinh: 15/03/2006                        |
+-----------------------------------------------+
| Tổ hợp A00 (TO + LY + HO, hs=1)              |
|   Toán: 9.5, Lý: 9.0, Hóa: 8.8               |
|   Điểm tổ hợp: 27.3                          |
+-----------------------------------------------+
| Điểm cộng: 0.5 (IELTS)                       |
| Điểm ưu tiên: 0.0                            |
| ───────────────────────────────────────────── |
| Điểm xét tuyển: 27.8                         |
+-----------------------------------------------+
| Ngành: Công nghệ thông tin                    |
| Điểm chuẩn: 26.0                              |
| Chỉ tiêu: 200                                 |
| ───────────────────────────────────────────── |
| Kết quả: ✓ Đậu (27.8 >= 26.0)                |
+-----------------------------------------------+
|                      [Đóng]                   |
+-----------------------------------------------+
```

---

## 5. Công thức tính điểm

### Điểm tổ hợp (diem_thxt)

```
diem_thxt = mon1 * hs1 + mon2 * hs2 + mon3 * hs3

Ví dụ: Tổ hợp A00 (TO + LY + HO, hs=1)
  diem_thxt = 9.5 + 9.0 + 8.8 = 27.3
```

### Điểm xét tuyển (diem_xettuyen)

```
diem_xettuyen = diem_thxt + diem_utqd + diem_cong

Ví dụ:
  diem_thxt = 27.3
  diem_utqd = 0.0
  diem_cong = 0.5
  ─────────────────
  diem_xettuyen = 27.8
```

---

## 6. Luồng xử lý

### Khi nhấn "Xét tuyển toàn bộ"

```
1. Load tất cả nguyện vọng
2. Group by ngành
3. Với mỗi ngành:
   a. Lấy thông tin ngành (điểm chuẩn, chỉ tiêu)
   b. Nếu chưa có điểm chuẩn → CHO_XET tất cả
   c. Nếu có điểm chuẩn:
      - Tính điểm cho NV chưa có
      - Sort theo điểm
      - Duyệt xét theo thứ tự
4. Lưu tất cả kết quả
5. Refresh table
```

### Khi nhấn "Chi tiết điểm" (row)

```
1. Lấy cccd và manganh từ row
2. Gọi getScoreDetails()
3. Show modal với data
```

---

## 7. Các ràng buộc

- Mỗi thí sinh chỉ được đậu **1 ngành** (nguyện vọng ưu tiên cao nhất đậu)
- NV1 đậu → NV2, NV3 tự động TRUOT
- Điểm chưa có → CHO_XET (chưa xét)
- Thí sinh có nhiều điểm (THPT, DGNL...) → chọn 1 phương thức để xét

---

## 8. Files cần implement

| File                       | Mô tả               |
| -------------------------- | ------------------- |
| `AdmissionService.java`    | Logic xét tuyển     |
| `AdmissionController.java` | Controller cho UI   |
| `AdmissionPanel.java`      | Panel UI Swing      |
| `ScoreDetailModal.java`    | Modal chi tiết điểm |

---

## 9. Repository methods

### NguyenVongRepository

```java
List<XtNguyenvongxettuyen> findByNnCccd(String cccd);
List<XtNguyenvongxettuyen> findByNvKetqua(String nvKetqua);
```

### ScoreRepository

```java
Optional<XtDiemthixettuyen> findByCccdAndDPhuongthuc(String cccd, String phuongthuc);
```

### MajorRepository

```java
Optional<XtNganh> findByManganh(String manganh);
```

### NganhTohopRepository

```java
List<XtNganhTohop> findByManganh(String manganh);
```

### CandidateRepository

```java
Optional<XtThisinhxettuyen25> findByCccd(String cccd);
```
