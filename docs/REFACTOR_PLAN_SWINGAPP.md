# BÁO CÁO KẾ HOẠCH REFACTOR SWINGAPP - ADMISSIONS MANAGEMENT

**Ngày:** 2026-05-04
**Branch hiện tại:** feature/47-fix-logic-score-management
**Ràng buộc:** KHÔNG thay đổi database schema

---

## PHẦN 1: PHÂN TÍCH SOURCE HIỆN TẠI

### 1.1 Các Entity liên quan

| Entity                | Bảng              | Các field hiện có                                                                                                                                    | Đánh giá                                             |
| --------------------- | ------------------ | ------------------------------------------------------------------------------------------------------------------------------------------------------- | ------------------------------------------------------- |
| `XtNganh`           | xt_nganh           | idnganh, manganh, tennganh, n_tohopgoc, n_chitieu, n_diemsan, n_diemtrungtuyen, n_tuyenthang, n_dgnl, n_thpt, n_vsat, sl_xtt, sl_dgnl, sl_vsat, sl_thpt | Có đầy đủ field cần thiết                        |
| `XtNganhTohop`      | xt_nganh_tohop     | id, manganh, matohop, th_mon1,**hsmon1**, th_mon2, **hsmon2**, th_mon3, **hsmon3**                                                    | **Hệ số đã có sẵn: hsmon1, hsmon2, hsmon3** |
| `XtTohopMonthi`     | xt_tohop_monthi    | id, matohop, mon1, mon2, mon3, tentohop                                                                                                                 | Lưu môn dạng text                                    |
| `XtBangquydoi`      | xt_bangquydoi      | id, d_phuongthuc, d_tohop, d_mon, d_diema, d_diemb, d_diemc, d_diemd                                                                                    | Bảng quy đổi đã có sẵn                           |
| `XtDiemthixettuyen` | xt_diemthixettuyen | cccd, sobaodanh, d_phuongthuc, to, li, ho, si, su, di, va, n1_thi, n1_cc, nl1, nk1, nk2                                                                 | Có đầy đủ điểm                                   |

### 1.2 Đánh giá chi tiết từng yêu cầu

#### 1.2.1 Hệ số tổ hợp trong xt_nganh_tohop

**KẾT LUẬN: ĐÃ CÓ SẴN TRONG DB - KHÔNG CẦN WORKAROUND**

- Bảng `xt_nganh_tohop` đã có các cột: `hsmon1`, `hsmon2`, `hsmon3`
- Entity `XtNganhTohop` đã map đúng các field này với `@Column(name = "hsmon1")`
- **Cần thay đổi:** Chỉ cần thêm UI để nhập hệ số khi gán tổ hợp cho ngành
- **Validation cần thêm:** hsmon > 0, không null

#### 1.2.2 Chỉ tiêu tổng readonly, chỉ tiêu theo phương thức

**KẾT LUẬN: IMPLEMENT ĐƯỢC - Các cột sl_* LƯU CHỈ TIÊU TỪNG PHƯƠNG THỨC**

- Bảng `xt_nganh` có đầy đủ các cột lưu chỉ tiêu theo phương thức:
  - `sl_xtt` → chỉ tiêu tuyển thẳng
  - `sl_dgnl` → chỉ tiêu ĐGNL
  - `sl_vsat` → chỉ tiêu VSAT
  - `sl_thpt` → chỉ tiêu THPT
  - `n_chitieu` → tổng chỉ tiêu (tính từ sl_*)

**Implementation:**
- UI: Checkbox phương thức + ô nhập chỉ tiêu cho từng phương thức (sl_*)
- Tổng chỉ tiêu = sl_xtt + sl_dgnl + sl_vsat + sl_thpt → hiển thị readonly
- Khi lưu: Lưu sl_* vào đúng cột tương ứng, cập nhật n_chitieu = tổng
- Khi load: Hiển thị đúng giá trị từ sl_* cho từng phương thức

#### 1.2.3 Điểm sàn trước, điểm chuẩn công bố sau

**KẾT LUẬN: IMPLEMENT ĐƯỢC**

- Bảng `xt_nganh` có sẵn 2 cột: `n_diemsan` (điểm sàn), `n_diemtrungtuyen` (điểm chuẩn)
- Entity đã map đúng: `nDiemsan`, `nDiemtrungtuyen`
- **Thay đổi UI:**
  - Cho nhập điểm sàn khi tạo/sửa ngành
  - Điểm chuẩn để trống hoặc readonly ban đầu
  - Hiển thị "Chưa công bố" nếu `n_diemtrungtuyen` = null

#### 1.2.4 Không cho nhập tay môn trong tổ hợp

**KẾT LUẬN: IMPLEMENT ĐƯỢC**

- Bảng `xt_tohop_monthi` lưu mon1, mon2, mon3 dạng text
- Có thể chuyển JTextField → JComboBox
- Danh sách môn: Tạo enum hoặc hardcoded list các môn chuẩn
- Khi lưu vẫn lưu text vào mon1/mon2/mon3

#### 1.2.5 Không chọn trùng môn trong tổ hợp

**KẾT LUẬN: IMPLEMENT ĐƯỢC**

- Validation ở cả UI và Service
- Kiểm tra mon1 != mon2 != mon3

#### 1.2.6 Thêm môn năng khiếu

**KẾT LUẬN: CHỈ LƯU TẠM THỜI TRONG COMBOBOX RUNTIME**

- Bảng `xt_tohop_monthi` không có bảng môn học riêng
- KHÔNG được tạo bảng mới
- **Workaround:**
  - ComboBox cho phép nhập thêm item (editable combo)
  - Hoặc dùng dialog thêm môn → thêm vào list runtime
  - Khi lưu vẫn lưu text vào mon1/mon2/mon3
  - Không có bảng lưu vĩnh viễn môn mới tạo

#### 1.2.7 Quy đổi điểm

**KẾT LUẬN: IMPLEMENT ĐƯỢC**

- Bảng `xt_bangquydoi` đã có cấu trúc hoàn chỉnh
- Service `ConversionTableService` đã có sẵn
- Cần tạo pipeline tính điểm xét tuyển có áp dụng quy đổi

---

## PHẦN 2: KẾ HOẠCH THAY ĐỔI THEO CHECKLIST

### 2.1 Nhóm 1: Quản lý tổ hợp (Subject Group)

#### File cần sửa:

- [ ] `SubjectGroupFormDialog.java` - Chuyển JTextField → JComboBox cho 3 môn
- [ ] `SubjectGroupFormDialog.java` - Thêm validation không trùng môn
- [ ] `SubjectGroupFormDialog.java` - Thêm nút "Thêm môn" với dialog popup

#### File cần thêm:

- [ ] `SubjectService.java` (interface) - Thêm method `getAllSubjects()` trả về danh sách môn
- [ ] `SubjectServiceImpl.java` - Implement lấy danh sách môn (từ hardcoded enum hoặc runtime list)

#### Validation cần thêm:

- [ ] UI: Kiểm tra mon1 != mon2 != mon3 khi chọn
- [ ] Service: Validate trong `SubjectGroupServiceImpl.create/update()`

---

### 2.2 Nhóm 2: Quản lý ngành (Major)

#### File cần sửa:

- [ ] `MajorFormDialog.java` - Thêm checkbox phương thức + ô nhập chỉ tiêu
- [ ] `MajorFormDialog.java` - Tổng chỉ tiêu readonly, tự động tính
- [ ] `MajorFormDialog.java` - Điểm chuẩn để trống, hiển thị "Chưa công bố"
- [ ] `MajorServiceImpl.java` - Thêm logic tính tổng chỉ tiêu từ các phương thức
- [ ] `MajorServiceImpl.java` - Validation: ít nhất 1 phương thức, chỉ tiêu > 0

#### File cần thêm:

- [ ] `MajorFormModel.java` - DTO/FormModel mới chứa thông tin chỉ tiêu theo phương thức

#### Validation cần thêm:

- [ ] UI: Checkbox chưa tick thì ô nhập chỉ tiêu disabled
- [ ] UI: Tổng chỉ tiêu readonly, tự cập nhật
- [ ] Service: Validate phương thức được chọn phải có chỉ tiêu > 0
- [ ] Service: Tổng chỉ tiêu phải > 0

---

### 2.3 Nhóm 3: Gán tổ hợp cho ngành (Major-ToHop Mapping)

#### File cần sửa:

- [ ] `MajorTohopDTO.java` - Thêm các field hệ số
- [ ] `MajorFormDialog.java` hoặc dialog gán tổ hợp - Thêm ô nhập hệ số cho mỗi môn
- [ ] `MajorServiceImpl.addTohop()` - Validate hệ số > 0

#### Validation cần thêm:

- [ ] UI: Hệ số phải là số dương, không được rỗng
- [ ] Service: hsmon1, hsmon2, hsmon3 > 0

---

### 2.4 Nhóm 4: Quy đổi điểm & Tính điểm xét tuyển

#### File cần sửa:

- [ ] `ScoreServiceImpl.java` - Thêm method `calculateAdmissionScore()` có áp dụng quy đổi
- [ ] Thêm pipeline theo thứ tự:
  1. Lấy điểm gốc từ XtDiemthixettuyen
  2. Xác định phương thức xét tuyển
  3. Xác định ngành và tổ hợp
  4. Lấy điểm các môn thuộc tổ hợp
  5. **Áp dụng quy đổi điểm** (từ XtBangquydoi)
  6. **Áp dụng hệ số** (từ XtNganhTohop.hsmon1/2/3)
  7. Tính điểm xét tuyển cuối cùng
  8. So sánh với điểm sàn

#### File cần thêm:

- [ ] `ScoreCalculationService.java` (interface) - Service tính điểm mới
- [ ] `ScoreCalculationServiceImpl.java` - Implement đầy đủ pipeline
- [ ] `ConversionTableService.java` - Thêm method `findConversionRule(phuongThuc, toHop, mon)`

#### Validation cần thêm:

- [ ] Nếu không tìm thấy rule quy đổi → giữ nguyên điểm gốc
- [ ] Nếu có nhiều rule trùng → log cảnh báo cấu hình
- [ ] Điểm xét tuyển < điểm sàn → loại

---

## PHẦN 3: DANH SÁCH FILE CỤ THỂ

### 3.1 UI Panels (Swing)

| File                                                  | Thay đổi                                                     |
| ----------------------------------------------------- | -------------------------------------------------------------- |
| `ui/panel/major/MajorFormDialog.java`               | Thêm checkbox phương thức, ô chỉ tiêu, hệ số tổ hợp |
| `ui/panel/subjectgroup/SubjectGroupFormDialog.java` | Chuyển text → combobox, thêm validation                     |

### 3.2 Service (BUS)

| File                                       | Thay đổi                         |
| ------------------------------------------ | ---------------------------------- |
| `bus/interfaces/MajorService.java`       | Thêm validation methods           |
| `bus/impl/MajorServiceImpl.java`         | Thêm logic tính tổng chỉ tiêu |
| `bus/interfaces/ScoreService.java`       | Thêm method tính điểm          |
| `bus/impl/ScoreServiceImpl.java`         | Thêm pipeline quy đổi điểm    |
| Thêm `ScoreCalculationService.java`     | Interface mới                     |
| Thêm `ScoreCalculationServiceImpl.java` | Implement pipeline đầy đủ      |

### 3.3 DTO/FormModel

| File                             | Thay đổi                                                        |
| -------------------------------- | ----------------------------------------------------------------- |
| `dto/major/MajorTohopDTO.java` | Thêm hsmon1, hsmon2, hsmon3                                      |
| Thêm `MajorFormModel.java`    | FormModel cho MajorFormDialog với chỉ tiêu theo phương thức |
| Thêm `SubjectDTO.java`        | DTO cho danh sách môn                                           |

### 3.4 Repository

| File             | Thay đổi                                      |
| ---------------- | ----------------------------------------------- |
| Không cần sửa | Các repository hiện tại đã đáp ứng đủ |

---

## PHẦN 4: LIMITATIONS (KHI KHÔNG ĐƯỢC ĐỔI DB)

### 4.1 Chỉ tiêu theo phương thức

- **Hạn chế:** Nếu thêm phương thức xét tuyển mới, cần thêm cột mới trong DB
- **Hiện tại:** 4 phương thức (Tuyển thẳng, ĐGNL, VSAT, THPT) đã có đủ cột sl_*
- **Ảnh hưởng:** Mở rộng phương thức cần thay đổi schema

### 4.2 Thêm môn năng khiếu

- **Hạn chế:** Không có bảng lưu môn học riêng
- **Workaround:** ComboBox editable hoặc dialog thêm runtime → lưu text đơn thuần
- **Ảnh hưởng:** Môn mới tạo không có trong danh sách khi khởi động lại app

### 4.3 Điểm chuẩn

- **Hạn chế:** Nếu DB bắt buộc NOT NULL cho `n_diemtrungtuyen`
- **Workaround:** Set giá trị mặc định 0 hoặc giá trị an toàn khi tạo mới
- **Ghi chú:** Cần check constraint thực tế trong DB

---

## PHẦN 5: TEST CASE CẦN KIỂM TRA

### 5.1 Test tổ hợp môn

- [ ] Tạo tổ hợp mới với 3 môn khác nhau → thành công
- [ ] Tạo tổ hợp trùng môn (mon1 = mon2) → báo lỗi
- [ ] Thêm môn năng khiếu mới → hiển thị trong combobox
- [ ] Sửa tổ hợp có sẵn → load đúng dữ liệu

### 5.2 Test ngành

- [ ] Tạo ngành với checkbox phương thức → lưu thành công
- [ ] Không tick phương thức nào → báo lỗi validation
- [ ] Tích chọn phương thức nhưng không nhập chỉ tiêu → báo lỗi
- [ ] Tổng chỉ tiêu tự động cập nhật khi thay đổi ô chỉ tiêu
- [ ] Điểm chuẩn null → hiển thị "Chưa công bố"
- [ ] Điểm sàn hợp lệ (0-30)

### 5.3 Test hệ số tổ hợp

- [ ] Gán tổ hợp cho ngành với hệ số → lưu thành công
- [ ] Hệ số âm → báo lỗi
- [ ] Hệ số = 0 → báo lỗi
- [ ] Hệ số null → báo lỗi

### 5.4 Test quy đổi điểm

- [ ] Có rule quy đổi → áp dụng đúng
- [ ] Không có rule quy đổi → giữ nguyên điểm gốc
- [ ] Điểm sau quy đổi < điểm sàn → loại

---

## PHẦN 6: HƯỚNG NÂNG CẤP SAU (NẾU ĐƯỢC PHÉP THAY ĐỔI DB)

1. **Tạo bảng `xt_chitieu_phuongthuc`** - Lưu chỉ tiêu chi tiết theo từng phương thức (linh hoạt hơn cột cố định sl_*)
2. **Tạo bảng `xt_monhoc`** - Quản lý danh sách môn học riêng
3. **Thêm cột `loai_mon`** vào bảng môn - Phân biệt văn hóa/năng khiếu
4. **Tạo bảng `xt_diem_xettuyen`** - Lưu điểm xét tuyển đã tính toán

---

## PHẦN 7: XÁC NHẬN TỪ USER

Vui lòng duyệt kế hoạch trên trước khi tôi bắt đầu implement.

**Các điểm chính:**

1. ✅ Hệ số tổ hợp đã có sẵn trong DB (`hsmon1/2/3`)
2. ⚠️ Chỉ tiêu theo phương thức chỉ lưu tổng vào `n_chitieu` (không lưu chi tiết)
3. ⚠️ Thêm môn năng khiếu chỉ là runtime (không lưu vĩnh viễn)
4. ✅ Quy đổi điểm đã có bảng `xt_bangquydoi` đầy đủ

**Chờ bạn xác nhận để implement.**
