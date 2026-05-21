---
title: Đồng bộ đối tượng & khu vực ưu tiên theo UEF, auto-tạo row điểm cộng khi nhập điểm
status: implementing
created: 2026-05-19
updated: 2026-05-19
refs:
  specs:
    - .kiro/specs/score-calculator-tool/design.md
    - .kiro/specs/score-conversion-correctness-cleanup/requirements.md
  files:
    - swing-app/src/main/java/com/example/managementadmissionwf/utils/PriorityScoreCalculator.java
    - swing-app/src/main/java/com/example/managementadmissionwf/ui/panel/candidate/CandidateFormDialog.java
    - swing-app/src/main/java/com/example/managementadmissionwf/ui/panel/candidate/CandidatePanel.java
    - swing-app/src/main/java/com/example/managementadmissionwf/ui/panel/score/ScoreFormDialog.java
    - swing-app/src/main/java/com/example/managementadmissionwf/bus/impl/BonusScoreServiceImpl.java
    - swing-app/src/main/java/com/example/managementadmissionwf/dal/entity/XtThisinhxettuyen25.java
    - thymeleaf_web/src/main/resources/templates/score/calculator.html
    - thymeleaf_web/src/main/java/com/example/thymeleaf_web/model/dto/ScoreCalculatorForm.java
    - seed-data/generate_seed_data.py
---

# Đồng bộ đối tượng & khu vực ưu tiên theo UEF, auto-tạo row điểm cộng khi nhập điểm

## Intent

Hiện tại danh sách "đối tượng ưu tiên" và "khu vực ưu tiên" trong swing-app và thymeleaf_web không nhất quán với quy chế UEF: dropdown đối tượng trong form thí sinh đang chứa giá trị khu vực ("KV1", "KV2-NT"…), thiếu lựa chọn KV2-NT trong filter, và web dùng giá trị số trực tiếp (0.5, 1.0…) thay vì mã. Bên cạnh đó, khi cán bộ nhập điểm cho thí sinh thì bảng `xt_diemcongxettuyen` không tự động có row tương ứng — phải vào `BonusScorePanel` mở thêm tay, dễ sót. Spec này chuẩn hoá danh sách đối tượng/khu vực và mức điểm cộng theo quy chế (Thông tư 08/2022/TT-BGDĐT, theo trang UEF), đồng thời thêm auto-upsert row điểm cộng ngay tại form nhập điểm thi.

## Decisions

- **Mã đối tượng giữ `UT1` / `UT2` / `Không`** (không đổi sang 01–07). Mức điểm: UT1 = +2.0, UT2 = +1.0, Không = 0. Lý do: đơn giản, không phải migrate dữ liệu cũ từ KT1/KT2/KT3 sang 01–07; vẫn đúng quy chế (UT1 = nhóm 01–04, UT2 = nhóm 05–07).
- **Loại bỏ `KT1`, `KT2`, `KT3`** khỏi mọi dropdown — các giá trị này đều = 0 điểm và không tồn tại trong quy chế. Migrate dữ liệu cũ KT* → "Không" qua một SQL update one-shot.
- **Khu vực giữ 4 mã** `KV1` / `KV2-NT` / `KV2` / `KV3` với điểm cộng `0.75 / 0.50 / 0.25 / 0.00`. Phải bổ sung `KV2-NT` vào dropdown filter `CandidatePanel` (đang thiếu) và sửa dropdown đối tượng `CandidateFormDialog` (đang chứa nhầm giá trị khu vực).
- **Schema `xt_diemcongxettuyen` giữ nguyên** một cột `diemUtxt` (gộp đối tượng + khu vực). Không tách thành 2 cột riêng — schema hiện đã ổn định, tách thêm rủi ro migration không tương xứng giá trị mang lại.
- **Trigger auto-upsert row điểm cộng = `CandidateService.createCandidate` + `updateCandidate`** (khi tạo/sửa thí sinh ở `CandidateFormDialog`). Sau khi save candidate thành công, gọi `bonusScoreService.upsertForCandidate(cccd)` để tạo hoặc cập nhật row trong `xt_diemcongxettuyen` với `diemUtxt` tính từ `PriorityScoreCalculator.calculateForCandidate(...)`. Idempotent — gọi nhiều lần cũng cho ra cùng kết quả.
- **Không động vào `ScoreFormDialog`** — combo đối tượng/khu vực không thuộc khái niệm "điểm thi", nó là thuộc tính thí sinh. `ScoreFormDialog` giữ nguyên.
- **Mức điểm cộng tách khỏi UI**, đặt trong `PriorityScoreCalculator` như single source of truth. Mọi chỗ khác (UI dropdown labels, web calculator, seed) đọc mapping từ đây thay vì hardcode lại.
- **Web calculator: đổi cả values lẫn labels.** Hiện `calculator.html` đang sai theo UEF: UT2=0.5, UT1=1.0, có thêm "UT đặc biệt"=2.0. Phải sửa thành đúng UT2=1.0, UT1=2.0, bỏ "UT đặc biệt". DTO `ScoreCalculatorForm` (`Double doiTuongUuTien`, `Double khuVucUuTien`) giữ nguyên — chỉ đổi option `value` + `text` trong template. Khu vực hiện đã đúng (KV3=0, KV2=0.25, KV2-NT=0.5, KV1=0.75) → chỉ chỉnh label cho rõ.
- **Migration SQL chạy thủ công.** Project chưa có Flyway/Liquibase setup. Đặt SQL ở `docs/migrations/` và document trong README; DBA/dev chạy tay một lần, sau đó gọi `bonusScoreService.recomputeAllPriorityPoints()` qua admin action hoặc một lần boot có cờ.
- **Công thức cap 22.5** đã có sẵn (`PriorityScoreCalculator.applyCap`), không động đến.

## Approach

Trên swing-app, gom mapping đối tượng/khu vực vào `PriorityScoreCalculator` thành nguồn duy nhất, rồi cập nhật `CandidateFormDialog` (sửa combo đối tượng đang chứa nhầm giá trị khu vực) và `CandidatePanel` filter (đối tượng đang sai + thiếu KV2-NT trong filter khu vực). Logic auto-upsert đặt ngay trong `CandidateServiceImpl.createCandidate` và `updateCandidate` — sau khi save candidate thành công, gọi `bonusScoreService.upsertForCandidate(cccd)`. Method mới này find-or-create row trong `xt_diemcongxettuyen`, recompute `diemUtxt` từ đối tượng + khu vực hiện tại, giữ `diemCc` cũ nếu đã có. `ScoreFormDialog` giữ nguyên — đối tượng/khu vực không phải khái niệm điểm thi nên không thuộc form đó. Trên thymeleaf_web, `calculator.html` hiện sai cả values (UT2=0.5, có "UT đặc biệt") — sửa cả values lẫn labels theo bảng UEF, DTO `ScoreCalculatorForm` giữ nguyên. Cuối cùng cập nhật `generate_seed_data.py` để seed mới sinh ra `UT1/UT2/Không` thay cho `KT1/KT2/KT3`, và viết một SQL migrate one-shot (chạy thủ công vì chưa có Flyway).

## Scope

**In:**
- Sửa `CandidateFormDialog`: dropdown đối tượng đổi thành `["Không", "UT1", "UT2"]` (đang chứa nhầm giá trị khu vực + "Con thương binh"); dropdown khu vực đổi thành `["KV1", "KV2-NT", "KV2", "KV3"]`.
- Sửa `CandidatePanel` filter: filter đối tượng (line 47) **thay toàn bộ** thành `["Tất cả", "Không", "UT1", "UT2"]` (hiện chứa nhầm giá trị khu vực + "Con thương binh"); filter khu vực (line 42) bổ sung `KV2-NT`.
- Sửa `PriorityScoreCalculator`: bỏ map `KT1/KT2/KT3`, giữ `UT1/UT2`, thêm key `"Không"` (= 0). Expose `DOI_TUONG_OPTIONS`, `KHU_VUC_OPTIONS`, `displayLabel(code, points)` để UI dùng chung.
- Trong `BonusScoreService` thêm method **mới** `upsertForCandidate(cccd)` idempotent: `findByCccd` → nếu rỗng tạo `XtDiemcongxettuyen` mới với `diemCc=0`, recompute `diemUtxt` qua `PriorityScoreCalculator.calculateForCandidate(candidate)`, save; nếu có giữ nguyên `diemCc`, recompute `diemUtxt`, save. Đây là method mới (không chỉ là expose `autoFillUtxtFromCandidate` — method cũ chỉ mutate DTO, không tự find/create/save).
- Hook trong `CandidateServiceImpl.createCandidate(...)` và `updateCandidate(...)`: sau khi save candidate thành công → `bonusScoreService.upsertForCandidate(cccd)`. Cùng `@Transactional` để 2 thao tác atomic.
- `ScoreFormDialog` / `ScoreController` / `ScorePanel`: **không động**.
- Cập nhật `thymeleaf_web/templates/score/calculator.html`: sửa cả `value` và label các option theo UEF (`doiTuongUuTien`: 0/1.0/2.0 với label `Không / UT2 (+1.0đ) / UT1 (+2.0đ)`; bỏ "UT đặc biệt"); khu vực giữ values, chỉnh label cho rõ.
- Tìm và sửa các template thymeleaf khác liệt kê doi_tuong/khu_vuc (filter, hiển thị) cho nhất quán — grep `doi_tuong|khu_vuc|doiTuong|khuVuc` trong `templates/`.
- Cập nhật `generate_seed_data.py`: `DOI_TUONG = ["Không", "UT1", "UT2"]`, sửa tính `utxt` để cộng cả điểm đối tượng + khu vực (hiện chỉ tính khu vực).
- SQL migrate one-shot (chạy thủ công, đặt ở `docs/migrations/`): `UPDATE xt_thisinhxettuyen25 SET doi_tuong='Không' WHERE doi_tuong IN ('KT1','KT2','KT3','Con thương binh') OR doi_tuong IS NULL OR doi_tuong=''`. Sau migration chạy `bonusScoreService.recomputeAllPriorityPoints()` để recompute toàn bộ bonus rows.

**Out:**
- Tách schema `xt_diemcongxettuyen` thành 2 cột `diem_doi_tuong` + `diem_khu_vuc` (đã quyết: giữ 1 cột `diemUtxt`).
- Đổi mã sang định dạng 01–07 (đã quyết: giữ UT1/UT2).
- Động vào `ScoreFormDialog` / `ScoreController` / `ScorePanel` (đã quyết: trigger nằm ở quản lí thí sinh, không ở quản lí điểm).
- Refactor `calculatePriorityScore` ở các service (đã đúng và đã có test theo spec score-conversion-correctness-cleanup).
- Sửa thymeleaf form admin nhập điểm cộng (chưa tồn tại — `thymeleaf_web` chỉ là tool tra cứu).
- Thêm cột `ghi_chu` cho bonus row.

## Future Notes

- Nếu sau này cần audit chi tiết "thí sinh được +bao nhiêu từ đối tượng vs khu vực", có thể tách `diem_doi_tuong` + `diem_khu_vuc` thành 2 cột — schema hiện không cản trở mở rộng.
- Có thể chuyển `PriorityScoreCalculator` thành enum `DoiTuongUuTien` / `KhuVucUuTien` (Java enum với field `code` + `mucDiem`) để type-safe hơn. Hiện giữ `Map<String, Double>` cho gọn vì chỉ có 3+4 giá trị.
- Thymeleaf web nếu sau này có form admin nhập điểm thi (không chỉ tool tra cứu), nên áp dụng cùng pattern auto-upsert ở controller save.

## Progress

spec written, pending review

## Review

_Filled in after completion._

## Learnings

_Filled in after completion._

---

> **Agent reference — not for user review.** Everything below this line is working notes for implement-spec.

## Implementation Notes

### Bảng mapping chính thức (single source of truth)

Đặt trong `PriorityScoreCalculator.java`:

```java
public static final Map<String, Double> DOI_TUONG_POINTS = Map.of(
    "Không", 0.00,
    "UT1",   2.00,   // nhóm đối tượng 01-04
    "UT2",   1.00    // nhóm đối tượng 05-07
);

public static final Map<String, Double> KHU_VUC_POINTS = Map.of(
    "KV1",    0.75,
    "KV2-NT", 0.50,
    "KV2",    0.25,
    "KV3",    0.00
);

public static final List<String> DOI_TUONG_OPTIONS = List.of("Không", "UT1", "UT2");
public static final List<String> KHU_VUC_OPTIONS   = List.of("KV1", "KV2-NT", "KV2", "KV3");

public static String displayLabel(String code, Map<String, Double> points) {
    Double p = points.getOrDefault(code, 0.0);
    return code + " (+" + new DecimalFormat("0.##").format(p) + "đ)";
}
```

### File-by-file

#### swing-app

1. **`utils/PriorityScoreCalculator.java`** (line 43–57)
   - Thay `DOI_TUONG_POINTS` bỏ `KT1/KT2/KT3`, thêm `"Không"`.
   - Thêm `DOI_TUONG_OPTIONS`, `KHU_VUC_OPTIONS`, `displayLabel()`.
   - Method `calculateForCandidate(XtThisinhxettuyen25 candidate)` đã có — giữ nguyên, chỉ đảm bảo nó dùng `getOrDefault(code, 0.0)` để key lạ không crash.

2. **`ui/panel/candidate/CandidateFormDialog.java:136`**
   - `cboDoiTuong = createComboBox(PriorityScoreCalculator.DOI_TUONG_OPTIONS.toArray(new String[0]));`
   - Render label thành `displayLabel(...)` qua custom `ListCellRenderer` (giữ value là code thuần).
   - Line 143 (`cboKhuVuc`): đổi sang `KHU_VUC_OPTIONS`.

3. **`ui/panel/candidate/CandidatePanel.java:42, 46-47`**
   - Filter khu vực: `["Tất cả", "KV1", "KV2-NT", "KV2", "KV3"]`.
   - Filter đối tượng: `["Tất cả", "Không", "UT1", "UT2"]`.

4. **`bus/impl/BonusScoreServiceImpl.java`** + interface `bus/BonusScoreService.java`:
   - Thêm method **mới** `upsertForCandidate(String cccd)` (không chỉ là expose `autoFillUtxtFromCandidate` — method cũ private + chỉ mutate DTO, không có find/create/save logic):
     ```java
     public void upsertForCandidate(String cccd) {
         var candidate = candidateRepository.findByCccd(cccd)
             .orElseThrow(() -> new IllegalArgumentException("No candidate: " + cccd));
         var entity = bonusScoreRepository.findByCccd(cccd).orElseGet(() -> {
             var e = new XtDiemcongxettuyen();
             e.setCccd(cccd);
             e.setDiemCc(0.0);
             return e;
         });
         double diemUtxt = PriorityScoreCalculator.calculateForCandidate(candidate);
         entity.setDiemUtxt(diemUtxt);
         entity.setDiemTong(BigDecimal.valueOf(entity.getDiemCc() + diemUtxt));
         bonusScoreRepository.save(entity);
     }
     ```
   - Idempotent: gọi nhiều lần liên tiếp với cùng candidate state → cùng kết quả; không reset `diemCc` nếu đã có.

5. **`bus/impl/CandidateServiceImpl.java`** — hook auto-upsert vào `createCandidate` và `updateCandidate`:
   - Inject `BonusScoreService` qua constructor.
   - Cuối method `createCandidate(...)`, sau khi `candidateRepository.save(entity)` thành công:
     ```java
     bonusScoreService.upsertForCandidate(saved.getCccd());
     ```
   - Tương tự cuối method `updateCandidate(...)` — vì user có thể đổi đối tượng/khu vực khi sửa thí sinh, cần recompute lại bonus row.
   - Cả 2 thao tác (`save candidate` + `upsert bonus`) phải nằm cùng một `@Transactional` để rollback chung khi có lỗi.
   - **Không cần thêm method `updateUuTien` riêng** — đã quyết hook trực tiếp vào `createCandidate/updateCandidate` thay vì có endpoint riêng cho 2 trường này.

6. **`config/DataSeeder.java`** (nếu có seed candidate): không bắt buộc, để generate_seed_data.py lo. Nhưng nếu `DataSeeder` có hardcode `KT1/KT2/KT3` thì xoá.

7. **Score-side files (`ScoreFormDialog`, `ScorePanel`, `ScoreController`)**: KHÔNG động vào — đã đồng ý đối tượng/khu vực thuộc quản lí thí sinh, không thuộc form điểm.

#### thymeleaf_web

1. **`templates/score/calculator.html` (line 242–254)** — sửa cả values và labels (hiện sai theo UEF: UT2=0.5, có "UT đặc biệt"=2.0):
   ```html
   <select th:field="*{doiTuongUuTien}">
       <option value="0.0">Không (0đ)</option>
       <option value="1.0">UT2 — nhóm 05–07 (+1.0đ)</option>
       <option value="2.0">UT1 — nhóm 01–04 (+2.0đ)</option>
   </select>
   <select th:field="*{khuVucUuTien}">
       <option value="0.0">KV3 (0đ)</option>
       <option value="0.25">KV2 (+0.25đ)</option>
       <option value="0.5">KV2-NT (+0.50đ)</option>
       <option value="0.75">KV1 (+0.75đ)</option>
   </select>
   ```
   DTO `ScoreCalculatorForm` (`Double doiTuongUuTien`, `Double khuVucUuTien`) giữ nguyên — chỉ template thay đổi.

2. **Tìm thêm template nào liệt kê doi_tuong/khu_vuc**: `grep -rn "doi_tuong\|khu_vuc\|doiTuong\|khuVuc" thymeleaf_web/src/main/resources/templates/`. Nếu có dropdown filter trong list/search → đồng bộ label.

#### seed-data

1. **`seed-data/generate_seed_data.py:299-300`**
   - `KHU_VUC = ["KV1", "KV2-NT", "KV2", "KV3"]` (giữ nguyên, đã đúng).
   - `DOI_TUONG = ["Không", "UT1", "UT2"]` (bỏ `KT1/KT2/KT3`).
   - Trọng số phân bố nên giữ "Không" chiếm ~70%, UT2 ~20%, UT1 ~10% (sát thực tế).

2. **`seed-data/generate_seed_data.py:391`** — tính `utxt`:
   - Hiện chỉ tính từ khu_vuc. Phải sửa thành:
     ```python
     dt_pts = {"Không": 0.0, "UT1": 2.0, "UT2": 1.0}.get(ts["doi_tuong"], 0.0)
     kv_pts = {"KV1": 0.75, "KV2-NT": 0.5, "KV2": 0.25, "KV3": 0.0}.get(ts["khu_vuc"], 0.0)
     utxt = dt_pts + kv_pts
     ```

#### Migration SQL (one-shot)

Tạo file `swing-app/src/main/resources/db/migration/V20260520__normalize_doi_tuong.sql` (hoặc tương đương theo cấu hình Flyway):

```sql
-- Normalize legacy đối tượng codes to UEF-aligned values
UPDATE xt_thisinhxettuyen25
SET doi_tuong = 'Không'
WHERE doi_tuong IN ('KT1', 'KT2', 'KT3', 'Con thương binh')
   OR doi_tuong IS NULL
   OR doi_tuong = '';

-- Recompute điểm UTXT cho các bonus rows hiện có (BonusScoreService.recomputeAllPriorityPoints
-- nên được gọi sau migration để đồng bộ, nhưng để chắc chắn ngay ở DB level:
-- application-side recompute is preferred over a hardcoded SQL formula here.
```

Nếu project chưa dùng Flyway/Liquibase, đặt SQL vào `docs/migrations/` và document trong README để DBA chạy tay; sau đó gọi `bonusScoreService.recomputeAllPriorityPoints()` qua một CLI command hoặc startup hook một lần.

### Test cases cần thêm

`swing-app/src/test/java/.../PriorityScoreCalculatorTest.java`:
- `DOI_TUONG_POINTS` chứa đúng 3 keys, `KHU_VUC_POINTS` chứa đúng 4 keys.
- `displayLabel("UT1", DOI_TUONG_POINTS)` = `"UT1 (+2đ)"`.
- `calculateForCandidate(candidate UT1+KV1)` = 2.75.
- `calculateForCandidate(candidate có code lạ)` = 0 (graceful).

`swing-app/src/test/java/.../BonusScoreServiceImplTest.java`:
- `upsertForCandidate(cccd_chưa_có_row)` → tạo mới, `diemCc=0`, `diemUtxt` đúng.
- `upsertForCandidate(cccd_đã_có_row_với_diemCc=1.5)` → giữ `diemCc=1.5`, recompute `diemUtxt`.
- Gọi `upsertForCandidate` 2 lần liên tiếp → kết quả giống nhau (idempotent).

`swing-app/src/test/java/.../CandidateServiceImplTest.java`:
- `createCandidate(...)` → bonus row được tạo trong `xt_diemcongxettuyen` với `diemUtxt` khớp công thức.
- `updateCandidate(...)` đổi `khuVuc` từ `KV3` → `KV1` → bonus row tương ứng `diemUtxt` chuyển từ 0 → 0.75 (giả sử đối tượng = "Không").
- Nếu candidate save thất bại → bonus row không được tạo (rollback transaction).

### Thứ tự thực hiện đề xuất

1. Update `PriorityScoreCalculator` (foundation).
2. Sửa `CandidateFormDialog`, `CandidatePanel` (UI dropdowns).
3. Thêm `BonusScoreService.upsertForCandidate()`.
4. Hook vào `CandidateServiceImpl.createCandidate` và `updateCandidate` (cùng transaction).
5. Sửa `calculator.html` + grep template khác.
6. Sửa `generate_seed_data.py`.
7. Viết migration SQL.
8. Viết tests.
9. Build cả 2 module, smoke test: tạo thí sinh mới với UT1+KV1 → vào BonusScorePanel xem row đã tự xuất hiện với `diemUtxt = 2.75`.
