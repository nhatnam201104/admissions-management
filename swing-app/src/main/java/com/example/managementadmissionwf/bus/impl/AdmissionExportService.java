package com.example.managementadmissionwf.bus.impl;

import com.example.managementadmissionwf.dto.admission.AdmissionResultDTO;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.text.Normalizer;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AdmissionExportService {

    /**
     * Xuất danh sách kết quả xét tuyển ra Excel theo cấu hình cột do người
     * dùng chọn (true = chọn cột).
     */
    public void exportToExcel(List<AdmissionResultDTO> results,
                              OutputStream outputStream,
                              Map<String, Boolean> columns) {
        List<ExportColumn> exportColumns = resolveExportColumns(columns);
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Ket qua xet tuyen");
            CellStyle headerStyle = createExportHeaderStyle(workbook);

            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < exportColumns.size(); i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(exportColumns.get(i).header());
                cell.setCellStyle(headerStyle);
            }

            for (int rowIndex = 0; rowIndex < results.size(); rowIndex++) {
                Row row = sheet.createRow(rowIndex + 1);
                AdmissionResultDTO result = results.get(rowIndex);
                for (int colIndex = 0; colIndex < exportColumns.size(); colIndex++) {
                    Object value = exportColumns.get(colIndex).value(result, rowIndex);
                    setExportCellValue(row.createCell(colIndex), value);
                }
            }

            for (int i = 0; i < exportColumns.size(); i++) {
                sheet.autoSizeColumn(i);
            }
            workbook.write(outputStream);
        } catch (Exception e) {
            throw new RuntimeException("Không thể xuất Excel: " + e.getMessage(), e);
        }
    }

    /**
     * Xuất ra file PDF tối giản (Courier ASCII). Đủ để demo rubric, không cần
     * dependency PDF library nặng.
     */
    public void exportToPDF(List<AdmissionResultDTO> results,
                            OutputStream outputStream,
                            Map<String, Boolean> columns) {
        List<ExportColumn> exportColumns = resolveExportColumns(columns);
        try {
            List<String> lines = buildPdfLines(results, exportColumns);
            writeSimplePdf(lines, outputStream);
        } catch (Exception e) {
            throw new RuntimeException("Không thể xuất PDF: " + e.getMessage(), e);
        }
    }

    // ----- Internal helpers -----

    private List<ExportColumn> resolveExportColumns(Map<String, Boolean> selected) {
        List<ExportColumn> allColumns = List.of(
                new ExportColumn("STT", "STT", 5, (dto, index) -> index + 1),
                new ExportColumn("CCCD", "CCCD", 14, (dto, index) -> dto.getCccd()),
                new ExportColumn("HoTen", "Ho ten", 24, (dto, index) -> dto.getHoTen()),
                new ExportColumn("SBD", "SBD", 10, (dto, index) -> dto.getSobaodanh()),
                new ExportColumn("NguyenVong", "NV", 4, (dto, index) -> dto.getNvTt()),
                new ExportColumn("Nganh", "Nganh", 28, (dto, index) -> formatMajor(dto)),
                new ExportColumn("PhuongThuc", "PT", 10, (dto, index) -> dto.getPhuongThuc()),
                new ExportColumn("ToHop", "To hop", 8, (dto, index) -> dto.getTohop()),
                new ExportColumn("Diem", "Diem XT", 10, (dto, index) -> dto.getDiemXettuyen()),
                new ExportColumn("DiemChuan", "Diem chuan", 11, (dto, index) -> dto.getDiemChuan()),
                new ExportColumn("KetQua", "Ket qua", 12, (dto, index) -> dto.getKetQua()),
                new ExportColumn("NgayXet", "Ngay xet", 10, (dto, index) -> dto.getNgayXet())
        );

        if (selected == null || selected.isEmpty()) {
            return allColumns;
        }
        List<ExportColumn> filtered = allColumns.stream()
                .filter(column -> Boolean.TRUE.equals(selected.get(column.key())))
                .toList();
        return filtered.isEmpty() ? allColumns : filtered;
    }

    private String formatMajor(AdmissionResultDTO dto) {
        if (dto.getTennganh() == null || dto.getTennganh().isBlank()) {
            return dto.getManganh();
        }
        return dto.getManganh() + " - " + dto.getTennganh();
    }

    private CellStyle createExportHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        return style;
    }

    private void setExportCellValue(Cell cell, Object value) {
        if (value == null) {
            cell.setBlank();
        } else if (value instanceof Number number) {
            cell.setCellValue(number.doubleValue());
        } else {
            cell.setCellValue(value.toString());
        }
    }

    private List<String> buildPdfLines(List<AdmissionResultDTO> results, List<ExportColumn> columns) {
        List<String> lines = new ArrayList<>();
        lines.add("BAO CAO KET QUA XET TUYEN");
        lines.add("Xuat luc: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
        lines.add("So dong: " + results.size());
        lines.add("");
        lines.add(formatPdfRow(columns, null, -1));
        lines.add(columns.stream()
                .map(column -> "-".repeat(column.width()))
                .collect(Collectors.joining(" ")));

        for (int i = 0; i < results.size(); i++) {
            lines.add(formatPdfRow(columns, results.get(i), i));
        }
        return lines;
    }

    private String formatPdfRow(List<ExportColumn> columns, AdmissionResultDTO result, int rowIndex) {
        return columns.stream()
                .map(column -> {
                    String text = result == null ? column.header() : stringifyExportValue(column.value(result, rowIndex));
                    return padRight(toPdfSafe(text), column.width());
                })
                .collect(Collectors.joining(" "));
    }

    private String stringifyExportValue(Object value) {
        if (value == null) {
            return "";
        }
        if (value instanceof Double d) {
            return String.format(Locale.ROOT, "%.2f", d);
        }
        return value.toString();
    }

    private String padRight(String value, int width) {
        String truncated = value.length() > width ? value.substring(0, Math.max(0, width - 1)) + "~" : value;
        return String.format(Locale.ROOT, "%-" + width + "s", truncated);
    }

    private String toPdfSafe(String value) {
        if (value == null) {
            return "";
        }
        String normalized = Normalizer.normalize(value, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .replace('Đ', 'D')
                .replace('đ', 'd');
        return normalized.replaceAll("[^\\x20-\\x7E]", "?");
    }

    private void writeSimplePdf(List<String> lines, OutputStream outputStream) throws IOException {
        int linesPerPage = 50;
        List<String> pageContents = new ArrayList<>();
        for (int start = 0; start < lines.size(); start += linesPerPage) {
            List<String> pageLines = lines.subList(start, Math.min(start + linesPerPage, lines.size()));
            pageContents.add(buildPdfPageContent(pageLines));
        }

        List<String> objects = new ArrayList<>();
        objects.add("<< /Type /Catalog /Pages 2 0 R >>");
        objects.add("");
        objects.add("<< /Type /Font /Subtype /Type1 /BaseFont /Courier >>");

        List<String> pageRefs = new ArrayList<>();
        for (String pageContent : pageContents) {
            int pageObjectNumber = objects.size() + 1;
            int contentObjectNumber = pageObjectNumber + 1;
            pageRefs.add(pageObjectNumber + " 0 R");
            objects.add("<< /Type /Page /Parent 2 0 R /MediaBox [0 0 842 595] " +
                    "/Resources << /Font << /F1 3 0 R >> >> /Contents " + contentObjectNumber + " 0 R >>");

            String stream = pageContent + "\n";
            int length = stream.getBytes(StandardCharsets.ISO_8859_1).length;
            objects.add("<< /Length " + length + " >>\nstream\n" + stream + "endstream");
        }
        objects.set(1, "<< /Type /Pages /Kids [" + String.join(" ", pageRefs) + "] /Count " + pageRefs.size() + " >>");

        ByteArrayOutputStream pdf = new ByteArrayOutputStream();
        writePdfAscii(pdf, "%PDF-1.4\n");
        List<Integer> offsets = new ArrayList<>();
        offsets.add(0);
        for (int i = 0; i < objects.size(); i++) {
            offsets.add(pdf.size());
            writePdfAscii(pdf, (i + 1) + " 0 obj\n");
            writePdfAscii(pdf, objects.get(i));
            writePdfAscii(pdf, "\nendobj\n");
        }

        int xrefOffset = pdf.size();
        writePdfAscii(pdf, "xref\n0 " + (objects.size() + 1) + "\n");
        writePdfAscii(pdf, "0000000000 65535 f \n");
        for (int i = 1; i < offsets.size(); i++) {
            writePdfAscii(pdf, String.format(Locale.ROOT, "%010d 00000 n \n", offsets.get(i)));
        }
        writePdfAscii(pdf, "trailer\n<< /Size " + (objects.size() + 1) + " /Root 1 0 R >>\n");
        writePdfAscii(pdf, "startxref\n" + xrefOffset + "\n%%EOF\n");
        pdf.writeTo(outputStream);
    }

    private String buildPdfPageContent(List<String> pageLines) {
        StringBuilder content = new StringBuilder();
        content.append("BT\n/F1 7 Tf\n1 0 0 1 30 560 Tm\n");
        for (String line : pageLines) {
            content.append("(").append(escapePdfText(line)).append(") Tj\n0 -10 Td\n");
        }
        content.append("ET");
        return content.toString();
    }

    private String escapePdfText(String value) {
        return value.replace("\\", "\\\\")
                .replace("(", "\\(")
                .replace(")", "\\)");
    }

    private void writePdfAscii(ByteArrayOutputStream outputStream, String value) throws IOException {
        outputStream.write(value.getBytes(StandardCharsets.ISO_8859_1));
    }

    private record ExportColumn(String key, String header, int width, ExportValueProvider valueProvider) {
        Object value(AdmissionResultDTO dto, int index) {
            return valueProvider.value(dto, index);
        }
    }

    @FunctionalInterface
    private interface ExportValueProvider {
        Object value(AdmissionResultDTO dto, int index);
    }
}
