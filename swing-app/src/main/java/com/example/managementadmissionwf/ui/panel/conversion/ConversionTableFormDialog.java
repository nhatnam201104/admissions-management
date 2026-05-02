package com.example.managementadmissionwf.ui.panel.conversion;

import com.example.managementadmissionwf.dto.ConversionTableDTO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import java.awt.*;
import java.util.Arrays;
import java.util.List;

/**
 * Form dialog để thêm / sửa bảng quy đổi điểm
 */
public class ConversionTableFormDialog extends JDialog {

    private static final List<String> PHUONG_THUC_LIST = Arrays.asList("VSAT", "THPT", "DGNL", "IELTS", "TOEIC");
    private static final List<String> TO_HOP_LIST = Arrays.asList("A00", "A01", "A02", "B00", "B01", "C00", "D01",
            "D07");
    private static final List<String> MON_VSAT_THPT_LIST = Arrays.asList("TO", "LY", "HH", "SH", "LS", "DL", "AN",
            "NV");

    // Colors
    private final Color PRIMARY_COLOR = new Color(44, 62, 80);
    private final Color SUCCESS_COLOR = new Color(39, 174, 96);
    private final Color DANGER_COLOR = new Color(231, 76, 60);
    private final Color BG_COLOR = Color.WHITE;
    private final Color CARD_BG = new Color(250, 252, 255);
    private final Color FIELD_BG = new Color(248, 249, 250);
    private final Color BORDER_COLOR = new Color(220, 220, 230);
    private final Color LABEL_COLOR = new Color(80, 80, 90);
    private final Color SECTION_COLOR = new Color(52, 73, 94);

    // Fonts
    private final Font MAIN_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    private final Font BOLD_FONT = new Font("Segoe UI", Font.BOLD, 14);
    private final Font LABEL_FONT = new Font("Segoe UI", Font.PLAIN, 13);
    private final Font SECTION_FONT = new Font("Segoe UI", Font.BOLD, 14);
    private final Font FIELD_FONT = new Font("Segoe UI", Font.PLAIN, 15);

    private final ConversionTableController controller;
    private final ConversionTableDTO currentDto;

    // Form fields
    private JComboBox<String> cbPhuongThuc;
    private JComboBox<String> cbToHop;
    private JComboBox<String> cbMon;
    private JTextField txtDiemA;
    private JTextField txtDiemB;
    private JTextField txtDiemC;
    private JTextField txtDiemD;

    // Error labels
    private JLabel lblErrorDiemA;
    private JLabel lblErrorDiemB;
    private JLabel lblErrorDiemC;
    private JLabel lblErrorDiemD;

    public ConversionTableFormDialog(Frame parent, ConversionTableController controller, ConversionTableDTO editDto) {
        super(parent, editDto == null ? "Thêm bảng quy đổi" : "Cập nhật bảng quy đổi", true);
        this.controller = controller;
        this.currentDto = editDto;

        initializeFields();
        initComponents();
        if (editDto != null) {
            loadDataForEdit();
        }
        setSize(720, 650);
        setLocationRelativeTo(parent);
        setResizable(false);
    }

    private void initializeFields() {
        lblErrorDiemA = new JLabel();
        lblErrorDiemB = new JLabel();
        lblErrorDiemC = new JLabel();
        lblErrorDiemD = new JLabel();

        txtDiemA = createTextField();
        txtDiemB = createTextField();
        txtDiemC = createTextField();
        txtDiemD = createTextField();

        cbPhuongThuc = createComboBox(PHUONG_THUC_LIST.toArray(new String[0]));
        cbPhuongThuc.addActionListener(e -> onPhuongThucChanged());

        cbToHop = createComboBoxWithEmpty();
        for (String th : TO_HOP_LIST) {
            cbToHop.addItem(th);
        }

        cbMon = createComboBoxWithEmpty();
        updateMonComboBox();
    }

    private void initComponents() {
        setLayout(new BorderLayout(0, 0));
        getContentPane().setBackground(BG_COLOR);

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(PRIMARY_COLOR);
        headerPanel.setBorder(new EmptyBorder(20, 30, 20, 30));
        headerPanel.setPreferredSize(new Dimension(0, 65));

        JLabel lblTitle = new JLabel(currentDto == null ? "THÊM BẢNG QUY ĐỔI ĐIỂM" : "CẬP NHẬT BẢNG QUY ĐỔI ĐIỂM");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setForeground(Color.WHITE);
        headerPanel.add(lblTitle, BorderLayout.WEST);
        add(headerPanel, BorderLayout.NORTH);

        // Form content
        JPanel content = createFormPanel();
        add(content, BorderLayout.CENTER);

        // Bottom buttons
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));
        bottom.setBackground(BG_COLOR);
        bottom.setBorder(new EmptyBorder(15, 30, 20, 30));

        JButton btnCancel = createButton("Hủy", DANGER_COLOR, false);
        JButton btnSave = createButton(currentDto == null ? "Thêm mới" : "Lưu thay đổi", SUCCESS_COLOR, true);

        btnCancel.addActionListener(e -> dispose());
        btnSave.addActionListener(e -> saveConversionTable());

        bottom.add(btnCancel);
        bottom.add(btnSave);
        add(bottom, BorderLayout.SOUTH);
    }

    private JPanel createFormPanel() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(BG_COLOR);
        mainPanel.setBorder(new EmptyBorder(20, 30, 10, 30));

        // Section 1: Thông tin cơ bản
        mainPanel.add(createSectionLabel("THÔNG TIN CƠ BẢN"));
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(createBasicInfoCard());
        mainPanel.add(Box.createVerticalStrut(20));

        // Section 2: Thông tin điểm
        mainPanel.add(createSectionLabel("THÔNG TIN ĐIỂM"));
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(createScoreCard());
        mainPanel.add(Box.createVerticalStrut(15));

        // Note
        JLabel lblNote = new JLabel("Điểm A < Điểm B | Điểm C < Điểm D");
        lblNote.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        lblNote.setForeground(new Color(130, 130, 140));
        lblNote.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPanel.add(lblNote);

        return mainPanel;
    }

    private JLabel createSectionLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(SECTION_FONT);
        lbl.setForeground(SECTION_COLOR);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        return lbl;
    }

    private JPanel createBasicInfoCard() {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER_COLOR, 1),
                new EmptyBorder(18, 20, 18, 20)));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 160));

        // Row 1: Phương thức - full width
        card.add(createFullWidthRow("Phương thức *", cbPhuongThuc, 220));
        card.add(Box.createVerticalStrut(15));

        // Row 2: Tổ hợp + Môn side by side
        JPanel row2 = new JPanel(new GridLayout(1, 2, 20, 0));
        row2.setBackground(CARD_BG);
        row2.add(createComboRow("Tổ hợp", cbToHop, 180));
        row2.add(createComboRow("Môn *", cbMon, 180));
        card.add(row2);

        return card;
    }

    private JPanel createScoreCard() {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER_COLOR, 1),
                new EmptyBorder(18, 20, 18, 20)));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 220));

        // Row 1: DiemA and DiemB - bigger fields
        JPanel row1 = new JPanel(new GridLayout(1, 2, 25, 0));
        row1.setBackground(CARD_BG);
        row1.add(createScoreField("Điểm A (Tối thiểu) *", txtDiemA, lblErrorDiemA));
        row1.add(createScoreField("Điểm B (Tối đa) *", txtDiemB, lblErrorDiemB));
        card.add(row1);
        card.add(Box.createVerticalStrut(18));

        // Row 2: DiemC and DiemD - bigger fields
        JPanel row2 = new JPanel(new GridLayout(1, 2, 25, 0));
        row2.setBackground(CARD_BG);
        row2.add(createScoreField("Điểm quy đổi C *", txtDiemC, lblErrorDiemC));
        row2.add(createScoreField("Điểm quy đổi D *", txtDiemD, lblErrorDiemD));
        card.add(row2);

        return card;
    }

    private JPanel createFullWidthRow(String labelText, JComboBox<String> comboBox, int labelWidth) {
        JPanel row = new JPanel(new GridBagLayout());
        row.setBackground(CARD_BG);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 0, 0, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        JLabel lbl = new JLabel(labelText);
        lbl.setFont(LABEL_FONT);
        lbl.setForeground(LABEL_COLOR);
        lbl.setPreferredSize(new Dimension(labelWidth, 30));

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0;
        row.add(lbl, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        row.add(comboBox, gbc);

        return row;
    }

    private JPanel createComboRow(String labelText, JComboBox<String> comboBox, int labelWidth) {
        JPanel row = new JPanel(new GridBagLayout());
        row.setBackground(CARD_BG);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 0, 0, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        JLabel lbl = new JLabel(labelText);
        lbl.setFont(LABEL_FONT);
        lbl.setForeground(LABEL_COLOR);
        lbl.setPreferredSize(new Dimension(labelWidth, 30));

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0;
        row.add(lbl, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        row.add(comboBox, gbc);

        return row;
    }

    private JPanel createScoreField(String labelText, JTextField textField, JLabel errorLabel) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(CARD_BG);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lbl = new JLabel(labelText);
        lbl.setFont(LABEL_FONT);
        lbl.setForeground(LABEL_COLOR);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(lbl);
        panel.add(Box.createVerticalStrut(6));

        textField.setFont(FIELD_FONT);
        textField.setPreferredSize(new Dimension(0, 42));
        textField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        textField.setBackground(FIELD_BG);
        textField.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER_COLOR, 1),
                new EmptyBorder(0, 14, 0, 14)));
        panel.add(textField);

        errorLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        errorLabel.setForeground(DANGER_COLOR);
        errorLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(Box.createVerticalStrut(4));
        panel.add(errorLabel);

        return panel;
    }

    private JComboBox<String> createComboBox(String[] items) {
        JComboBox<String> cb = new JComboBox<>(items);
        cb.setFont(FIELD_FONT);
        cb.setPreferredSize(new Dimension(0, 42));
        cb.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        cb.setBackground(FIELD_BG);
        cb.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER_COLOR, 1),
                new EmptyBorder(0, 14, 0, 14)));
        return cb;
    }

    private JComboBox<String> createComboBoxWithEmpty() {
        JComboBox<String> cb = new JComboBox<>();
        cb.setFont(FIELD_FONT);
        cb.setPreferredSize(new Dimension(0, 42));
        cb.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        cb.setBackground(FIELD_BG);
        cb.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER_COLOR, 1),
                new EmptyBorder(0, 14, 0, 14)));
        cb.addItem("");
        return cb;
    }

    private JTextField createTextField() {
        JTextField txt = new JTextField(15);
        txt.setFont(FIELD_FONT);
        txt.setBackground(FIELD_BG);
        txt.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER_COLOR, 1),
                new EmptyBorder(0, 14, 0, 14)));
        ((AbstractDocument) txt.getDocument()).setDocumentFilter(new DecimalDocumentFilter());
        return txt;
    }

    private void onPhuongThucChanged() {
        updateMonComboBox();
    }

    private void updateMonComboBox() {
        if (cbPhuongThuc == null || cbMon == null) {
            return;
        }

        String phuongThuc = (String) cbPhuongThuc.getSelectedItem();
        if (phuongThuc == null) {
            return;
        }

        cbMon.removeAllItems();
        cbMon.addItem("");

        if ("IELTS".equals(phuongThuc) || "TOEIC".equals(phuongThuc)) {
            cbMon.addItem(phuongThuc);
            if (cbToHop != null) {
                cbToHop.setEnabled(false);
                cbToHop.setSelectedIndex(0);
            }
        } else {
            for (String mon : MON_VSAT_THPT_LIST) {
                cbMon.addItem(mon);
            }
            if (cbToHop != null) {
                cbToHop.setEnabled(true);
            }
        }
    }

    private void loadDataForEdit() {
        if (currentDto == null) {
            return;
        }

        if (currentDto.getPhuongThuc() != null) {
            cbPhuongThuc.setSelectedItem(currentDto.getPhuongThuc());
            updateMonComboBox();
        }

        if (currentDto.getMon() != null) {
            cbMon.setSelectedItem(currentDto.getMon());
        }

        if (currentDto.getToHop() != null && !currentDto.getToHop().isEmpty()) {
            cbToHop.setSelectedItem(currentDto.getToHop());
        }

        if (txtDiemA != null && currentDto.getDiemA() != null) {
            txtDiemA.setText(currentDto.getDiemA().toString());
        }
        if (txtDiemB != null && currentDto.getDiemB() != null) {
            txtDiemB.setText(currentDto.getDiemB().toString());
        }
        if (txtDiemC != null && currentDto.getDiemC() != null) {
            txtDiemC.setText(currentDto.getDiemC().toString());
        }
        if (txtDiemD != null && currentDto.getDiemD() != null) {
            txtDiemD.setText(currentDto.getDiemD().toString());
        }
    }

    private void clearAllErrors() {
        if (lblErrorDiemA != null)
            lblErrorDiemA.setText("");
        if (lblErrorDiemB != null)
            lblErrorDiemB.setText("");
        if (lblErrorDiemC != null)
            lblErrorDiemC.setText("");
        if (lblErrorDiemD != null)
            lblErrorDiemD.setText("");
    }

    private boolean validateForm() {
        clearAllErrors();
        boolean isValid = true;
        String phuongThuc = (String) cbPhuongThuc.getSelectedItem();
        String mon = (String) cbMon.getSelectedItem();

        if (phuongThuc == null || phuongThuc.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn Phương thức!", "Lỗi validation",
                    JOptionPane.WARNING_MESSAGE);
            return false;
        }

        if (mon == null || mon.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn Môn!", "Lỗi validation", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        Double diemA = parseScore(txtDiemA.getText(), lblErrorDiemA, "Điểm A");
        if (diemA == null)
            isValid = false;

        Double diemB = parseScore(txtDiemB.getText(), lblErrorDiemB, "Điểm B");
        if (diemB == null)
            isValid = false;

        Double diemC = parseScore(txtDiemC.getText(), lblErrorDiemC, "Điểm C");
        if (diemC == null)
            isValid = false;

        Double diemD = parseScore(txtDiemD.getText(), lblErrorDiemD, "Điểm D");
        if (diemD == null)
            isValid = false;

        if (!isValid)
            return false;

        if (diemA != null && diemB != null && diemA >= diemB) {
            lblErrorDiemA.setText("Điểm A phải nhỏ hơn Điểm B");
            lblErrorDiemB.setText("Điểm B phải lớn hơn Điểm A");
            JOptionPane.showMessageDialog(this, "Điểm A phải nhỏ hơn Điểm B!", "Lỗi validation",
                    JOptionPane.WARNING_MESSAGE);
            return false;
        }

        if (diemC != null && diemD != null && diemC >= diemD) {
            lblErrorDiemC.setText("Điểm C phải nhỏ hơn Điểm D");
            lblErrorDiemD.setText("Điểm D phải lớn hơn Điểm C");
            JOptionPane.showMessageDialog(this, "Điểm C phải nhỏ hơn Điểm D!", "Lỗi validation",
                    JOptionPane.WARNING_MESSAGE);
            return false;
        }

        if (!isValidScoreRange(phuongThuc, diemA) || !isValidScoreRange(phuongThuc, diemB)
                || !isValidScoreRange(phuongThuc, diemC) || !isValidScoreRange(phuongThuc, diemD)) {
            JOptionPane.showMessageDialog(this,
                    "Điểm số phải nằm trong khoảng hợp lệ!\n- VSAT/THPT: 0 - 10\n- IELTS: 0 - 9\n- TOEIC: 0 - 990\n- DGNL: 0 - 1000",
                    "Lỗi validation", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        return true;
    }

    private Double parseScore(String text, JLabel errorLabel, String fieldName) {
        if (text == null || text.trim().isEmpty()) {
            errorLabel.setText(fieldName + " không được trống");
            return null;
        }
        try {
            double value = Double.parseDouble(text.trim());
            if (value < 0) {
                errorLabel.setText(fieldName + " phải >= 0");
                return null;
            }
            return value;
        } catch (NumberFormatException e) {
            errorLabel.setText(fieldName + " phải là số hợp lệ");
            return null;
        }
    }

    private boolean isValidScoreRange(String phuongThuc, Double value) {
        if (value == null)
            return true;
        return switch (phuongThuc) {
            case "IELTS" -> value >= 0 && value <= 9;
            case "DGNL" -> value >= 0 && value <= 1000;
            case "TOEIC" -> value >= 0 && value <= 990;
            default -> value >= 0 && value <= 10;
        };
    }

    private void saveConversionTable() {
        if (!validateForm())
            return;

        String phuongThuc = (String) cbPhuongThuc.getSelectedItem();
        String toHop = (String) cbToHop.getSelectedItem();
        String mon = (String) cbMon.getSelectedItem();

        ConversionTableDTO dto = new ConversionTableDTO();
        if (currentDto != null)
            dto.setId(currentDto.getId());
        dto.setPhuongThuc(phuongThuc);
        dto.setMon(mon);

        if (toHop != null && !toHop.trim().isEmpty()) {
            dto.setToHop(toHop);
        } else {
            dto.setToHop(null);
        }

        dto.setDiemA(parseDouble(txtDiemA.getText()));
        dto.setDiemB(parseDouble(txtDiemB.getText()));
        dto.setDiemC(parseDouble(txtDiemC.getText()));
        dto.setDiemD(parseDouble(txtDiemD.getText()));

        try {
            if (currentDto == null) {
                controller.addConversionTable(dto);
                JOptionPane.showMessageDialog(this, "Thêm bảng quy đổi thành công!", "Thành công",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                controller.updateConversionTable(currentDto.getId(), dto);
                JOptionPane.showMessageDialog(this, "Cập nhật bảng quy đổi thành công!", "Thành công",
                        JOptionPane.INFORMATION_MESSAGE);
            }
            dispose();
        } catch (Exception e) {
            String message = e.getMessage();
            if (message != null && message.contains("đã tồn tại")) {
                JOptionPane.showMessageDialog(this,
                        "Bảng quy đổi đã tồn tại cho tổ hợp này!\nVui lòng kiểm tra lại thông tin.",
                        "Trùng dữ liệu", JOptionPane.WARNING_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Lỗi: " + message, "Lỗi hệ thống", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private Double parseDouble(String text) {
        if (text == null || text.trim().isEmpty())
            return null;
        return Double.parseDouble(text.trim());
    }

    private JButton createButton(String text, Color bgColor, boolean isPrimary) {
        JButton btn = new JButton(text);
        btn.setFont(BOLD_FONT);
        btn.setBackground(bgColor);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(isPrimary ? 160 : 100, 42));
        return btn;
    }

    private static class DecimalDocumentFilter extends DocumentFilter {
        @Override
        public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr)
                throws BadLocationException {
            if (isValidInput(fb.getDocument().getText(0, fb.getDocument().getLength()) + string)) {
                super.insertString(fb, offset, string, attr);
            }
        }

        @Override
        public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
                throws BadLocationException {
            String currentText = fb.getDocument().getText(0, fb.getDocument().getLength());
            String newText = currentText.substring(0, offset) + text + currentText.substring(offset + length);
            if (isValidInput(newText)) {
                super.replace(fb, offset, length, text, attrs);
            }
        }

        private boolean isValidInput(String text) {
            if (text.isEmpty())
                return true;
            return text.matches("^[0-9]*\\.?[0-9]*$");
        }
    }
}