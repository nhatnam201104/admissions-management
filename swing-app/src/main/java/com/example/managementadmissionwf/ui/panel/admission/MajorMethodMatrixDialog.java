package com.example.managementadmissionwf.ui.panel.admission;

import com.example.managementadmissionwf.bus.interfaces.StatisticService;
import com.example.managementadmissionwf.dto.statistic.MajorMethodAdmissionStat;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.time.LocalDate;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * Dialog hiển thị "Trúng tuyển theo ngành × phương thức".
 *
 * <p>Báo cáo phục vụ rubric mục 6 desktop (10đ cuối): "Danh sách số lượng
 * trúng tuyển từng phương thức theo ngành".
 *
 * <p>Cột:
 * <ul>
 *   <li>Mã ngành / Tên ngành</li>
 *   <li>Phương thức (THPT/DGNL/VSAT/TUYEN_THANG)</li>
 *   <li>Tổng chỉ tiêu = {@code xt_nganh.n_chitieu}</li>
 *   <li>Chỉ tiêu PT = {@code xt_nganh.sl_*} (có thể 0 nếu chưa cấu hình)</li>
 *   <li>NV đăng ký = COUNT(nv) group by ngành+phương thức</li>
 *   <li>Trúng tuyển = SUM(CASE TRUNG_TUYEN)</li>
 *   <li>Tỉ lệ chọi = NV/chỉ tiêu</li>
 *   <li>Tỉ lệ lấp đầy (%) = trúng tuyển / chỉ tiêu × 100</li>
 * </ul>
 */
public class MajorMethodMatrixDialog extends JDialog {

    private static final String[] COLUMNS = {
            "Mã ngành", "Tên ngành", "Phương thức",
            "Tổng chỉ tiêu", "NV đăng ký", "Trúng tuyển",
            "Tỉ lệ chọi", "Tỉ lệ lấp đầy (%)"
    };


    private final StatisticService statisticService;
    private DefaultTableModel model;

    public MajorMethodMatrixDialog(Frame parent, StatisticService statisticService) {
        super(parent, "Báo cáo trúng tuyển theo ngành × phương thức", true);
        this.statisticService = statisticService;
        initComponents();
        loadData();
    }

    private void initComponents() {
        setSize(1100, 600);
        setLocationRelativeTo(getParent());
        setLayout(new BorderLayout(0, 0));

        JLabel header = new JLabel("Số lượng trúng tuyển từng phương thức theo ngành");
        header.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 16));
        header.setBorder(BorderFactory.createEmptyBorder(12, 16, 8, 16));
        add(header, BorderLayout.NORTH);

        model = new DefaultTableModel(COLUMNS, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                // Cột 0..2 string, còn lại là Number
                return columnIndex >= 3 ? Number.class : String.class;
            }
        };
        JTable table = new JTable(model);
        table.setRowHeight(28);
        table.setAutoCreateRowSorter(true);

        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton exportBtn = new JButton("Xuất Excel");
        exportBtn.addActionListener(e -> exportExcel());
        JButton refreshBtn = new JButton("Làm mới");
        refreshBtn.addActionListener(e -> loadData());
        JButton closeBtn = new JButton("Đóng");
        closeBtn.addActionListener(e -> dispose());
        footer.add(exportBtn);
        footer.add(refreshBtn);
        footer.add(closeBtn);
        add(footer, BorderLayout.SOUTH);
    }

    private void loadData() {
        model.setRowCount(0);
        List<MajorMethodAdmissionStat> rows = statisticService.getMajorMethodMatrix();
        if (rows == null || rows.isEmpty()) {
            return;
        }
        for (MajorMethodAdmissionStat r : rows) {
            model.addRow(new Object[]{
                    r.manganh(),
                    r.tennganh(),
                    r.phuongThuc(),
                    r.totalQuota(),
                    r.totalApply(),
                    r.admitted(),
                    r.competitionRate(),
                    r.fillRate()
            });
        }

    }

    private void exportExcel() {
        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new java.io.File("BaoCao_TrungTuyen_Nganh_PhuongThuc_"
                + LocalDate.now() + ".xlsx"));
        if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }
        try (OutputStream os = new FileOutputStream(chooser.getSelectedFile());
             Workbook wb = new XSSFWorkbook()) {
            Sheet sheet = wb.createSheet("MatrixNganhPhuongThuc");

            CellStyle headerStyle = wb.createCellStyle();
            Font font = wb.createFont();
            font.setBold(true);
            headerStyle.setFont(font);

            Row header = sheet.createRow(0);
            for (int i = 0; i < COLUMNS.length; i++) {
                Cell c = header.createCell(i);
                c.setCellValue(COLUMNS[i]);
                c.setCellStyle(headerStyle);
            }
            for (int r = 0; r < model.getRowCount(); r++) {
                Row row = sheet.createRow(r + 1);
                for (int c = 0; c < COLUMNS.length; c++) {
                    Object val = model.getValueAt(r, c);
                    Cell cell = row.createCell(c);
                    if (val instanceof Number n) {
                        cell.setCellValue(n.doubleValue());
                    } else if (val != null) {
                        cell.setCellValue(val.toString());
                    }
                }
            }
            for (int i = 0; i < COLUMNS.length; i++) {
                sheet.autoSizeColumn(i);
            }
            wb.write(os);
            JOptionPane.showMessageDialog(this, "Xuất Excel thành công.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi xuất Excel: " + ex.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
}
