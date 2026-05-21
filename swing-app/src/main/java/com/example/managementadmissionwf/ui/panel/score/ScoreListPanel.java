package com.example.managementadmissionwf.ui.panel.score;

import com.example.managementadmissionwf.dto.score.ScoreDTO;
import com.example.managementadmissionwf.dto.score.BonusScoreDTO;
import com.example.managementadmissionwf.ui.util.UIFactory;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.Map;
import java.util.Vector;

public class ScoreListPanel extends JPanel {
    private JTable table;
    private DefaultTableModel tableModel;
    
    private List<ScoreDTO> scores;
    private List<BonusScoreDTO> bonusScores;
    
    private boolean isShowingBonus = false;

    public ScoreListPanel() {
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        createTable();

        
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));

        add(scrollPane, BorderLayout.CENTER);
    }

    private void createTable() {
        tableModel = new DefaultTableModel(0, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(tableModel);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        UIFactory.applyStandardHeaderStyle(table);
        table.setRowHeight(25);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        table.setDefaultRenderer(Object.class, centerRenderer);
    }

    public void loadData(List<ScoreDTO> list, Map<String, BonusScoreDTO> bonusMap) {
        this.scores = list;
        this.isShowingBonus = false;

        String[] columns = {
            "STT", "CCCD", "SBD", "PT",
            "T_TO", "T_LI", "T_HO", "T_SI", "T_SU", "T_DI", "T_VA",
            "N1", "NL1", "Điểm cộng"
        };
        tableModel.setColumnIdentifiers(columns);

        tableModel.setRowCount(0);

        if (list == null || list.isEmpty()) return;

        int stt = 1;

        for (ScoreDTO score : list) {
            Vector<Object> row = new Vector<>();

            String cccd = score.getCccd() != null ? score.getCccd().trim() : "";

            BonusScoreDTO bonus = bonusMap.get(cccd);

            row.add(stt++);
            row.add(cccd);
            row.add(score.getSobaodanh()); 
            row.add(score.getPhuongThuc()); 

            row.add(formatScore(score.getToan()));
            row.add(formatScore(score.getLy()));
            row.add(formatScore(score.getHoa()));
            row.add(formatScore(score.getSinh()));
            row.add(formatScore(score.getSu()));
            row.add(formatScore(score.getDia()));
            row.add(formatScore(score.getVan()));

            row.add(formatScore(score.getN1CcCalculated()));

            row.add(formatScore(score.getNl1()));

            row.add(bonus != null ? formatScore(bonus.getDiemTong()) : "-");

            tableModel.addRow(row);
        }

        setScoreColumnWidths();
    }

    public ScoreDTO getSelectedScore() {
        int selectedRow = table.getSelectedRow();

        if (!isShowingBonus && selectedRow >= 0 
            && scores != null 
            && selectedRow < scores.size()) {

            return scores.get(selectedRow);
        }

        return null;
    }

    private void setScoreColumnWidths() {
        if (table.getColumnCount() >= 14) {
            table.getColumnModel().getColumn(0).setPreferredWidth(40);   // STT
            table.getColumnModel().getColumn(1).setPreferredWidth(180);  // CCCD
            table.getColumnModel().getColumn(2).setPreferredWidth(180);  // SBD
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
            table.getColumnModel().getColumn(13).setPreferredWidth(80);  // Điểm cộng
        }
    }

    // 2. QUẢN LÝ DỮ LIỆU ĐIỂM ƯU TIÊN (BONUS SCORE)

    public void loadBonusData(List<BonusScoreDTO> bonusScores) {
        this.bonusScores = bonusScores;
        this.isShowingBonus = true;

        // 1. Cài đặt lại tên cột cho bảng Điểm ưu tiên
        String[] columnNames = { "STT", "CCCD", "Điểm CC", "Điểm UTXT", "Tổng điểm cộng" };
        tableModel.setColumnIdentifiers(columnNames);
        setBonusColumnWidths();

        tableModel.setRowCount(0);

        // 2. Đổ dữ liệu
        int stt = 1;
        for (BonusScoreDTO bonus : bonusScores) {
            Object[] rowData = {
                    stt++,
                    bonus.getCccd(),
                    formatScore(bonus.getDiemCc()),
                    formatScore(bonus.getDiemUtxt()),
                    formatScore(bonus.getDiemTong())
            };
            tableModel.addRow(rowData);
        }
    }

    public BonusScoreDTO getSelectedBonusScore() {
        int selectedRow = table.getSelectedRow();
        if (isShowingBonus && selectedRow >= 0 && bonusScores != null && selectedRow < bonusScores.size()) {
            return bonusScores.get(selectedRow);
        }
        return null;
    }

    private void setBonusColumnWidths() {
        if (table.getColumnCount() == 5) {
            table.getColumnModel().getColumn(0).setPreferredWidth(50);   // STT
            table.getColumnModel().getColumn(1).setPreferredWidth(250);  // CCCD
            table.getColumnModel().getColumn(2).setPreferredWidth(150);  // Điểm CC
            table.getColumnModel().getColumn(3).setPreferredWidth(150);  // Điểm UTXT
            table.getColumnModel().getColumn(4).setPreferredWidth(150);  // Tổng
        }
    }

    private String formatScore(Double score) {
        if (score == null) {
            return "-";
        }
        return String.format("%.1f", score);
    }

    public void clearSelection() {
        table.clearSelection();
    }

    public JTable getTable() {
        return table;
    }
}