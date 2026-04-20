package com.example.managementadmissionwf.ui.panel.score;

import com.example.managementadmissionwf.dto.score.ScoreDTO;

import javax.swing.*;
import javax.swing.text.NumberFormatter;
import java.awt.*;
import java.text.DecimalFormat;

public class ScoreFormDialog extends JDialog {

    private ScoreDTO score;
    private boolean saved = false;

    private JTextField txtCccd;
    private JTextField txtSobaodanh;
    private JComboBox<String> cboPhuongThuc;

    private JFormattedTextField txtToan, txtLy, txtHoa, txtSinh, txtSu, txtDia, txtVan;
    private JFormattedTextField txtN1Thi, txtN1Cc, txtNl1, txtNk1, txtNk2;

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
        add(createButtonPanel(), BorderLayout.SOUTH);
    }

    private JPanel createExamPanel() {
        JPanel p = basePanel();

        addRow(p, 0, "CCCD:", txtCccd, "SBD:", txtSobaodanh);

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
        if (!validateForm()) return;

        if (score == null) score = new ScoreDTO();

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

        saved = true;
        dispose();
    }

    private boolean validateForm() {
        if (txtCccd.getText().length() != 12) {
            JOptionPane.showMessageDialog(this, "CCCD không hợp lệ");
            return false;
        }
        if (txtSobaodanh.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Không có SBD");
            return false;
        }
        return true;
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
        formatter.setAllowsInvalid(true);

        JFormattedTextField f = new JFormattedTextField(formatter);
        f.setColumns(10);

        addPlaceholder(f, "0.0");

        return f;
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
        try {
            String t = f.getText().trim();
            if (t.isEmpty() || t.equals("0.0")) return 0.0;
            return Double.parseDouble(t);
        } catch (Exception e) {
            return 0.0;
        }
    }

    private void setValue(JFormattedTextField f, Double v) {
        if (v != null) {
            f.setText(String.valueOf(v));
            f.setForeground(Color.BLACK);
        }
    }

    public boolean isSaved() { return saved; }
    public ScoreDTO getScore() { return score; }
}