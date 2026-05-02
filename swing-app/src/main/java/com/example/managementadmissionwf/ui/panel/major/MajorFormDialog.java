package com.example.managementadmissionwf.ui.panel.major;

import com.example.managementadmissionwf.bus.interfaces.SubjectGroupService;
import com.example.managementadmissionwf.dto.major.MajorDTO;
import com.example.managementadmissionwf.dto.common.Paging;
import com.example.managementadmissionwf.dto.response.SubjectGroupResponse;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;

/**
 * Form dialog để thêm / sửa ngành học
 * Tổ hợp gốc được thay bằng JComboBox để chọn từ danh sách tổ hợp môn có sẵn
 */
public class MajorFormDialog extends JDialog {

    private final Color PRIMARY_COLOR = new Color(44, 62, 80);
    private final Color SUCCESS_COLOR = new Color(39, 174, 96);
    private final Color DANGER_COLOR = new Color(231, 76, 60);
    private final Color BG_COLOR = Color.WHITE;
    private final Font MAIN_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    private final Font BOLD_FONT = new Font("Segoe UI", Font.BOLD, 14);

    private final MajorController controller;
    private final SubjectGroupService subjectGroupService;  // Inject service tổ hợp môn
    private final MajorDTO currentDto;

    // Form fields
    private JTextField txtMaNganh, txtTenNganh;
    private JComboBox<String> cbToHopGoc;   // ← Thay JTextField bằng JComboBox
    private JSpinner spChiTieu, spDiemSan, spDiemChuan;
    private JCheckBox chkTuyenThang, chkDGNL, chkTHPT, chkVSAT;

    public MajorFormDialog(Frame parent, MajorController controller,
                           SubjectGroupService subjectGroupService, MajorDTO editDto) {
        super(parent, editDto == null ? "Thêm ngành mới" : "Cập nhật ngành", true);
        this.controller = controller;
        this.subjectGroupService = subjectGroupService;
        this.currentDto = editDto;

        initComponents();
        if (editDto != null) loadDataForEdit();
        setSize(780, 580);
        setLocationRelativeTo(parent);
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        getContentPane().setBackground(BG_COLOR);

        JLabel lblTitle = new JLabel(currentDto == null ? " THÊM NGÀNH MỚI" : " CẬP NHẬT NGÀNH");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setForeground(PRIMARY_COLOR);
        lblTitle.setBorder(new EmptyBorder(15, 15, 15, 15));
        add(lblTitle, BorderLayout.NORTH);

        JPanel content = createInfoPanel();
        add(content, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));
        bottom.setBackground(BG_COLOR);

        JButton btnCancel = styleButton(new JButton("Hủy"), DANGER_COLOR);
        JButton btnSave = styleButton(new JButton(currentDto == null ? "Thêm mới" : "Lưu thay đổi"), SUCCESS_COLOR);

        btnCancel.addActionListener(e -> dispose());
        btnSave.addActionListener(e -> saveMajor());

        bottom.add(btnCancel);
        bottom.add(btnSave);
        add(bottom, BorderLayout.SOUTH);
    }

    private JPanel createInfoPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(BG_COLOR);
        panel.setBorder(new EmptyBorder(25, 30, 25, 30));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(12, 8, 12, 8);
        gbc.anchor = GridBagConstraints.WEST;

        // Mã ngành
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Mã ngành (*):"), gbc);
        gbc.gridx = 1;
        txtMaNganh = createModernTextField();
        if (currentDto != null) txtMaNganh.setEditable(false);
        panel.add(txtMaNganh, gbc);

        // Tên ngành
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Tên ngành (*):"), gbc);
        gbc.gridx = 1;
        txtTenNganh = createModernTextField();
        panel.add(txtTenNganh, gbc);

        // Tổ hợp gốc - JComboBox
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Tổ hợp gốc:"), gbc);
        gbc.gridx = 1;
        cbToHopGoc = new JComboBox<>();
        cbToHopGoc.setFont(MAIN_FONT);
        loadToHopGocComboBox();                    // Load danh sách tổ hợp
        panel.add(cbToHopGoc, gbc);

        // Chỉ tiêu
        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("Chỉ tiêu (*):"), gbc);
        gbc.gridx = 1;
        spChiTieu = new JSpinner(new SpinnerNumberModel(100, 1, 10000, 10));
        spChiTieu.setFont(MAIN_FONT);
        panel.add(spChiTieu, gbc);

        // Điểm sàn & chuẩn
        gbc.gridx = 0; gbc.gridy = 4;
        panel.add(new JLabel("Điểm sàn / Điểm chuẩn:"), gbc);
        gbc.gridx = 1;
        JPanel scorePanel = new JPanel(new GridLayout(1, 2, 12, 0));
        scorePanel.setBackground(BG_COLOR);
        spDiemSan = new JSpinner(new SpinnerNumberModel(15.0, 0.0, 30.0, 0.5));
        spDiemChuan = new JSpinner(new SpinnerNumberModel(15.0, 0.0, 30.0, 0.5));
        spDiemSan.setFont(MAIN_FONT);
        spDiemChuan.setFont(MAIN_FONT);
        scorePanel.add(spDiemSan);
        scorePanel.add(spDiemChuan);
        panel.add(scorePanel, gbc);

        // Phương thức
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2;
        JPanel methodPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 5));
        methodPanel.setBackground(BG_COLOR);
        methodPanel.setBorder(BorderFactory.createTitledBorder("Phương thức xét tuyển"));

        chkTuyenThang = new JCheckBox("Tuyển thẳng");
        chkDGNL = new JCheckBox("ĐGNL");
        chkTHPT = new JCheckBox("Xét THPT");
        chkVSAT = new JCheckBox("VSAT");

        methodPanel.add(chkTuyenThang);
        methodPanel.add(chkDGNL);
        methodPanel.add(chkTHPT);
        methodPanel.add(chkVSAT);
        panel.add(methodPanel, gbc);

        return panel;
    }

    // Load danh sách tổ hợp môn vào ComboBox
    private void loadToHopGocComboBox() {
        cbToHopGoc.removeAllItems();
        cbToHopGoc.addItem(""); // Option trống

        try {
            Paging<SubjectGroupResponse> paging = subjectGroupService.search(null, 1, 500); // Lấy nhiều
            for (SubjectGroupResponse sg : paging.getData()) {
                String display = sg.getMatohop() + " - " + sg.getTentohop();
                cbToHopGoc.addItem(display);
            }
        } catch (Exception e) {
            System.err.println("Không thể load danh sách tổ hợp gốc: " + e.getMessage());
        }
    }

    private void loadDataForEdit() {
        if (currentDto == null) return;

        txtMaNganh.setText(currentDto.getMaNganh());
        txtTenNganh.setText(currentDto.getTenNganh());

        // Chọn giá trị tổ hợp gốc hiện tại
        if (currentDto.getTohopGoc() != null && !currentDto.getTohopGoc().isEmpty()) {
            for (int i = 0; i < cbToHopGoc.getItemCount(); i++) {
                String item = cbToHopGoc.getItemAt(i);
                if (item != null && item.startsWith(currentDto.getTohopGoc() + " -")) {
                    cbToHopGoc.setSelectedIndex(i);
                    break;
                }
            }
        }

        spChiTieu.setValue(currentDto.getChiTieu() != null ? currentDto.getChiTieu() : 100);
        spDiemSan.setValue(currentDto.getDiemSan() != null ? currentDto.getDiemSan() : 15.0);
        spDiemChuan.setValue(currentDto.getDiemTrungTuyen() != null ? currentDto.getDiemTrungTuyen() : 15.0);

        chkTuyenThang.setSelected(Boolean.TRUE.equals(currentDto.getTuyenThang()));
        chkDGNL.setSelected(Boolean.TRUE.equals(currentDto.getDgnl()));
        chkTHPT.setSelected(Boolean.TRUE.equals(currentDto.getThpt()));
        chkVSAT.setSelected(Boolean.TRUE.equals(currentDto.getVsat()));
    }

    private void saveMajor() {
        String maNganh = txtMaNganh.getText().trim();
        String tenNganh = txtTenNganh.getText().trim();

        // Validation nhanh tại giao diện
        if (maNganh.isEmpty() || tenNganh.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ Mã ngành và Tên ngành!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        MajorDTO dto = new MajorDTO();
        dto.setMaNganh(maNganh);
        dto.setTenNganh(tenNganh);

        String selectedTohop = (String) cbToHopGoc.getSelectedItem();
        if (selectedTohop != null && !selectedTohop.trim().isEmpty()) {
            String tohopGoc = selectedTohop.split(" - ")[0].trim();
            dto.setTohopGoc(tohopGoc);
        }

        dto.setChiTieu((Integer) spChiTieu.getValue());
        dto.setDiemSan((Double) spDiemSan.getValue());
        dto.setDiemTrungTuyen((Double) spDiemChuan.getValue());
        dto.setTuyenThang(chkTuyenThang.isSelected());
        dto.setDgnl(chkDGNL.isSelected());
        dto.setThpt(chkTHPT.isSelected());
        dto.setVsat(chkVSAT.isSelected());

        try {
            if (currentDto == null) {
                controller.addMajor(dto);
                JOptionPane.showMessageDialog(this, "Thêm mới ngành học: " + dto.getTenNganh() + " thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
            } else {
                controller.updateMajor(currentDto.getMaNganh(), dto);
                JOptionPane.showMessageDialog(this, "Cập nhật ngành " + dto.getTenNganh() + " thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
            }
            dispose();                    // ← Chỉ đóng khi THÀNH CÔNG
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Lỗi: " + e.getMessage(),
                    "Lỗi hệ thống",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private JTextField createModernTextField() {
        JTextField txt = new JTextField(25);
        txt.setFont(MAIN_FONT);
        txt.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(Color.LIGHT_GRAY, 1),
                new EmptyBorder(7, 10, 7, 10)));
        return txt;
    }

    private JButton styleButton(JButton btn, Color bgColor) {
        btn.setFont(BOLD_FONT);
        btn.setBackground(bgColor);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(140, 38));
        return btn;
    }
}