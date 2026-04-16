package com.example.managementadmissionwf.ui.panel.score;

import com.example.managementadmissionwf.dto.score.ScoreDTO;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import javax.swing.text.NumberFormatter;
import java.awt.*;
import java.text.DecimalFormat;

public class ScoreFormDialog extends JDialog {
    private ScoreDTO score;
    private boolean saved = false;
    
    // Form fields
    private JTextField txtCccd;
    private JTextField txtSobaodanh;
    private JComboBox<String> cboPhuongThuc;
    
    // Điểm thi chính
    private JFormattedTextField txtToan;
    private JFormattedTextField txtLy;
    private JFormattedTextField txtHoa;
    private JFormattedTextField txtSinh;
    private JFormattedTextField txtSu;
    private JFormattedTextField txtDia;
    private JFormattedTextField txtVan;
    
    // Ngoại ngữ & Bài thi khác
    private JFormattedTextField txtN1Thi;
    private JFormattedTextField txtN1Cc;
    private JFormattedTextField txtNl1;
    private JFormattedTextField txtNk1;
    private JFormattedTextField txtNk2;
    
    // Điểm cộng
    private JFormattedTextField txtDiemCc;
    private JFormattedTextField txtDiemUtxt;
    private JFormattedTextField txtDiemTong;
    
    private JTabbedPane tabbedPane;
    private JButton btnSave;
    private JButton btnCancel;
    
    public ScoreFormDialog(Frame parent, String title, ScoreDTO score) {
        super(parent, title, true);
        this.score = score;
        initComponents();
        setupValidation();
        loadScoreData();
        setLocationRelativeTo(parent);
    }
    
    private void initComponents() {
        setSize(650, 450);
        setLayout(new BorderLayout());
        


        txtSobaodanh = new JTextField();
        txtSobaodanh.setPreferredSize(new Dimension(200, 30));
        txtSobaodanh.setEditable(true);
        
        txtCccd = new JTextField();
        txtCccd.setPreferredSize(new Dimension(200, 30));
        
        btnSave = new JButton("Lưu");
        btnSave.setEnabled(false);

        // Create tabbed pane
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        // Tab 1: Điểm thi chính
        JPanel tab1 = createExamScoresPanel();
        tabbedPane.addTab("Điểm thi chính", tab1);
        
        // Tab 2: Ngoại ngữ & Bài thi khác
        JPanel tab2 = createOtherScoresPanel();
        tabbedPane.addTab("Ngoại ngữ & Khác", tab2);
        
        // Add tabbed pane
        add(tabbedPane, BorderLayout.CENTER);
        
        // Button panel
        JPanel buttonPanel = createButtonPanel();
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private void setupValidation() {
        // 1. Validate CCCD: Chỉ số, tối đa 12 ký tự
        ((AbstractDocument) txtCccd.getDocument()).setDocumentFilter(new DocumentFilter() {
            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
                if (text.matches("\\d*") && (fb.getDocument().getLength() + text.length() - length <= 12)) {
                    super.replace(fb, offset, length, text, attrs);
                    checkFormValidity();
                }
            }
        });

        txtSobaodanh.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { checkFormValidity(); }
            public void removeUpdate(DocumentEvent e) { checkFormValidity(); }
            public void changedUpdate(DocumentEvent e) { checkFormValidity(); }
        });
    }

    private void checkFormValidity() {
        boolean isCccdValid = txtCccd.getText().length() == 12;
        boolean isSbdValid = !txtSobaodanh.getText().trim().isEmpty();
        btnSave.setEnabled(isCccdValid && isSbdValid);
    }

    private JPanel createExamScoresPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(Color.WHITE);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Info fields
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(createLabel("CCCD:"), gbc);
        gbc.gridx = 1;
        txtCccd.setEditable(false);
        panel.add(txtCccd, gbc);
        
        gbc.gridx = 2;
        panel.add(createLabel("SBD:"), gbc);
        gbc.gridx = 3;
        txtSobaodanh = createTextField();
        txtSobaodanh.setEditable(true);
        panel.add(txtSobaodanh, gbc);
        
        // Separator
        gbc.gridx = 0; gbc.gridy = 1;
        gbc.gridwidth = 4;
        panel.add(createSeparator(), gbc);
        gbc.gridwidth = 1;
        
        // Scores - 3 columns
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(createLabel("Toán (TO):"), gbc);
        gbc.gridx = 1;
        txtToan = createFormattedTextField();
        panel.add(txtToan, gbc);
        
        gbc.gridx = 2;
        panel.add(createLabel("Lý (LI):"), gbc);
        gbc.gridx = 3;
        txtLy = createFormattedTextField();
        panel.add(txtLy, gbc);
        
        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(createLabel("Hóa (HO):"), gbc);
        gbc.gridx = 1;
        txtHoa = createFormattedTextField();
        panel.add(txtHoa, gbc);
        
        gbc.gridx = 2;
        panel.add(createLabel("Sinh (SI):"), gbc);
        gbc.gridx = 3;
        txtSinh = createFormattedTextField();
        panel.add(txtSinh, gbc);
        
        gbc.gridx = 0; gbc.gridy = 4;
        panel.add(createLabel("Sử (SU):"), gbc);
        gbc.gridx = 1;
        txtSu = createFormattedTextField();
        panel.add(txtSu, gbc);
        
        gbc.gridx = 2;
        panel.add(createLabel("Địa (DI):"), gbc);
        gbc.gridx = 3;
        txtDia = createFormattedTextField();
        panel.add(txtDia, gbc);
        
        gbc.gridx = 0; gbc.gridy = 5;
        panel.add(createLabel("Văn (VA):"), gbc);
        gbc.gridx = 1;
        txtVan = createFormattedTextField();
        panel.add(txtVan, gbc);
        
        return panel;
    }
    
    private JPanel createOtherScoresPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(Color.WHITE);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Phương thức
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(createLabel("Phương thức:"), gbc);
        gbc.gridx = 1;
        cboPhuongThuc = createComboBox(new String[]{"THPT", "DGNL", "VSAT"});
        cboPhuongThuc.setSelectedIndex(0);
        panel.add(cboPhuongThuc, gbc);
        
        // Separator
        gbc.gridx = 0; gbc.gridy = 1;
        gbc.gridwidth = 2;
        panel.add(createSeparator(), gbc);
        gbc.gridwidth = 1;
        
        // Ngoại ngữ
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(createLabel("N1_Thị:"), gbc);
        gbc.gridx = 1;
        txtN1Thi = createFormattedTextField();
        panel.add(txtN1Thi, gbc);
        
        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(createLabel("N1_CC:"), gbc);
        gbc.gridx = 1;
        txtN1Cc = createFormattedTextField();
        txtN1Cc.setToolTipText("N1_CC = max(N1_Thị, N1_CC)");
        panel.add(txtN1Cc, gbc);
        
        // Separator
        gbc.gridx = 0; gbc.gridy = 4;
        gbc.gridwidth = 2;
        panel.add(createSeparator(), gbc);
        gbc.gridwidth = 1;
        
        // Bài thi khác
        gbc.gridx = 0; gbc.gridy = 5;
        panel.add(createLabel("NL1:"), gbc);
        gbc.gridx = 1;
        txtNl1 = createFormattedTextField();
        panel.add(txtNl1, gbc);
        
        gbc.gridx = 0; gbc.gridy = 6;
        panel.add(createLabel("NK1:"), gbc);
        gbc.gridx = 1;
        txtNk1 = createFormattedTextField();
        panel.add(txtNk1, gbc);
        
        gbc.gridx = 0; gbc.gridy = 7;
        panel.add(createLabel("NK2:"), gbc);
        gbc.gridx = 1;
        txtNk2 = createFormattedTextField();
        panel.add(txtNk2, gbc);
        
        return panel;
    }
    
    private JPanel createBonusScoresPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(Color.WHITE);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 8, 15, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(createLabel("Điểm CC:"), gbc);
        gbc.gridx = 1;
        txtDiemCc = createFormattedTextField();
        panel.add(txtDiemCc, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(createLabel("Điểm ƯuTXT:"), gbc);
        gbc.gridx = 1;
        txtDiemUtxt = createFormattedTextField();
        panel.add(txtDiemUtxt, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(createLabel("Tổng điểm cộng:"), gbc);
        gbc.gridx = 1;
        txtDiemTong = createFormattedTextField();
        txtDiemTong.setEditable(false);
        txtDiemTong.setBackground(new Color(230, 230, 230));
        panel.add(txtDiemTong, gbc);
        
        // Auto-calculate
        txtDiemCc.addPropertyChangeListener("value", e -> calculateTotalBonus());
        txtDiemUtxt.addPropertyChangeListener("value", e -> calculateTotalBonus());
        
        return panel;
    }
    
    private void calculateTotalBonus() {
        double cc = getDoubleValue(txtDiemCc);
        double utxt = getDoubleValue(txtDiemUtxt);
        double total = cc + utxt;
        
        try {
            txtDiemTong.setValue(total);
        } catch (Exception e) {
            // Ignore
        }
    }
    
    private double getDoubleValue(JFormattedTextField field) {
        try {
            Object value = field.getValue();
            if (value instanceof Number) {
                return ((Number) value).doubleValue();
            }
            String text = field.getText().trim();
            if (text.isEmpty()) {
                return 0.0;
            }
            return Double.parseDouble(text);
        } catch (Exception e) {
            return 0.0;
        }
    }
    
    private void loadScoreData() {
        if (score == null) {
            txtCccd.setText("");
            txtSobaodanh.setText("");

            if (cboPhuongThuc.getItemCount() > 0) {
                cboPhuongThuc.setSelectedIndex(0);
            }

            resetAllScoreFields();
            return;
        }

        txtCccd.setText(safeString(score.getCccd()));
        txtSobaodanh.setText(safeString(score.getSobaodanh()));

        String phuongThuc = score.getPhuongThuc();
        if (phuongThuc != null && containsItem(cboPhuongThuc, phuongThuc)) {
            cboPhuongThuc.setSelectedItem(phuongThuc);
        } else {
            if (cboPhuongThuc.getItemCount() > 0) {
                cboPhuongThuc.setSelectedIndex(0);
            }
        }
        setFieldValue(txtToan, score.getToan());
        setFieldValue(txtLy, score.getLy());
        setFieldValue(txtHoa, score.getHoa());
        setFieldValue(txtSinh, score.getSinh());
        setFieldValue(txtSu, score.getSu());
        setFieldValue(txtDia, score.getDia());
        setFieldValue(txtVan, score.getVan());

        setFieldValue(txtN1Thi, score.getN1Thi());
        setFieldValue(txtN1Cc, score.getN1Cc());
        setFieldValue(txtNl1, score.getNl1());
        setFieldValue(txtNk1, score.getNk1());
        setFieldValue(txtNk2, score.getNk2());
    }
    private String safeString(String value) {
        return value != null ? value : "";
    }
    private boolean containsItem(JComboBox<String> comboBox, String value) {
        for (int i = 0; i < comboBox.getItemCount(); i++) {
            if (comboBox.getItemAt(i).equals(value)) {
                return true;
            }
        }
        return false;
    }
    private void resetAllScoreFields() {
        setFieldValue(txtToan, null);
        setFieldValue(txtLy, null);
        setFieldValue(txtHoa, null);
        setFieldValue(txtSinh, null);
        setFieldValue(txtSu, null);
        setFieldValue(txtDia, null);
        setFieldValue(txtVan, null);

        setFieldValue(txtN1Thi, null);
        setFieldValue(txtN1Cc, null);
        setFieldValue(txtNl1, null);
        setFieldValue(txtNk1, null);
        setFieldValue(txtNk2, null);
    }
    private void setFieldValue(JFormattedTextField field, Double value) {
        if (value != null) {
            try {
                field.setValue(value);
            } catch (Exception e) {
                // Ignore
            }
        }
    }
    
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        panel.setBackground(new Color(240, 240, 240));
        
        btnSave = createButton("Lưu", new Color(46, 204, 113));
        btnCancel = createButton("Hủy", new Color(231, 76, 60));
        
        btnSave.addActionListener(e -> saveData());
        btnCancel.addActionListener(e -> dispose());
        
        panel.add(btnSave);
        panel.add(btnCancel);
        
        return panel;
    }
    
    private void saveData() {
        if (!validateForm()) {
            return;
        }
        
        try {
            if (score == null) {
                score = new ScoreDTO();
            }
            
            score.setCccd(txtCccd.getText().trim());
            score.setSobaodanh(txtSobaodanh.getText().trim());
            score.setPhuongThuc((String) cboPhuongThuc.getSelectedItem());
            
            score.setToan(getDoubleValue(txtToan));
            score.setLy(getDoubleValue(txtLy));
            score.setHoa(getDoubleValue(txtHoa));
            score.setSinh(getDoubleValue(txtSinh));
            score.setSu(getDoubleValue(txtSu));
            score.setDia(getDoubleValue(txtDia));
            score.setVan(getDoubleValue(txtVan));
            
            score.setN1Thi(getDoubleValue(txtN1Thi));
            score.setN1Cc(getDoubleValue(txtN1Cc));
            score.setNl1(getDoubleValue(txtNl1));
            score.setNk1(getDoubleValue(txtNk1));
            score.setNk2(getDoubleValue(txtNk2));
            
            saved = true;
            dispose();
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private boolean validateForm() {
        String cccd = txtCccd.getText().trim();
        if (cccd.isEmpty() || !cccd.matches("^\\d{12}$")) {
            JOptionPane.showMessageDialog(this, "CCCD phải bao gồm đúng 12 chữ số!", "Lỗi xác thực", JOptionPane.ERROR_MESSAGE);
            txtCccd.requestFocus();
            return false;
        }

        if (txtSobaodanh.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Số báo danh không được để trống!", "Lỗi xác thực", JOptionPane.ERROR_MESSAGE);
            txtSobaodanh.requestFocus();
            return false;
        }
        double toan = getDoubleValue(txtToan);
        double ly = getDoubleValue(txtLy);
        if (toan > 10.0 || ly > 10.0 /* || thêm các môn khác */) {
            JOptionPane.showMessageDialog(this, "Điểm thi THPT không được vượt quá 10!", "Lỗi xác thực", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        return true;
    }
    
    public ScoreDTO getScore() {
        return score;
    }
    
    public boolean isSaved() {
        return saved;
    }
    
    // Helper methods
    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        return label;
    }
    
    private JTextField createTextField() {
        JTextField field = new JTextField();
        field.setPreferredSize(new Dimension(150, 30));
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        return field;
    }
    
    private JFormattedTextField createFormattedTextField() {
        DecimalFormat format = new DecimalFormat("#0.0#");
        NumberFormatter formatter = new NumberFormatter(format);
        formatter.setValueClass(Double.class);
        formatter.setAllowsInvalid(false);
        formatter.setMinimum(0.0);
        
        JFormattedTextField field = new JFormattedTextField(formatter);
        field.setPreferredSize(new Dimension(150, 30));
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setValue(0.0);
        return field;
    }
    
    private JComboBox<String> createComboBox(String[] items) {
        JComboBox<String> comboBox = new JComboBox<>(items);
        comboBox.setPreferredSize(new Dimension(150, 30));
        comboBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        return comboBox;
    }
    
    private JSeparator createSeparator() {
        JSeparator separator = new JSeparator();
        return separator;
    }
    
    private JButton createButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(100, 35));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }
}