package com.example.managementadmissionwf.ui.panel.candidate;

import com.example.managementadmissionwf.dal.entity.XtDiemcongxettuyen;
import com.example.managementadmissionwf.dal.entity.XtDiemthixettuyen;
import com.example.managementadmissionwf.dal.entity.XtNguyenvongxettuyen;
import com.example.managementadmissionwf.dto.candidate.CandidateDTO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

public class CandidateDetailDialog extends JDialog {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final CandidateDTO candidate;
    private final List<XtDiemthixettuyen> scores;
    private final XtDiemcongxettuyen bonusScore;
    private final List<XtNguyenvongxettuyen> aspirations;
    private final Map<String, String> majorNames;

    public CandidateDetailDialog(Frame parent,
                                 CandidateDTO candidate,
                                 List<XtDiemthixettuyen> scores,
                                 XtDiemcongxettuyen bonusScore,
                                 List<XtNguyenvongxettuyen> aspirations,
                                 Map<String, String> majorNames) {
        super(parent, "Chi tiết thí sinh - " + candidate.getCccd(), true);
        this.candidate = candidate;
        this.scores = scores != null ? scores : List.of();
        this.bonusScore = bonusScore;
        this.aspirations = aspirations != null ? aspirations : List.of();
        this.majorNames = majorNames != null ? majorNames : Map.of();
        initComponents();
        setLocationRelativeTo(parent);
    }

    private void initComponents() {
        setSize(980, 680);
        setLayout(new BorderLayout(10, 10));
        ((JPanel) getContentPane()).setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Thông tin", createInfoPanel());
        tabs.addTab("Điểm thi", createScoresPanel());
        tabs.addTab("Điểm cộng", createBonusPanel());
        tabs.addTab("Nguyện vọng", createAspirationsPanel());

        JButton closeButton = new JButton("Đóng");
        closeButton.addActionListener(e -> dispose());
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actionPanel.add(closeButton);

        add(tabs, BorderLayout.CENTER);
        add(actionPanel, BorderLayout.SOUTH);
    }

    private JPanel createInfoPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 8, 6, 8);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;

        addInfoRow(panel, gbc, 0, "CCCD", candidate.getCccd());
        addInfoRow(panel, gbc, 1, "Số báo danh", candidate.getSobaodanh());
        addInfoRow(panel, gbc, 2, "Họ tên", candidate.getHoTen());
        addInfoRow(panel, gbc, 3, "Ngày sinh",
                candidate.getNgaySinh() != null ? candidate.getNgaySinh().format(DATE_FORMATTER) : "-");
        addInfoRow(panel, gbc, 4, "Điện thoại", candidate.getDienThoai());
        addInfoRow(panel, gbc, 5, "Email", candidate.getEmail());
        addInfoRow(panel, gbc, 6, "Đối tượng", candidate.getDoiTuong());
        addInfoRow(panel, gbc, 7, "Khu vực", candidate.getKhuVuc());

        gbc.gridy = 8;
        gbc.weighty = 1;
        panel.add(Box.createVerticalGlue(), gbc);
        return panel;
    }

    private void addInfoRow(JPanel panel, GridBagConstraints gbc, int row, String label, String value) {
        gbc.gridy = row;
        gbc.gridx = 0;
        gbc.weightx = 0;
        JLabel labelComponent = new JLabel(label + ":");
        labelComponent.setFont(labelComponent.getFont().deriveFont(Font.BOLD));
        panel.add(labelComponent, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        panel.add(new JLabel(value != null && !value.isBlank() ? value : "-"), gbc);
    }

    private JComponent createScoresPanel() {
        String[] columns = {
                "Phương thức", "Toán", "Lý", "Hóa", "Sinh", "Sử", "Địa", "Văn",
                "N1 Thi", "N1 CC", "NL1", "NK1", "NK2"
        };
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        for (XtDiemthixettuyen score : scores) {
            model.addRow(new Object[]{
                    valueOrDash(score.getDPhuongthuc()),
                    formatScore(score.getTo()),
                    formatScore(score.getLi()),
                    formatScore(score.getHo()),
                    formatScore(score.getSi()),
                    formatScore(score.getSu()),
                    formatScore(score.getDi()),
                    formatScore(score.getVa()),
                    formatScore(score.getN1Thi()),
                    formatScore(score.getN1Cc()),
                    formatScore(score.getNl1()),
                    formatScore(score.getNk1()),
                    formatScore(score.getNk2())
            });
        }

        JTable table = new JTable(model);
        table.setRowHeight(28);
        return new JScrollPane(table);
    }

    private JPanel createBonusPanel() {
        JPanel panel = new JPanel(new GridLayout(0, 2, 12, 12));
        panel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        addMetric(panel, "Điểm chứng chỉ", bonusScore != null ? formatScore(bonusScore.getDiemCc()) : "-");
        addMetric(panel, "Điểm ưu tiên xét tuyển", bonusScore != null ? formatScore(bonusScore.getDiemUtxt()) : "-");
        addMetric(panel, "Tổng điểm cộng", bonusScore != null ? formatScore(bonusScore.getDiemTong()) : "-");
        return panel;
    }

    private void addMetric(JPanel panel, String label, String value) {
        JPanel box = new JPanel(new BorderLayout(4, 4));
        box.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220)),
                BorderFactory.createEmptyBorder(12, 12, 12, 12)));
        JLabel title = new JLabel(label);
        JLabel number = new JLabel(value);
        number.setFont(number.getFont().deriveFont(Font.BOLD, 22f));
        box.add(title, BorderLayout.NORTH);
        box.add(number, BorderLayout.CENTER);
        panel.add(box);
    }

    private JComponent createAspirationsPanel() {
        String[] columns = {
                "NV", "Mã ngành", "Tên ngành", "Phương thức", "Tổ hợp",
                "Điểm nền", "Điểm ƯT", "Điểm cộng", "Điểm XT", "Kết quả"
        };
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        for (XtNguyenvongxettuyen aspiration : aspirations) {
            model.addRow(new Object[]{
                    aspiration.getNvTt(),
                    valueOrDash(aspiration.getNvManganh()),
                    majorNames.getOrDefault(aspiration.getNvManganh(), aspiration.getNvManganh()),
                    valueOrDash(aspiration.getTtPhuongthuc()),
                    valueOrDash(aspiration.getTtThm()),
                    formatScore(aspiration.getDiemThxt()),
                    formatScore(aspiration.getDiemUtqd()),
                    formatScore(aspiration.getDiemCong()),
                    formatScore(aspiration.getDiemXettuyen()),
                    valueOrDash(aspiration.getNvKetqua())
            });
        }

        JTable table = new JTable(model);
        table.setRowHeight(28);
        return new JScrollPane(table);
    }

    private String valueOrDash(String value) {
        return value != null && !value.isBlank() ? value : "-";
    }

    private String formatScore(Double value) {
        return value != null ? String.format("%.2f", value) : "-";
    }
}
