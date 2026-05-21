package com.example.managementadmissionwf.utils;

import com.example.managementadmissionwf.annotation.ExcelColumn;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class ExcelUtil {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    /**
     * Import data from Excel to List of objects
     */
    public static <T> List<T> importExcel(InputStream inputStream, Class<T> clazz) throws Exception {
        List<T> result = new ArrayList<>();

        try (Workbook workbook = WorkbookFactory.create(inputStream)) {
            Sheet sheet = workbook.getSheetAt(0);
            if (sheet == null || sheet.getPhysicalNumberOfRows() <= 1) {
                return result; // Empty sheet or only headers
            }

            // Get header mappings
            Row headerRow = sheet.getRow(0);
            Map<String, Integer> headerIndexMap = new HashMap<>();
            for (Cell cell : headerRow) {
                headerIndexMap.put(cell.getStringCellValue().trim(), cell.getColumnIndex());
            }

            // Map fields with @ExcelColumn
            List<Field> excelFields = getExcelFields(clazz);

            // Sanity check: if NONE of the @ExcelColumn names match the headers,
            // the file is the wrong format. Fail loud instead of silently
            // returning an empty list (đây là bug "import thành công nhưng không
            // có dòng nào được nhập").
            List<String> expectedHeaders = excelFields.stream()
                    .map(f -> f.getAnnotation(ExcelColumn.class).name())
                    .toList();
            boolean anyMatch = expectedHeaders.stream().anyMatch(headerIndexMap::containsKey);
            if (!anyMatch) {
                throw new IllegalArgumentException(
                        "File Excel không đúng định dạng. Cần có ít nhất một cột trong: "
                                + expectedHeaders + ". Nhưng file đang có headers: "
                                + headerIndexMap.keySet());
            }


            // Read data rows
            for (int i = 1; i < sheet.getPhysicalNumberOfRows(); i++) {
                Row row = sheet.getRow(i);
                if (row == null || isRowEmpty(row)) {
                    continue;
                }

                T instance = clazz.getDeclaredConstructor().newInstance();
                boolean hasData = false;

                for (Field field : excelFields) {
                    ExcelColumn annotation = field.getAnnotation(ExcelColumn.class);
                    String headerName = annotation.name();

                    Integer colIndex = headerIndexMap.get(headerName);
                    if (colIndex != null) {
                        Cell cell = row.getCell(colIndex);
                        Object value = getCellValue(cell, field.getType());

                        if (value != null) {
                            field.setAccessible(true);
                            field.set(instance, value);
                            hasData = true;
                        }
                    }
                }

                if (hasData) {
                    result.add(instance);
                }
            }
        }
        return result;
    }

    /**
     * Export data from List of objects to Excel
     */
    public static <T> void exportExcel(List<T> data, Class<T> clazz, OutputStream outputStream) throws Exception {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Data");
            List<Field> excelFields = getExcelFields(clazz);

            // Create header row
            Row headerRow = sheet.createRow(0);
            CellStyle headerStyle = createHeaderStyle(workbook);

            for (int i = 0; i < excelFields.size(); i++) {
                Field field = excelFields.get(i);
                ExcelColumn annotation = field.getAnnotation(ExcelColumn.class);
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(annotation.name());
                cell.setCellStyle(headerStyle);
            }

            // Write data
            if (data != null && !data.isEmpty()) {
                for (int i = 0; i < data.size(); i++) {
                    Row row = sheet.createRow(i + 1);
                    T instance = data.get(i);

                    for (int j = 0; j < excelFields.size(); j++) {
                        Field field = excelFields.get(j);
                        field.setAccessible(true);
                        Object value = field.get(instance);
                        Cell cell = row.createCell(j);
                        setCellValue(cell, value);
                    }
                }
            }

            // Auto-size columns
            for (int i = 0; i < excelFields.size(); i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(outputStream);
        }
    }

    private static List<Field> getExcelFields(Class<?> clazz) {
        return Arrays.stream(clazz.getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(ExcelColumn.class))
                .collect(Collectors.toList());
    }

    private static boolean isRowEmpty(Row row) {
        if (row == null) {
            return true;
        }
        for (int c = row.getFirstCellNum(); c < row.getLastCellNum(); c++) {
            Cell cell = row.getCell(c);
            if (cell != null && cell.getCellType() != CellType.BLANK) {
                return false;
            }
        }
        return true;
    }

    private static Object getCellValue(Cell cell, Class<?> fieldType) {
        if (cell == null || cell.getCellType() == CellType.BLANK) {
            return null;
        }

        try {
            switch (cell.getCellType()) {
                case STRING:
                    String strValue = cell.getStringCellValue().trim();
                    if (strValue.isEmpty())
                        return null;
                    if (fieldType == String.class)
                        return strValue;
                    if (fieldType == Integer.class || fieldType == int.class)
                        return Integer.parseInt(strValue);
                    if (fieldType == Long.class || fieldType == long.class)
                        return Long.parseLong(strValue);
                    if (fieldType == Double.class || fieldType == double.class)
                        return Double.parseDouble(normalizeNumberText(strValue));
                    if (fieldType == Boolean.class || fieldType == boolean.class)
                        return Boolean.parseBoolean(strValue);
                    if (fieldType == LocalDate.class)
                        return LocalDate.parse(strValue, DATE_FORMATTER);
                    if (fieldType == LocalDateTime.class)
                        return LocalDateTime.parse(strValue, DATETIME_FORMATTER);
                    break;

                case NUMERIC:
                    if (DateUtil.isCellDateFormatted(cell)) {
                        if (fieldType == LocalDate.class) {
                            return cell.getLocalDateTimeCellValue().toLocalDate();
                        }
                        if (fieldType == LocalDateTime.class) {
                            return cell.getLocalDateTimeCellValue();
                        }
                    } else {
                        double numValue = cell.getNumericCellValue();
                        if (fieldType == String.class) {
                            // Convert numeric to string without decimal if it's an integer
                            if (numValue == (long) numValue) {
                                return String.format("%d", (long) numValue);
                            }
                            return String.valueOf(numValue);
                        }
                        if (fieldType == Integer.class || fieldType == int.class)
                            return (int) numValue;
                        if (fieldType == Long.class || fieldType == long.class)
                            return (long) numValue;
                        if (fieldType == Double.class || fieldType == double.class)
                            return numValue;
                    }
                    break;

                case BOOLEAN:
                    boolean boolValue = cell.getBooleanCellValue();
                    if (fieldType == Boolean.class || fieldType == boolean.class)
                        return boolValue;
                    if (fieldType == String.class)
                        return String.valueOf(boolValue);
                    break;

                case FORMULA:
                    // Evaluate formula? For simplicity, we get cached string or numeric
                    try {
                        return cell.getStringCellValue();
                    } catch (Exception e) {
                        return cell.getNumericCellValue();
                    }
                default:
                    break;
            }
        } catch (Exception e) {
            // Ignore format issues, return null
        }
        return null;
    }

    private static String normalizeNumberText(String text) {
        String normalized = text == null ? "" : text.trim().replace(" ", "");
        int lastComma = normalized.lastIndexOf(',');
        int lastDot = normalized.lastIndexOf('.');

        if (lastComma >= 0 && lastDot >= 0) {
            if (lastComma > lastDot) {
                return normalized.replace(".", "").replace(',', '.');
            }
            return normalized.replace(",", "");
        }

        if (lastComma >= 0) {
            return normalized.replace(',', '.');
        }

        return normalized;
    }

    private static void setCellValue(Cell cell, Object value) {
        if (value == null) {
            cell.setBlank();
            return;
        }

        if (value instanceof String) {
            cell.setCellValue((String) value);
        } else if (value instanceof Integer) {
            cell.setCellValue((Integer) value);
        } else if (value instanceof Long) {
            cell.setCellValue((Long) value);
        } else if (value instanceof Double) {
            cell.setCellValue((Double) value);
        } else if (value instanceof Boolean) {
            cell.setCellValue((Boolean) value);
        } else if (value instanceof LocalDate) {
            cell.setCellValue(((LocalDate) value).format(DATE_FORMATTER));
        } else if (value instanceof LocalDateTime) {
            cell.setCellValue(((LocalDateTime) value).format(DATETIME_FORMATTER));
        } else {
            cell.setCellValue(value.toString());
        }
    }

    private static CellStyle createHeaderStyle(Workbook workbook) {
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
}
