package com.example.managementadmissionwf.ui.panel.score;

import com.example.managementadmissionwf.dto.score.BonusScoreDTO;
import com.example.managementadmissionwf.dto.score.ScoreDTO;

import javax.swing.*;
import javax.swing.text.NumberFormatter;
import java.awt.*;
import java.text.DecimalFormat;

public class BonusScoreFormDialog extends JDialog {
    private BonusScoreDTO bonusScore;
    private boolean saved = false;

    private JTextField txtCccd;
    private JFormattedTextField txtDiemCc; 
    private JFormattedTextField txtDiemUtxt; 
    private JFormattedTextField txtDiemTong; 

    private JButton btnSave;
    private JButton btnCancel;

    public BonusScoreFormDialog(Frame parent, String title, BonusScoreDTO bonusScore) {
        super(parent, title, true);
        this.bonusScore = bonusScore;
        initComponents();
        loadBonusData();
        setLocationRelativeTo(parent);
    }

    private void initComponents() {
        setSize(450, 380);
        setLayout(new BorderLayout());
        
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        mainPanel.setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        mainPanel.add(createLabel("CCCD:"), gbc);
        gbc.gridx = 1;
        txtCccd = new JTextField();
        txtCccd.setPreferredSize(new Dimension(200, 30));
        txtCccd.setEditable(false);
        txtCccd.setFont(new Font("Segoe UI", Font.BOLD, 14));
        mainPanel.add(txtCccd, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        mainPanel.add(createLabel("Điểm CC:"), gbc);
        gbc.gridx = 1;
        txtDiemCc = createFormattedTextField();
        mainPanel.add(txtDiemCc, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        mainPanel.add(createLabel("Điểm UTXT:"), gbc);
        gbc.gridx = 1;
        txtDiemUtxt = createFormattedTextField();
        mainPanel.add(txtDiemUtxt, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        mainPanel.add(createLabel("Tổng điểm cộng:"), gbc);
        gbc.gridx = 1;
        txtDiemTong = createFormattedTextField();
        txtDiemTong.setEditable(false);
        txtDiemTong.setBackground(new Color(236, 240, 241));
        mainPanel.add(txtDiemTong, gbc);

        add(mainPanel, BorderLayout.CENTER);

        add(createButtonPanel(), BorderLayout.SOUTH);

        addCalculationListeners();
    }

    private void addCalculationListeners() {
        java.beans.PropertyChangeListener calcAction = e -> {
            double cc = getDoubleValue(txtDiemCc);
            double utxt = getDoubleValue(txtDiemUtxt);
            txtDiemTong.setValue(cc + utxt);
        };

        txtDiemCc.addPropertyChangeListener("value", calcAction);
        txtDiemUtxt.addPropertyChangeListener("value", calcAction);
    }

    private void loadBonusData() {
        if (bonusScore != null) {
            txtCccd.setText(bonusScore.getCccd());
            setFieldValue(txtDiemCc, bonusScore.getDiemCc());
            setFieldValue(txtDiemUtxt, bonusScore.getDiemUtxt());
            setFieldValue(txtDiemTong, bonusScore.getDiemTong());
        }
    }

    private boolean validateForm() {
        String cccd = txtCccd.getText().trim();
        if (cccd.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Không xác định được CCCD!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        // Formatter đã chặn số âm, ta có thể chặn ngưỡng tối đa hợp lý nếu cần
        double cc = getDoubleValue(txtDiemCc);
        double utxt = getDoubleValue(txtDiemUtxt);
        if (cc > 10.0 || utxt > 10.0) { // Tùy quy chế tuyển sinh của trường
            JOptionPane.showMessageDialog(this, "Điểm cộng có vẻ không hợp lý (quá cao)!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            // Có thể return false nếu muốn chặn cứng
        }
        return true;
    }

    private void saveData() {
        if (!validateForm()) return;

        try {
            if (bonusScore == null) {
                bonusScore = new BonusScoreDTO();
            }
            bonusScore.setCccd(txtCccd.getText());
            bonusScore.setDiemCc(getDoubleValue(txtDiemCc));
            bonusScore.setDiemUtxt(getDoubleValue(txtDiemUtxt));
            bonusScore.setDiemTong(getDoubleValue(txtDiemTong));

            saved = true;
            dispose();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi lưu dữ liệu: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }


    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));
        panel.setBackground(new Color(245, 245, 245));

        btnSave = createButton("Lưu", new Color(46, 204, 113));
        btnCancel = createButton("Hủy", new Color(231, 76, 60));

        btnSave.addActionListener(e -> saveData());
        btnCancel.addActionListener(e -> dispose());

        panel.add(btnSave);
        panel.add(btnCancel);
        return panel;
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        return label;
    }

    private JFormattedTextField createFormattedTextField() {
        DecimalFormat format = new DecimalFormat("#0.0#");
        NumberFormatter formatter = new NumberFormatter(format);
        formatter.setValueClass(Double.class);
        formatter.setMinimum(0.0);

        JFormattedTextField field = new JFormattedTextField(formatter);
        field.setPreferredSize(new Dimension(200, 30));
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setValue(0.0);
        return field;
    }

    private JButton createButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(90, 35));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    private void setFieldValue(JFormattedTextField field, Double value) {
        if (value != null) field.setValue(value);
    }

    private double getDoubleValue(JFormattedTextField field) {
        Object value = field.getValue();
        return (value instanceof Number) ? ((Number) value).doubleValue() : 0.0;
    }

    public BonusScoreDTO getBonusScore() { return bonusScore; }
    public boolean isSaved() { return saved; }
}