# Công Thức Tính Điểm Xét Tuyển

## Tổng quan

Hệ thống tính điểm xét tuyển theo 5 bước, đưa mọi phương thức về thang 30 điểm.

---

## Bước 1: Tiền xử lý dữ liệu (Quy đổi điểm)

Điểm thi $x$ thuộc vùng $[a, b]$ được quy về thang điểm $[c, d]$ theo công thức nội suy tuyến tính:

$$y = c + \left(\frac{x - a}{b - a}\right) \times (d - c)$$

### Các trường hợp:

- **V-SAT**: Quy đổi về thang điểm 10
- **ĐGNL**: Quy đổi về thang điểm 30 của phương thức THPT

---

## Bước 2: Tính Điểm Tổ Hợp Xét Tuyển (ĐTHXT)

Đưa mọi phương thức về thang 30.

**Ký hiệu:**

- $W = w_1 + w_2 + w_3$ (tổng hệ số)
- $d_1, d_2, d_3$: điểm các môn (đã quy đổi)
- $w_1, w_2, w_3$: hệ số các môn

### Công thức CHUNG:

$$ĐTHXT = \left[ \frac{d_1 \times w_1 + d_2 \times w_2 + d_3 \times w_3}{W} \right] \times 3$$

### Theo phương thức:

| Phương thức | ĐTHXT                                                                                     |
| ----------- | ----------------------------------------------------------------------------------------- |
| THPT        | $\left[ \frac{d_1 w_1 + d_2 w_2 + d_3 w_3}{W} \right] \times 3$                           |
| V-SAT       | $\left[ \frac{d_1 w_1 + d_2 w_2 + d_3 w_3}{W} \right] \times 3$ (điểm đã quy về thang 10) |
| ĐGNL        | Điểm thi ĐGNL đã quy đổi tương đương thang 30                                             |

---

## Bước 3: Tính Điểm Tổ Hợp Gốc Xét Tuyển (ĐTHGXT)

Đưa tổ hợp thực tế về hệ quy chiếu của tổ hợp gốc.

### Theo phương thức:

| Phương thức | ĐTHGXT                                                         |
| ----------- | -------------------------------------------------------------- |
| ĐGNL        | $ĐTHGXT_{ĐGNL} = ĐTHXT_{ĐGNL}$ (không áp dụng ma trận độ lệch) |
| THPT, V-SAT | $ĐTHGXT = ĐTHXT - \text{Mức điểm chênh lệch}$                  |

**Ghi chú:** Mức điểm chênh lệch = tra ma trận (hàng = tổ hợp gốc, cột = tổ hợp thực tế). Nếu tổ hợp không có trong ma trận → mức chênh lệch = 0.

---

## Bước 4: Xác định Điểm Ưu Tiên (ĐƯT)

**Ký hiệu:**

- $ĐC$: Điểm cộng (tối đa 3 điểm thang 30)
- $MĐƯT$: Mức điểm ưu tiên theo quy định

### Công thức:

**Nếu** $(ĐTHGXT + ĐC) < 22,5$:
$$ĐƯT = MĐƯT$$

**Nếu** $(ĐTHGXT + ĐC) \geq 22,5$:
$$ĐƯT = \left[ \frac{30 - ĐTHXT - ĐC}{7,5} \right] \times MĐƯT$$

---

## Bước 5: Tính Điểm Xét Tuyển (ĐXT)

Điểm xét tuyển tối đa là **30 điểm**.

$$ĐXT = ĐTHGXT + ĐC + ĐƯT$$

---

## Tóm tắt các bước

| Bước | Tên          | Output                                 |
| ---- | ------------ | -------------------------------------- |
| 1    | Quy đổi điểm | $d_1, d_2, d_3$ (thang đã chuẩn hóa)   |
| 2    | Tính ĐTHXT   | $ĐTHXT$ (thang 30)                     |
| 3    | Tính ĐTHGXT  | $ĐTHGXT$ (thang 30, đã trừ chênh lệch) |
| 4    | Tính ĐƯT     | $ĐƯT$ (thang 30)                       |
| 5    | Tính ĐXT     | $ĐXT$ (thang 30, tối đa 30)            |

---

## Pseudo-code

```java
public double calculateScore(Aspiration aspiration) {
    // Bước 1: Quy đổi điểm
    double[] convertedScores = convertScores(aspiration);

    // Bước 2: Tính ĐTHXT
    double dthxt = calculateDTHXT(convertedScores, weights);

    // Bước 3: Tính ĐTHGXT
    double dthgxt = calculateDTHGXT(dthxt, method, tohopGoc, tohopThucTe);

    // Bước 4: Tính ĐƯT
    double dut = calculateDUT(dthgxt, dthxt, diemCong, mucUuTien);

    // Bước 5: Tính ĐXT
    double dxt = Math.min(30, dthgxt + diemCong + dut);

    return dxt;
}
```
