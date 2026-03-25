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
        String[] columnNames = {"Mã tổ hợp", "Môn 1", "Môn 2", "Môn 3"};
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
        
        // Style header
        JTableHeader header = table.getTableHeader();
        header.setFont(FONT_BOLD);
        header.setBackground(new Color(232, 240, 254));
        header.setPreferredSize(new Dimension(100, 40));
        
        // Add sample data
        loadSampleData();
        
        // Wrap in scroll pane
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        add(scrollPane, BorderLayout.CENTER);
    }
    
    private void loadSampleData() {
        // Sample subject group data
        Object[][] sampleData = {
                {"A00", "Toán", "Vật lý", "Hóa học"},
                {"A01", "Toán", "Vật lý", "Tiếng Anh"},
                {"D01", "Toán", "Ngữ văn", "Tiếng Anh"},
                {"D07", "Toán", "Hóa học", "Tiếng Anh"},
                {"B00", "Toán", "Hóa học", "Sinh học"},
                {"C00", "Ngữ văn", "Lịch sử", "Địa lý"},
                {"D14", "Ngữ văn", "Lịch sử", "Tiếng Anh"}
        };
        
        for (Object[] row : sampleData) {
            tableModel.addRow(row);
        }
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
}