package com.example.managementadmissionwf.ui.panel.score;

import com.example.managementadmissionwf.dto.score.BonusScoreDTO;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.NumberFormatter;
import java.awt.*;
import java.text.DecimalFormat;
public class BonusScoreFormDialog extends JDialog {
    private static final String SCORE_PLACEHOLDER = "Nhập điểm...";
    private static final Dimension FIELD_SIZE = new Dimension(240, 32);
    private static final Dimension ERROR_SIZE = new Dimension(260, 18);
    private static final Dimension SAVE_ERROR_SIZE = new Dimension(410, 36);
    private static final Dimension BUTTON_SIZE = new Dimension(100, 36);

    public interface SaveHandler {
        void save(BonusScoreDTO bonusScore) throws Exception;
    }

    private BonusScoreDTO bonusScore;
    private boolean saved = false;
    private SaveHandler saveHandler;

    private JTextField txtCccd;
    private JFormattedTextField txtDiemCc;
    private JFormattedTextField txtDiemUtxt;
    private JFormattedTextField txtDiemTong;

    private JLabel lblErrorCc;
    private JLabel lblErrorUtxt;
    private JLabel lblErrorSave;

    private final java.util.List<JFormattedTextField> trackedFields = new java.util.ArrayList<>();

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
        setPreferredSize(new Dimension(520, 430));
        setMinimumSize(new Dimension(500, 410));
        setLayout(new BorderLayout());
        
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(22, 34, 18, 34));
        mainPanel.setBackground(Color.WHITE);

        // CCCD
        txtCccd = createTextField();
        txtCccd.setEditable(false);
        txtCccd.setFont(new Font("Segoe UI", Font.BOLD, 14));
        addFormRow(mainPanel, 0, "CCCD:", txtCccd);

        // Điểm CC
        txtDiemCc = createFormattedTextField();
        addPlaceholder(txtDiemCc, SCORE_PLACEHOLDER);
        addFormRow(mainPanel, 1, "Điểm CC:", txtDiemCc);
        lblErrorCc = createErrorLabel();
        addErrorRow(mainPanel, 2, lblErrorCc);

        // Điểm UTXT
        txtDiemUtxt = createFormattedTextField();
        addPlaceholder(txtDiemUtxt, SCORE_PLACEHOLDER);
        addFormRow(mainPanel, 3, "Điểm UTXT:", txtDiemUtxt);
        lblErrorUtxt = createErrorLabel();
        addErrorRow(mainPanel, 4, lblErrorUtxt);

        // Tổng điểm
        txtDiemTong = createFormattedTextField();
        txtDiemTong.setEditable(false);
        txtDiemTong.setBackground(new Color(236, 240, 241));
        addFormRow(mainPanel, 5, "Tổng điểm cộng:", txtDiemTong);

        // Error message for save
        lblErrorSave = createErrorLabel();
        lblErrorSave.setPreferredSize(SAVE_ERROR_SIZE);
        lblErrorSave.setMinimumSize(SAVE_ERROR_SIZE);
        addWideRow(mainPanel, 6, lblErrorSave);

        add(mainPanel, BorderLayout.CENTER);
        add(createButtonPanel(), BorderLayout.SOUTH);

        addCalculationListeners();
        pack();
    }

    private void addCalculationListeners() {
        java.beans.PropertyChangeListener calcAction = e -> {
            updateTotalField();
        };

        txtDiemCc.addPropertyChangeListener("value", calcAction);
        txtDiemUtxt.addPropertyChangeListener("value", calcAction);

        DocumentListener documentListener = new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                updateTotalField();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updateTotalField();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                updateTotalField();
            }
        };

        txtDiemCc.getDocument().addDocumentListener(documentListener);
        txtDiemUtxt.getDocument().addDocumentListener(documentListener);
    }

    private void loadBonusData() {
        if (bonusScore != null) {
            txtCccd.setText(bonusScore.getCccd());
            setFieldValue(txtDiemCc, bonusScore.getDiemCc());
            setFieldValue(txtDiemUtxt, bonusScore.getDiemUtxt());
            updateTotalField();
        }
    }

    private boolean validateForm() {
        clearErrors();
        String cccd = txtCccd.getText().trim();
        boolean valid = true;

        if (cccd.isEmpty()) {
            lblErrorSave.setText("Không xác định được CCCD!");
            valid = false;
        }

        Double cc = getDoubleValue(txtDiemCc);
        Double utxt = getDoubleValue(txtDiemUtxt);

        if (hasInvalidNumberText(txtDiemCc)) {
            lblErrorCc.setText("Điểm CC phải là số hợp lệ");
            valid = false;
        } else if (cc != null && (cc < 0.0 || cc > 10.0)) {
            lblErrorCc.setText("Điểm CC phải từ 0 đến 10");
            valid = false;
        }

        if (hasInvalidNumberText(txtDiemUtxt)) {
            lblErrorUtxt.setText("Điểm UTXT phải là số hợp lệ");
            valid = false;
        } else if (utxt != null && (utxt < 0.0 || utxt > 10.0)) {
            lblErrorUtxt.setText("Điểm UTXT phải từ 0 đến 10");
            valid = false;
        }

        return valid;
    }

    private void clearErrors() {
        lblErrorCc.setText("");
        lblErrorUtxt.setText("");
        lblErrorSave.setText("");
        lblErrorSave.setToolTipText(null);
    }

    private JLabel createErrorLabel() {
        JLabel label = new JLabel();
        label.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        label.setForeground(new Color(231, 76, 60));
        label.setPreferredSize(ERROR_SIZE);
        label.setMinimumSize(ERROR_SIZE);
        return label;
    }

    private void saveData() {
        if (!validateForm()) return;

        try {
            if (bonusScore == null) {
                bonusScore = new BonusScoreDTO();
            }

            Double diemCc = getDoubleValue(txtDiemCc);
            Double diemUtxt = getDoubleValue(txtDiemUtxt);
            double diemTong = calculateTotal(diemCc, diemUtxt);

            bonusScore.setCccd(txtCccd.getText());
            bonusScore.setDiemCc(diemCc);
            bonusScore.setDiemUtxt(diemUtxt);
            bonusScore.setDiemTong(diemTong);

            if (saveHandler != null) {
                try {
                    setSaving(true);
                    saveHandler.save(bonusScore);
                } catch (Exception e) {
                    showSaveError(getErrorMessage(e));
                    return;
                } finally {
                    setSaving(false);
                }
            }

            saved = true;
            dispose();
        } catch (Exception e) {
            showSaveError("Lỗi: " + getErrorMessage(e));
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

    private JTextField createTextField() {
        JTextField field = new JTextField();
        field.setPreferredSize(FIELD_SIZE);
        field.setMinimumSize(FIELD_SIZE);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        return field;
    }

    private void addFormRow(JPanel panel, int row, String labelText, JComponent field) {
        GridBagConstraints labelGbc = new GridBagConstraints();
        labelGbc.gridx = 0;
        labelGbc.gridy = row;
        labelGbc.insets = new Insets(8, 6, 8, 12);
        labelGbc.anchor = GridBagConstraints.EAST;
        panel.add(createLabel(labelText), labelGbc);

        GridBagConstraints fieldGbc = new GridBagConstraints();
        fieldGbc.gridx = 1;
        fieldGbc.gridy = row;
        fieldGbc.insets = new Insets(8, 0, 8, 6);
        fieldGbc.fill = GridBagConstraints.HORIZONTAL;
        fieldGbc.weightx = 1.0;
        panel.add(field, fieldGbc);
    }

    private void addErrorRow(JPanel panel, int row, JLabel errorLabel) {
        GridBagConstraints errorGbc = new GridBagConstraints();
        errorGbc.gridx = 1;
        errorGbc.gridy = row;
        errorGbc.insets = new Insets(0, 0, 4, 6);
        errorGbc.fill = GridBagConstraints.HORIZONTAL;
        errorGbc.weightx = 1.0;
        panel.add(errorLabel, errorGbc);
    }

    private void addWideRow(JPanel panel, int row, JComponent component) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(8, 6, 0, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        panel.add(component, gbc);
    }

    private JFormattedTextField createFormattedTextField() {
        DecimalFormat format = new DecimalFormat("#0.0#");
        NumberFormatter formatter = new NumberFormatter(format);

        formatter.setValueClass(Double.class);
        formatter.setMinimum(0.0);
        formatter.setAllowsInvalid(true);
        formatter.setCommitsOnValidEdit(true);

        JFormattedTextField field = new JFormattedTextField(formatter);
        field.setPreferredSize(FIELD_SIZE);
        field.setMinimumSize(FIELD_SIZE);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        field.setValue(null);
        trackedFields.add(field);

        return field;
    }

    @Override
    public void dispose() {
        for (JFormattedTextField f : trackedFields) {
            for (java.awt.event.FocusListener l : f.getFocusListeners()) {
                f.removeFocusListener(l);
            }
            for (java.beans.PropertyChangeListener l : f.getPropertyChangeListeners()) {
                f.removePropertyChangeListener(l);
            }
        }
        super.dispose();
    }

    private void addPlaceholder(JFormattedTextField field, String placeholder) {
        field.setForeground(Color.GRAY);
        field.setText(placeholder);

        field.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                if (isPlaceholderText(field)) {
                    field.setText("");
                }
                field.setForeground(Color.BLACK);
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                if (field.getText().trim().isEmpty()) {
                    showPlaceholder(field);
                }
            }
        });
    }

    private JButton createButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setPreferredSize(BUTTON_SIZE);
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    private void setFieldValue(JFormattedTextField field, Double value) {
        if (value != null) {
            field.setValue(value);
            field.setForeground(Color.BLACK);
        } else {
            field.setValue(null);
            showPlaceholder(field);
        }
    }

    private Double getDoubleValue(JFormattedTextField field) {
        String text = field.getText() == null ? "" : field.getText().trim();
        if (text.isEmpty() || isPlaceholderText(field)) {
            return null;
        }

        Double parsedValue = parseNumberText(text);
        if (parsedValue != null) {
            return parsedValue;
        }

        try {
            Object value = field.getValue();
            if (value == null) return null;
            if (value instanceof Double) return (Double) value;
            if (value instanceof Number) return ((Number) value).doubleValue();
            String valueText = value.toString().trim();
            if (valueText.isEmpty()) return null;
            return Double.parseDouble(valueText);
        } catch (Exception e) {
            return null;
        }
    }

    private boolean hasInvalidNumberText(JFormattedTextField field) {
        String text = field.getText() == null ? "" : field.getText().trim();
        if (text.isEmpty() || isPlaceholderText(field)) {
            return false;
        }
        return parseNumberText(text) == null;
    }

    private boolean isPlaceholderText(JFormattedTextField field) {
        String text = field.getText();
        return text != null && text.trim().equals(SCORE_PLACEHOLDER);
    }

    private void showPlaceholder(JFormattedTextField field) {
        field.setText(SCORE_PLACEHOLDER);
        field.setForeground(Color.GRAY);
    }

    private void updateTotalField() {
        Double cc = getDoubleValue(txtDiemCc);
        Double utxt = getDoubleValue(txtDiemUtxt);
        txtDiemTong.setValue(calculateTotal(cc, utxt));
        txtDiemTong.setForeground(Color.BLACK);
    }

    private double calculateTotal(Double cc, Double utxt) {
        return (cc != null ? cc : 0.0) + (utxt != null ? utxt : 0.0);
    }

    private Double parseNumberText(String text) {
        try {
            return Double.parseDouble(normalizeDecimalText(text));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private String normalizeDecimalText(String text) {
        String normalized = text.replace(" ", "");
        int lastComma = normalized.lastIndexOf(',');
        int lastDot = normalized.lastIndexOf('.');

        if (lastComma >= 0 && lastDot >= 0) {
            if (lastComma > lastDot) {
                return normalized.replace(".", "").replace(',', '.');
            }
            return normalized.replace(",", "");
        }

        if (lastComma >= 0) {
            return normalized.replace(',', '.');
        }

        return normalized;
    }

    private void showSaveError(String message) {
        String text = (message == null || message.trim().isEmpty())
                ? "Lưu dữ liệu thất bại. Vui lòng kiểm tra lại thông tin!"
                : message.trim();
        lblErrorSave.setText(text);
        lblErrorSave.setToolTipText(text);
        Toolkit.getDefaultToolkit().beep();
    }

    private void setSaving(boolean saving) {
        btnSave.setEnabled(!saving);
        btnCancel.setEnabled(!saving);
        btnSave.setText(saving ? "Đang lưu..." : "Lưu");
    }

    private String getErrorMessage(Exception e) {
        return e.getMessage() != null ? e.getMessage() : e.toString();
    }

    public BonusScoreDTO getBonusScore() {
        return bonusScore;
    }

    public boolean isSaved() {
        return saved;
    }

    public void setSaveHandler(SaveHandler saveHandler) {
        this.saveHandler = saveHandler;
    }
}
