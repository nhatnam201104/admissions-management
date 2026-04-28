package com.example.managementadmissionwf.ui.panel.score;

import com.example.managementadmissionwf.dto.score.ScoreDTO;

import javax.swing.*;
import javax.swing.text.NumberFormatter;
import java.awt.*;
import java.text.DecimalFormat;
import java.text.ParseException;

public class ScoreFormDialog extends JDialog {

    public interface SaveHandler {
        void save(ScoreDTO score) throws Exception;
    }

    private ScoreDTO score;
    private boolean saved = false;
    private SaveHandler saveHandler;

    private JTextField txtCccd;
    private JTextField txtSobaodanh;
    private JComboBox<String> cboPhuongThuc;

    private JLabel lblErrorCccd;
    private JLabel lblErrorSbd;
    private JLabel lblGeneralError;

    private JFormattedTextField txtToan, txtLy, txtHoa, txtSinh, txtSu, txtDia, txtVan;
    private JFormattedTextField txtN1Thi, txtN1Cc, txtNl1, txtNk1, txtNk2;

    private final java.util.List<JFormattedTextField> numberFields = new java.util.ArrayList<>();

    private JButton btnSave, btnCancel;

    public ScoreFormDialog(Frame parent, String title, ScoreDTO score) {
        super(parent, title, true);
        this.score = score;

        initUI();
        loadData();

        setSize(650, 450);
        setLocationRelativeTo(parent);
    }

    // ================= UI =================
    private void initUI() {
        setLayout(new BorderLayout());

        txtCccd = new JTextField();
        txtCccd.setEditable(false);

        txtSobaodanh = new JTextField();
        txtSobaodanh.setEditable(false);

        JTabbedPane tab = new JTabbedPane();
        tab.addTab("Điểm thi chính", createExamPanel());
        tab.addTab("Ngoại ngữ & Khác", createOtherPanel());

        add(tab, BorderLayout.CENTER);
        add(createFooterPanel(), BorderLayout.SOUTH);
    }

    private JPanel createExamPanel() {
        JPanel p = basePanel();

        addRow(p, 0, "CCCD:", txtCccd, "SBD:", txtSobaodanh);

        lblErrorCccd = createErrorLabel();
        lblErrorSbd = createErrorLabel();

        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(0, 5, 0, 5);
        g.gridx = 1; g.gridy = 1; g.gridwidth = 1;
        p.add(lblErrorCccd, g);
        g.gridx = 3; g.gridy = 1;
        p.add(lblErrorSbd, g);

        txtToan = createNumberField();
        txtLy = createNumberField();
        txtHoa = createNumberField();
        txtSinh = createNumberField();
        txtSu = createNumberField();
        txtDia = createNumberField();
        txtVan = createNumberField();

        addRow(p, 2, "Toán:", txtToan, "Lý:", txtLy);
        addRow(p, 3, "Hóa:", txtHoa, "Sinh:", txtSinh);
        addRow(p, 4, "Sử:", txtSu, "Địa:", txtDia);
        addRow(p, 5, "Văn:", txtVan, null, null);

        return p;
    }

    private JPanel createOtherPanel() {
        JPanel p = basePanel();

        cboPhuongThuc = new JComboBox<>(new String[]{"THPT", "DGNL", "VSAT"});

        txtN1Thi = createNumberField();
        txtN1Cc = createNumberField();
        txtNl1 = createNumberField();
        txtNk1 = createNumberField();
        txtNk2 = createNumberField();

        addRow(p, 0, "Phương thức:", cboPhuongThuc, null, null);
        addRow(p, 2, "N1 Thi:", txtN1Thi, "N1 CC:", txtN1Cc);
        addRow(p, 4, "NL1:", txtNl1, "NK1:", txtNk1);
        addRow(p, 5, "NK2:", txtNk2, null, null);

        return p;
    }

    // ================= BUTTON (GIỮ NGUYÊN) =================
    private JPanel createFooterPanel() {
        JPanel footer = new JPanel(new BorderLayout());
        footer.setBackground(new Color(240, 240, 240));

        lblGeneralError = createErrorLabel();
        lblGeneralError.setBorder(BorderFactory.createEmptyBorder(8, 20, 0, 20));

        footer.add(lblGeneralError, BorderLayout.NORTH);
        footer.add(createButtonPanel(), BorderLayout.SOUTH);
        return footer;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        panel.setBackground(new Color(240, 240, 240));

        btnSave = createButton("Lưu", new Color(46, 204, 113));
        btnCancel = createButton("Hủy", new Color(231, 76, 60));

        btnSave.addActionListener(e -> save());
        btnCancel.addActionListener(e -> dispose());

        panel.add(btnSave);
        panel.add(btnCancel);

        return panel;
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

    // ================= LOGIC =================
    private void loadData() {
        if (score == null) return;

        txtCccd.setText(score.getCccd());
        txtSobaodanh.setText(score.getSobaodanh());

        cboPhuongThuc.setSelectedItem(score.getPhuongThuc());

        setValue(txtToan, score.getToan());
        setValue(txtLy, score.getLy());
        setValue(txtHoa, score.getHoa());
        setValue(txtSinh, score.getSinh());
        setValue(txtSu, score.getSu());
        setValue(txtDia, score.getDia());
        setValue(txtVan, score.getVan());

        setValue(txtN1Thi, score.getN1Thi());
        setValue(txtN1Cc, score.getN1Cc());
        setValue(txtNl1, score.getNl1());
        setValue(txtNk1, score.getNk1());
        setValue(txtNk2, score.getNk2());
    }

    private void save() {
        if (!validateFormWithScoreFields()) return;

        if (score == null) score = new ScoreDTO();

        applyFormValues();

        if (saveHandler != null) {
            try {
                setSaving(true);
                saveHandler.save(score);
            } catch (Exception e) {
                showGeneralError(getErrorMessage(e));
                SwingUtilities.invokeLater(btnSave::requestFocusInWindow);
                return;
            } finally {
                setSaving(false);
            }
        }

        saved = true;
        dispose();
    }

    private void applyFormValues() {
        score.setCccd(txtCccd.getText());
        score.setSobaodanh(txtSobaodanh.getText());
        score.setPhuongThuc((String) cboPhuongThuc.getSelectedItem());

        score.setToan(getValue(txtToan));
        score.setLy(getValue(txtLy));
        score.setHoa(getValue(txtHoa));
        score.setSinh(getValue(txtSinh));
        score.setSu(getValue(txtSu));
        score.setDia(getValue(txtDia));
        score.setVan(getValue(txtVan));

        score.setN1Thi(getValue(txtN1Thi));
        score.setN1Cc(getValue(txtN1Cc));
        score.setNl1(getValue(txtNl1));
        score.setNk1(getValue(txtNk1));
        score.setNk2(getValue(txtNk2));
    }

    private boolean validateFormWithScoreFields() {
        clearAllErrors();

        java.util.List<String> errors = new java.util.ArrayList<>();
        JComponent[] firstInvalidField = new JComponent[1];
        boolean valid = true;

        if (txtCccd.getText().length() != 12) {
            String message = "CCCD phải gồm đúng 12 chữ số!";
            showFieldErrorMessageWithLabel(txtCccd, lblErrorCccd, message);
            addError(errors, firstInvalidField, txtCccd, message);
            valid = false;
        }

        if (txtSobaodanh.getText().isEmpty()) {
            String message = "Số báo danh không được để trống!";
            showFieldErrorMessageWithLabel(txtSobaodanh, lblErrorSbd, message);
            addError(errors, firstInvalidField, txtSobaodanh, message);
            valid = false;
        }

        valid &= validateScoreField(txtToan, "Toán", 0, 10, errors, firstInvalidField);
        valid &= validateScoreField(txtLy, "Lý", 0, 10, errors, firstInvalidField);
        valid &= validateScoreField(txtHoa, "Hóa", 0, 10, errors, firstInvalidField);
        valid &= validateScoreField(txtSinh, "Sinh", 0, 10, errors, firstInvalidField);
        valid &= validateScoreField(txtSu, "Sử", 0, 10, errors, firstInvalidField);
        valid &= validateScoreField(txtDia, "Địa", 0, 10, errors, firstInvalidField);
        valid &= validateScoreField(txtVan, "Văn", 0, 10, errors, firstInvalidField);
        valid &= validateScoreField(txtN1Thi, "N1 Thi", 0, 10, errors, firstInvalidField);
        valid &= validateScoreField(txtN1Cc, "N1 CC", 0, 10, errors, firstInvalidField);
        valid &= validateScoreField(txtNl1, "NL1", 0, 1200, errors, firstInvalidField);
        valid &= validateScoreField(txtNk1, "NK1", 0, 100, errors, firstInvalidField);
        valid &= validateScoreField(txtNk2, "NK2", 0, 100, errors, firstInvalidField);

        if (!valid && !errors.isEmpty()) {
            lblGeneralError.setText(errors.size() == 1
                    ? errors.get(0)
                    : errors.get(0) + " (" + errors.size() + " lỗi)");

            if (firstInvalidField[0] != null) {
                SwingUtilities.invokeLater(firstInvalidField[0]::requestFocusInWindow);
            }
        }

        return valid;
    }

    private boolean validateScoreField(JFormattedTextField field, String fieldName, double min, double max,
                                       java.util.List<String> errors, JComponent[] firstInvalidField) {
        Double value = parseNumberText(field.getText());

        if (value == null) {
            String message = fieldName + " phải là số hợp lệ!";
            showFieldErrorMessage(field, message);
            addError(errors, firstInvalidField, field, message);
            return false;
        }

        if (value < min || value > max) {
            String message = fieldName + " phải từ " + formatLimit(min) + " đến " + formatLimit(max) + "!";
            showFieldErrorMessage(field, message);
            addError(errors, firstInvalidField, field, message);
            return false;
        }

        return true;
    }

    private void addError(java.util.List<String> errors, JComponent[] firstInvalidField,
                          JComponent field, String message) {
        errors.add(message);
        if (firstInvalidField[0] == null) {
            firstInvalidField[0] = field;
        }
    }

    private void showFieldErrorMessageWithLabel(JComponent field, JLabel label, String message) {
        showFieldErrorMessage(field, message);
        label.setText(message);
    }

    private void showFieldErrorMessage(JComponent field, String message) {
        field.setBorder(BorderFactory.createLineBorder(new Color(231, 76, 60), 2));
        field.setToolTipText(message);
    }

    private void showGeneralError(String message) {
        String text = (message == null || message.trim().isEmpty())
                ? "Lưu dữ liệu thất bại. Vui lòng kiểm tra lại thông tin!"
                : message.trim();
        lblGeneralError.setText(text);
        lblGeneralError.setToolTipText(text);
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

    private void clearAllErrors() {
        txtCccd.setBorder(UIManager.getBorder("TextField.border"));
        txtCccd.setToolTipText(null);
        txtSobaodanh.setBorder(UIManager.getBorder("TextField.border"));
        txtSobaodanh.setToolTipText(null);

        for (JFormattedTextField field : numberFields) {
            field.setBorder(UIManager.getBorder("FormattedTextField.border"));
            field.setToolTipText(null);
        }

        lblErrorCccd.setText("");
        lblErrorSbd.setText("");
        lblGeneralError.setText("");
        lblGeneralError.setToolTipText(null);
    }

    private JLabel createErrorLabel() {
        JLabel label = new JLabel();
        label.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        label.setForeground(new Color(231, 76, 60));
        return label;
    }

    // ================= HELPER =================
    private JPanel basePanel() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        return p;
    }

    private void addRow(JPanel p, int y, String l1, JComponent c1, String l2, JComponent c2) {
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(5,5,5,5);
        g.fill = GridBagConstraints.HORIZONTAL;

        g.gridx = 0; g.gridy = y;
        if (l1 != null) p.add(new JLabel(l1), g);

        g.gridx = 1;
        if (c1 != null) p.add(c1, g);

        if (l2 != null) {
            g.gridx = 2;
            p.add(new JLabel(l2), g);

            g.gridx = 3;
            p.add(c2, g);
        }
    }

    private JFormattedTextField createNumberField() {
        DecimalFormat format = new DecimalFormat("#0.0#");
        NumberFormatter formatter = new NumberFormatter(format);
        formatter.setValueClass(Double.class);
        formatter.setAllowsInvalid(true);
        formatter.setCommitsOnValidEdit(true);

        JFormattedTextField f = new JFormattedTextField(formatter);
        f.setColumns(10);

        addPlaceholder(f, "0.0");
        numberFields.add(f);

        return f;
    }

    @Override
    public void dispose() {
        for (JFormattedTextField f : numberFields) {
            for (java.awt.event.FocusListener l : f.getFocusListeners()) {
                f.removeFocusListener(l);
            }
        }
        super.dispose();
    }

    private void addPlaceholder(JFormattedTextField field, String text) {
        field.setForeground(Color.GRAY);
        field.setText(text);

        field.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent e) {
                if (field.getText().equals(text)) {
                    field.setText("");
                    field.setForeground(Color.BLACK);
                }
            }
            public void focusLost(java.awt.event.FocusEvent e) {
                if (field.getText().isEmpty()) {
                    field.setText(text);
                    field.setForeground(Color.GRAY);
                }
            }
        });
    }

    private Double getValue(JFormattedTextField f) {
        String t = f.getText() == null ? "" : f.getText().trim();
        if (t.isEmpty()) return 0.0;

        try {
            f.commitEdit();
            Object value = f.getValue();
            if (value instanceof Number number) {
                return number.doubleValue();
            }
        } catch (ParseException ignored) {
            // Fallback below accepts both "8.5" and "8,5".
        }

        try {
            Double value = parseNumberText(t);
            return value != null ? value : 0.0;
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    private Double parseNumberText(String text) {
        String t = text == null ? "" : text.trim();
        if (t.isEmpty()) return 0.0;

        try {
            return Double.parseDouble(normalizeDecimalText(t));
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

    private String formatLimit(double value) {
        if (value == Math.rint(value)) {
            return String.valueOf((long) value);
        }
        return String.valueOf(value);
    }

    private void setValue(JFormattedTextField f, Double v) {
        if (v != null) {
            f.setText(String.valueOf(v));
            f.setForeground(Color.BLACK);
        }
    }

    public boolean isSaved() { return saved; }
    public ScoreDTO getScore() { return score; }
    public void setSaveHandler(SaveHandler saveHandler) { this.saveHandler = saveHandler; }
}
