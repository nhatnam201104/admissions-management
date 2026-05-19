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
    private String loadedMethod;
    private boolean saved = false;
    private SaveHandler saveHandler;

    // Header
    private JTextField txtCccd;
    private JTextField txtSobaodanh;

    // Method selection
    private JRadioButton rbTHPT, rbDGNL, rbVSAT;
    private ButtonGroup methodGroup;

    // CardLayout
    private CardLayout cardLayout;
    private JPanel cardPanel;
    private JLabel lblVsatNote;

    // THPT fields
    private JFormattedTextField txtToan, txtLy, txtHoa, txtSinh, txtSu, txtDia, txtVan;
    private JFormattedTextField txtN1Thi, txtN1Cc;
    private JFormattedTextField txtNk1, txtNk2;

    // DGNL fields (NK1/NK2 đã dời sang THPT — DGNL chỉ còn NL1)
    private JFormattedTextField txtNl1;

    // VSAT fields (8 môn thi V-SAT: Toán, Lý, Hóa, Sinh, Sử, Địa, Anh, Văn)
    private JFormattedTextField txtVsatToan, txtVsatLy, txtVsatHoa, txtVsatSinh;
    private JFormattedTextField txtVsatSu, txtVsatDia, txtVsatAnh, txtVsatVan;

    private final java.util.List<JFormattedTextField> numberFields = new java.util.ArrayList<>();

    // Tabbed pane
    private JTabbedPane tabbedPane;

    // Error labels
    private JLabel lblErrorCccd, lblErrorSbd, lblGeneralError;

    private JButton btnSave, btnCancel;

    public ScoreFormDialog(Frame parent, String title, ScoreDTO score) {
        super(parent, title, true);
        this.score = score;
        this.loadedMethod = score != null ? score.getPhuongThuc() : null;

        initUI();
        loadData();

        setSize(700, 550);
        setLocationRelativeTo(parent);
    }

    // ================= UI =================

    private void initUI() {
        setLayout(new BorderLayout());

        txtCccd = new JTextField();
        txtCccd.setEditable(false);
        txtSobaodanh = new JTextField();
        txtSobaodanh.setEditable(false);

        add(createHeaderPanel(), BorderLayout.NORTH);
        add(createTabbedPane(), BorderLayout.CENTER);
        add(createFooterPanel(), BorderLayout.SOUTH);
    }

    private JTabbedPane createTabbedPane() {
        tabbedPane = new JTabbedPane();

        // Tab 1: Nhập điểm
        JPanel inputPanel = createMethodAndFieldsPanel();
        tabbedPane.addTab("Nhập điểm", inputPanel);

        // Tab 2: Preview
        JPanel previewPanel = createPreviewPanel();
        tabbedPane.addTab("Preview", previewPanel);

        return tabbedPane;
    }

    private JPanel createPreviewPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JLabel title = new JLabel("Xem trước điểm đã nhập");
        title.setFont(new Font("Segoe UI", Font.BOLD, 14));
        panel.add(title, BorderLayout.NORTH);

        JPanel previewContent = new JPanel(new GridBagLayout());
        previewContent.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(5, 5, 5, 5);
        g.fill = GridBagConstraints.HORIZONTAL;
        g.anchor = GridBagConstraints.WEST;

        int row = 0;
        JLabel lblMethod = new JLabel("Phương thức:");
        lblMethod.setFont(new Font("Segoe UI", Font.BOLD, 12));
        g.gridx = 0; g.gridy = row;
        previewContent.add(lblMethod, g);
        g.gridx = 1; g.gridy = row++;
        JLabel lblMethodValue = new JLabel("-");
        lblMethodValue.setName("lblPreviewMethod");
        previewContent.add(lblMethodValue, g);

        row++; // spacing

        // THPT subjects preview
        JLabel lblSubjects = new JLabel("Điểm các môn:");
        lblSubjects.setFont(new Font("Segoe UI", Font.BOLD, 12));
        g.gridx = 0; g.gridy = row++;
        previewContent.add(lblSubjects, g);

        String[] thptLabels = {"Toán", "Lý", "Hóa", "Sinh", "Sử", "Địa", "Văn", "NK1", "NK2"};
        JFormattedTextField[] thptFields = {txtToan, txtLy, txtHoa, txtSinh, txtSu, txtDia, txtVan, txtNk1, txtNk2};
        for (int i = 0; i < thptLabels.length; i++) {
            final int idx = i;
            JLabel lbl = new JLabel(thptLabels[i] + ":");
            g.gridx = 0; g.gridy = row;
            previewContent.add(lbl, g);

            JLabel val = new JLabel("-");
            val.setName("lblPreview" + thptLabels[i]);
            g.gridx = 1; g.gridy = row++;

            // Add listener to update preview when field changes
            thptFields[i].addFocusListener(new java.awt.event.FocusAdapter() {
                public void focusLost(java.awt.event.FocusEvent e) {
                    updatePreviewField("lblPreview" + thptLabels[idx], thptFields[idx]);
                }
            });
            thptFields[i].addKeyListener(new java.awt.event.KeyAdapter() {
                public void keyReleased(java.awt.event.KeyEvent e) {
                    updatePreviewField("lblPreview" + thptLabels[idx], thptFields[idx]);
                }
            });

            previewContent.add(val, g);
        }

        // DGNL preview (chỉ còn NL1 — NK1/NK2 đã dời sang THPT)
        row++;
        JLabel lblDgnl = new JLabel("Điểm ĐGNL:");
        lblDgnl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        g.gridx = 0; g.gridy = row++;
        previewContent.add(lblDgnl, g);

        String[] dgnlLabels = {"NL1"};
        JFormattedTextField[] dgnlFields = {txtNl1};
        for (int i = 0; i < dgnlLabels.length; i++) {
            final int idx = i;
            JLabel lbl = new JLabel(dgnlLabels[i] + ":");
            g.gridx = 0; g.gridy = row;
            previewContent.add(lbl, g);

            JLabel val = new JLabel("-");
            val.setName("lblPreview" + dgnlLabels[i]);
            g.gridx = 1; g.gridy = row++;

            dgnlFields[i].addFocusListener(new java.awt.event.FocusAdapter() {
                public void focusLost(java.awt.event.FocusEvent e) {
                    updatePreviewField("lblPreview" + dgnlLabels[idx], dgnlFields[idx]);
                }
            });
            dgnlFields[i].addKeyListener(new java.awt.event.KeyAdapter() {
                public void keyReleased(java.awt.event.KeyEvent e) {
                    updatePreviewField("lblPreview" + dgnlLabels[idx], dgnlFields[idx]);
                }
            });

            previewContent.add(val, g);
        }

        // VSAT preview
        row++;
        JLabel lblVsat = new JLabel("Điểm V-SAT:");
        lblVsat.setFont(new Font("Segoe UI", Font.BOLD, 12));
        g.gridx = 0; g.gridy = row++;
        previewContent.add(lblVsat, g);

        String[] vsatLabels = {"Toán", "Lý", "Hóa", "Sinh", "Sử", "Địa", "Anh", "Văn"};
        JFormattedTextField[] vsatFields = {txtVsatToan, txtVsatLy, txtVsatHoa, txtVsatSinh, txtVsatSu, txtVsatDia, txtVsatAnh, txtVsatVan};
        for (int i = 0; i < vsatLabels.length; i++) {
            final int idx = i;
            JLabel lbl = new JLabel(vsatLabels[i] + ":");
            g.gridx = 0; g.gridy = row;
            previewContent.add(lbl, g);

            JLabel val = new JLabel("-");
            val.setName("lblPreviewVsat" + vsatLabels[i]);
            g.gridx = 1; g.gridy = row++;

            vsatFields[i].addFocusListener(new java.awt.event.FocusAdapter() {
                public void focusLost(java.awt.event.FocusEvent e) {
                    updatePreviewField("lblPreviewVsat" + vsatLabels[idx], vsatFields[idx]);
                }
            });
            vsatFields[i].addKeyListener(new java.awt.event.KeyAdapter() {
                public void keyReleased(java.awt.event.KeyEvent e) {
                    updatePreviewField("lblPreviewVsat" + vsatLabels[idx], vsatFields[idx]);
                }
            });

            previewContent.add(val, g);
        }

        panel.add(previewContent, BorderLayout.CENTER);
        return panel;
    }

    private void updatePreviewField(String name, JFormattedTextField field) {
        for (java.awt.Component c : tabbedPane.getComponents()) {
            if (c instanceof JPanel) {
                for (java.awt.Component c2 : ((JPanel) c).getComponents()) {
                    if (c2 instanceof JLabel && name.equals(c2.getName())) {
                        String text = field.getText();
                        ((JLabel) c2).setText(text != null && !text.trim().isEmpty() ? text : "-");
                        return;
                    }
                }
            }
        }
    }
    private JPanel createHeaderPanel() {
        JPanel header = new JPanel(new GridBagLayout());
        header.setBorder(BorderFactory.createEmptyBorder(10, 20, 5, 20));

        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(3, 5, 3, 5);
        g.fill = GridBagConstraints.HORIZONTAL;

        g.gridx = 0; g.gridy = 0;
        header.add(new JLabel("CCCD:"), g);
        g.gridx = 1;
        header.add(txtCccd, g);

        g.gridx = 2;
        header.add(new JLabel("SBD:"), g);
        g.gridx = 3;
        header.add(txtSobaodanh, g);

        lblErrorCccd = createErrorLabel();
        lblErrorSbd = createErrorLabel();
        g.gridx = 1; g.gridy = 1;
        header.add(lblErrorCccd, g);
        g.gridx = 3;
        header.add(lblErrorSbd, g);

        return header;
    }

    private JPanel createMethodAndFieldsPanel() {
        JPanel wrapper = new JPanel(new BorderLayout(0, 5));
        wrapper.setBorder(BorderFactory.createEmptyBorder(5, 20, 10, 20));

        // Method selector
        JPanel methodBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        methodBar.add(new JLabel("Phương thức:"));

        rbTHPT = new JRadioButton("THPT");
        rbDGNL = new JRadioButton("DGNL");
        rbVSAT = new JRadioButton("VSAT");

        methodGroup = new ButtonGroup();
        methodGroup.add(rbTHPT);
        methodGroup.add(rbDGNL);
        methodGroup.add(rbVSAT);

        rbTHPT.addActionListener(e -> showMethodPanel());
        rbDGNL.addActionListener(e -> showMethodPanel());
        rbVSAT.addActionListener(e -> showMethodPanel());

        methodBar.add(rbTHPT);
        methodBar.add(rbDGNL);
        methodBar.add(rbVSAT);

        lblVsatNote = new JLabel("Nhập điểm thô V-SAT (thang 150, sẽ quy đổi tự động theo bảng quy đổi)");
        lblVsatNote.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        lblVsatNote.setForeground(new Color(100, 100, 100));
        lblVsatNote.setVisible(false);

        wrapper.add(methodBar, BorderLayout.NORTH);

        JPanel center = new JPanel(new BorderLayout());
        center.add(lblVsatNote, BorderLayout.NORTH);

        // CardLayout
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        cardPanel.add(createThptPanel(), "THPT");
        cardPanel.add(createDgnlPanel(), "DGNL");
        cardPanel.add(createVsatPanel(), "VSAT");

        center.add(cardPanel, BorderLayout.CENTER);
        wrapper.add(center, BorderLayout.CENTER);

        return wrapper;
    }

    private JPanel createThptPanel() {
        JPanel p = basePanel();

        txtToan = createNumberField();
        txtLy = createNumberField();
        txtHoa = createNumberField();
        txtSinh = createNumberField();
        txtSu = createNumberField();
        txtDia = createNumberField();
        txtVan = createNumberField();

        addRow(p, 0, "Toán:", txtToan, "Lý:", txtLy);
        addRow(p, 1, "Hóa:", txtHoa, "Sinh:", txtSinh);
        addRow(p, 2, "Sử:", txtSu, "Địa:", txtDia);
        addRow(p, 3, "Văn:", txtVan, null, null);

        txtN1Thi = createNumberField();
        txtN1Cc = createNumberField();

        addRow(p, 5, "N1 Thi:", txtN1Thi, "N1 CC:", txtN1Cc);

        txtNk1 = createNumberField();
        txtNk2 = createNumberField();

        addRow(p, 6, "NK1:", txtNk1, "NK2:", txtNk2);

        return p;
    }

    private JPanel createDgnlPanel() {
        JPanel p = basePanel();

        txtNl1 = createNumberField();

        addRow(p, 0, "NL1:", txtNl1, null, null);

        return p;
    }

    private JPanel createVsatPanel() {
        JPanel p = basePanel();

        txtVsatToan = createNumberField();
        txtVsatLy = createNumberField();
        txtVsatHoa = createNumberField();
        txtVsatSinh = createNumberField();
        txtVsatSu = createNumberField();
        txtVsatDia = createNumberField();
        txtVsatAnh = createNumberField();
        txtVsatVan = createNumberField();

        addRow(p, 0, "Toán:", txtVsatToan, "Lý:", txtVsatLy);
        addRow(p, 1, "Hóa:", txtVsatHoa, "Sinh:", txtVsatSinh);
        addRow(p, 2, "Sử:", txtVsatSu, "Địa:", txtVsatDia);
        addRow(p, 3, "Anh:", txtVsatAnh, "Văn:", txtVsatVan);

        return p;
    }

    private void showMethodPanel() {
        String method = getSelectedMethod();
        switch (method) {
            case "DGNL" -> cardLayout.show(cardPanel, "DGNL");
            case "VSAT" -> cardLayout.show(cardPanel, "VSAT");
            default -> cardLayout.show(cardPanel, "THPT");
        }
        lblVsatNote.setVisible("VSAT".equals(method));

        if (loadedMethod != null && !loadedMethod.equals(method)) {
            clearAllScoreFields();
        }
        loadedMethod = method;
    }

    // ================= BUTTON =================

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

    private String getSelectedMethod() {
        if (rbTHPT.isSelected()) return "THPT";
        if (rbDGNL.isSelected()) return "DGNL";
        if (rbVSAT.isSelected()) return "VSAT";
        return "THPT";
    }

    private void loadData() {
        if (score == null) return;

        txtCccd.setText(score.getCccd());
        txtSobaodanh.setText(score.getSobaodanh());

        // Set method radio
        String method = score.getPhuongThuc();
        if ("DGNL".equals(method)) rbDGNL.setSelected(true);
        else if ("VSAT".equals(method)) rbVSAT.setSelected(true);
        else rbTHPT.setSelected(true);

        // THPT fields
        setValue(txtToan, score.getToan());
        setValue(txtLy, score.getLy());
        setValue(txtHoa, score.getHoa());
        setValue(txtSinh, score.getSinh());
        setValue(txtSu, score.getSu());
        setValue(txtDia, score.getDia());
        setValue(txtVan, score.getVan());
        setValue(txtN1Thi, score.getN1Thi());
        setValue(txtN1Cc, score.getN1Cc());

        // DGNL fields
        setValue(txtNl1, score.getNl1());
        setValue(txtNk1, score.getNk1());
        setValue(txtNk2, score.getNk2());

        // VSAT fields (Anh stored in N1Thi)
        setValue(txtVsatToan, score.getToan());
        setValue(txtVsatLy, score.getLy());
        setValue(txtVsatHoa, score.getHoa());
        setValue(txtVsatSinh, score.getSinh());
        setValue(txtVsatSu, score.getSu());
        setValue(txtVsatDia, score.getDia());
        setValue(txtVsatAnh, score.getN1Thi());
        setValue(txtVsatVan, score.getVan());

        showMethodPanel();
    }

    private void save() {
        if (!validateForm()) return;

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
        String method = getSelectedMethod();

        score.setCccd(txtCccd.getText());
        score.setSobaodanh(txtSobaodanh.getText());
        score.setPhuongThuc(method);

        if ("DGNL".equals(method)) {
            score.setNl1(getValue(txtNl1));
            // NK1/NK2 không thuộc DGNL — đã chuyển sang THPT.
            score.setNk1(null);
            score.setNk2(null);
            clearThptFields();
            clearVsatFields();
        } else if ("VSAT".equals(method)) {
            score.setToan(getValue(txtVsatToan));
            score.setLy(getValue(txtVsatLy));
            score.setHoa(getValue(txtVsatHoa));
            score.setSinh(getValue(txtVsatSinh));
            score.setSu(getValue(txtVsatSu));
            score.setDia(getValue(txtVsatDia));
            score.setVan(getValue(txtVsatVan));
            // VSAT: Anh stored in N1Thi field
            score.setN1Thi(getValue(txtVsatAnh));
            score.setN1Cc(null);
            clearDgnlFields();
        } else {
            score.setToan(getValue(txtToan));
            score.setLy(getValue(txtLy));
            score.setHoa(getValue(txtHoa));
            score.setSinh(getValue(txtSinh));
            score.setSu(getValue(txtSu));
            score.setDia(getValue(txtDia));
            score.setVan(getValue(txtVan));
            score.setN1Thi(getValue(txtN1Thi));
            score.setN1Cc(getValue(txtN1Cc));
            score.setNk1(getValue(txtNk1));
            score.setNk2(getValue(txtNk2));
            clearDgnlFields();
            clearVsatFields();
        }
    }

    private void clearThptFields() {
        score.setToan(null);
        score.setLy(null);
        score.setHoa(null);
        score.setSinh(null);
        score.setSu(null);
        score.setDia(null);
        score.setVan(null);
        score.setN1Thi(null);
        score.setN1Cc(null);
        score.setNk1(null);
        score.setNk2(null);
    }

    private void clearDgnlFields() {
        score.setNl1(null);
    }

    private void clearVsatFields() {
        // VSAT fields don't need explicit clearing as they map to THPT fields
    }

    private boolean validateForm() {
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

        String method = getSelectedMethod();

        if ("DGNL".equals(method)) {
            valid &= validateScoreField(txtNl1, "NL1", 0, 1200, errors, firstInvalidField);
        } else if ("VSAT".equals(method)) {
            valid &= validateScoreField(txtVsatToan, "Toán", 0, 150, errors, firstInvalidField);
            valid &= validateScoreField(txtVsatLy, "Lý", 0, 150, errors, firstInvalidField);
            valid &= validateScoreField(txtVsatHoa, "Hóa", 0, 150, errors, firstInvalidField);
            valid &= validateScoreField(txtVsatSinh, "Sinh", 0, 150, errors, firstInvalidField);
            valid &= validateScoreField(txtVsatSu, "Sử", 0, 150, errors, firstInvalidField);
            valid &= validateScoreField(txtVsatDia, "Địa", 0, 150, errors, firstInvalidField);
            valid &= validateScoreField(txtVsatAnh, "Anh", 0, 150, errors, firstInvalidField);
            valid &= validateScoreField(txtVsatVan, "Văn", 0, 150, errors, firstInvalidField);
        } else {
            valid &= validateScoreField(txtToan, "Toán", 0, 10, errors, firstInvalidField);
            valid &= validateScoreField(txtLy, "Lý", 0, 10, errors, firstInvalidField);
            valid &= validateScoreField(txtHoa, "Hóa", 0, 10, errors, firstInvalidField);
            valid &= validateScoreField(txtSinh, "Sinh", 0, 10, errors, firstInvalidField);
            valid &= validateScoreField(txtSu, "Sử", 0, 10, errors, firstInvalidField);
            valid &= validateScoreField(txtDia, "Địa", 0, 10, errors, firstInvalidField);
            valid &= validateScoreField(txtVan, "Văn", 0, 10, errors, firstInvalidField);
            valid &= validateScoreField(txtN1Thi, "N1 Thi", 0, 10, errors, firstInvalidField);
            valid &= validateScoreField(txtN1Cc, "N1 CC", 0, 10, errors, firstInvalidField);
            valid &= validateScoreField(txtNk1, "NK1", 0, 10, errors, firstInvalidField);
            valid &= validateScoreField(txtNk2, "NK2", 0, 10, errors, firstInvalidField);
        }

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

    // ================= VALIDATION HELPERS =================

    private boolean validateScoreField(JFormattedTextField field, String fieldName, double min, double max,
                                       java.util.List<String> errors, JComponent[] firstInvalidField) {
        String rawText = field.getText();
        if (rawText != null && rawText.trim().isEmpty()) {
            return true;
        }

        Double value = parseNumberText(rawText);

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
        p.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        return p;
    }

    private void addRow(JPanel p, int y, String l1, JComponent c1, String l2, JComponent c2) {
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(5, 5, 5, 5);
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
        if (t.isEmpty()) return null;

        try {
            f.commitEdit();
            Object value = f.getValue();
            if (value instanceof Number number) {
                return number.doubleValue();
            }
        } catch (ParseException ignored) {
        }

        return parseNumberText(t);
    }

    private Double parseNumberText(String text) {
        String t = text == null ? "" : text.trim();
        if (t.isEmpty()) return null;

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
        } else {
            f.setText("-");
            f.setForeground(Color.GRAY);
        }
    }

    private void clearAllScoreFields() {
        for (JFormattedTextField f : numberFields) {
            f.setText("0.0");
            f.setForeground(Color.GRAY);
        }
    }

    public boolean isSaved() { return saved; }
    public ScoreDTO getScore() { return score; }
    public void setSaveHandler(SaveHandler saveHandler) { this.saveHandler = saveHandler; }
}
