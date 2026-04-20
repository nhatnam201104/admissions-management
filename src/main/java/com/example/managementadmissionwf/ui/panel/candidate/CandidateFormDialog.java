package com.example.managementadmissionwf.ui.panel.candidate;

import com.example.managementadmissionwf.dto.candidate.CandidateDTO;
import com.example.managementadmissionwf.mapper.CandidateMapper;
import com.example.managementadmissionwf.ui.util.UIConstants;

import javax.swing.*;
import java.awt.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
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
    private JSpinner spnNgaySinh;
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
        spnNgaySinh = createDateSpinner();
        panel.add(spnNgaySinh, gbc);
        
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
            	spnNgaySinh.setValue(CandidateMapper.toDate(candidate.getNgaySinh()));
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
        
        if (candidate == null) {
		    candidate = new CandidateDTO();
		}
		
		candidate.setCccd(txtCccd.getText().trim());
		candidate.setSobaodanh(txtSobaodanh.getText().trim());
		candidate.setHo(txtHo.getText().trim());
		candidate.setTen(txtTen.getText().trim());
		candidate.setNgaySinh(CandidateMapper.toLocalDate((Date) spnNgaySinh.getValue()));
		
		String dt = txtDienThoai.getText().trim();
	    candidate.setDienThoai(dt.isEmpty() ? null : dt);
	    
	    String mail = txtEmail.getText().trim();
	    candidate.setEmail(mail.isEmpty() ? null : mail);
	    
		candidate.setGioiTinh((String) cboGioiTinh.getSelectedItem());
		candidate.setNoiSinh(txtNoiSinh.getText().trim());
		candidate.setDoiTuong((String) cboDoiTuong.getSelectedItem());
		candidate.setKhuVuc((String) cboKhuVuc.getSelectedItem());
		
		saved = true;
		dispose();
    }
    
    private boolean validateForm() {
        String cccd = txtCccd.getText().trim();
        if (cccd.isEmpty()) {
            showError("CCCD không được để trống", txtCccd);
            return false;
        }
        if (!cccd.matches("^\\d{12}$")) {
            showError("CCCD phải bao gồm đúng 12 chữ số", txtCccd);
            return false;
        }

        String sbd = txtSobaodanh.getText().trim();
        if (sbd.isEmpty()) {
            showError("Số báo danh không được để trống", txtSobaodanh);
            return false;
        }
        
        if (!sbd.matches("^\\d+$")) {
            showError("Số báo danh chỉ được chứa các chữ số", txtSobaodanh);
            return false;
        }
        
        String regexName = "^[\\p{L}\\s]+$";

        String ho = txtHo.getText().trim();
        if (ho.isEmpty()) {
            showError("Họ không được để trống!", txtHo);
            return false;
        }
        if (!ho.matches(regexName)) {
            showError("Họ không hợp lệ (không được chứa số hoặc ký tự đặc biệt)!", txtHo);
            return false;
        }

        String ten = txtTen.getText().trim();
        if (ten.isEmpty()) {
            showError("Tên không được để trống!", txtTen);
            return false;
        }
        if (!ten.matches(regexName)) {
            showError("Tên không hợp lệ (không được chứa số hoặc ký tự đặc biệt)!", txtTen);
            return false;
        }
        
        Date selectedDate = (Date) spnNgaySinh.getValue(); 
        
        if (selectedDate == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn ngày sinh!", "Lỗi nhập liệu", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        LocalDate dob = selectedDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate today = LocalDate.now();

        if (dob.isAfter(today)) {
            JOptionPane.showMessageDialog(this, "Ngày sinh không hợp lệ (không thể là ngày trong tương lai)!", "Lỗi nhập liệu", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        int age = java.time.Period.between(dob, today).getYears();
        if (age < 15) {
            JOptionPane.showMessageDialog(this, "Thí sinh không hợp lệ (Phải từ 15 tuổi trở lên. Tuổi hiện tại: " + age + ")", "Lỗi nhập liệu", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        String sdt = txtDienThoai.getText().trim();
        if (!sdt.isEmpty() && !sdt.matches("^0\\d{9}$")) {
            showError("Số điện thoại phải có 10 chữ số và bắt đầu bằng số 0", txtDienThoai);
            return false;
        }

        String email = txtEmail.getText().trim();
        if (!email.isEmpty() && !email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            showError("Email không đúng định dạng", txtEmail);
            return false;
        }

        return true;
    }

    private void showError(String message, JTextField field) {
        JOptionPane.showMessageDialog(this, message, "Lỗi nhập liệu", JOptionPane.WARNING_MESSAGE);
        field.requestFocus();
    }
    
    public CandidateDTO getCandidate() {
        return candidate;
    }
    
    public boolean isSaved() {
        return saved;
    }
    
    public void setSaved(boolean saved) {
        this.saved = saved;
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
    
    private JSpinner createDateSpinner() {
        SpinnerDateModel model = new SpinnerDateModel();
        JSpinner spinner = new JSpinner(model);
        JSpinner.DateEditor editor = new JSpinner.DateEditor(spinner, "dd/MM/yyyy");
        spinner.setEditor(editor);
        spinner.setPreferredSize(new Dimension(150, 30));
        spinner.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        return spinner;
    }
}