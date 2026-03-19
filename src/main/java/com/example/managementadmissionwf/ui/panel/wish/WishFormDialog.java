package com.example.managementadmissionwf.ui.panel.wish;

import com.example.managementadmissionwf.dto.wish.WishDTO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.awt.event.*;

public class WishFormDialog extends JDialog {

    private JComboBox<String> cbCandidate, cbMajor, cbOrder;
    private JTextField txtCombination, txtPriorityScore, txtBonusScore;
    private JLabel lblTotalScore;
    private JButton btnSave, btnCancel;

    private WishDTO editingDto;
    private WishDTO resultDto;

    public WishFormDialog(Frame parent, String title, WishDTO dto) {
        super(parent, title, true);
        setLayout(new BorderLayout());
        setBackground(new Color(245, 247, 250));
        setSize(600, 580); 
        setLocationRelativeTo(parent);
        setResizable(false);

        this.editingDto = dto;

        add(createHeaderPanel(), BorderLayout.NORTH);
        add(createMainContent(), BorderLayout.CENTER);
        add(createActionPanel(), BorderLayout.SOUTH);

        if (dto != null) populateForm(dto);
    }

    private JPanel createHeaderPanel() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(33, 150, 243));
        header.setBorder(new EmptyBorder(14, 20, 14, 20));

        JLabel titleLabel = new JLabel("Chi tiết Nguyện vọng", SwingConstants.LEFT);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);

        header.add(titleLabel, BorderLayout.WEST);

        return header;
    }

    private JPanel createMainContent() {
        JPanel content = new JPanel(new GridBagLayout());
        content.setBackground(new Color(245, 247, 250));
        content.setBorder(new EmptyBorder(15, 25, 15, 25));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 8, 8, 8);  // giảm insets tối đa để gọn
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;

        // Thông tin nguyện vọng
        addSectionTitle(content, gbc, "Thông tin nguyện vọng", 0);

        gbc.gridy = 1; gbc.gridx = 0; gbc.gridwidth = 1;
        content.add(createLabel("Số CCCD / Định danh:"), gbc);

        gbc.gridx = 1; gbc.gridwidth = 1;
        cbCandidate = new JComboBox<>(new String[]{
            "079204001234 - Nguyễn Văn A",
            "079204005678 - Trần Thị B",
            "079204009876 - Lê Văn C",
            "079204003210 - Phạm Thị D"
        });
        styleComboBox(cbCandidate);
        content.add(cbCandidate, gbc);

        gbc.gridy = 2; gbc.gridx = 0;
        content.add(createLabel("Thứ tự NV:"), gbc);

        gbc.gridx = 1;
        String[] orders = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15"};
        cbOrder = new JComboBox<>(orders);
        styleComboBox(cbOrder);
        content.add(cbOrder, gbc);

        gbc.gridy = 3; gbc.gridx = 0;
        content.add(createLabel("Mã ngành:"), gbc);

        gbc.gridx = 1;
        cbMajor = new JComboBox<>(new String[]{
            "7480201 - Công nghệ thông tin",
            "7480103 - Kỹ thuật phần mềm",
            "7480104 - Hệ thống thông tin",
            "7480202 - An toàn thông tin"
        });
        styleComboBox(cbMajor);
        content.add(cbMajor, gbc);

        gbc.gridy = 4; gbc.gridx = 0;
        content.add(createLabel("Tổ hợp môn:"), gbc);

        gbc.gridx = 1;
        txtCombination = new JTextField("A00 (Toán, Lý, Hóa)");
        txtCombination.setEditable(false);
        txtCombination.setBackground(new Color(250, 250, 250));
        txtCombination.setBorder(new MatteBorder(1, 1, 1, 1, new Color(200, 200, 200)));
        txtCombination.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtCombination.setPreferredSize(new Dimension(320, 34));  
        content.add(txtCombination, gbc);

        // Bảng điểm - nhóm gọn lại
        gbc.gridy = 6; gbc.gridx = 0; gbc.gridwidth = 2;
        addSectionTitle(content, gbc, "Bảng điểm & Điểm xét tuyển", 6);

        gbc.gridy = 7; gbc.gridwidth = 1; gbc.gridx = 0;
        content.add(createLabel("Điểm thi:"), gbc);

        gbc.gridx = 1;
        JLabel lblExamScore = new JLabel("26.50");
        lblExamScore.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblExamScore.setForeground(new Color(33, 150, 243));
        content.add(lblExamScore, gbc);

        gbc.gridy = 8; gbc.gridx = 0;
        content.add(createLabel("Điểm ưu tiên:"), gbc);

        gbc.gridx = 1;
        txtPriorityScore = new JTextField("1.00");
        styleTextField(txtPriorityScore);
        content.add(txtPriorityScore, gbc);

        gbc.gridy = 9; gbc.gridx = 0;
        content.add(createLabel("Điểm cộng thêm:"), gbc);

        gbc.gridx = 1;
        txtBonusScore = new JTextField("0.50");
        styleTextField(txtBonusScore);
        content.add(txtBonusScore, gbc);

        gbc.gridy = 10; gbc.gridx = 0;
        content.add(createLabel("Tổng điểm xét tuyển:"), gbc);

        gbc.gridx = 1;
        lblTotalScore = new JLabel("27.50");
        lblTotalScore.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTotalScore.setForeground(new Color(46, 204, 113));
        content.add(lblTotalScore, gbc);

        setupAutoCalculate();

        return content;
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        label.setForeground(new Color(50, 50, 50));
        return label;
    }

    private void addSectionTitle(JPanel panel, GridBagConstraints gbc, String text, int y) {
        gbc.gridy = y;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(16, 5, 8, 5);  

        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lbl.setForeground(new Color(33, 150, 243));
        panel.add(lbl, gbc);

        gbc.insets = new Insets(8, 8, 8, 8);
    }

    private void styleComboBox(JComboBox<String> combo) {
        combo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        combo.setBackground(Color.WHITE);
        combo.setBorder(new MatteBorder(1, 1, 1, 1, new Color(180, 180, 180)));
        combo.setFocusable(false);
        combo.setPreferredSize(new Dimension(320, 36));  
    }

    private void styleTextField(JTextField field) {
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBorder(new MatteBorder(1, 1, 1, 1, new Color(180, 180, 180)));
        field.setBackground(Color.WHITE);
        field.setPreferredSize(new Dimension(320, 36));
    }

    private JPanel createActionPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 18));
        panel.setBackground(new Color(245, 247, 250));
        panel.setBorder(new MatteBorder(1, 0, 0, 0, new Color(220, 220, 220)));

        btnCancel = new JButton("Hủy bỏ");
        btnCancel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnCancel.setForeground(new Color(80, 80, 80));
        btnCancel.setBackground(Color.WHITE);
        btnCancel.setBorder(new MatteBorder(1, 1, 1, 1, new Color(160, 160, 160)));
        btnCancel.setPreferredSize(new Dimension(110, 40));
        btnCancel.setFocusPainted(false);
        btnCancel.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnCancel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btnCancel.setBorder(new MatteBorder(1, 1, 1, 1, new Color(100, 100, 100)));
            }
            @Override
            public void mouseExited(MouseEvent e) {
                btnCancel.setBorder(new MatteBorder(1, 1, 1, 1, new Color(160, 160, 160)));
            }
        });

        btnCancel.addActionListener(e -> dispose());

        btnSave = new JButton("Lưu cập nhật");
        btnSave.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnSave.setForeground(Color.WHITE);
        btnSave.setBackground(new Color(46, 204, 113));
        btnSave.setBorderPainted(false);
        btnSave.setPreferredSize(new Dimension(140, 40));
        btnSave.setFocusPainted(false);
        btnSave.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSave.addActionListener(e -> validateAndSave());

        panel.add(btnCancel);
        panel.add(btnSave);

        return panel;
    }

    private void setupAutoCalculate() {
        Runnable calc = () -> {
            try {
                double base = 26.50;
                double ut = parseDoubleSafe(txtPriorityScore.getText(), 0.0);
                double cong = parseDoubleSafe(txtBonusScore.getText(), 0.0);
                double total = base + ut + cong;
                lblTotalScore.setText(String.format("%.2f", total));
                lblTotalScore.setForeground(total >= 25 ? new Color(46, 204, 113) : new Color(231, 76, 60));
            } catch (Exception ex) {
                lblTotalScore.setText("—");
                lblTotalScore.setForeground(Color.GRAY);
            }
        };

        ActionListener al = e -> calc.run();
        cbCandidate.addActionListener(al);
        cbMajor.addActionListener(al);

        FocusAdapter fa = new FocusAdapter() {
            @Override public void focusLost(FocusEvent e) { calc.run(); }
        };
        txtPriorityScore.addFocusListener(fa);
        txtBonusScore.addFocusListener(fa);

        calc.run();
    }

    private double parseDoubleSafe(String s, double defaultValue) {
        try {
            return s.trim().isEmpty() ? defaultValue : Double.parseDouble(s.trim());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    private void populateForm(WishDTO dto) {
        for (int i = 0; i < cbCandidate.getItemCount(); i++) {
            if (cbCandidate.getItemAt(i).startsWith(dto.getNnCccd())) {
                cbCandidate.setSelectedIndex(i);
                break;
            }
        }
        for (int i = 0; i < cbMajor.getItemCount(); i++) {
            if (cbMajor.getItemAt(i).startsWith(dto.getNvManganh())) {
                cbMajor.setSelectedIndex(i);
                break;
            }
        }
        cbOrder.setSelectedItem(String.valueOf(dto.getNvTt()));
        txtPriorityScore.setText(String.format("%.2f", dto.getDiemUtqd()));
        txtBonusScore.setText(String.format("%.2f", dto.getDiemCong()));
    }

    private void validateAndSave() {
        if (cbCandidate.getSelectedItem() == null || cbMajor.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn đầy đủ thí sinh và ngành!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String[] cand = ((String) cbCandidate.getSelectedItem()).split(" - ", 2);
        String[] maj = ((String) cbMajor.getSelectedItem()).split(" - ", 2);

        WishDTO dto = new WishDTO();
        if (editingDto != null) {
            dto.setId(editingDto.getId());
        }
        dto.setNnCccd(cand[0]);
        dto.setHoTenThiSinh(cand.length > 1 ? cand[1].trim() : "");
        dto.setNvManganh(maj[0]);
        dto.setTenNganh(maj.length > 1 ? maj[1].trim() : "");
        dto.setNvTt(Integer.parseInt((String) cbOrder.getSelectedItem()));
        dto.setDiemUtqd(parseDoubleSafe(txtPriorityScore.getText(), 0.0));
        dto.setDiemCong(parseDoubleSafe(txtBonusScore.getText(), 0.0));
        dto.setDiemThxt(26.50);
        dto.setDiemXettuyen(parseDoubleSafe(lblTotalScore.getText(), 26.50));
        dto.setNvKetqua("CHO_XET");

        resultDto = dto;
        JOptionPane.showMessageDialog(this, "Đã lưu nguyện vọng thành công!");
        dispose();
    }

    public WishDTO getResult() {
        return resultDto;
    }
}