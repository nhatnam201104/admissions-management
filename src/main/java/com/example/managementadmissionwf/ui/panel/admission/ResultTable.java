package com.example.managementadmissionwf.ui.panel.admission;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public class ResultTable extends JPanel{
	private JTable table;
    private DefaultTableModel tableModel;
    
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
        // Column names
        String[] columnNames = {
        		"STT", "CCCD", "Họ tên", "SBD", "Nguyện vọng","Ngành", "Phương thức", "Điểm XT", "Kết quả", "Ngày xét"
        };
        
        // Create table model
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        // Create table
        table = new JTable(tableModel);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.getTableHeader().setBackground(new Color(44, 62, 80));
        table.getTableHeader().setForeground(Color.WHITE);
        table.setRowHeight(30);
        //table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION); 
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        table.setAutoCreateRowSorter(true);
        
        // Center align cells
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
        
        // Set column widths
        table.getColumnModel().getColumn(0).setPreferredWidth(50);  // STT
        table.getColumnModel().getColumn(1).setPreferredWidth(120); // CCCD
        table.getColumnModel().getColumn(2).setPreferredWidth(180); // Họ tên
        table.getColumnModel().getColumn(3).setPreferredWidth(100); // SBD
        table.getColumnModel().getColumn(4).setPreferredWidth(110); // Nguyện vọng
        table.getColumnModel().getColumn(5).setPreferredWidth(200); // Ngành
        table.getColumnModel().getColumn(6).setPreferredWidth(150); // Phương thức
        table.getColumnModel().getColumn(7).setPreferredWidth(80);  // Điểm XT
        table.getColumnModel().getColumn(8).setPreferredWidth(120); // Kết quả
        table.getColumnModel().getColumn(9).setPreferredWidth(120); // Ngày xét
    }
    
    private void setupColorRenderer() {
        DefaultTableCellRenderer colorRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, 
                    boolean isSelected, boolean hasFocus, int row, int column) {
                
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setHorizontalAlignment(JLabel.CENTER);

                String ketQua = (String) table.getValueAt(row, 8);

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
    
    public void loadMockData() {
    	tableModel.setRowCount(0);

    }
    
    public JTable getTable() {
        return table;
    }
    
    public DefaultTableModel getTableModel() {
        return tableModel;
    }
}
