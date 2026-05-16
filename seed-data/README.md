# seed-data/

Sinh full bộ Excel seed data cho project `admissions-management`. Mục đích:

1. **Import trực tiếp vào swing-app**: file trong `output/` dùng đúng header
   theo `@ExcelColumn(name=...)` của `CandidateDTO`, `ScoreDTO`,
   `BonusScoreDTO`, `MajorDTO`, `SubjectGroupResponse`, `ConversionTableDTO`.
   Ngày sinh đã ở định dạng `dd/MM/yyyy`. Bấm nút **Nhập Excel** trong panel
   tương ứng và chọn file là chạy.
2. **Import vào MySQL Workbench / DBeaver**: file trong `output/db/` giữ tên
   cột giống MySQL (`xt_*`), ngày `yyyy-MM-dd`. Bao gồm các bảng quan hệ
   (nguyện vọng, mapping ngành ↔ tổ hợp, users) mà swing-app không có form
   import Excel.
3. **Demo theo SGU 2025**: tuỳ chọn `--fetch` cố gắng kéo điểm chuẩn 2025 từ
   trang công khai của Đại học Sài Gòn; nếu offline thì fallback sang dữ
   liệu cứng.

## Chạy

```powershell
# Mặc định: offline + dữ liệu fallback (>=100 row mỗi bảng)
python seed-data/generate_seed_data.py

# Bật fetch SGU 2025
python seed-data/generate_seed_data.py --fetch

# Tăng số thí sinh giả (mặc định 120)
python seed-data/generate_seed_data.py --candidates 200
```

Script tự cài `pandas` và `openpyxl` nếu môi trường thiếu. Khi `--fetch`,
cài thêm `requests` + `beautifulsoup4`.

## Output

### `output/` — App-import-ready

| File                        | Panel swing-app                   | Header dùng                          |
| --------------------------- | --------------------------------- | ------------------------------------ |
| `candidates.xlsx`           | Quản lý thí sinh → Nhập Excel     | `CandidateDTO @ExcelColumn`          |
| `scores.xlsx`               | Quản lý điểm → Nhập Excel         | `ScoreDTO @ExcelColumn`              |
| `bonus_scores.xlsx`         | Điểm cộng (form thêm thủ công)    | `BonusScoreDTO @ExcelColumn`         |
| `majors.xlsx`               | Danh sách ngành → Nhập Excel      | `MajorDTO @ExcelColumn`              |
| `subject_groups.xlsx`       | Tổ hợp môn → Nhập Excel           | `SubjectGroupResponse @ExcelColumn`  |
| `conversion_table.xlsx`     | Bảng quy đổi (nếu UI có)          | `ConversionTableDTO @ExcelColumn`    |

> Lưu ý: tổ hợp môn `subject_groups.xlsx` import qua `SubjectGroupResponse`
> nên có thêm cột `STT` không có trong DTO; cột thừa sẽ bị bỏ qua.

### `output/db/` — DB-style (MySQL Workbench / DBeaver)

| File                              | Bảng DB                  | # rows |
| --------------------------------- | ------------------------ | -----: |
| `01_xt_tohop_monthi.xlsx`         | `xt_tohop_monthi`        |   100 |
| `02_xt_nganh.xlsx`                | `xt_nganh`               |   110 |
| `03_xt_nganh_tohop.xlsx`          | `xt_nganh_tohop`         |   296 |
| `04_xt_thisinhxettuyen25.xlsx`    | `xt_thisinhxettuyen25`   |   120 |
| `05_xt_diemthixettuyen.xlsx`      | `xt_diemthixettuyen`     |   172 |
| `06_xt_diemcongxettuyen.xlsx`     | `xt_diemcongxettuyen`    |   120 |
| `07_xt_nguyenvongxettuyen.xlsx`   | `xt_nguyenvongxettuyen`  |   347 |
| `08_xt_bangquydoi.xlsx`           | `xt_bangquydoi`          |   223 |
| `09_users.xlsx`                   | `users`                  |   123 |

Mọi bảng đều >=100 row đáp ứng yêu cầu rubric demo.

## Bug import "thành công nhưng không có dòng nào"

Trước đây file Excel chỉ chứa header DB (như `cccd`, `ho`, `ten`), trong khi
swing-app match theo display name (`CCCD`, `Họ`, `Tên`, ...) ở
`@ExcelColumn(name=...)`. Hệ quả: import báo "Thành công 0, Lỗi 0".

Sau khi sửa:
- File `output/` dùng đúng display name.
- `ExcelUtil.importExcel` giờ throw `IllegalArgumentException` rõ ràng nếu
  KHÔNG khớp được header nào — không còn fail im lặng nữa.

## Khi nào nên dùng cái nào

| Tình huống                              | Khuyên dùng                            |
| --------------------------------------- | -------------------------------------- |
| Muốn nhập nhanh thí sinh + điểm + ngành | `output/candidates.xlsx`, `scores.xlsx`, `majors.xlsx` |
| Muốn nhập nguyện vọng / mapping         | `output/db/*.xlsx` (Workbench)         |
| Demo full database từ scratch           | Drop DB → import toàn bộ `output/db/`  |
| Test riêng `swing-app` nhập Excel       | `output/*.xlsx`                         |
| Cần dữ liệu SGU 2025 chính xác          | `--fetch` (yêu cầu mạng)               |
