package com.example.managementadmissionwf.ui.panel.candidate;

import com.example.managementadmissionwf.dto.candidate.CandidateDTO;
import com.example.managementadmissionwf.ui.util.UIConstants;

import javax.swing.*;
import java.awt.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Form Dialog for Add/Edit Candidate
 */
public class CandidateFormDialog extends JDialog {
    private CandidateDTO candidate;
    private boolean saved = false;
    
    // Form fields
    private JTextField txtCccd;
    private JTextField txtSobaodanh;
    private JTextField txtHo;
    private JTextField txtTen;
    private JFormattedTextField txtNgaySinh;
    private JTextField txtDienThoai;
    private JTextField txtEmail;
    private JComboBox<String> cboGioiTinh;
    private JTextField txtNoiSinh;
    private JComboBox<String> cboDoiTuong;
    private JComboBox<String> cboKhuVuc;
    
    private JButton btnSave;
    private JButton btnCancel;
    
    private final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
    
    public CandidateFormDialog(Frame parent, String title, CandidateDTO candidate) {
        super(parent, title, true);
        this.candidate = candidate;
        initComponents();
        loadCandidateData();
        setLocationRelativeTo(parent);
    }
    
    private void initComponents() {
        setSize(600, 500);
        setLayout(new BorderLayout());
        
        // Main form panel
        JPanel formPanel = createFormPanel();
        add(formPanel, BorderLayout.CENTER);
        
        // Button panel
        JPanel buttonPanel = createButtonPanel();
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(Color.WHITE);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // CCCD
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(createLabel("CCCD *:"), gbc);
        gbc.gridx = 1;
        txtCccd = createTextField();
        panel.add(txtCccd, gbc);
        
        // Số báo danh
        gbc.gridx = 2;
        panel.add(createLabel("SBD *:"), gbc);
        gbc.gridx = 3;
        txtSobaodanh = createTextField();
        panel.add(txtSobaodanh, gbc);
        
        // Họ
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(createLabel("Họ *:"), gbc);
        gbc.gridx = 1;
        txtHo = createTextField();
        panel.add(txtHo, gbc);
        
        // Tên
        gbc.gridx = 2;
        panel.add(createLabel("Tên *:"), gbc);
        gbc.gridx = 3;
        txtTen = createTextField();
        panel.add(txtTen, gbc);
        
        // Ngày sinh
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(createLabel("Ngày sinh:"), gbc);
        gbc.gridx = 1;
        txtNgaySinh = createFormattedTextField();
        panel.add(txtNgaySinh, gbc);
        
        // Giới tính
        gbc.gridx = 2;
        panel.add(createLabel("Giới tính:"), gbc);
        gbc.gridx = 3;
        cboGioiTinh = createComboBox(new String[]{"Nam", "Nữ"});
        panel.add(cboGioiTinh, gbc);
        
        // Số điện thoại
        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(createLabel("Số điện thoại:"), gbc);
        gbc.gridx = 1;
        txtDienThoai = createTextField();
        panel.add(txtDienThoai, gbc);
        
        // Email
        gbc.gridx = 2;
        panel.add(createLabel("Email:"), gbc);
        gbc.gridx = 3;
        txtEmail = createTextField();
        panel.add(txtEmail, gbc);
        
        // Nơi sinh
        gbc.gridx = 0; gbc.gridy = 4;
        panel.add(createLabel("Nơi sinh:"), gbc);
        gbc.gridx = 1;
        txtNoiSinh = createTextField();
        panel.add(txtNoiSinh, gbc);
        
        // Đối tượng ưu tiên
        gbc.gridx = 2;
        panel.add(createLabel("Đối tượng:"), gbc);
        gbc.gridx = 3;
        cboDoiTuong = createComboBox(new String[]{"Không", "KV1", "KV2-NT", "KV2", "KV3", "Con thương binh"});
        panel.add(cboDoiTuong, gbc);
        
        // Khu vực
        gbc.gridx = 0; gbc.gridy = 5;
        panel.add(createLabel("Khu vực:"), gbc);
        gbc.gridx = 1;
        cboKhuVuc = createComboBox(new String[]{"KV1", "KV2", "KV3"});
        panel.add(cboKhuVuc, gbc);
        
        return panel;
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
    
    private void loadCandidateData() {
        if (candidate != null) {
            txtCccd.setText(candidate.getCccd());
            txtCccd.setEnabled(false); // Cannot edit CCCD
            txtSobaodanh.setText(candidate.getSobaodanh());
            txtHo.setText(candidate.getHo());
            txtTen.setText(candidate.getTen());
            
            if (candidate.getNgaySinh() != null) {
                txtNgaySinh.setText(sdf.format(candidate.getNgaySinh()));
            }
            
            txtDienThoai.setText(candidate.getDienThoai());
            txtEmail.setText(candidate.getEmail());
            cboGioiTinh.setSelectedItem(candidate.getGioiTinh());
            txtNoiSinh.setText(candidate.getNoiSinh());
            cboDoiTuong.setSelectedItem(candidate.getDoiTuong());
            cboKhuVuc.setSelectedItem(candidate.getKhuVuc());
        }
    }
    
    private void saveData() {
        if (!validateForm()) {
            return;
        }
        
        try {
            if (candidate == null) {
                candidate = new CandidateDTO();
            }
            
            candidate.setCccd(txtCccd.getText().trim());
            candidate.setSobaodanh(txtSobaodanh.getText().trim());
            candidate.setHo(txtHo.getText().trim());
            candidate.setTen(txtTen.getText().trim());
            
            String ngaySinhStr = txtNgaySinh.getText().trim();
            if (!ngaySinhStr.isEmpty()) {
                candidate.setNgaySinh(sdf.parse(ngaySinhStr));
            }
            
            candidate.setDienThoai(txtDienThoai.getText().trim());
            candidate.setEmail(txtEmail.getText().trim());
            candidate.setGioiTinh((String) cboGioiTinh.getSelectedItem());
            candidate.setNoiSinh(txtNoiSinh.getText().trim());
            candidate.setDoiTuong((String) cboDoiTuong.getSelectedItem());
            candidate.setKhuVuc((String) cboKhuVuc.getSelectedItem());
            
            saved = true;
            dispose();
            
        } catch (ParseException e) {
            JOptionPane.showMessageDialog(this, 
                "Định dạng ngày sinh không đúng (dd/MM/yyyy)", 
                "Lỗi", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private boolean validateForm() {
        if (txtCccd.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "CCCD không được để trống", "Lỗi", JOptionPane.ERROR_MESSAGE);
            txtCccd.requestFocus();
            return false;
        }
        
        if (txtSobaodanh.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Số báo danh không được để trống", "Lỗi", JOptionPane.ERROR_MESSAGE);
            txtSobaodanh.requestFocus();
            return false;
        }
        
        if (txtHo.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Họ không được để trống", "Lỗi", JOptionPane.ERROR_MESSAGE);
            txtHo.requestFocus();
            return false;
        }
        
        if (txtTen.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Tên không được để trống", "Lỗi", JOptionPane.ERROR_MESSAGE);
            txtTen.requestFocus();
            return false;
        }
        
        return true;
    }
    
    public CandidateDTO getCandidate() {
        return candidate;
    }
    
    public boolean isSaved() {
        return saved;
    }
    
    // Helper methods
    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(UIConstants.FONT_BODY.equals("Segoe UI") ?
            new Font("Segoe UI", Font.PLAIN, 14) : new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        return label;
    }
    
    private JTextField createTextField() {
        JTextField field = new JTextField();
        field.setPreferredSize(new Dimension(150, 30));
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        return field;
    }
    
    private JFormattedTextField createFormattedTextField() {
        JFormattedTextField field = new JFormattedTextField(sdf);
        field.setPreferredSize(new Dimension(150, 30));
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setToolTipText("dd/MM/yyyy");
        return field;
    }
    
    private JComboBox<String> createComboBox(String[] items) {
        JComboBox<String> comboBox = new JComboBox<>(items);
        comboBox.setPreferredSize(new Dimension(150, 30));
        comboBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        return comboBox;
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