package com.example.managementadmissionwf.ui.panel.subjectgroup;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;

/**
 * Panel containing the table for displaying subject groups
 */
public class SubjectGroupListPanel extends JPanel {

    private JTable table;
    private DefaultTableModel tableModel;

    private static final Color PRIMARY = new Color(33, 150, 243);
    private static final Color BORDER = new Color(220, 220, 220);
    private static final Font FONT = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font FONT_BOLD = new Font("Segoe UI", Font.BOLD, 14);

    public SubjectGroupListPanel() {
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // Create table
        // Adding hidden ID column at index 4 for tracking entity ID
        String[] columnNames = { "Mã tổ hợp", "Tên tổ hợp", "Môn 1", "Môn 2", "Môn 3", "ID" };
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(tableModel);
        table.setFont(FONT);
        table.setRowHeight(36);
        table.setForeground(Color.BLACK);
        table.setBackground(Color.WHITE);
        table.setGridColor(BORDER);
        table.setSelectionBackground(new Color(225, 245, 254));
        table.setSelectionForeground(Color.BLACK);
        table.getTableHeader().setReorderingAllowed(false);

        // Hide ID column
        table.getColumnModel().getColumn(5).setMinWidth(0);
        table.getColumnModel().getColumn(5).setMaxWidth(0);
        table.getColumnModel().getColumn(5).setWidth(0);

        // Style header
        JTableHeader header = table.getTableHeader();
        header.setFont(FONT_BOLD);
        header.setBackground(new Color(232, 240, 254));
        header.setPreferredSize(new Dimension(100, 40));

        // Wrap in scroll pane
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        add(scrollPane, BorderLayout.CENTER);
    }

    public JTable getTable() {
        return table;
    }

    public DefaultTableModel getTableModel() {
        return tableModel;
    }

    /**
     * Clear all data from table
     */
    public void clearData() {
        tableModel.setRowCount(0);
    }

    /**
     * Add a row to the table
     */
    public void addRow(Object[] rowData) {
        tableModel.addRow(rowData);
    }

    /**
     * Get selected row index
     */
    public int getSelectedRow() {
        return table.getSelectedRow();
    }

    /**
     * Get ID of the selected row
     */
    public Integer getSelectedId() {
        int row = table.getSelectedRow();
        if (row != -1) {
            Object idVal = table.getValueAt(row, 5);
            if (idVal instanceof Integer) {
                return (Integer) idVal;
            }
        }
        return null;
    }
}
