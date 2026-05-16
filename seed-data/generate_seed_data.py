"""
generate_seed_data.py
=====================
Sinh đầy đủ các file Excel seed data cho project admissions-management.

Output có 2 nhánh:
  output/          - **App-import ready**: header dùng @ExcelColumn(name) đúng
                     cho ScoreDTO/CandidateDTO/MajorDTO/... + ngày dd/MM/yyyy.
                     File ở đây import thẳng được vào swing-app (Quản lý
                     thí sinh / điểm / ngành / tổ hợp / điểm cộng / quy đổi).
  output/db/       - **DB-style**: header trùng tên cột MySQL, ngày yyyy-MM-dd,
                     dùng để import bằng MySQL Workbench / DBeaver.

Hai chế độ:
  1. **SGU 2025 fetch** (--fetch): kéo điểm chuẩn từ tuyensinh.sgu.edu.vn.
  2. **Fallback embedded** (mặc định): >=100 row/bảng từ dữ liệu nội bộ.

Pattern dependency self-install: cùng style với swing-app/generate_tohopmon.py.

Chạy:
  python seed-data/generate_seed_data.py            # mặc định (offline+seed)
  python seed-data/generate_seed_data.py --fetch    # bật fetch SGU 2025
"""

from __future__ import annotations

import argparse
import random
import subprocess
import sys
from datetime import date, datetime
from pathlib import Path


# ---------------------------------------------------------------------------
# 0. Tự cài dependency
# ---------------------------------------------------------------------------

def _ensure(pkg: str) -> None:
    try:
        __import__(pkg)
    except ImportError:  # pragma: no cover
        print(f"[setup] Installing missing dependency: {pkg}")
        subprocess.check_call([sys.executable, "-m", "pip", "install", pkg])


_ensure("pandas")
_ensure("openpyxl")

import pandas as pd  # noqa: E402

OUT_ROOT = Path(__file__).resolve().parent / "output"
OUT_APP = OUT_ROOT
OUT_DB = OUT_ROOT / "db"
OUT_APP.mkdir(parents=True, exist_ok=True)
OUT_DB.mkdir(parents=True, exist_ok=True)
TODAY = date.today().isoformat()

# ---------------------------------------------------------------------------
# 1. SGU fallback majors (40 ngành; pad lên >=100 row sau)
# ---------------------------------------------------------------------------

SGU_FALLBACK_MAJORS = [
    # (manganh, tennganh, n_tohopgoc, n_chitieu, n_diemsan, n_diemtrungtuyen,
    #  thpt, dgnl, vsat, tuyenthang, sl_xtt, sl_dgnl, sl_vsat, sl_thpt)
    ("7140114", "Quản lý giáo dục",                  "C00", 60, 18.0, 22.50, 1, 1, 0, 0,  0, 12,  0, 48),
    ("7140201", "Giáo dục Mầm non",                  "M00", 80, 18.0, 21.30, 1, 0, 0, 1,  6,  0,  0, 74),
    ("7140202", "Giáo dục Tiểu học",                 "C00", 120, 18.0, 25.40, 1, 1, 0, 1, 12, 18,  0, 90),
    ("7140204", "Giáo dục Công dân",                 "C00", 50, 18.0, 21.00, 1, 1, 0, 0,  0, 10,  0, 40),
    ("7140209", "Sư phạm Toán học",                  "A00", 100, 18.0, 26.30, 1, 1, 0, 1, 10, 20,  0, 70),
    ("7140210", "Sư phạm Tin học",                   "A00", 60, 18.0, 24.40, 1, 1, 0, 0,  0, 12,  0, 48),
    ("7140211", "Sư phạm Vật lí",                    "A00", 60, 18.0, 25.10, 1, 1, 0, 0,  0, 12,  0, 48),
    ("7140212", "Sư phạm Hoá học",                   "A00", 60, 18.0, 25.30, 1, 1, 0, 0,  0, 12,  0, 48),
    ("7140213", "Sư phạm Sinh học",                  "B00", 50, 18.0, 24.50, 1, 1, 0, 0,  0, 10,  0, 40),
    ("7140217", "Sư phạm Ngữ văn",                   "C00", 100, 18.0, 26.40, 1, 1, 0, 1, 10, 18,  0, 72),
    ("7140218", "Sư phạm Lịch sử",                   "C00", 60, 18.0, 26.10, 1, 1, 0, 0,  0, 12,  0, 48),
    ("7140219", "Sư phạm Địa lí",                    "C00", 60, 18.0, 26.00, 1, 1, 0, 0,  0, 12,  0, 48),
    ("7140231", "Sư phạm Tiếng Anh",                 "D01", 100, 18.0, 26.20, 1, 1, 0, 1, 10, 18,  0, 72),
    ("7220201", "Ngôn ngữ Anh",                      "D01", 200, 18.0, 24.95, 1, 1, 0, 0,  0, 30,  0, 170),
    ("7229040", "Văn học",                           "C00", 80, 18.0, 22.55, 1, 1, 0, 0,  0, 14,  0, 66),
    ("7229030", "Quốc tế học",                       "D01", 80, 18.0, 22.42, 1, 1, 0, 0,  0, 14,  0, 66),
    ("7310101", "Kinh tế",                           "A00", 120, 18.0, 23.85, 1, 1, 0, 0,  0, 20,  0, 100),
    ("7310301", "Xã hội học",                        "C00", 80, 18.0, 22.10, 1, 1, 0, 0,  0, 14,  0, 66),
    ("7310401", "Tâm lý học",                        "C00", 80, 18.0, 24.45, 1, 1, 0, 0,  0, 16,  0, 64),
    ("7310608", "Việt Nam học",                      "C00", 60, 18.0, 21.75, 1, 1, 0, 0,  0, 12,  0, 48),
    ("7320101", "Báo chí",                           "C00", 80, 18.0, 24.95, 1, 1, 0, 0,  0, 14,  0, 66),
    ("7320201", "Thông tin học",                     "C00", 60, 18.0, 21.30, 1, 1, 0, 0,  0, 12,  0, 48),
    ("7320205", "Quản lý thông tin",                 "C00", 60, 18.0, 21.45, 1, 1, 0, 0,  0, 12,  0, 48),
    ("7340101", "Quản trị kinh doanh",               "D01", 200, 20.0, 24.20, 1, 1, 0, 1, 10, 30,  0, 160),
    ("7340201", "Tài chính - Ngân hàng",             "D01", 150, 20.0, 24.00, 1, 1, 0, 0,  0, 24,  0, 126),
    ("7340301", "Kế toán",                           "D01", 180, 20.0, 23.70, 1, 1, 0, 1,  8, 28,  0, 144),
    ("7340405", "Hệ thống thông tin quản lý",        "D01", 100, 20.0, 24.00, 1, 1, 0, 0,  0, 18,  0, 82),
    ("7380101", "Luật",                              "C00", 120, 19.0, 24.65, 1, 1, 0, 0,  0, 20,  0, 100),
    ("7440112", "Hóa học",                           "A00", 60, 18.0, 22.95, 1, 1, 0, 0,  0, 12,  0, 48),
    ("7460112", "Toán ứng dụng",                     "A00", 80, 18.0, 22.65, 1, 1, 0, 0,  0, 14,  0, 66),
    ("7480101", "Khoa học máy tính",                 "A00", 100, 22.0, 26.20, 1, 1, 1, 0,  5, 18, 12, 65),
    ("7480103", "Kỹ thuật phần mềm",                 "A00", 150, 22.0, 25.95, 1, 1, 0, 0,  8, 24,  0, 118),
    ("7480104", "Hệ thống thông tin",                "A00", 80, 22.0, 24.85, 1, 1, 0, 0,  0, 16,  0, 64),
    ("7480201", "Công nghệ thông tin",               "A00", 220, 22.0, 26.10, 1, 1, 0, 0, 12, 32,  0, 176),
    ("7480202", "An toàn thông tin",                 "A00", 100, 22.0, 25.50, 1, 1, 0, 0,  5, 18,  0, 77),
    ("7510605", "Logistics và Quản lý chuỗi cung ứng","D01", 120, 22.0, 25.70, 1, 1, 0, 0,  0, 20,  0, 100),
    ("7580302", "Quản lý xây dựng",                  "A00", 60, 18.0, 21.85, 1, 1, 0, 0,  0, 12,  0, 48),
    ("7810103", "Quản trị dịch vụ du lịch và lữ hành","C00", 80, 18.0, 22.65, 1, 1, 0, 0,  0, 14,  0, 66),
    ("7810201", "Quản trị khách sạn",                "D01", 80, 18.0, 22.40, 1, 1, 0, 0,  0, 14,  0, 66),
    ("7850101", "Quản lý tài nguyên và môi trường",  "B00", 60, 18.0, 21.30, 1, 1, 0, 0,  0, 12,  0, 48),
    ("7860107", "Quản lý nhà nước",                  "C00", 100, 18.0, 23.45, 1, 1, 0, 0,  0, 18,  0, 82),
]


def _expand_majors(base, target=110):
    """Pad >=target ngành bằng cách thêm chương trình CLC/LK/SE."""
    if len(base) >= target:
        return base
    extras = []
    suffixes = [
        ("CLC", "Chương trình chất lượng cao"),
        ("LK", "Chương trình liên kết quốc tế"),
        ("SE", "Chương trình tăng cường tiếng Anh"),
    ]
    i = 0
    while len(base) + len(extras) < target:
        src = base[i % len(base)]
        manganh, tennganh, tohop, chitieu, sapn, dchuan, *flags = src
        suffix_code, suffix_name = suffixes[(i // len(base)) % len(suffixes)]
        new_code = f"{manganh}-{suffix_code}{(i // len(base)) + 1}"
        new_name = f"{tennganh} ({suffix_name})"
        delta = ((i % 7) - 3) * 0.1
        extras.append((new_code, new_name, tohop,
                       max(20, chitieu // 2),
                       round(sapn, 2),
                       round(dchuan + delta, 2),
                       *flags))
        i += 1
    return list(base) + extras


SGU_FALLBACK_MAJORS = _expand_majors(SGU_FALLBACK_MAJORS, target=110)


# ---------------------------------------------------------------------------
# 2. Tổ hợp môn
# ---------------------------------------------------------------------------
_TOHOP_BASE = [
    ("A00", "TO", "LI", "HO", "Khối A00 - Toán, Vật lí, Hóa học"),
    ("A01", "TO", "LI", "AN", "Khối A01 - Toán, Vật lí, Tiếng Anh"),
    ("A02", "TO", "LI", "SI", "Khối A02 - Toán, Vật lí, Sinh học"),
    ("A03", "TO", "LI", "SU", "Khối A03 - Toán, Vật lí, Lịch sử"),
    ("A04", "TO", "LI", "DI", "Khối A04 - Toán, Vật lí, Địa lí"),
    ("A05", "TO", "HO", "SU", "Khối A05 - Toán, Hóa học, Lịch sử"),
    ("A06", "TO", "HO", "DI", "Khối A06 - Toán, Hóa học, Địa lí"),
    ("A07", "TO", "SU", "DI", "Khối A07 - Toán, Lịch sử, Địa lí"),
    ("A08", "TO", "SU", "VA", "Khối A08 - Toán, Lịch sử, Ngữ văn"),
    ("A09", "TO", "DI", "VA", "Khối A09 - Toán, Địa lí, Ngữ văn"),
    ("A10", "TO", "LI", "VA", "Khối A10 - Toán, Vật lí, Ngữ văn"),
    ("B00", "TO", "HO", "SI", "Khối B00 - Toán, Hóa học, Sinh học"),
    ("B01", "TO", "SI", "DI", "Khối B01 - Toán, Sinh học, Địa lí"),
    ("B02", "TO", "SI", "VA", "Khối B02 - Toán, Sinh học, Ngữ văn"),
    ("B03", "TO", "SI", "AN", "Khối B03 - Toán, Sinh học, Tiếng Anh"),
    ("B04", "TO", "SI", "SU", "Khối B04 - Toán, Sinh học, Lịch sử"),
    ("B08", "TO", "HO", "AN", "Khối B08 - Toán, Hóa học, Tiếng Anh"),
    ("C00", "VA", "SU", "DI", "Khối C00 - Ngữ văn, Lịch sử, Địa lí"),
    ("C01", "VA", "SU", "LI", "Khối C01 - Ngữ văn, Lịch sử, Vật lí"),
    ("C02", "VA", "SU", "HO", "Khối C02 - Ngữ văn, Lịch sử, Hóa học"),
    ("C03", "VA", "TO", "SU", "Khối C03 - Ngữ văn, Toán, Lịch sử"),
    ("C04", "VA", "TO", "DI", "Khối C04 - Ngữ văn, Toán, Địa lí"),
    ("C05", "VA", "LI", "HO", "Khối C05 - Ngữ văn, Vật lí, Hóa học"),
    ("C14", "VA", "TO", "GDC", "Khối C14 - Ngữ văn, Toán, GDCD"),
    ("C19", "VA", "SU", "GDC", "Khối C19 - Ngữ văn, Lịch sử, GDCD"),
    ("C20", "VA", "DI", "GDC", "Khối C20 - Ngữ văn, Địa lí, GDCD"),
    ("D01", "TO", "VA", "AN", "Khối D01 - Toán, Ngữ văn, Tiếng Anh"),
    ("D02", "TO", "VA", "NGA", "Khối D02 - Toán, Ngữ văn, Tiếng Nga"),
    ("D03", "TO", "VA", "PHA", "Khối D03 - Toán, Ngữ văn, Tiếng Pháp"),
    ("D04", "TO", "VA", "TRU", "Khối D04 - Toán, Ngữ văn, Tiếng Trung"),
    ("D05", "TO", "VA", "DUC", "Khối D05 - Toán, Ngữ văn, Tiếng Đức"),
    ("D06", "TO", "VA", "NHA", "Khối D06 - Toán, Ngữ văn, Tiếng Nhật"),
    ("D07", "TO", "HO", "AN", "Khối D07 - Toán, Hóa học, Tiếng Anh"),
    ("D08", "TO", "SI", "AN", "Khối D08 - Toán, Sinh học, Tiếng Anh"),
    ("D09", "TO", "SU", "AN", "Khối D09 - Toán, Lịch sử, Tiếng Anh"),
    ("D10", "TO", "DI", "AN", "Khối D10 - Toán, Địa lí, Tiếng Anh"),
    ("D14", "VA", "SU", "AN", "Khối D14 - Ngữ văn, Lịch sử, Tiếng Anh"),
    ("D15", "VA", "DI", "AN", "Khối D15 - Ngữ văn, Địa lí, Tiếng Anh"),
    ("D66", "VA", "GDC", "AN", "Khối D66 - Ngữ văn, GDCD, Tiếng Anh"),
    ("D78", "VA", "KHX", "AN", "Khối D78 - Ngữ văn, KHXH, Tiếng Anh"),
    ("D90", "TO", "KHT", "AN", "Khối D90 - Toán, KHTN, Tiếng Anh"),
    ("M00", "TO", "VA", "DH", "Khối M00 - Toán, Văn, Đọc diễn cảm + Hát"),
    ("M01", "VA", "SU", "DH", "Khối M01 - Văn, Sử, Đọc kể diễn cảm"),
    ("M02", "TO", "NK1", "NK2", "Khối M02 - Toán, Năng khiếu 1 & 2"),
    ("V00", "TO", "LI", "VEH", "Khối V00 - Toán, Vật lí, Vẽ Mỹ thuật"),
    ("V01", "TO", "VA", "VEH", "Khối V01 - Toán, Văn, Vẽ Mỹ thuật"),
    ("H00", "VA", "VEH", "VTT", "Khối H00 - Văn, Vẽ Mỹ thuật, Vẽ Trang trí"),
    ("H01", "TO", "VA", "VEH", "Khối H01 - Toán, Văn, Vẽ Mỹ thuật"),
    ("N00", "VA", "NK1", "NK2", "Khối N00 - Văn, Năng khiếu Âm nhạc 1 & 2"),
    ("T00", "TO", "SI", "NK1", "Khối T00 - Toán, Sinh, Năng khiếu TDTT"),
    ("T01", "TO", "VA", "NK1", "Khối T01 - Toán, Văn, Năng khiếu TDTT"),
    ("R00", "VA", "SU", "NK1", "Khối R00 - Văn, Sử, Năng khiếu Báo chí"),
]


def _pad_tohop(rows, target=110):
    if len(rows) >= target:
        return rows
    extra = []
    base = list(rows)
    i = 0
    while len(base) + len(extra) < target:
        ma, m1, m2, m3, ten = rows[i % len(rows)]
        new_code = f"{ma}_v{(i // len(rows)) + 2}"
        extra.append((new_code, m1, m2, m3, ten + " (biến thể)"))
        i += 1
    return base + extra


TOHOP_DATA = _pad_tohop(_TOHOP_BASE, target=100)


def build_nganh_tohop(majors):
    pairs = []
    secondary_for = {
        "A00": ["A01"], "A01": ["A00"],
        "B00": ["B01", "A02"],
        "C00": ["C01", "D01"],
        "D01": ["D07", "D09"],
        "M00": ["C00"],
    }
    for m in majors:
        manganh, _, tohopgoc, *_ = m
        groups = [tohopgoc] + secondary_for.get(tohopgoc, [])
        for code in groups:
            pairs.append(_make_nganh_tohop(manganh, code))
    while len(pairs) < 110:
        manganh = random.choice(majors)[0]
        code = random.choice(["A00", "A01", "B00", "C00", "D01"])
        pairs.append(_make_nganh_tohop(manganh, code))
    return pairs


def _make_nganh_tohop(manganh, matohop):
    tohop = next((t for t in TOHOP_DATA if t[0] == matohop), TOHOP_DATA[0])
    return (manganh, matohop, tohop[1], tohop[2], tohop[3], 1.0, 1.0, 1.0)


# ---------------------------------------------------------------------------
# 3. Thí sinh
# ---------------------------------------------------------------------------
VN_LASTNAMES = ["Nguyễn", "Trần", "Lê", "Phạm", "Hoàng", "Huỳnh", "Phan", "Vũ",
                "Võ", "Đặng", "Bùi", "Đỗ", "Hồ", "Ngô", "Dương", "Lý", "Mai",
                "Trịnh", "Đinh", "Cao", "Tô"]
VN_MIDDLENAMES = ["Văn", "Thị", "Minh", "Đức", "Thanh", "Hữu", "Xuân", "Ngọc",
                  "Quốc", "Thu", "Hải", "Đình", "Phúc", "Bảo", "Công", "Lê",
                  "Kim", "Hoàng", "Trọng", "Vũ", "Duy", "Phương", "Anh"]
VN_FIRSTNAMES = ["An", "Bình", "Chi", "Dũng", "Phúc", "Giang", "Hà",
                 "Hiếu", "Khôi", "Lam", "Mai", "Nam", "Oanh", "Phong", "Quân",
                 "Sang", "Tâm", "Uyên", "Vinh", "Yến", "Hương", "Linh", "Thảo",
                 "Khoa", "Tuấn", "Quốc", "Vy", "Trường", "Ngọc", "My", "Lan"]
NOI_SINH = ["Hà Nội", "TP.HCM", "Đà Nẵng", "Cần Thơ", "Hải Phòng", "Quảng Ninh",
            "Thanh Hóa", "Nghệ An", "Huế", "Bình Dương", "Đồng Nai", "Long An",
            "Tiền Giang", "Khánh Hòa", "Đắk Lắk", "Bình Định", "Phú Thọ"]
KHU_VUC = ["KV1", "KV2", "KV2-NT", "KV3"]
DOI_TUONG = ["KT1", "KT2", "KT3", "UT1", "UT2"]


def gen_thisinh(n=120, rng=None):
    rng = rng or random.Random(42)
    rows = []
    for i in range(n):
        cccd = f"079206{i+1:06d}"  # 12 ký tự (079=TPHCM, 20=2006, 6 số seq)
        # Số báo danh là chuỗi số thuần (8 chữ số) — yêu cầu của hệ thống tuyển sinh.
        # Trước đây có prefix "SBD" gây fail validate vì ScoreDTO.sobaodanh xem
        # như chuỗi tự do, nhưng các hệ thống ngoài (ICAB) thường yêu cầu numeric.
        sbd = f"{i+1:08d}"  # 00000001..00000120
        last = rng.choice(VN_LASTNAMES)

        mid = rng.choice(VN_MIDDLENAMES)
        first = rng.choice(VN_FIRSTNAMES)
        ho_va_ten = f"{last} {mid} {first}"
        gioi = "Nữ" if first in {"Hương", "Linh", "Thảo", "Vy", "My", "Lan", "Yến", "Mai", "Hà", "Chi", "Oanh", "Uyên"} else "Nam"
        ngay = date(2006, rng.randint(1, 12), rng.randint(1, 28))
        sdt = f"0{rng.randint(900000000, 999999999)}"
        email = f"thisinh{i+1:03d}@example.com"
        rows.append({
            "cccd": cccd,
            "sobaodanh": sbd,
            "ho": last,
            "ten": f"{mid} {first}",
            "ho_va_ten": ho_va_ten,
            "ngay_sinh": ngay,
            "gioi_tinh": gioi,
            "dien_thoai": sdt,
            "email": email,
            "noi_sinh": rng.choice(NOI_SINH),
            "doi_tuong": rng.choice(DOI_TUONG),
            "khu_vuc": rng.choice(KHU_VUC),
        })
    return rows


# ---------------------------------------------------------------------------
# 4. Điểm thi
# ---------------------------------------------------------------------------
def gen_diem_thi(thisinh_list, rng=None):
    rng = rng or random.Random(7)
    rows = []
    for i, ts in enumerate(thisinh_list):
        cccd, sbd = ts["cccd"], ts["sobaodanh"]
        rows.append({
            "cccd": cccd, "sobaodanh": sbd, "phuong_thuc": "THPT",
            "toan": round(rng.uniform(4.5, 9.5), 2),
            "ly":   round(rng.uniform(4.0, 9.5), 2),
            "hoa":  round(rng.uniform(4.0, 9.5), 2),
            "sinh": round(rng.uniform(4.0, 9.5), 2),
            "su":   round(rng.uniform(4.0, 9.5), 2),
            "dia":  round(rng.uniform(4.0, 9.5), 2),
            "van":  round(rng.uniform(4.5, 9.5), 2),
            "n1_thi": round(rng.uniform(4.0, 9.0), 2),
            "n1_cc":  round(rng.uniform(4.0, 9.0), 2),
            "nl1": None, "nk1": None, "nk2": None,
        })
        if i % 3 == 0:
            rows.append({
                "cccd": cccd, "sobaodanh": sbd, "phuong_thuc": "DGNL",
                "toan": None, "ly": None, "hoa": None, "sinh": None,
                "su": None, "dia": None, "van": None,
                "n1_thi": None, "n1_cc": round(rng.uniform(5.0, 9.0), 2),
                "nl1": round(rng.uniform(550, 1150)),
                "nk1": None, "nk2": None,
            })
        if i % 10 == 0:
            rows.append({
                "cccd": cccd, "sobaodanh": sbd, "phuong_thuc": "VSAT",
                "toan": round(rng.uniform(60, 145), 1),
                "ly":   round(rng.uniform(50, 140), 1),
                "hoa":  round(rng.uniform(50, 140), 1),
                "sinh": round(rng.uniform(50, 140), 1),
                "su":   round(rng.uniform(50, 130), 1),
                "dia":  round(rng.uniform(50, 130), 1),
                "van":  round(rng.uniform(60, 140), 1),
                "n1_thi": None, "n1_cc": round(rng.uniform(5.0, 9.0), 2),
                "nl1": None, "nk1": None, "nk2": None,
            })
    return rows


# ---------------------------------------------------------------------------
# 5. Điểm cộng
# ---------------------------------------------------------------------------
def gen_diem_cong(thisinh_list, rng=None):
    rng = rng or random.Random(11)
    rows = []
    for ts in thisinh_list:
        utxt = {"KV1": 0.75, "KV2": 0.5, "KV2-NT": 0.5, "KV3": 0.0}.get(ts["khu_vuc"], 0.0)
        cc = rng.choice([0.0, 0.0, 0.5, 1.0, 1.5, 2.0]) if rng.random() < 0.35 else 0.0
        rows.append({
            "cccd": ts["cccd"],
            "diem_cc": cc,
            "diem_utxt": utxt,
            "diem_tong": round(cc + utxt, 2),
        })
    return rows


# ---------------------------------------------------------------------------
# 6. Nguyện vọng (DB-only, không có Excel import qua app)
# ---------------------------------------------------------------------------
def gen_nguyen_vong(thisinh_list, majors, nganh_tohop, rng=None):
    """Sinh nguyện vọng có ràng buộc chặt:
    - CCCD lấy từ thisinh_list (giữ nguyên 12 ký tự).
    - Mã ngành chỉ trong tập `majors` truyền vào (không bị duplicate suffix).
    - Tổ hợp phải nằm trong mapping `nganh_tohop` của chính ngành đó.
    - Nếu ngành ko có tổ hợp nào, bỏ qua dòng (không sinh row sai).
    """
    rng = rng or random.Random(13)
    pairs_by_major = {}
    for ng_th in nganh_tohop:
        pairs_by_major.setdefault(ng_th[0], []).append(ng_th)

    # Chỉ pick những ngành có tổ hợp mapping → tránh tổ hợp = None.
    eligible_majors = [m for m in majors if m[0] in pairs_by_major]
    if not eligible_majors:
        eligible_majors = list(majors)

    rows = []
    for ts in thisinh_list:
        kv = ts["khu_vuc"]
        n_nv = rng.randint(2, 4)
        n_nv = min(n_nv, len(eligible_majors))
        majors_chosen = rng.sample(eligible_majors, k=n_nv)
        base = round(rng.uniform(15.0, 28.5), 2)
        utqd = {"KV1": 0.75, "KV2": 0.5, "KV2-NT": 0.5, "KV3": 0.0}.get(kv, 0.0)
        cong = round(rng.uniform(0.0, 2.0), 2) if rng.random() < 0.4 else 0.0
        admitted_once = False
        for tt, m in enumerate(majors_chosen, start=1):
            manganh = m[0]
            tohop_options = pairs_by_major.get(manganh, [])
            if not tohop_options:
                continue  # skip rather than emit invalid tổ hợp
            tohop_chosen = rng.choice(tohop_options)
            tohop_code = tohop_chosen[1]
            # Phương thức chỉ dùng các giá trị mà ngành cho phép theo flags.
            allowed = []
            if m[6]:  # n_thpt
                allowed.append("THPT")
            if m[7]:  # n_dgnl
                allowed.append("DGNL")
            if m[8]:  # n_vsat
                allowed.append("VSAT")
            if not allowed:
                allowed = ["THPT"]
            phuong_thuc = rng.choices(allowed,
                                      weights=[1] * len(allowed))[0]
            diem_thxt = round(min(30.0, max(0.0, base + rng.uniform(-1.5, 1.0))), 2)
            diem_xt = round(min(30.0, diem_thxt + utqd + cong), 2)
            ket_qua = "TRUOT"
            if not admitted_once and diem_xt >= m[5]:
                ket_qua = "TRUNG_TUYEN"
                admitted_once = True
            elif not admitted_once and rng.random() < 0.05:
                ket_qua = "CHO_XET"
            rows.append((ts["cccd"], manganh, tt, diem_thxt, utqd, cong, diem_xt,
                         ket_qua, phuong_thuc, tohop_code))
    return rows


# ---------------------------------------------------------------------------
# 7. Bảng quy đổi
# ---------------------------------------------------------------------------
def build_bangquydoi():
    rows = []
    vsat_blocks = {
        "TO": [
            (132.0, 150.0, 8.5, 10.0), (128.5, 132.0, 8.10, 8.50),
            (122.5, 128.5, 7.75, 8.10), (114.5, 122.5, 7.00, 7.75),
            (108.0, 114.5, 6.60, 7.00), (102.5, 108.0, 6.25, 6.60),
            (97.0, 102.5, 6.00, 6.25), (91.0, 97.0, 5.60, 6.00),
            (85.0, 91.0, 5.25, 5.60), (77.0, 85.0, 5.00, 5.25),
            (68.0, 77.0, 4.50, 5.00), (6.0, 68.0, 1.50, 4.50),
        ],
        "LI": [
            (123.0, 147.0, 9.50, 10.0), (118.5, 123.0, 9.25, 9.50),
            (112.5, 118.5, 9.00, 9.25), (105.0, 112.5, 8.50, 9.00),
            (99.5, 105.0, 8.00, 8.50), (94.5, 99.5, 7.75, 8.00),
            (90.0, 94.5, 7.50, 7.75), (85.0, 90.0, 7.25, 7.50),
            (80.0, 85.0, 6.75, 7.25), (74.0, 80.0, 6.35, 6.75),
            (66.5, 74.0, 5.75, 6.35), (17.0, 66.5, 3.05, 5.75),
        ],
        "HO": [
            (129.0, 150.0, 9.50, 10.0), (124.5, 129.0, 9.25, 9.50),
            (117.0, 124.5, 8.75, 9.25), (107.5, 117.0, 8.25, 8.75),
            (100.5, 107.5, 7.75, 8.25), (94.0, 100.5, 7.25, 7.75),
            (88.0, 94.0, 6.75, 7.25), (81.5, 88.0, 6.25, 6.75),
            (75.5, 81.5, 5.75, 6.25), (68.5, 75.5, 5.25, 5.75),
            (59.5, 68.5, 4.60, 5.25), (20.0, 59.5, 1.35, 4.60),
        ],
        "SI": [
            (130.5, 150.0, 9.00, 9.75), (126.5, 130.5, 8.75, 9.00),
            (120.5, 126.5, 8.34, 8.75), (112.5, 120.5, 7.85, 8.34),
            (105.5, 112.5, 7.50, 7.85), (100.0, 105.5, 7.25, 7.50),
            (94.5, 100.0, 6.85, 7.25), (88.5, 94.5, 6.50, 6.85),
            (82.5, 88.5, 6.25, 6.50), (76.0, 82.5, 5.85, 6.25),
            (66.5, 76.0, 5.25, 5.85), (26.5, 66.5, 2.80, 5.25),
        ],
        "SU": [
            (133.5, 150.0, 9.75, 10.0), (131.0, 133.5, 9.50, 9.75),
            (126.5, 131.0, 9.25, 9.50), (120.5, 126.5, 9.00, 9.25),
            (115.0, 120.5, 8.50, 9.00), (110.0, 115.0, 8.25, 8.50),
            (105.5, 110.0, 8.00, 8.25), (101.0, 105.5, 7.75, 8.00),
            (95.5, 101.0, 7.50, 7.75), (88.5, 95.5, 7.00, 7.50),
            (79.5, 88.5, 6.35, 7.00), (36.5, 79.5, 2.95, 6.35),
        ],
        "DI": [
            (124.0, 141.0, 10.0, 10.0), (120.5, 124.0, 10.0, 10.0),
            (115.5, 120.5, 9.75, 10.0), (108.5, 115.5, 9.25, 9.75),
            (103.0, 108.5, 9.00, 9.25), (98.5, 103.0, 8.75, 9.00),
            (94.0, 98.5, 8.50, 8.75), (89.5, 94.0, 8.25, 8.50),
            (84.5, 89.5, 7.75, 8.25), (79.0, 84.5, 7.25, 7.75),
            (71.0, 79.0, 6.50, 7.25), (31.0, 71.0, 3.00, 6.50),
        ],
        "AN": [
            (131.0, 150.0, 7.75, 9.75), (127.5, 131.0, 7.50, 7.75),
            (120.5, 127.5, 7.00, 7.50), (112.0, 120.5, 6.50, 7.00),
            (105.0, 112.0, 6.00, 6.50), (98.5, 105.0, 5.75, 6.00),
            (92.0, 98.5, 5.50, 5.75), (85.5, 92.0, 5.25, 5.50),
            (78.5, 85.5, 5.00, 5.25), (70.5, 78.5, 4.50, 5.00),
            (60.0, 70.5, 4.00, 4.50), (20.5, 60.0, 1.25, 4.00),
        ],
        "VA": [
            (129.5, 146.0, 9.25, 9.75), (127.5, 129.5, 9.00, 9.25),
            (124.0, 127.5, 9.00, 9.00), (119.5, 124.0, 8.75, 9.00),
            (115.5, 119.5, 8.50, 8.75), (112.5, 115.5, 8.25, 8.50),
            (109.0, 112.5, 8.00, 8.25), (106.0, 109.0, 7.75, 8.00),
            (102.0, 106.0, 7.50, 7.75), (97.0, 102.0, 7.25, 7.50),
            (90.0, 97.0, 6.75, 7.25), (5.0, 90.0, 3.50, 6.75),
        ],
    }
    for mon, ranges in vsat_blocks.items():
        for a, b, c, d in ranges:
            rows.append(("VSAT", "A00", mon, a, b, c, d))

    thpt_curve = vsat_blocks["TO"]
    for mon in ("TO", "LI", "HO", "SI", "SU", "DI", "AN", "VA"):
        for a, b, c, d in thpt_curve:
            rows.append(("THPT", "A00", mon, a, b, c, d))

    dgnl = [
        (1080.0, 1200.0, 27.00, 30.00), (990.0, 1080.0, 24.75, 27.00),
        (900.0, 990.0, 22.50, 24.75), (810.0, 900.0, 20.25, 22.50),
        (720.0, 810.0, 18.00, 20.25), (630.0, 720.0, 15.75, 18.00),
        (540.0, 630.0, 13.50, 15.75), (450.0, 540.0, 11.25, 13.50),
        (360.0, 450.0,  9.00, 11.25), (270.0, 360.0,  6.75,  9.00),
        (180.0, 270.0,  4.50,  6.75), (0.0,   180.0,  0.00,  4.50),
    ]
    for a, b, c, d in dgnl:
        rows.append(("DGNL", "", "NL1", a, b, c, d))

    ielts = [
        (8.5, 9.0, 8.0, 9.0), (8.0, 8.5, 7.5, 8.0), (7.5, 8.0, 7.0, 7.5),
        (7.0, 7.5, 6.5, 7.0), (6.5, 7.0, 6.0, 6.5), (6.0, 6.5, 5.5, 6.0),
        (5.5, 6.0, 5.0, 5.5), (5.0, 5.5, 4.5, 5.0), (4.0, 5.0, 3.5, 4.5),
    ]
    for a, b, c, d in ielts:
        rows.append(("IELTS", "", "IELTS", a, b, c, d))

    toeic = [
        (900.0, 990.0, 8.5, 10.0), (850.0, 900.0, 8.0, 8.5),
        (800.0, 850.0, 7.5, 8.0), (750.0, 800.0, 7.0, 7.5),
        (700.0, 750.0, 6.5, 7.0), (650.0, 700.0, 6.0, 6.5),
        (600.0, 650.0, 5.5, 6.0), (550.0, 600.0, 5.0, 5.5),
        (500.0, 550.0, 4.5, 5.0), (0.0, 500.0, 1.0, 4.5),
    ]
    for a, b, c, d in toeic:
        rows.append(("TOEIC", "", "TOEIC", a, b, c, d))

    return rows


# ---------------------------------------------------------------------------
# 8. Users (db-only)
# ---------------------------------------------------------------------------
def gen_users(thisinh_list):
    rows = [
        ("admin", "Quản trị hệ thống", "admin@example.com", "ADMIN", "admin123"),
        ("teacher01", "Cô Nguyễn Hoa", "hoa.nguyen@example.com", "ADMIN", "admin123"),
        ("teacher02", "Thầy Trần Hùng", "hung.tran@example.com", "ADMIN", "admin123"),
    ]
    for i, ts in enumerate(thisinh_list, start=1):
        rows.append((f"ts{i:04d}", ts["ho_va_ten"], ts["email"], "STUDENT", "admin123"))
    return rows


# ---------------------------------------------------------------------------
# 9. Optional fetch SGU 2025
# ---------------------------------------------------------------------------
def try_fetch_sgu_majors(timeout=10.0):
    try:
        _ensure("requests")
        _ensure("bs4")
        import requests  # type: ignore
        from bs4 import BeautifulSoup  # type: ignore
    except Exception as exc:  # pragma: no cover
        print(f"[fetch] Không cài được dependencies fetch: {exc}")
        return None
    candidate_urls = [
        "https://tuyensinh.sgu.edu.vn/diem-chuan-2025/",
        "https://sgu.edu.vn/diem-chuan-2025",
    ]
    headers = {"User-Agent": "Mozilla/5.0 (admissions-management seed-data)"}
    for url in candidate_urls:
        try:
            print(f"[fetch] Thử tải {url} ...")
            resp = requests.get(url, headers=headers, timeout=timeout)
            if resp.status_code != 200:
                continue
            soup = BeautifulSoup(resp.text, "html.parser")
            table = soup.find("table")
            if not table:
                continue
            rows = []
            for tr in table.find_all("tr"):
                tds = [td.get_text(strip=True) for td in tr.find_all("td")]
                if len(tds) < 5 or not tds[1].startswith("7"):
                    continue
                manganh = tds[1].split()[0]
                tennganh = tds[2]
                tohop = (tds[3].split(",")[0] or "A00").strip()
                try:
                    diem = float(str(tds[4]).replace(",", "."))
                except ValueError:
                    continue
                rows.append((manganh, tennganh, tohop, 100,
                             max(15.0, diem - 4.0), diem,
                             1, 1, 0, 0, 0, 15, 0, 85))
            if rows:
                print(f"[fetch] Lấy được {len(rows)} ngành từ {url}.")
                return rows
        except Exception as exc:  # pragma: no cover
            print(f"[fetch] Lỗi khi tải {url}: {exc}")
            continue
    return None


# ---------------------------------------------------------------------------
# 10. Excel writer (text-mode dates, app-friendly)
# ---------------------------------------------------------------------------

# Tên cột phải giữ nguyên dạng text (ko để Excel convert sang số mất leading zero).
TEXT_COLUMNS = {
    "cccd", "sbd", "số báo danh", "sobaodanh", "nn_cccd",
    "mã ngành", "manganh", "nv_manganh",
    "mã tổ hợp", "matohop", "tt_thm", "th_mon1", "th_mon2", "th_mon3",
    "tổ hợp", "tổ hợp gốc", "n_tohopgoc", "tohop",
    "phuong_thuc", "phương thức", "d_phuongthuc", "tt_phuongthuc",
    "username", "password_plain",
}


def _write(out_dir: Path, filename, columns, rows):
    df = pd.DataFrame(rows, columns=columns)
    out = out_dir / filename
    with pd.ExcelWriter(out, engine="openpyxl") as writer:
        df.to_excel(writer, index=False, sheet_name="data")
        ws = writer.sheets["data"]
        # Đặt format text cho các cột nhạy cảm (CCCD, Mã ngành, ...) để Excel
        # không auto-convert sang số (mất leading zero, hiện khoa học).
        for i, col in enumerate(columns, start=1):
            sample = [str(v) for v in df[col].head(1000).tolist()]
            max_len = max([len(str(col))] + [len(v) for v in sample]) if sample else len(str(col))
            letter = ws.cell(row=1, column=i).column_letter
            ws.column_dimensions[letter].width = min(max_len + 2, 40)
            if str(col).lower() in TEXT_COLUMNS:
                for row_idx in range(2, 2 + len(df)):
                    cell = ws.cell(row=row_idx, column=i)
                    cell.number_format = "@"
                    # Cell value vẫn được pandas ghi dạng object/str, ta ép str
                    if cell.value is not None:
                        cell.value = str(cell.value)
    print(f"  ✓ {out.relative_to(OUT_ROOT.parent)}  ({len(rows):>5} rows)")


def _fmt_date(v):
    if v is None:
        return None
    if isinstance(v, (date, datetime)):
        return v.strftime("%d/%m/%Y")
    return str(v)


def _to_iso_date(v):
    if v is None:
        return None
    if isinstance(v, (date, datetime)):
        return v.strftime("%Y-%m-%d")
    return str(v)


def _bool_cell(value):
    """Boolean cell: write True/False so openpyxl emits BOOLEAN cell type."""
    if value in (1, True):
        return True
    if value in (0, False):
        return False
    return value


# ---------------------------------------------------------------------------
# 11. App-import-ready writers (header dùng @ExcelColumn)
# ---------------------------------------------------------------------------

def write_app_candidates(thisinh):
    rows = []
    for t in thisinh:
        rows.append([
            t["cccd"], t["sobaodanh"], t["ho"], t["ten"], _fmt_date(t["ngay_sinh"]),
            t["dien_thoai"], t["email"], t["gioi_tinh"], t["noi_sinh"],
            t["doi_tuong"], t["khu_vuc"],
        ])
    _write(OUT_APP, "candidates.xlsx",
           ["CCCD", "SBD", "Họ", "Tên", "Ngày Sinh", "SĐT", "Email",
            "Giới Tính", "Nơi Sinh", "Đối Tượng", "Khu Vực"],
           rows)


def write_app_scores(diem_thi):
    rows = []
    for d in diem_thi:
        rows.append([
            d["cccd"], d["sobaodanh"], d["phuong_thuc"],
            d["toan"], d["ly"], d["hoa"], d["sinh"], d["su"], d["dia"], d["van"],
            d["n1_thi"], d["n1_cc"], d["nl1"], d["nk1"], d["nk2"],
        ])
    _write(OUT_APP, "scores.xlsx",
           ["CCCD", "Số báo danh", "Phương thức",
            "Toán", "Lý", "Hóa", "Sinh", "Sử", "Địa", "Văn",
            "Ngoại ngữ (Thi)", "Ngoại ngữ (CC)", "NL1", "NK1", "NK2"],
           rows)


def write_app_bonus(diem_cong):
    rows = []
    for d in diem_cong:
        rows.append([d["cccd"], d["diem_cc"], d["diem_utxt"], d["diem_tong"]])
    _write(OUT_APP, "bonus_scores.xlsx",
           ["CCCD", "Điểm CC", "Điểm UTXT", "Tổng điểm cộng"],
           rows)


def write_app_majors(majors):
    rows = []
    for m in majors:
        manganh, tennganh, tohop, chitieu, sapn, dchuan, thpt, dgnl, vsat, tt, *_ = m
        rows.append([
            manganh, tennganh, tohop, chitieu, sapn, dchuan,
            _bool_cell(tt), _bool_cell(dgnl), _bool_cell(thpt), _bool_cell(vsat),
        ])
    _write(OUT_APP, "majors.xlsx",
           ["Mã ngành", "Tên ngành", "Tổ hợp gốc", "Chỉ tiêu",
            "Điểm sàn", "Điểm chuẩn",
            "Tuyển thẳng", "ĐGNL", "THPT", "VSAT"],
           rows)


def write_app_subject_groups(tohop_data):
    rows = []
    for t in tohop_data:
        rows.append([t[0], t[1], t[2], t[3], t[4]])
    _write(OUT_APP, "subject_groups.xlsx",
           ["Mã Tổ Hợp", "Môn 1", "Môn 2", "Môn 3", "Tên Tổ Hợp"],
           rows)


def write_app_aspirations(nguyen_vong, valid_manganh, valid_cccds=None,
                          filename="aspirations.xlsx"):
    """Map NguyenVong → AspirationImportDTO @ExcelColumn (CCCD, Mã ngành, NV,
    Phương thức, Tổ hợp).

    - `valid_manganh`: tập mã ngành đã có trong DB; nếu `None` thì không filter.
    - `valid_cccds`: tập CCCD đã có trong DB; nếu `None` thì không filter.

    Mục đích: tránh import bị reject hàng loạt với lỗi "CCCD không tồn tại"
    hoặc "Mã ngành không tồn tại".
    """
    rows = []
    seen_pairs = set()  # Tránh trùng (cccd, nv)
    for r in nguyen_vong:
        cccd, manganh, tt, _thxt, _utqd, _cong, _xt, _ketqua, phuong_thuc, tohop = r
        if valid_manganh is not None and manganh not in valid_manganh:
            continue
        if valid_cccds is not None and cccd not in valid_cccds:
            continue
        key = (cccd, tt)
        if key in seen_pairs:
            continue
        seen_pairs.add(key)
        rows.append([cccd, manganh, int(tt), phuong_thuc or "THPT", tohop or ""])
    _write(OUT_APP, filename,
           ["CCCD", "Mã ngành", "NV", "Phương thức", "Tổ hợp"],
           rows)


def synthesize_aspirations_for_dataseeder(majors, nganh_tohop, rng=None):
    """Sinh nguyện vọng cho 7 thí sinh đã được DataSeeder.java seed sẵn
    (CCCD `001082001234..001082001240`). Không cần phải import candidates.xlsx
    trước, chỉ cần chạy Spring Boot rồi import file này → có ngay 21 NV demo.
    """
    rng = rng or random.Random(2025)
    seeded_cccds = [
        "001082001234", "001082001235", "001082001236", "001082001237",
        "001082001238", "001082001239", "001082001240",
    ]
    seeded_majors = [m for m in majors if m[0] in {
        "7480201", "7480103", "7480101", "7480202",
        "7340101", "7340301", "7340201",
        "7380101", "7140209", "7210101",
    }]
    pairs_by_major = {}
    for ng_th in nganh_tohop:
        pairs_by_major.setdefault(ng_th[0], []).append(ng_th)

    fake_thisinh = [{"cccd": c, "khu_vuc": "KV3"} for c in seeded_cccds]
    return gen_nguyen_vong(fake_thisinh, seeded_majors, nganh_tohop, rng=rng)


def write_app_conversion(bangquydoi):

    rows = []
    for r in bangquydoi:
        # r = (phuongThuc, tohop, mon, diema, diemb, diemc, diemd)
        rows.append([
            r[0], r[1] or "", r[2],
            r[3], r[4], r[5], r[6],
            "", "",
        ])
    _write(OUT_APP, "conversion_table.xlsx",
           ["Phương thức", "Tổ hợp", "Môn",
            "Điểm A (Min)", "Điểm B (Max)",
            "Điểm quy đổi (C)", "Điểm quy đổi (D)",
            "Mã quy đổi", "Phạm vi"],
           rows)


# ---------------------------------------------------------------------------
# 12. DB-style writers (giữ tên cột MySQL)
# ---------------------------------------------------------------------------

def write_db_files(thisinh, diem_thi, diem_cong, majors, nganh_tohop,
                    nguyen_vong, bangquydoi, users):
    _write(OUT_DB, "01_xt_tohop_monthi.xlsx",
           ["matohop", "mon1", "mon2", "mon3", "tentohop"],
           TOHOP_DATA)
    _write(OUT_DB, "02_xt_nganh.xlsx",
           ["manganh", "tennganh", "n_tohopgoc", "n_chitieu",
            "n_diemsan", "n_diemtrungtuyen",
            "n_thpt", "n_dgnl", "n_vsat", "n_tuyenthang",
            "sl_xtt", "sl_dgnl", "sl_vsat", "sl_thpt"],
           majors)
    _write(OUT_DB, "03_xt_nganh_tohop.xlsx",
           ["manganh", "matohop", "th_mon1", "th_mon2", "th_mon3",
            "hsmon1", "hsmon2", "hsmon3"],
           nganh_tohop)

    rows = []
    for t in thisinh:
        rows.append((
            t["cccd"], t["sobaodanh"], t["ho"], t["ten"], t["ho_va_ten"],
            _to_iso_date(t["ngay_sinh"]),
            t["gioi_tinh"], t["dien_thoai"], t["email"], t["noi_sinh"],
            t["doi_tuong"], t["khu_vuc"],
        ))
    _write(OUT_DB, "04_xt_thisinhxettuyen25.xlsx",
           ["cccd", "sobaodanh", "ho", "ten", "ho_va_ten", "ngay_sinh",
            "gioi_tinh", "dien_thoai", "email", "noi_sinh",
            "doi_tuong", "khu_vuc"],
           rows)

    rows = []
    for d in diem_thi:
        rows.append((
            d["cccd"], d["sobaodanh"], d["phuong_thuc"],
            d["toan"], d["ly"], d["hoa"], d["sinh"], d["su"], d["dia"], d["van"],
            d["n1_thi"], d["n1_cc"], d["nl1"], d["nk1"], d["nk2"],
        ))
    _write(OUT_DB, "05_xt_diemthixettuyen.xlsx",
           ["cccd", "sobaodanh", "d_phuongthuc",
            "TO", "LI", "HO", "SI", "SU", "DI", "VA",
            "N1_THI", "N1_CC", "NL1", "NK1", "NK2"],
           rows)

    rows = []
    for d in diem_cong:
        rows.append((d["cccd"], d["diem_cc"], d["diem_utxt"], d["diem_tong"]))
    _write(OUT_DB, "06_xt_diemcongxettuyen.xlsx",
           ["cccd", "diemCC", "diemUtxt", "diemTong"],
           rows)

    _write(OUT_DB, "07_xt_nguyenvongxettuyen.xlsx",
           ["nn_cccd", "nv_manganh", "nv_tt",
            "diem_thxt", "diem_utqd", "diem_cong", "diem_xettuyen",
            "nv_ketqua", "tt_phuongthuc", "tt_thm"],
           nguyen_vong)
    _write(OUT_DB, "08_xt_bangquydoi.xlsx",
           ["d_phuongthuc", "d_tohop", "d_mon",
            "d_diema", "d_diemb", "d_diemc", "d_diemd"],
           bangquydoi)
    _write(OUT_DB, "09_users.xlsx",
           ["username", "fullname", "email", "role", "password_plain"],
           users)


# ---------------------------------------------------------------------------
# Main
# ---------------------------------------------------------------------------
def main():
    parser = argparse.ArgumentParser()
    parser.add_argument("--fetch", action="store_true",
                        help="Bật fetch SGU 2025.")
    parser.add_argument("--no-fetch", action="store_true",
                        help="Tắt fetch.")
    parser.add_argument("--candidates", type=int, default=120)
    args = parser.parse_args()

    print(f"Generating seed-data Excel files to: {OUT_ROOT}")
    print(f"Run date: {TODAY}\n")

    majors = None
    if args.fetch and not args.no_fetch:
        majors = try_fetch_sgu_majors()
    if not majors:
        print("[fetch] Sử dụng dữ liệu fallback nội bộ (Đại học Sài Gòn 2025).")
        majors = SGU_FALLBACK_MAJORS

    rng = random.Random(2025)
    thisinh = gen_thisinh(n=max(100, args.candidates), rng=rng)
    diem_thi = gen_diem_thi(thisinh, rng=rng)
    diem_cong = gen_diem_cong(thisinh, rng=rng)
    nganh_tohop = build_nganh_tohop(majors)
    nguyen_vong = gen_nguyen_vong(thisinh, majors, nganh_tohop, rng=rng)
    bangquydoi = build_bangquydoi()
    users = gen_users(thisinh)

    print("\n--- App-import-ready files (output/) ---")
    write_app_candidates(thisinh)
    write_app_scores(diem_thi)
    write_app_bonus(diem_cong)
    write_app_majors(majors)
    write_app_subject_groups(TOHOP_DATA)
    # Tập 10 mã ngành thật trong DataSeeder.java — aspirations chỉ dùng tập này
    # để khi import vào DB chuẩn (chưa có 110 ngành) không bị lỗi "Mã ngành ko
    # tồn tại". Nếu DB đã import majors.xlsx (110 ngành), set valid=None.
    dataseeder_manganh = {
        "7480201", "7480103", "7480101", "7480202",
        "7340101", "7340301", "7340201",
        "7380101", "7140209", "7210101",
    }
    # 1) aspirations_dataseeder.xlsx — chỉ 7 thí sinh + 10 mã ngành đã có sẵn
    #    trong DataSeeder.java. Dùng cho demo nhanh: chạy Spring Boot xong
    #    import file này, không cần làm gì khác.
    ds_aspirations = synthesize_aspirations_for_dataseeder(majors, nganh_tohop)
    write_app_aspirations(
        ds_aspirations,
        valid_manganh=dataseeder_manganh,
        valid_cccds=None,  # 7 CCCD đã có sẵn từ DataSeeder.java
        filename="aspirations_dataseeder.xlsx",
    )
    # 2) aspirations.xlsx — 120 thí sinh × 10 ngành. Yêu cầu trước đó user
    #    phải import candidates.xlsx (để tạo CCCD), majors.xlsx tuỳ chọn.
    full_cccds = {ts["cccd"] for ts in thisinh}
    write_app_aspirations(
        nguyen_vong,
        valid_manganh=dataseeder_manganh,
        valid_cccds=full_cccds,
        filename="aspirations.xlsx",
    )
    write_app_conversion(bangquydoi)



    print("\n--- DB-style files (output/db/) ---")
    write_db_files(thisinh, diem_thi, diem_cong, majors, nganh_tohop,
                   nguyen_vong, bangquydoi, users)

    summary = (
        f"\nSummary:\n"
        f"  Tổ hợp môn:         {len(TOHOP_DATA)} rows\n"
        f"  Ngành tuyển sinh:   {len(majors)} rows\n"
        f"  Mapping ngành↔TH:   {len(nganh_tohop)} rows\n"
        f"  Thí sinh:           {len(thisinh)} rows\n"
        f"  Điểm thi:           {len(diem_thi)} rows\n"
        f"  Điểm cộng:          {len(diem_cong)} rows\n"
        f"  Nguyện vọng:        {len(nguyen_vong)} rows\n"
        f"  Quy đổi:            {len(bangquydoi)} rows\n"
        f"  Users:              {len(users)} rows\n"
    )
    print(summary)
    print("Done. Files in:", OUT_ROOT)


if __name__ == "__main__":
    main()
