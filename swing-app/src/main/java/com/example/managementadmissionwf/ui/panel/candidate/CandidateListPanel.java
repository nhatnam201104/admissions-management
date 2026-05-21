package com.example.managementadmissionwf.ui.panel.candidate;

import com.example.managementadmissionwf.dto.candidate.CandidateDTO;
import com.example.managementadmissionwf.ui.util.UIFactory;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * List Panel for Candidate Management
 * Displays JTable with candidate list
 */
public class CandidateListPanel extends JPanel {
    private JTable table;
    private DefaultTableModel tableModel;
    private List<CandidateDTO> candidates;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    
    public CandidateListPanel() {
        initComponents();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Create table
        createTable();
        
        // Add scroll pane
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        
        add(scrollPane, BorderLayout.CENTER);
    }
    
    private void createTable() {
        // Column names
        String[] columnNames = {
            "STT", "CCCD", "SBD", "Họ tên", "Ngày sinh", 
            "SĐT", "Email", "Đối tượng", "Khu vực"
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
        UIFactory.applyStandardHeaderStyle(table);
        table.setRowHeight(30);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        
        // Center align cells
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
        
        // Set column widths
        table.getColumnModel().getColumn(0).setPreferredWidth(50);  // STT
        table.getColumnModel().getColumn(1).setPreferredWidth(120); // CCCD
        table.getColumnModel().getColumn(2).setPreferredWidth(100); // SBD
        table.getColumnModel().getColumn(3).setPreferredWidth(150); // Họ tên
        table.getColumnModel().getColumn(4).setPreferredWidth(100); // Ngày sinh
        table.getColumnModel().getColumn(5).setPreferredWidth(120); // SĐT
        table.getColumnModel().getColumn(6).setPreferredWidth(180); // Email
        table.getColumnModel().getColumn(7).setPreferredWidth(100); // Đối tượng
        table.getColumnModel().getColumn(8).setPreferredWidth(80);  // Khu vực
    }
    
    public void loadData(List<CandidateDTO> candidates) {
        this.candidates = candidates;
        tableModel.setRowCount(0);
        
        int stt = 1;
        for (CandidateDTO candidate : candidates) {
            Object[] rowData = {
                stt++,
                candidate.getCccd(),
                candidate.getSobaodanh(),
                candidate.getHoTen(),
                candidate.getNgaySinh() != null ? candidate.getNgaySinh().format(dateFormatter) : "",
                candidate.getDienThoai(),
                candidate.getEmail(),
                candidate.getDoiTuong(),
                candidate.getKhuVuc()
            };
            tableModel.addRow(rowData);
        }
    }
    
    public CandidateDTO getSelectedCandidate() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow >= 0 && candidates != null && selectedRow < candidates.size()) {
            return candidates.get(selectedRow);
        }
        return null;
    }
    
    public void clearSelection() {
        table.clearSelection();
    }
    
    public JTable getTable() {
        return table;
    }
}