package com.example.managementadmissionwf.ui.panel.score;

import com.example.managementadmissionwf.dto.score.ScoreDTO;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.List;

/**
 * List Panel for Score Management
 * Displays JTable with score list
 */
public class ScoreListPanel extends JPanel {
    private JTable table;
    private DefaultTableModel tableModel;
    private List<ScoreDTO> scores;

    public ScoreListPanel() {
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
                "STT", "CCCD", "SBD", "PT", "T_TO", "T_LI", "T_HO",
                "T_SI", "T_SU", "T_DI", "T_VA", "N1", "NL1", "Điểm cộng"
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
        table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        table.getTableHeader().setBackground(new Color(44, 62, 80));
        table.getTableHeader().setForeground(Color.WHITE);
        table.setRowHeight(25);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
//        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);


        // Center align cells
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        // Set column widths
        table.getColumnModel().getColumn(0).setPreferredWidth(40);   // STT
        table.getColumnModel().getColumn(1).setPreferredWidth(180);  // CCCD
        table.getColumnModel().getColumn(2).setPreferredWidth(180);   // SBD
        table.getColumnModel().getColumn(3).setPreferredWidth(50);   // PT
        table.getColumnModel().getColumn(4).setPreferredWidth(50);   // T_TO
        table.getColumnModel().getColumn(5).setPreferredWidth(50);   // T_LI
        table.getColumnModel().getColumn(6).setPreferredWidth(50);   // T_HO
        table.getColumnModel().getColumn(7).setPreferredWidth(50);   // T_SI
        table.getColumnModel().getColumn(8).setPreferredWidth(50);   // T_SU
        table.getColumnModel().getColumn(9).setPreferredWidth(50);   // T_DI
        table.getColumnModel().getColumn(10).setPreferredWidth(50);  // T_VA
        table.getColumnModel().getColumn(11).setPreferredWidth(50);  // N1
        table.getColumnModel().getColumn(12).setPreferredWidth(50);  // NL1
        table.getColumnModel().getColumn(13).setPreferredWidth(80);

    }


    public void loadData(List<ScoreDTO> scores) {
        this.scores = scores;
        tableModel.setRowCount(0);

        int stt = 1;
        for (ScoreDTO score : scores) {
            Object[] rowData = {
                    stt++,
                    score.getCccd(),
                    score.getSobaodanh(),
                    score.getPhuongThuc(),
                    formatScore(score.getToan()),
                    formatScore(score.getLy()),
                    formatScore(score.getHoa()),
                    formatScore(score.getSinh()),
                    formatScore(score.getSu()),
                    formatScore(score.getDia()),
                    formatScore(score.getVan()),
                    formatScore(score.getN1CcCalculated()),
                    formatScore(score.getNl1()),
                    formatScore(score.calculateTotalBonus())
            };
            tableModel.addRow(rowData);
        }
    }

    private String formatScore(Double score) {
        if (score == null) {
            return "-";
        }
        return String.format("%.1f", score);
    }

    public ScoreDTO getSelectedScore() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow >= 0 && scores != null && selectedRow < scores.size()) {
            return scores.get(selectedRow);
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