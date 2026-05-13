# Statistic Feature - Implementation Plan

> Created: 2026-05-05  
> Status: ✅ APPROVED  
> Stack: JFreeChart, Apache POI, iText

---

## 1. Mục tiêu

Xây dựng dashboard thống kê cho hệ thống xét tuyển với:

- 4 summary cards
- Bar chart (Top 10 ngành)
- Pie chart (phân bố phương thức)
- Histogram (phân bố điểm)
- Export PDF/Excel

---

## 2. Architecture

```
┌─────────────────────────────────────────────────────┐
│                  StatisticPanel (UI)                 │
│  - SummaryCardsPanel                                │
│  - MajorStatsChartPanel (JFreeChart)                │
│  - MethodStatsChartPanel (JFreeChart)               │
│  - ScoreDistributionChartPanel (JFreeChart)         │
│  - ExportButtonsPanel                               │
└─────────────────────────────────────────────────────┘
                          ↓
┌─────────────────────────────────────────────────────┐
│               StatisticController                    │
│  - loadDashboard()                                 │
│  - loadMajorChart()                                │
│  - loadMethodChart()                               │
│  - loadScoreChart()                                │
│  - exportPDF() / exportExcel()                     │
└─────────────────────────────────────────────────────┘
                          ↓
┌─────────────────────────────────────────────────────┐
│                StatisticService                      │
│  - getSummary() → StatisticSummary                  │
│  - getMajorStatistics() → List<MajorStatistic>      │
│  - getMethodStatistics() → List<MethodStatistic>    │
│  - getScoreDistribution() → List<ScoreDistribution> │
└─────────────────────────────────────────────────────┘
                          ↓
┌─────────────────────────────────────────────────────┐
│          NguyenVongRepository (Custom Queries)       │
└─────────────────────────────────────────────────────┘
```

---

## 3. DTOs

### 3.1 StatisticSummary

```java
record StatisticSummary(
    long totalCandidates,
    long totalAspirations,
    long admitted,
    long rejected,
    double admissionRate,
    double avgScore,
    double maxScore,
    double minScore
)
```

### 3.2 MajorStatistic

```java
record MajorStatistic(
    String manganh,
    String tennganh,
    long totalAspirations,
    long admitted,
    int target,
    double avgScore,
    double fillRate
)
```

### 3.3 MethodStatistic

```java
record MethodStatistic(
    String phuongThuc,
    long total,
    long admitted,
    double rate
)
```

### 3.4 ScoreDistribution

```java
record ScoreDistribution(
    String range,
    long count,
    double percentage
)
```

---

## 4. Metrics & Queries

### 4.1 Summary Metrics

| Metric            | SQL                                                                                  |
| ----------------- | ------------------------------------------------------------------------------------ |
| Total Candidates  | `SELECT COUNT(DISTINCT nn_cccd) FROM xt_nguyenvongxettuyen WHERE is_deleted = false` |
| Total Aspirations | `SELECT COUNT(*) FROM xt_nguyenvongxettuyen WHERE is_deleted = false`                |
| Admitted          | `SELECT COUNT(*) WHERE nv_ketqua = 'TRUNG_TUYEN'`                                    |
| Rejected          | `SELECT COUNT(*) WHERE nv_ketqua = 'TRUOT'`                                          |
| Avg Score         | `SELECT AVG(diem_xettuyen)`                                                          |
| Max Score         | `SELECT MAX(diem_xettuyen)`                                                          |
| Min Score         | `SELECT MIN(diem_xettuyen)`                                                          |

### 4.2 Major Statistics (Top 10)

```sql
SELECT
    n.manganh,
    n.tennganh,
    COUNT(nv.id) as total,
    SUM(CASE WHEN nv.nv_ketqua = 'TRUNG_TUYEN' THEN 1 ELSE 0 END) as admitted,
    n.n_chitieu as target,
    AVG(nv.diem_xettuyen) as avg_score
FROM xt_nguyenvongxettuyen nv
JOIN xt_nganh n ON nv.nv_manganh = n.manganh
WHERE nv.is_deleted = false
GROUP BY n.manganh, n.tennganh, n.n_chitieu
ORDER BY total DESC
LIMIT 10
```

### 4.3 Method Statistics

```sql
SELECT
    tt_phuongthuc as phuongThuc,
    COUNT(*) as total,
    SUM(CASE WHEN nv_ketqua = 'TRUNG_TUYEN' THEN 1 ELSE 0 END) as admitted
FROM xt_nguyenvongxettuyen
WHERE is_deleted = false
GROUP BY tt_phuongthuc
```

### 4.4 Score Distribution (5 ranges)

```sql
SELECT
    CASE
        WHEN diem_xettuyen >= 30 THEN '30+'
        WHEN diem_xettuyen >= 25 THEN '25-29.9'
        WHEN diem_xettuyen >= 20 THEN '20-24.9'
        WHEN diem_xettuyen >= 15 THEN '15-19.9'
        ELSE '<15'
    END as range,
    COUNT(*) as count
FROM xt_nguyenvongxettuyen
WHERE is_deleted = false AND diem_xettuyen IS NOT NULL
GROUP BY range
ORDER BY range DESC
```

---

## 5. UI Layout

```
┌────────────────────────────────────────────────────────────────┐
│  [Refresh Button]                          [Export PDF] [Excel]│
├────────────────────────────────────────────────────────────────┤
│ ┌──────────────┐ ┌──────────────┐ ┌──────────────┐ ┌──────────┐│
│ │ Tổng thí sinh│ │Tổng nguyện   │ │ Tỷ lệ đậu   │ │Điểm TB   ││
│ │    1,234     │ │   vọng: 5,678│ │    45.2%     │ │  23.5    ││
│ └──────────────┘ └──────────────┘ └──────────────┘ └──────────┘│
├────────────────────────────────────────────────────────────────┤
│                    THỐNG KÊ THEO NGÀNH (Top 10)                │
│ ┌─────────────────────────────────────┐                        │
│ │                                     │                        │
│ │         [Bar Chart - JFreeChart]    │                        │
│ │                                     │                        │
│ └─────────────────────────────────────┘                        │
├─────────────────────────────────┬──────────────────────────────┤
│   PHÂN BỐ ĐIỂM                  │   THEO PHƯƠNG THỨC            │
│ ┌─────────────────────────────┐ │ ┌────────────────────────┐   │
│ │  [Histogram - JFreeChart]  │ │ │  [Pie Chart]           │   │
│ │                             │ │ │                        │   │
│ └─────────────────────────────┘ │ └────────────────────────┘   │
└─────────────────────────────────┴──────────────────────────────┘
```

---

## 6. Implementation Order

### Phase 1: DTOs & Service

1. [ ] Create `dto/` package with 4 DTOs
2. [ ] Create `StatisticService` interface
3. [ ] Create `StatisticServiceImpl` with @Service
4. [ ] Add custom queries to `NguyenVongRepository`

### Phase 2: Controller

5. [ ] Create `StatisticController` @Component
6. [ ] Implement data loading methods

### Phase 3: UI

7. [ ] Update `StatisticPanel` with BorderLayout
8. [ ] Create `SummaryCardsPanel` with 4 JPanel cards
9. [ ] Create chart panels with JFreeChart
10. [ ] Add export buttons

### Phase 4: Export

11. [ ] Implement PDF export with iText
12. [ ] Implement Excel export with Apache POI

---

## 7. Dependencies (Already in pom.xml)

```xml
<!-- JFreeChart for charts -->
<dependency>
    <groupId>org.jfree</groupId>
    <artifactId>jfreechart</artifactId>
    <version>1.5.4</version>
</dependency>

<!-- Apache POI for Excel -->
<dependency>
    <groupId>org.apache.poi</groupId>
    <artifactId>poi-ooxml</artifactId>
    <version>5.2.5</version>
</dependency>

<!-- iText for PDF -->
<dependency>
    <groupId>com.itextpdf</groupId>
    <artifactId>itext7-core</artifactId>
    <version>7.2.5</version>
</dependency>
```

---

## 8. File Structure

```
ui/
├── panel/
│   └── StatisticPanel.java          # Main panel (updated)
├── component/
│   └── StatisticController.java     # New: Controller
service/
├── StatisticService.java            # New: Interface
├── impl/
│   └── StatisticServiceImpl.java    # New: Implementation
dto/
├── StatisticSummary.java            # New: Record
├── MajorStatistic.java              # New: Record
├── MethodStatistic.java             # New: Record
└── ScoreDistribution.java          # New: Record
```

---

## 9. Notes

- No year/period filter (not in DB schema)
- Refresh button to reload data
- Charts use JFreeChart built-in themes
- Export saves to user-selected directory via JFileChooser
