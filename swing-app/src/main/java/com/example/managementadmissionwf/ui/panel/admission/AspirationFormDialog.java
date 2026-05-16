package com.example.managementadmissionwf.ui.panel.admission;

import com.example.managementadmissionwf.bus.interfaces.AspirationScoreService;
import com.example.managementadmissionwf.bus.interfaces.CandidateService;
import com.example.managementadmissionwf.config.ApplicationContextHolder;
import com.example.managementadmissionwf.dal.entity.XtNganh;
import com.example.managementadmissionwf.dal.entity.XtNganhTohop;
import com.example.managementadmissionwf.dal.entity.XtNguyenvongxettuyen;
import com.example.managementadmissionwf.dal.repository.MajorRepository;
import com.example.managementadmissionwf.dal.repository.NganhTohopRepository;
import com.example.managementadmissionwf.dal.repository.NguyenVongRepository;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Dialog thêm/sửa nguyện vọng
 */
public class AspirationFormDialog extends JDialog {

    private final AdmissionResultController controller;
    private final XtNguyenvongxettuyen editingAspiration;
    private JTextField txtCccd;
    private JLabel lblHoTen;
    private JLabel lblNgaySinh;
    private JComboBox<Integer> cboNvSo;
    private JComboBox<String> cboNganh;
    private JComboBox<String> cboTohop;
    private JComboBox<String> cboPhuongThuc;

    private String selectedManganh = null;
    private String selectedTohop = null;
    private List<XtNganh> allMajors = new ArrayList<>();
    private List<XtNganhTohop> currentTohops = new ArrayList<>();

    /** Add mode constructor */
    public AspirationFormDialog(Frame parent, AdmissionResultController controller) {
        this(parent, controller, null);
    }

    /** Edit mode constructor */
    public AspirationFormDialog(Frame parent, AdmissionResultController controller, XtNguyenvongxettuyen aspiration) {
        super(parent, aspiration == null ? "Thêm nguyện vọng" : "Sửa nguyện vọng", true);
        this.controller = controller;
        this.editingAspiration = aspiration;
        setSize(480, 520);
        setLocationRelativeTo(parent);
        initComponents();
        populateData();
    }

    private void initComponents() {
        JPanel content = new JPanel(new GridBagLayout());
        content.setBorder(new EmptyBorder(15, 15, 15, 15));
        content.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        // ===== THÔNG TIN THÍ SINH =====
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 3;
        JLabel lblTitle1 = new JLabel("Thông tin thí sinh");
        lblTitle1.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblTitle1.setForeground(new Color(52, 73, 94));
        content.add(lblTitle1, gbc);

        // CCCD
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 1;
        gbc.insets = new Insets(5, 5, 5, 5);
        content.add(new JLabel("CCCD (*):"), gbc);
        gbc.gridx = 1;
        txtCccd = new JTextField(15);
        txtCccd.addActionListener(e -> lookupCandidate());
        content.add(txtCccd, gbc);
        gbc.gridx = 2;
        JButton btnSearch = new JButton("Tìm");
        btnSearch.addActionListener(e -> lookupCandidate());
        content.add(btnSearch, gbc);

        // Họ tên
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 1;
        content.add(new JLabel("Họ tên:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 2;
        lblHoTen = new JLabel("-");
        lblHoTen.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblHoTen.setForeground(new Color(52, 152, 219));
        content.add(lblHoTen, gbc);

        // Ngày sinh
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 1;
        content.add(new JLabel("Ngày sinh:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 2;
        lblNgaySinh = new JLabel("-");
        lblNgaySinh.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        content.add(lblNgaySinh, gbc);

        // ===== NGUYỆN VỌNG =====
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 3;
        gbc.insets = new Insets(15, 5, 5, 5);
        JLabel lblTitle2 = new JLabel("Nguyện vọng");
        lblTitle2.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblTitle2.setForeground(new Color(52, 73, 94));
        content.add(lblTitle2, gbc);

        // NV số
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 1;
        gbc.insets = new Insets(5, 5, 5, 5);
        content.add(new JLabel("NV số (*):"), gbc);
        gbc.gridx = 1;
        cboNvSo = new JComboBox<>(new Integer[]{1, 2, 3, 4, 5});
        content.add(cboNvSo, gbc);

        // Ngành
        gbc.gridx = 0; gbc.gridy = 6;
        content.add(new JLabel("Ngành (*):"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 2;
        cboNganh = new JComboBox<>();
        cboNganh.addActionListener(e -> onMajorSelected());
        content.add(cboNganh, gbc);

        // Phương thức - LẤY TRƯỚC TỔ HỢP MÔN
        gbc.gridx = 0; gbc.gridy = 7;
        content.add(new JLabel("Phương thức (*):"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 2;
        cboPhuongThuc = new JComboBox<>();
        cboPhuongThuc.addActionListener(e -> onPhuongThucSelected());
        content.add(cboPhuongThuc, gbc);

        // Tổ hợp môn - ĐỂ SAU PHƯƠNG THỨC
        gbc.gridx = 0; gbc.gridy = 8;
        content.add(new JLabel("Tổ hợp môn:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 2;
        cboTohop = new JComboBox<>();
        cboTohop.addActionListener(e -> onTohopSelected());
        content.add(cboTohop, gbc);

        // Buttons
        gbc.gridx = 0; gbc.gridy = 9; gbc.gridwidth = 3;
        gbc.insets = new Insets(15, 5, 5, 5);
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnPanel.setBackground(Color.WHITE);

        JButton btnSave = new JButton("Lưu");
        btnSave.setPreferredSize(new Dimension(100, 35));
        btnSave.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnSave.setBackground(new Color(46, 204, 113));
        btnSave.setForeground(Color.WHITE);
        btnSave.addActionListener(e -> saveAspiration());
        btnPanel.add(btnSave);

        JButton btnCancel = new JButton("Hủy");
        btnCancel.setPreferredSize(new Dimension(100, 35));
        btnCancel.addActionListener(e -> dispose());
        btnPanel.add(btnCancel);

        content.add(btnPanel, gbc);

        add(content, BorderLayout.CENTER);
        
        loadMajors();
    }

    private void loadMajors() {
        MajorRepository majorRepository = ApplicationContextHolder.getBean(MajorRepository.class);
        allMajors = majorRepository.findByIsDeletedFalse();
        
        cboNganh.removeAllItems();
        for (XtNganh major : allMajors) {
            cboNganh.addItem(major.getTennganh() + " (" + major.getManganh() + ")");
        }
        
        if (!allMajors.isEmpty()) {
            cboNganh.setSelectedIndex(0);
            onMajorSelected();
        }
    }

    private void onMajorSelected() {
        int selectedIndex = cboNganh.getSelectedIndex();
        if (selectedIndex < 0 || selectedIndex >= allMajors.size()) {
            return;
        }
        
        XtNganh selectedMajor = allMajors.get(selectedIndex);
        this.selectedManganh = selectedMajor.getManganh();
        
        // Chỉ hiển thị các phương thức mà ngành này đã đăng ký
        // (các flag n_thpt, n_dgnl, n_vsat, n_tuyenthang trong xt_nganh).
        cboPhuongThuc.removeAllItems();
        if (Boolean.TRUE.equals(selectedMajor.getNThpt())) cboPhuongThuc.addItem("THPT");
        if (Boolean.TRUE.equals(selectedMajor.getNDgnl())) cboPhuongThuc.addItem("DGNL");
        if (Boolean.TRUE.equals(selectedMajor.getNVsat())) cboPhuongThuc.addItem("VSAT");
        if (Boolean.TRUE.equals(selectedMajor.getNTuyenthang())) cboPhuongThuc.addItem("TUYEN_THANG");
        if (cboPhuongThuc.getItemCount() == 0) {
            // Nếu ngành chưa cấu hình phương thức nào, fallback THPT để form
            // không bị chặn hoàn toàn (admin có thể sửa ngành sau).
            cboPhuongThuc.addItem("THPT");
        }


        
        // Auto-select first method
        cboPhuongThuc.setSelectedIndex(0);
        onPhuongThucSelected();
    }

    /**
     * Khi chọn phương thức → load tổ hợp môn và enable/disable dropdown
     */
    private void onPhuongThucSelected() {
        String phuongThuc = (String) cboPhuongThuc.getSelectedItem();
        boolean needTohop = "THPT".equals(phuongThuc) || "VSAT".equals(phuongThuc);
        
        // Load tổ hợp môn
        NganhTohopRepository nganhTohopRepository = ApplicationContextHolder.getBean(NganhTohopRepository.class);
        currentTohops = nganhTohopRepository.findByManganh(selectedManganh);
        
        cboTohop.removeAllItems();
        for (XtNganhTohop tohop : currentTohops) {
            cboTohop.addItem(tohop.getMatohop() + " - " + tohop.getThMon1() + tohop.getThMon2() + tohop.getThMon3());
        }
        
        // Enable/disable dropdown tổ hợp môn
        cboTohop.setEnabled(needTohop);
        
        if (needTohop && !currentTohops.isEmpty()) {
            cboTohop.setSelectedIndex(0);
            this.selectedTohop = currentTohops.get(0).getMatohop();
        } else {
            cboTohop.setSelectedIndex(-1);
            this.selectedTohop = null;
        }
    }

    private void onTohopSelected() {
        int selectedIndex = cboTohop.getSelectedIndex();
        if (selectedIndex >= 0 && selectedIndex < currentTohops.size()) {
            this.selectedTohop = currentTohops.get(selectedIndex).getMatohop();
        } else {
            this.selectedTohop = null;
        }
    }

    private void lookupCandidate() {
        String cccd = txtCccd.getText().trim();
        if (cccd.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập CCCD!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        CandidateService candidateService = ApplicationContextHolder.getBean(CandidateService.class);
        try {
            var candidate = candidateService.getCandidateByCccd(cccd);
            if (candidate != null) {
                lblHoTen.setText(candidate.getHoTen());
                lblNgaySinh.setText(candidate.getNgaySinh() != null ? candidate.getNgaySinh().toString() : "-");
            } else {
                lblHoTen.setText("Không tìm thấy");
                lblNgaySinh.setText("-");
                JOptionPane.showMessageDialog(this, "Không tìm thấy thí sinh với CCCD: " + cccd, "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            }
        } catch (Exception e) {
            lblHoTen.setText("Không tìm thấy");
            lblNgaySinh.setText("-");
            JOptionPane.showMessageDialog(this, "Không tìm thấy thí sinh với CCCD: " + cccd, "Cảnh báo", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void saveAspiration() {
        String cccd = txtCccd.getText().trim();
        if (cccd.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập CCCD!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            txtCccd.requestFocus();
            return;
        }

        if (cccd.length() != 12) {
            JOptionPane.showMessageDialog(this, "CCCD phải đủ 12 số!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            txtCccd.requestFocus();
            return;
        }

        if (selectedManganh == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn ngành!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String phuongThuc = (String) cboPhuongThuc.getSelectedItem();
        if (phuongThuc == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn phương thức!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Chỉ THPT và VSAT bắt buộc chọn tổ hợp
        if (("THPT".equals(phuongThuc) || "VSAT".equals(phuongThuc)) && selectedTohop == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn tổ hợp môn!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Integer nvSo = (Integer) cboNvSo.getSelectedItem();

        // Check trùng cccd + nvTt, và tối đa 5 nguyện vọng
        if (editingAspiration == null) {
            NguyenVongRepository nguyenVongRepository = ApplicationContextHolder.getBean(NguyenVongRepository.class);
            List<XtNguyenvongxettuyen> existingNVs = nguyenVongRepository.findByNnCccd(cccd);
            
            // Check trùng nguyện vọng số
            boolean exists = existingNVs.stream()
                .anyMatch(nv -> nvSo.equals(nv.getNvTt()));
            if (exists) {
                JOptionPane.showMessageDialog(this, "Nguyện vọng số " + nvSo + " đã tồn tại cho thí sinh này!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Check tối đa 5 nguyện vọng
            if (existingNVs.size() >= 5) {
                JOptionPane.showMessageDialog(this, "Thí sinh đã có 5 nguyện vọng, không thể thêm!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        // Save nguyện vọng
        XtNguyenvongxettuyen aspiration;
        if (editingAspiration != null) {
            aspiration = editingAspiration;
            aspiration.setNvManganh(selectedManganh);
            aspiration.setNvTt(nvSo);
            aspiration.setTtPhuongthuc(phuongThuc);
            aspiration.setTtThm(selectedTohop);
        } else {
            aspiration = XtNguyenvongxettuyen.builder()
                .nnCccd(cccd)
                .nvManganh(selectedManganh)
                .nvTt(nvSo)
                .ttPhuongthuc(phuongThuc)
                .ttThm(selectedTohop)
                .nvKetqua("CHO_XET")
                .build();
        }

        AspirationScoreService aspirationScoreService = ApplicationContextHolder.getBean(AspirationScoreService.class);
        var scoreResult = aspirationScoreService.calculateForAspiration(aspiration);
        if (scoreResult == null) {
            JOptionPane.showMessageDialog(this,
                "Không đủ dữ liệu điểm thi/tổ hợp để tính điểm xét tuyển.\nNguyện vọng đã được lưu nhưng đánh dấu THIEU_DIEM.",
                "Cảnh báo - Thiếu điểm", JOptionPane.WARNING_MESSAGE);
            controller.refreshData();
            dispose();
            return;
        }

        JOptionPane.showMessageDialog(this, editingAspiration == null ? "Thêm nguyện vọng thành công!" : "Cập nhật thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        controller.refreshData();
        dispose();
    }

    private void populateData() {
        if (editingAspiration == null) {
            return;
        }

        txtCccd.setText(editingAspiration.getNnCccd());
        txtCccd.setEnabled(false);
        lookupCandidate();

        cboNvSo.setSelectedItem(editingAspiration.getNvTt());

        String manganh = editingAspiration.getNvManganh();
        for (int i = 0; i < allMajors.size(); i++) {
            if (manganh.equals(allMajors.get(i).getManganh())) {
                cboNganh.setSelectedIndex(i);
                break;
            }
        }

        SwingUtilities.invokeLater(() -> {
            String tohop = editingAspiration.getTtThm();
            if (tohop != null) {
                for (int i = 0; i < cboTohop.getItemCount(); i++) {
                    if (cboTohop.getItemAt(i).toString().startsWith(tohop)) {
                        cboTohop.setSelectedIndex(i);
                        break;
                    }
                }
            }

            String phuongThuc = editingAspiration.getTtPhuongthuc();
            if (phuongThuc != null) {
                for (int i = 0; i < cboPhuongThuc.getItemCount(); i++) {
                    if (phuongThuc.equals(cboPhuongThuc.getItemAt(i))) {
                        cboPhuongThuc.setSelectedIndex(i);
                        break;
                    }
                }
            }
        });
    }
}
