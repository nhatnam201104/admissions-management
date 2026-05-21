package com.example.managementadmissionwf.ui.panel.admission;

import com.example.managementadmissionwf.ui.util.UIFactory;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

/**
 * Bảng kết quả nguyện vọng. Phân trang được thực hiện bởi
 * {@link com.example.managementadmissionwf.ui.panel.AbstractFeaturePanel}
 * (thanh dưới) — table này chỉ render slice của allRows tương ứng.
 */
public class ResultTable extends JPanel {
    private JTable table;
    private DefaultTableModel tableModel;

    private List<Object[]> allRows = new ArrayList<>();

    public ResultTable() {
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        createTable();
        setupColorRenderer();

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        add(scrollPane, BorderLayout.CENTER);
    }

    private void createTable() {
        String[] columnNames = {
                "STT", "CCCD", "SBD", "NV", "Ngành", "Phương thức", "Tổ hợp", "Điểm XT", "Điểm chuẩn", "Kết quả"
        };

        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(tableModel);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        UIFactory.applyStandardHeaderStyle(table);
        table.setRowHeight(30);
        table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        table.setAutoCreateRowSorter(true);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        table.getColumnModel().getColumn(0).setPreferredWidth(50);
        table.getColumnModel().getColumn(1).setPreferredWidth(130);
        table.getColumnModel().getColumn(2).setPreferredWidth(90);
        table.getColumnModel().getColumn(3).setPreferredWidth(50);
        table.getColumnModel().getColumn(4).setPreferredWidth(180);
        table.getColumnModel().getColumn(5).setPreferredWidth(100);
        table.getColumnModel().getColumn(6).setPreferredWidth(80);
        table.getColumnModel().getColumn(7).setPreferredWidth(80);
        table.getColumnModel().getColumn(8).setPreferredWidth(90);
        table.getColumnModel().getColumn(9).setPreferredWidth(110);
    }

    private void setupColorRenderer() {
        DefaultTableCellRenderer colorRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {

                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setHorizontalAlignment(JLabel.CENTER);

                String ketQua = (String) table.getValueAt(row, 9);

                if (!isSelected) {
                    if ("TRUNG_TUYEN".equals(ketQua)) {
                        c.setBackground(new Color(212, 237, 218));
                        c.setForeground(new Color(21, 87, 36));
                    } else if ("TRUOT".equals(ketQua)) {
                        c.setBackground(new Color(248, 215, 218));
                        c.setForeground(new Color(114, 28, 36));
                    } else if ("CHO_XET".equals(ketQua)) {
                        c.setBackground(new Color(255, 243, 205));
                        c.setForeground(new Color(133, 100, 4));
                    } else {
                        c.setBackground(Color.WHITE);
                        c.setForeground(Color.BLACK);
                    }
                }
                return c;
            }
        };

        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(colorRenderer);
        }
    }

    /** Lưu toàn bộ dữ liệu (controller giữ source of truth). */
    public void setAllRows(List<Object[]> rows) {
        this.allRows = rows != null ? new ArrayList<>(rows) : new ArrayList<>();
    }

    /** Render slice [start, start+size) cho trang hiện tại. */
    public void renderPage(int page, int pageSize) {
        tableModel.setRowCount(0);
        if (allRows.isEmpty()) return;
        int start = Math.max(0, (page - 1) * pageSize);
        int end = Math.min(start + pageSize, allRows.size());
        for (int i = start; i < end; i++) {
            tableModel.addRow(allRows.get(i));
        }
    }

    public int getTotalRowCount() {
        return allRows.size();
    }

    public void loadMockData() {
        tableModel.setRowCount(0);
        allRows.clear();
    }

    public JTable getTable() {
        return table;
    }

    public DefaultTableModel getTableModel() {
        return tableModel;
    }
}
