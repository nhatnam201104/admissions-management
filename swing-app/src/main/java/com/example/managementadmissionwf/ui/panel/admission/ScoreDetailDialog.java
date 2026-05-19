package com.example.managementadmissionwf.ui.panel.admission;

import com.example.managementadmissionwf.dto.CalculationStep;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.List;
import java.util.Map;

/**
 * Dialog hiển thị chi tiết điểm của một thí sinh
 * Dùng để đối chiếu so sánh điểm với điểm chuẩn ngành
 */
public class ScoreDetailDialog extends JDialog {

    // Colors for styling
    private static final Color COLOR_GREEN = new Color(46, 204, 113);
    private static final Color COLOR_DARK = new Color(44, 62, 80);
    private static final Color COLOR_GRAY = new Color(100, 100, 100);
    private static final Color COLOR_LIGHT_GRAY = new Color(245, 245, 245);
    private static final Color COLOR_FINAL_GREEN = new Color(232, 245, 233);
    private static final Color COLOR_WARNING = new Color(255, 193, 7);
    private static final Color COLOR_WHITE = Color.WHITE;

    public ScoreDetailDialog(Frame parent, Map<String, Object> scoreDetails) {
        super(parent, "Chi tiết điểm xét tuyển", true);
        setSize(700, 750);
        setLocationRelativeTo(parent);
        setResizable(true);
        
        initComponents(scoreDetails);
    }

    private void initComponents(Map<String, Object> data) {
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(new EmptyBorder(10, 15, 10, 15));
        content.setBackground(Color.WHITE);

        String phuongThuc = data.getOrDefault("phuongThuc", "THPT").toString();
        Boolean isIncomplete = (Boolean) data.getOrDefault("isIncomplete", false);

        // 1. Thông tin thí sinh
        content.add(createSection("Thông tin thí sinh", createCandidateInfo(data)));
        
        // 2. Tổ hợp xét tuyển - CHỈ hiển thị cho THPT và VSAT
        if (!"DGNL".equals(phuongThuc)) {
            content.add(Box.createVerticalStrut(6));
            content.add(createSection("Tổ hợp xét tuyển", createSubjectGroupInfo(data)));
        }
        
        // 3. Điểm thi
        content.add(Box.createVerticalStrut(6));
        content.add(createSection("Điểm thi", createExamScoreInfo(data)));
        
        // 4. Điểm cộng / ưu tiên
        content.add(Box.createVerticalStrut(6));
        content.add(createSection("Điểm cộng / Ưu tiên", createBonusScoreInfo(data)));
        
        // 5. Tổng điểm xét tuyển
        content.add(Box.createVerticalStrut(6));
        content.add(createTotalScoreSection(data));
        
        // 6. Điểm chuẩn ngành
        content.add(Box.createVerticalStrut(6));
        content.add(createMajorStandardSection(data));
        
        // 7. Kết quả so sánh
        content.add(Box.createVerticalStrut(10));
        content.add(createResultSection(data));
        
        // 8. Incomplete data warning
        if (Boolean.TRUE.equals(isIncomplete)) {
            content.add(Box.createVerticalStrut(6));
            content.add(createIncompleteWarning());
        }
        
        // 9. Công thức tính điểm chi tiết - TABLE LAYOUT
        content.add(Box.createVerticalStrut(10));
        content.add(createFormulaTableSection(data));

        // Scroll pane cho nội dung dài
        JScrollPane scrollPane = new JScrollPane(content);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBackground(Color.WHITE);
        
        add(scrollPane, BorderLayout.CENTER);
        
        // Button đóng
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(Color.WHITE);
        JButton btnClose = new JButton("Đóng");
        btnClose.setPreferredSize(new Dimension(100, 32));
        btnClose.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnClose.addActionListener(e -> dispose());
        buttonPanel.add(btnClose);
        
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private JPanel createSection(String title, JPanel content) {
        JPanel section = new JPanel();
        section.setLayout(new BoxLayout(section, BoxLayout.Y_AXIS));
        section.setBackground(Color.WHITE);
        
        TitledBorder border = BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            title,
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 11),
            new Color(44, 62, 80)
        );
        section.setBorder(BorderFactory.createCompoundBorder(
            new EmptyBorder(3, 3, 3, 3),
            border
        ));
        
        content.setBackground(Color.WHITE);
        section.add(content);
        
        return section;
    }

    private JPanel createCandidateInfo(Map<String, Object> data) {
        JPanel panel = new JPanel(new GridLayout(2, 3, 5, 3));
        panel.setBackground(Color.WHITE);
        
        String hoTen = data.getOrDefault("hoTen", "N/A").toString();
        String ngaySinh = data.getOrDefault("ngaySinh", "N/A").toString();
        String tenNganh = data.getOrDefault("tenNganh", "N/A").toString();
        
        panel.add(createLabel("Họ tên:", true));
        panel.add(createLabel(hoTen, false));
        panel.add(createLabel("Ngày sinh:", true));
        panel.add(createLabel(ngaySinh, false));
        panel.add(createLabel("Ngành:", true));
        panel.add(createLabel(tenNganh, false));
        
        return panel;
    }

    private JPanel createSubjectGroupInfo(Map<String, Object> data) {
        JPanel panel = new JPanel(new GridLayout(2, 4, 4, 3));
        panel.setBackground(Color.WHITE);
        
        String tohop = data.getOrDefault("tohop", "N/A").toString();
        String mon1 = data.getOrDefault("mon1", "-").toString();
        String mon2 = data.getOrDefault("mon2", "-").toString();
        String mon3 = data.getOrDefault("mon3", "-").toString();
        Double hs1 = (Double) data.getOrDefault("hs1", 1.0);
        Double hs2 = (Double) data.getOrDefault("hs2", 1.0);
        Double hs3 = (Double) data.getOrDefault("hs3", 1.0);
        
        panel.add(createLabel("Tổ hợp:", true));
        panel.add(createLabel(tohop, false));
        panel.add(createLabel("Hệ số:", true));
        panel.add(createLabel(String.format("%.1f-%.1f-%.1f", hs1, hs2, hs3), false));
        panel.add(createLabel("Môn 1:", true));
        panel.add(createLabel(mon1, false));
        panel.add(createLabel("Môn 2:", true));
        panel.add(createLabel(mon2, false));
        panel.add(createLabel("Môn 3:", true));
        panel.add(createLabel(mon3, false));
        panel.add(createLabel("", true));
        panel.add(createLabel("", false));
        
        return panel;
    }

    private JPanel createExamScoreInfo(Map<String, Object> data) {
        String phuongThuc = data.getOrDefault("phuongThuc", "THPT").toString();
        
        switch (phuongThuc) {
            case "DGNL" -> {
                return createDGNLScorePanel(data);
            }
            case "VSAT" -> {
                return createVSATScorePanel(data);
            }
            default -> { // THPT
                return createTHPTScorePanel(data);
            }
        }
    }
    
    private JPanel createTHPTScorePanel(Map<String, Object> data) {
        JPanel panel = new JPanel(new GridLayout(3, 4, 4, 3));
        panel.setBackground(Color.WHITE);
        
        Double to = getScoreValue(data.get("to"));
        Double li = getScoreValue(data.get("li"));
        Double ho = getScoreValue(data.get("ho"));
        Double si = getScoreValue(data.get("si"));
        Double su = getScoreValue(data.get("su"));
        Double di = getScoreValue(data.get("di"));
        Double va = getScoreValue(data.get("va"));
        Double n1 = getScoreValue(data.get("n1"));
        
        panel.add(createLabel("Toán:", true));
        panel.add(createLabel(formatScore(to), false));
        panel.add(createLabel("Lý:", true));
        panel.add(createLabel(formatScore(li), false));
        panel.add(createLabel("Hóa:", true));
        panel.add(createLabel(formatScore(ho), false));
        panel.add(createLabel("Sinh:", true));
        panel.add(createLabel(formatScore(si), false));
        panel.add(createLabel("Sử:", true));
        panel.add(createLabel(formatScore(su), false));
        panel.add(createLabel("Địa:", true));
        panel.add(createLabel(formatScore(di), false));
        panel.add(createLabel("Văn:", true));
        panel.add(createLabel(formatScore(va), false));
        panel.add(createLabel("Ngoại ngữ:", true));
        panel.add(createLabel(formatScore(n1), false));
        
        return panel;
    }
    
    private JPanel createDGNLScorePanel(Map<String, Object> data) {
        JPanel panel = new JPanel(new GridLayout(1, 4, 4, 3));
        panel.setBackground(Color.WHITE);
        
        Double nl1 = getScoreValue(data.get("nl1"));
        
        panel.add(createLabel("ĐGNL (thang 1200):", true));
        panel.add(createLabel(formatScore(nl1), false));
        panel.add(createLabel("", true));
        panel.add(createLabel("", false));
        
        return panel;
    }
    
    private JPanel createVSATScorePanel(Map<String, Object> data) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        
        String m1 = data.getOrDefault("mon1", "-").toString();
        String m2 = data.getOrDefault("mon2", "-").toString();
        String m3 = data.getOrDefault("mon3", "-").toString();
        String m1Label = getSubjectLabel(m1);
        String m2Label = getSubjectLabel(m2);
        String m3Label = getSubjectLabel(m3);
        
        Double d1 = getScoreValue(data.get("vsatMon1Score"));
        Double d2 = getScoreValue(data.get("vsatMon2Score"));
        Double d3 = getScoreValue(data.get("vsatMon3Score"));
        Double tongDiem = getScoreValue(data.get("vsatTongDiem3Mon"));
        
        JPanel row1 = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 2));
        row1.setBackground(Color.WHITE);
        row1.add(createLabel(m1Label + ":", true));
        row1.add(createLabel(formatScore(d1), false));
        panel.add(row1);
        
        JPanel row2 = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 2));
        row2.setBackground(Color.WHITE);
        row2.add(createLabel(m2Label + ":", true));
        row2.add(createLabel(formatScore(d2), false));
        panel.add(row2);
        
        JPanel row3 = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 2));
        row3.setBackground(Color.WHITE);
        row3.add(createLabel(m3Label + ":", true));
        row3.add(createLabel(formatScore(d3), false));
        panel.add(row3);
        
        panel.add(Box.createVerticalStrut(3));
        
        JPanel rowSum = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 2));
        rowSum.setBackground(Color.WHITE);
        JLabel sumLabel = new JLabel("Tổng 3 môn: " + formatScore(tongDiem));
        sumLabel.setFont(new Font("Segoe UI", Font.BOLD, 11));
        sumLabel.setForeground(COLOR_DARK);
        rowSum.add(sumLabel);
        panel.add(rowSum);
        
        return panel;
    }
    
    private String getSubjectLabel(String monCode) {
        if (monCode == null) return "-";
        return switch (monCode.toUpperCase()) {
            case "TO" -> "Toán";
            case "LI" -> "Vật Lý";
            case "HO" -> "Hóa Học";
            case "SI" -> "Sinh Học";
            case "SU" -> "Lịch Sử";
            case "DI" -> "Địa Lý";
            case "VA" -> "Ngữ Văn";
            case "AN" -> "Tiếng Anh";
            default -> monCode;
        };
    }

    private JPanel createBonusScoreInfo(Map<String, Object> data) {
        String phuongThuc = data.getOrDefault("phuongThuc", "THPT").toString();
        
        Double diemCong = getScoreValue(data.get("diemCong"));
        Double diemUtqd = getScoreValue(data.get("diemUtqd"));
        
        JPanel panel = new JPanel(new GridLayout(1, 4, 5, 3));
        panel.setBackground(Color.WHITE);
        
        switch (phuongThuc) {
            case "DGNL" -> {
                panel.add(createLabel("Điểm ưu tiên:", true));
                panel.add(createLabel(formatScore(diemUtqd), false));
                panel.add(createLabel("Điểm cộng khác:", true));
                panel.add(createLabel(formatScore(diemCong), false));
            }
            case "VSAT" -> {
                panel.add(createLabel("Điểm ưu tiên VSAT:", true));
                panel.add(createLabel(formatScore(diemUtqd), false));
                panel.add(createLabel("Điểm cộng:", true));
                panel.add(createLabel(formatScore(diemCong), false));
            }
            default -> {
                panel.add(createLabel("ĐTổ hợp:", true));
                panel.add(createLabel(formatScore(getScoreValue(data.get("diemThxt"))), false));
                panel.add(createLabel("Đ.cộng:", true));
                panel.add(createLabel(formatScore(diemCong), false));
            }
        }
        
        return panel;
    }

    private JPanel createTotalScoreSection(Map<String, Object> data) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panel.setBackground(Color.WHITE);
        
        Double diemXettuyen = getScoreValue(data.get("diemXettuyen"));
        
        JLabel lblTitle = new JLabel("ĐIỂM XÉT TUYỂN:");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTitle.setForeground(COLOR_DARK);
        
        JLabel lblScore = new JLabel(String.format("%.2f", diemXettuyen));
        lblScore.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblScore.setForeground(COLOR_GREEN);
        
        panel.add(lblTitle);
        panel.add(Box.createHorizontalStrut(10));
        panel.add(lblScore);
        
        return panel;
    }

    private JPanel createMajorStandardSection(Map<String, Object> data) {
        JPanel panel = new JPanel(new GridLayout(1, 4, 5, 3));
        panel.setBackground(Color.WHITE);
        
        Double diemChuan = getScoreValue(data.get("diemChuan"));
        Integer chitieu = (Integer) data.getOrDefault("chiTieu", 0);
        
        panel.add(createLabel("Điểm chuẩn:", true));
        panel.add(createLabel(diemChuan != null ? String.format("%.2f", diemChuan) : "Chưa có", false));
        panel.add(createLabel("Chỉ tiêu:", true));
        panel.add(createLabel(String.valueOf(chitieu), false));
        
        return panel;
    }

    private JPanel createResultSection(Map<String, Object> data) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            new EmptyBorder(8, 8, 8, 8)
        ));
        
        // Get actual result from database (ketQua)
        String ketQua = data.getOrDefault("ketQua", "").toString();
        String lyDo = data.getOrDefault("lyDo", "").toString();
        Double diemXettuyen = getScoreValue(data.get("diemXettuyen"));
        Double diemChuan = getScoreValue(data.get("diemChuan"));
        
        JLabel lblResult = new JLabel();
        lblResult.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblResult.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Use ketQua from database, not just score comparison
        if ("TRUNG_TUYEN".equals(ketQua)) {
            lblResult.setText("TRÚNG TUYỂN (Điểm: " + String.format("%.2f", diemXettuyen) + ")");
            lblResult.setForeground(new Color(21, 87, 36));
            panel.setBackground(new Color(232, 245, 233));
        } else if ("TRUOT".equals(ketQua)) {
            // User failed - show the actual reason based on score vs benchmark
            String reason;
            if (diemChuan != null && diemXettuyen >= diemChuan) {
                // Score >= benchmark but failed → quota full
                reason = lyDo.isEmpty() ? "Ngành đã đủ chỉ tiêu" : lyDo;
            } else {
                // Score < benchmark → did not meet benchmark
                reason = lyDo.isEmpty() ? "Không đạt điểm chuẩn" : lyDo;
            }
            lblResult.setText(reason);
            lblResult.setForeground(new Color(114, 28, 36));
            panel.setBackground(new Color(255, 235, 238));
        } else if ("CHO_XET".equals(ketQua)) {
            lblResult.setText("ĐANG CHỜ XÉT TUYỂN");
            lblResult.setForeground(new Color(33, 33, 33));
        } else {
            // Fallback to score comparison if ketQua is not set
            if (diemChuan == null) {
                lblResult.setText("CHƯA CÓ ĐIỂM CHUẨN");
                lblResult.setForeground(new Color(133, 100, 4));
            } else if (diemXettuyen >= diemChuan) {
                lblResult.setText("ĐẠT (Điểm: " + String.format("%.2f", diemXettuyen) + " >= " + String.format("%.2f", diemChuan) + ")");
                lblResult.setForeground(new Color(21, 87, 36));
            } else {
                lblResult.setText("KHÔNG ĐẠT (Thiếu: " + String.format("%.2f", diemChuan - diemXettuyen) + " điểm)");
                lblResult.setForeground(new Color(114, 28, 36));
            }
        }
        
        lblResult.setBorder(new EmptyBorder(5, 0, 5, 0));
        panel.add(lblResult);
        
        return panel;
    }
    
    private JPanel createIncompleteWarning() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBackground(COLOR_WARNING);
        panel.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
        
        JLabel warning = new JLabel("[!] DU LIEU CHUA DU - Mot so diem thi bi thieu");
        warning.setFont(new Font("Segoe UI", Font.BOLD, 12));
        warning.setForeground(new Color(33, 33, 33));
        
        panel.add(warning);
        
        return panel;
    }

    private JPanel createFormulaTableSection(Map<String, Object> data) {
        JPanel section = new JPanel();
        section.setLayout(new BoxLayout(section, BoxLayout.Y_AXIS));
        section.setBackground(Color.WHITE);
        
        TitledBorder border = BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(52, 73, 94)),
            "Chi tiết công thức tính điểm",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 11),
            new Color(52, 73, 94)
        );
        section.setBorder(BorderFactory.createCompoundBorder(
            new EmptyBorder(5, 5, 5, 5),
            border
        ));
        
        // Get structured steps from data
        @SuppressWarnings("unchecked")
        List<CalculationStep> steps = (List<CalculationStep>) data.get("calculationSteps");
        
        if (steps != null && !steps.isEmpty()) {
            // Create table model
            String[] columns = {"Bước", "Mô tả", "Công thức / Tính toán", "Kết quả"};
            Object[][] rowData = new Object[steps.size()][4];
            
            for (int i = 0; i < steps.size(); i++) {
                CalculationStep step = steps.get(i);
                rowData[i][0] = step.stepNumber();
                rowData[i][1] = step.description();
                rowData[i][2] = step.formula();
                rowData[i][3] = String.format("%.2f", step.result());
            }
            
            JTable table = new JTable(rowData, columns) {
                @Override
                public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                    Component c = super.prepareRenderer(renderer, row, column);
                    
                    // Get the step for this row
                    CalculationStep step = steps.get(row);
                    
                    // Set background based on step type
                    if (step.isFinal()) {
                        c.setBackground(COLOR_FINAL_GREEN);
                    } else {
                        c.setBackground(COLOR_WHITE);
                    }
                    
                    // Set font based on step type
                    if (c instanceof JLabel) {
                        JLabel label = (JLabel) c;
                        if (step.isFinal()) {
                            label.setFont(new Font("Segoe UI", Font.BOLD, 11));
                        } else {
                            label.setFont(new Font("Segoe UI", Font.PLAIN, 11));
                        }
                        
                        // Color for result column
                        if (column == 3) {
                            if (step.isFinal()) {
                                label.setForeground(COLOR_GREEN);
                                label.setFont(new Font("Segoe UI", Font.BOLD, 14));
                            } else {
                                label.setForeground(COLOR_DARK);
                            }
                        } else if (column == 1) {
                            label.setForeground(COLOR_GRAY);
                        }
                        
                        // Add info icon for cap applied step
                        if (step.isCapApplied() && column == 2) {
                            label.setText(step.formula() + "  (i)");
                        }
                    }
                    
                    return c;
                }
            };
            
            // Configure table appearance
            table.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            table.setRowHeight(28);
            table.setGridColor(new Color(200, 200, 200));
            table.setBackground(COLOR_WHITE);
            table.setBorder(BorderFactory.createEmptyBorder());
            table.setIntercellSpacing(new Dimension(5, 2));
            table.setFillsViewportHeight(true);
            table.setEnabled(false);
            
            // Column widths
            table.getColumnModel().getColumn(0).setPreferredWidth(50);
            table.getColumnModel().getColumn(1).setPreferredWidth(120);
            table.getColumnModel().getColumn(2).setPreferredWidth(300);
            table.getColumnModel().getColumn(3).setPreferredWidth(80);
            
            // Header styling
            JTableHeader header = table.getTableHeader();
            header.setFont(new Font("Segoe UI", Font.BOLD, 11));
            header.setBackground(new Color(52, 73, 94));
            header.setForeground(COLOR_WHITE);
            header.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));
            
            // Create tooltip for cap applied
            table.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
                @Override
                public void mouseMoved(java.awt.event.MouseEvent e) {
                    int row = table.rowAtPoint(e.getPoint());
                    int col = table.columnAtPoint(e.getPoint());
                    
                    if (row >= 0 && row < steps.size()) {
                        CalculationStep step = steps.get(row);
                        if (step.isCapApplied() && col == 2) {
                            table.setToolTipText("Uu tien bi giam vi tong (DTHGXT + DC) >= 22.5 diem");
                        } else if (step.isIncomplete()) {
                            table.setToolTipText("[!] Du lieu thieu - diem mac dinh la 0.00");
                        } else {
                            table.setToolTipText(null);
                        }
                    }
                }
            });
            
            // Add table to scroll pane
            JScrollPane tableScroll = new JScrollPane(table);
            tableScroll.setPreferredSize(new Dimension(550, 160));
            tableScroll.setMaximumSize(new Dimension(550, 160));
            tableScroll.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
            tableScroll.setBackground(COLOR_WHITE);
            tableScroll.getViewport().setBackground(COLOR_WHITE);
            
            section.add(tableScroll);
        } else {
            // Fallback: show simple summary
            JPanel summaryPanel = new JPanel();
            summaryPanel.setLayout(new BoxLayout(summaryPanel, BoxLayout.Y_AXIS));
            summaryPanel.setBackground(COLOR_LIGHT_GRAY);
            
            Double dthxt = getScoreValue(data.get("dthxt"));
            Double dthgxt = getScoreValue(data.get("dthgxt"));
            Double dut = getScoreValue(data.get("dut"));
            
            summaryPanel.add(createFormulaRow("ĐTHXT (Bước 2):", String.format("%.2f / 30", dthxt)));
            summaryPanel.add(createFormulaRow("ĐTHGXT (Bước 3):", String.format("%.2f / 30", dthgxt)));
            summaryPanel.add(createFormulaRow("ĐƯT (Bước 4):", String.format("%.2f / 30", dut)));
            
            section.add(summaryPanel);
        }
        
        return section;
    }
    
    private JLabel createLabel(String text, boolean isLabel) {
        JLabel label = new JLabel(text);
        if (isLabel) {
            label.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            label.setForeground(COLOR_GRAY);
        } else {
            label.setFont(new Font("Segoe UI", Font.BOLD, 11));
            label.setForeground(COLOR_DARK);
        }
        return label;
    }

    private JPanel createFormulaRow(String label, String value) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 3));
        row.setBackground(COLOR_LIGHT_GRAY);
        
        JLabel lblLabel = new JLabel(label);
        lblLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblLabel.setForeground(COLOR_GRAY);
        
        JLabel lblValue = new JLabel(value);
        lblValue.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblValue.setForeground(COLOR_GREEN);
        
        row.add(lblLabel);
        row.add(lblValue);
        
        return row;
    }

    private String formatScore(Double score) {
        return score != null ? String.format("%.2f", score) : "0.00";
    }
    
    private Double getScoreValue(Object value) {
        if (value == null) return 0.0;
        if (value instanceof Double) return (Double) value;
        if (value instanceof Integer) return ((Integer) value).doubleValue();
        return 0.0;
    }
}