Database **`xettuyen2026`** được thiết kế cho  **hệ thống xét tuyển đại học** . Nó quản lý:

* Thí sinh
* Điểm thi
* Ngành
* Tổ hợp môn
* Nguyện vọng
* Điểm cộng / ưu tiên
* Bảng quy đổi điểm

Kiến trúc này khá giống  **hệ thống xét tuyển của Bộ GD (THPT / ĐGNL / VSAT / tuyển thẳng)** .

Tôi sẽ phân tích theo  **3 lớp logic** :

1️⃣ **Master Data (Danh mục)**

2️⃣ **Candidate Data (Dữ liệu thí sinh)**

3️⃣ **Admission Processing (Xử lý xét tuyển)**

---

# 1. MASTER DATA (DỮ LIỆU DANH MỤC)

## 1.1 `xt_nganh`

Danh sách **ngành đào tạo**

| Field            | Meaning           |
| ---------------- | ----------------- |
| idnganh          | id nội bộ       |
| manganh          | mã ngành        |
| tennganh         | tên ngành       |
| n_tohopgoc       | tổ hợp gốc     |
| n_chitieu        | chỉ tiêu        |
| n_diemsan        | điểm sàn       |
| n_diemtrungtuyen | điểm chuẩn     |
| n_tuyenthang     | có tuyển thẳng |
| n_dgnl           | có xét ĐGNL    |
| n_thpt           | xét điểm THPT  |
| n_vsat           | xét VSAT         |

### Các cột thống kê

| Field   | Meaning                   |
| ------- | ------------------------- |
| sl_xtt  | số lượng tuyển thẳng |
| sl_dgnl | số lượng ĐGNL         |
| sl_vsat | số lượng VSAT          |
| sl_thpt | số lượng THPT          |

👉 Đây là **bảng trung tâm của admission**

---

## 1.2 `xt_tohop_monthi`

Danh sách **tổ hợp môn**

| Field    | Meaning                    |
| -------- | -------------------------- |
| matohop  | mã tổ hợp (A00, D01...) |
| mon1     | môn 1                     |
| mon2     | môn 2                     |
| mon3     | môn 3                     |
| tentohop | tên tổ hợp              |

Ví dụ

| matohop | mon1 | mon2 | mon3 |
| ------- | ---- | ---- | ---- |
| A00     | TO   | LI   | HO   |
| D01     | TO   | VA   | N1   |

---

## 1.3 `xt_nganh_tohop`

Mapping **Ngành ↔ Tổ hợp**

| Field   | Meaning      |
| ------- | ------------ |
| manganh | mã ngành   |
| matohop | mã tổ hợp |
| th_mon1 | môn         |
| hsmon1  | hệ số      |
| th_mon2 | môn         |
| hsmon2  | hệ số      |
| th_mon3 | môn         |
| hsmon3  | hệ số      |

Ví dụ

Computer Science:

| manganh | matohop |
| ------- | ------- |
| 7480101 | A00     |
| 7480101 | D01     |

### Các flag môn

| Column |
| ------ |
| TO     |
| LI     |
| HO     |
| SI     |
| VA     |
| SU     |
| DI     |
| TI     |
| KTPL   |

👉 dùng để **detect môn có trong tổ hợp**

---

# 2. CANDIDATE DATA (DỮ LIỆU THÍ SINH)

## 2.1 `xt_thisinhxettuyen25`

Thông tin **thí sinh**

| Field      | Meaning                 |
| ---------- | ----------------------- |
| cccd       | ID                      |
| sobaodanh  | số báo danh           |
| ho         | họ                     |
| ten        | tên                    |
| ngay_sinh  | ngày sinh              |
| dien_thoai | phone                   |
| email      | email                   |
| gioi_tinh  | gender                  |
| noi_sinh   | birthplace              |
| doi_tuong  | đối tượng ưu tiên |
| khu_vuc    | khu vực                |

👉 CCCD = **primary identity**

---

## 2.2 `xt_diemthixettuyen`

Điểm thi của thí sinh

| Field        | Meaning        |
| ------------ | -------------- |
| cccd         | thí sinh      |
| sobaodanh    | SBD            |
| d_phuongthuc | phương thức |

### Điểm các môn

| Column | Subject |
| ------ | ------- |
| TO     | Toán   |
| LI     | Lý     |
| HO     | Hóa    |
| SI     | Sinh    |
| SU     | Sử     |
| DI     | Địa   |
| VA     | Văn    |

### Ngoại ngữ

| Column |
| ------ |
| N1_THI |
| N1_CC  |

N1_CC = max(thi, quy đổi)

---

### Các bài thi khác

| Column |
| ------ |
| NL1    |
| NK1    |
| NK2    |

---

# 3. ADMISSION PROCESSING (XÉT TUYỂN)

## 3.1 `xt_nguyenvongxettuyen`

Danh sách **nguyện vọng**

| Field      | Meaning     |
| ---------- | ----------- |
| nn_cccd    | thí sinh   |
| nv_manganh | ngành      |
| nv_tt      | thứ tự NV |

---

### Điểm

| Column        | Meaning             |
| ------------- | ------------------- |
| diem_thxt     | tổng điểm 3 môn |
| diem_utqd     | điểm ưu tiên    |
| diem_cong     | điểm cộng        |
| diem_xettuyen | điểm cuối        |

Công thức thường:

<pre class="overflow-visible! px-0!" data-start="2989" data-end="3045"><div class="relative w-full mt-4 mb-1"><div class=""><div class="relative"><div class="h-full min-h-0 min-w-0"><div class="h-full min-h-0 min-w-0"><div class="border border-token-border-light border-radius-3xl corner-superellipse/1.1 rounded-3xl"><div class="h-full w-full border-radius-3xl bg-token-bg-elevated-secondary corner-superellipse/1.1 overflow-clip rounded-3xl lxnfua_clipPathFallback"><div class="pointer-events-none absolute end-1.5 top-1 z-2 md:end-2 md:top-1"></div><div class="pe-11 pt-3"><div class="relative z-0 flex max-w-full"><div id="code-block-viewer" dir="ltr" class="q9tKkq_viewer cm-editor z-10 light:cm-light dark:cm-light flex h-full w-full flex-col items-stretch ͼk ͼy"><div class="cm-scroller"><div class="cm-content q9tKkq_readonly"><span>diem_thxt =</span><br/><span>mon1 * hs1 +</span><br/><span>mon2 * hs2 +</span><br/><span>mon3 * hs3</span></div></div></div></div></div></div></div></div></div><div class=""><div class=""></div></div></div></div></div></pre>

<pre class="overflow-visible! px-0!" data-start="3047" data-end="3104"><div class="relative w-full mt-4 mb-1"><div class=""><div class="relative"><div class="h-full min-h-0 min-w-0"><div class="h-full min-h-0 min-w-0"><div class="border border-token-border-light border-radius-3xl corner-superellipse/1.1 rounded-3xl"><div class="h-full w-full border-radius-3xl bg-token-bg-elevated-secondary corner-superellipse/1.1 overflow-clip rounded-3xl lxnfua_clipPathFallback"><div class="pointer-events-none absolute end-1.5 top-1 z-2 md:end-2 md:top-1"></div><div class="pe-11 pt-3"><div class="relative z-0 flex max-w-full"><div id="code-block-viewer" dir="ltr" class="q9tKkq_viewer cm-editor z-10 light:cm-light dark:cm-light flex h-full w-full flex-col items-stretch ͼk ͼy"><div class="cm-scroller"><div class="cm-content q9tKkq_readonly"><span>diem_xettuyen =</span><br/><span>diem_thxt</span><br/><span>+ diem_utqd</span><br/><span>+ diem_cong</span></div></div></div></div></div></div></div></div></div><div class=""><div class=""></div></div></div></div></div></pre>

---

### Kết quả

| Field     |
| --------- |
| nv_ketqua |

Ví dụ

<pre class="overflow-visible! px-0!" data-start="3160" data-end="3193"><div class="relative w-full mt-4 mb-1"><div class=""><div class="relative"><div class="h-full min-h-0 min-w-0"><div class="h-full min-h-0 min-w-0"><div class="border border-token-border-light border-radius-3xl corner-superellipse/1.1 rounded-3xl"><div class="h-full w-full border-radius-3xl bg-token-bg-elevated-secondary corner-superellipse/1.1 overflow-clip rounded-3xl lxnfua_clipPathFallback"><div class="pointer-events-none absolute end-1.5 top-1 z-2 md:end-2 md:top-1"></div><div class="pe-11 pt-3"><div class="relative z-0 flex max-w-full"><div id="code-block-viewer" dir="ltr" class="q9tKkq_viewer cm-editor z-10 light:cm-light dark:cm-light flex h-full w-full flex-col items-stretch ͼk ͼy"><div class="cm-scroller"><div class="cm-content q9tKkq_readonly"><span>TRUNG_TUYEN</span><br/><span>TRUOT</span><br/><span>CHO_XET</span></div></div></div></div></div></div></div></div></div><div class=""><div class=""></div></div></div></div></div></pre>

---

## 3.2 `xt_diemcongxetuyen`

Điểm **cộng thêm**

| Field    |
| -------- |
| diemCC   |
| diemUtxt |
| diemTong |

Ví dụ

| loại           | điểm |
| --------------- | ------ |
| IELTS           | 1      |
| học sinh giỏi | 0.5    |

---

## 3.3 `xt_bangquydoi`

Bảng **quy đổi điểm**

Ví dụ

| chứng chỉ | quy đổi  |
| ----------- | ---------- |
| IELTS       | điểm N1  |
| SAT         | điểm NL1 |

Columns

| Column       |
| ------------ |
| d_phuongthuc |
| d_tohop      |
| d_mon        |
| d_diema      |
| d_diemb      |
| d_diemc      |
| d_diemd      |

---

# 4. LUỒNG XỬ LÝ XÉT TUYỂN

Pipeline logic:

<pre class="overflow-visible! px-0!" data-start="3644" data-end="4012"><div class="relative w-full mt-4 mb-1"><div class=""><div class="relative"><div class="h-full min-h-0 min-w-0"><div class="h-full min-h-0 min-w-0"><div class="border border-token-border-light border-radius-3xl corner-superellipse/1.1 rounded-3xl"><div class="h-full w-full border-radius-3xl bg-token-bg-elevated-secondary corner-superellipse/1.1 overflow-clip rounded-3xl lxnfua_clipPathFallback"><div class="pointer-events-none absolute end-1.5 top-1 z-2 md:end-2 md:top-1"></div><div class="pe-11 pt-3"><div class="relative z-0 flex max-w-full"><div id="code-block-viewer" dir="ltr" class="q9tKkq_viewer cm-editor z-10 light:cm-light dark:cm-light flex h-full w-full flex-col items-stretch ͼk ͼy"><div class="cm-scroller"><div class="cm-content q9tKkq_readonly"><span>Thí sinh</span><br/><span>     │</span><br/><span>     ▼</span><br/><span>Điểm thi (xt_diemthixettuyen)</span><br/><span>     │</span><br/><span>     ▼</span><br/><span>Chọn nguyện vọng (xt_nguyenvongxettuyen)</span><br/><span>     │</span><br/><span>     ▼</span><br/><span>Mapping ngành + tổ hợp (xt_nganh_tohop)</span><br/><span>     │</span><br/><span>     ▼</span><br/><span>Tính điểm tổ hợp</span><br/><span>     │</span><br/><span>     ▼</span><br/><span>+ Điểm ưu tiên</span><br/><span>+ Điểm cộng</span><br/><span>     │</span><br/><span>     ▼</span><br/><span>Điểm xét tuyển</span><br/><span>     │</span><br/><span>     ▼</span><br/><span>Ranking theo ngành</span><br/><span>     │</span><br/><span>     ▼</span><br/><span>So sánh chỉ tiêu</span><br/><span>     │</span><br/><span>     ▼</span><br/><span>Trúng tuyển / Trượt</span></div></div></div></div></div></div></div></div></div><div class=""><div class=""></div></div></div></div></div></pre>

---

# 5. RELATIONSHIP (ERD)

<pre class="overflow-visible! px-0!" data-start="4044" data-end="4324"><div class="relative w-full mt-4 mb-1"><div class=""><div class="relative"><div class="h-full min-h-0 min-w-0"><div class="h-full min-h-0 min-w-0"><div class="border border-token-border-light border-radius-3xl corner-superellipse/1.1 rounded-3xl"><div class="h-full w-full border-radius-3xl bg-token-bg-elevated-secondary corner-superellipse/1.1 overflow-clip rounded-3xl lxnfua_clipPathFallback"><div class="pointer-events-none absolute end-1.5 top-1 z-2 md:end-2 md:top-1"></div><div class="pe-11 pt-3"><div class="relative z-0 flex max-w-full"><div id="code-block-viewer" dir="ltr" class="q9tKkq_viewer cm-editor z-10 light:cm-light dark:cm-light flex h-full w-full flex-col items-stretch ͼk ͼy"><div class="cm-scroller"><div class="cm-content q9tKkq_readonly"><span>xt_thisinhxettuyen25</span><br/><span>        │</span><br/><span>        │ cccd</span><br/><span>        ▼</span><br/><span>xt_diemthixettuyen</span><br/><span>        │</span><br/><span>        │</span><br/><span>        ▼</span><br/><span>xt_nguyenvongxettuyen</span><br/><span>        │</span><br/><span>        │ manganh</span><br/><span>        ▼</span><br/><span>xt_nganh</span><br/><span>        │</span><br/><span>        │</span><br/><span>        ▼</span><br/><span>xt_nganh_tohop</span><br/><span>        │</span><br/><span>        │ matohop</span><br/><span>        ▼</span><br/><span>xt_tohop_monthi</span></div></div></div></div></div></div></div></div></div></div></div></div></pre>
