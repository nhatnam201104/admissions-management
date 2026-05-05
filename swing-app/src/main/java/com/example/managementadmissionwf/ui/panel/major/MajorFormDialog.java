package com.example.managementadmissionwf.ui.panel.major;

import com.example.managementadmissionwf.bus.interfaces.SubjectGroupService;
import com.example.managementadmissionwf.dto.major.MajorDTO;
import com.example.managementadmissionwf.dto.major.MajorTohopDTO;
import com.example.managementadmissionwf.dto.common.Paging;
import com.example.managementadmissionwf.dto.response.SubjectGroupResponse;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MajorFormDialog extends JDialog {

    private final Color PRIMARY_COLOR = new Color(44, 62, 80);
    private final Color SUCCESS_COLOR = new Color(39, 174, 96);
    private final Color DANGER_COLOR = new Color(231, 76, 60);
    private final Color BG_COLOR = Color.WHITE;
    private final Font MAIN_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    private final Font BOLD_FONT = new Font("Segoe UI", Font.BOLD, 14);

    private final MajorController controller;
    private final SubjectGroupService subjectGroupService;
    private final MajorDTO currentDto;

    // Tab 1: Form fields
    private JTextField txtMaNganh, txtTenNganh;
    private JSpinner spDiemSan;
    private JTextField txtDiemChuan;
    private JCheckBox chkTuyenThang, chkDGNL, chkTHPT, chkVSAT;
    private JSpinner spSlXtt, spSlDgnl, spSlThpt, spSlVsat;
    private JTextField txtTongChiTieu;

    // Tab 2: To hop & He so
    private DefaultTableModel tohopTableModel;
    private final List<MajorTohopDTO> tohopList = new ArrayList<>();

    public MajorFormDialog(Frame parent, MajorController controller,
                           SubjectGroupService subjectGroupService, MajorDTO editDto) {
        super(parent, editDto == null ? "Thêm ngành mới" : "Cập nhật ngành", true);
        this.controller = controller;
        this.subjectGroupService = subjectGroupService;
        this.currentDto = editDto;

        initComponents();
        if (editDto != null) loadDataForEdit();
        setSize(880, 680);
        setLocationRelativeTo(parent);
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        getContentPane().setBackground(BG_COLOR);

        JLabel lblTitle = new JLabel(currentDto == null ? " THÊM NGÀNH MỚI" : "CẬP NHẬT NGÀNH");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setForeground(PRIMARY_COLOR);
        lblTitle.setBorder(new EmptyBorder(12, 15, 8, 15));
        add(lblTitle, BorderLayout.NORTH);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(MAIN_FONT);
        tabbedPane.setBackground(BG_COLOR);

        JScrollPane tab1Scroll = new JScrollPane(createInfoPanel());
        tab1Scroll.setBorder(null);
        tab1Scroll.getVerticalScrollBar().setUnitIncrement(16);
        tabbedPane.addTab("Thông tin ngành", tab1Scroll);

        tabbedPane.addTab("Tổ hợp & Hệ số", createTohopPanel());

        add(tabbedPane, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 12));
        bottom.setBackground(BG_COLOR);

        JButton btnCancel = styleButton(new JButton("Hủy"), DANGER_COLOR);
        JButton btnSave = styleButton(new JButton(currentDto == null ? "Thêm mới" : "Lưu thay đổi"), SUCCESS_COLOR);

        btnCancel.addActionListener(e -> dispose());
        btnSave.addActionListener(e -> saveMajor());

        bottom.add(btnCancel);
        bottom.add(btnSave);
        add(bottom, BorderLayout.SOUTH);
    }

    // ==================== Tab 1: Thong tin nganh ====================

    private JPanel createInfoPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(BG_COLOR);
        panel.setBorder(new EmptyBorder(15, 25, 15, 25));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 6, 8, 6);
        gbc.anchor = GridBagConstraints.WEST;

        // Ma nganh
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Mã ngành (*):"), gbc);
        gbc.gridx = 1;
        txtMaNganh = createModernTextField();
        if (currentDto != null) txtMaNganh.setEditable(false);
        panel.add(txtMaNganh, gbc);

        // Ten nganh
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Tên ngành (*):"), gbc);
        gbc.gridx = 1;
        txtTenNganh = createModernTextField();
        panel.add(txtTenNganh, gbc);

        // Phuong thuc xet tuyen + chi tieu
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        JPanel methodPanel = createMethodPanel();
        panel.add(methodPanel, gbc);

        // Tong chi tieu
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 1;
        panel.add(new JLabel("Tổng chỉ tiêu (*):"), gbc);
        gbc.gridx = 1;
        txtTongChiTieu = createReadonlyField("0");
        panel.add(txtTongChiTieu, gbc);

        // Diem san & Diem chuan
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 1;
        panel.add(new JLabel("Điểm sàn (*):"), gbc);
        gbc.gridx = 1;
        JPanel scorePanel = createScorePanel();
        panel.add(scorePanel, gbc);

        return panel;
    }

    private JPanel createMethodPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(BG_COLOR);
        panel.setBorder(BorderFactory.createTitledBorder("Phương thức xét tuyển và chỉ tiêu"));

        GridBagConstraints mgbc = new GridBagConstraints();
        mgbc.fill = GridBagConstraints.HORIZONTAL;
        mgbc.insets = new Insets(6, 12, 6, 12);
        mgbc.anchor = GridBagConstraints.WEST;

        // Header
        mgbc.gridx = 0; mgbc.gridy = 0;
        panel.add(createBoldLabel("Phương thức"), mgbc);
        mgbc.gridx = 1;
        panel.add(createBoldLabel("Chỉ tiêu"), mgbc);

        // Tuyen thang
        mgbc.gridx = 0; mgbc.gridy = 1;
        chkTuyenThang = new JCheckBox("Tuyển thẳng");
        chkTuyenThang.setFont(MAIN_FONT);
        panel.add(chkTuyenThang, mgbc);
        mgbc.gridx = 1;
        spSlXtt = createQuotaSpinner();
        spSlXtt.setEnabled(false);
        panel.add(spSlXtt, mgbc);

        // DGNL
        mgbc.gridx = 0; mgbc.gridy = 2;
        chkDGNL = new JCheckBox("DGNL");
        chkDGNL.setFont(MAIN_FONT);
        panel.add(chkDGNL, mgbc);
        mgbc.gridx = 1;
        spSlDgnl = createQuotaSpinner();
        spSlDgnl.setEnabled(false);
        panel.add(spSlDgnl, mgbc);

        // THPT
        mgbc.gridx = 0; mgbc.gridy = 3;
        chkTHPT = new JCheckBox("Xét THPT");
        chkTHPT.setFont(MAIN_FONT);
        panel.add(chkTHPT, mgbc);
        mgbc.gridx = 1;
        spSlThpt = createQuotaSpinner();
        spSlThpt.setEnabled(false);
        panel.add(spSlThpt, mgbc);

        // VSAT
        mgbc.gridx = 0; mgbc.gridy = 4;
        chkVSAT = new JCheckBox("VSAT");
        chkVSAT.setFont(MAIN_FONT);
        panel.add(chkVSAT, mgbc);
        mgbc.gridx = 1;
        spSlVsat = createQuotaSpinner();
        spSlVsat.setEnabled(false);
        panel.add(spSlVsat, mgbc);

        wireCheckboxToSpinner(chkTuyenThang, spSlXtt);
        wireCheckboxToSpinner(chkDGNL, spSlDgnl);
        wireCheckboxToSpinner(chkTHPT, spSlThpt);
        wireCheckboxToSpinner(chkVSAT, spSlVsat);

        wireSpinnerToTotal(spSlXtt);
        wireSpinnerToTotal(spSlDgnl);
        wireSpinnerToTotal(spSlThpt);
        wireSpinnerToTotal(spSlVsat);

        return panel;
    }

    private JPanel createScorePanel() {
        JPanel panel = new JPanel(new GridLayout(1, 2, 20, 0));
        panel.setBackground(BG_COLOR);

        JPanel sanPanel = new JPanel(new BorderLayout());
        sanPanel.setBackground(BG_COLOR);
        spDiemSan = new JSpinner(new SpinnerNumberModel(15.0, 0.0, 30.0, 0.5));
        spDiemSan.setFont(MAIN_FONT);
        sanPanel.add(spDiemSan, BorderLayout.CENTER);

        JPanel chuanPanel = new JPanel(new BorderLayout(5, 0));
        chuanPanel.setBackground(BG_COLOR);
        JLabel lblChuan = new JLabel("Điểm chuẩn:");
        lblChuan.setFont(MAIN_FONT);
        chuanPanel.add(lblChuan, BorderLayout.WEST);
        txtDiemChuan = new JTextField(8);
        txtDiemChuan.setFont(MAIN_FONT);
        txtDiemChuan.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(Color.LIGHT_GRAY, 1),
                new EmptyBorder(7, 10, 7, 10)));
        txtDiemChuan.setToolTipText("Để trống nếu chưa công bố điểm chuẩn");
        chuanPanel.add(txtDiemChuan, BorderLayout.CENTER);

        panel.add(sanPanel);
        panel.add(chuanPanel);
        return panel;
    }

    // ==================== Tab 2: To hop & He so ====================

    private JPanel createTohopPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(BG_COLOR);
        panel.setBorder(new EmptyBorder(15, 20, 15, 20));

        String[] columns = {"Mã TH", "Tên TH", "Môn 1", "HS 1", "Môn 2", "HS 2", "Môn 3", "HS 3"};
        tohopTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = new JTable(tohopTableModel);
        table.setFont(MAIN_FONT);
        table.setRowHeight(28);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));

        JScrollPane tableScroll = new JScrollPane(table);
        panel.add(tableScroll, BorderLayout.CENTER);

        // Buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        btnPanel.setBackground(BG_COLOR);

        JButton btnAdd = styleButton(new JButton("Thêm tổ hợp"), SUCCESS_COLOR);
        JButton btnRemove = styleButton(new JButton("Xóa tổ hợp"), DANGER_COLOR);

        btnAdd.addActionListener(e -> handleAddTohop());
        btnRemove.addActionListener(e -> handleRemoveTohop(table));

        btnPanel.add(btnAdd);
        btnPanel.add(btnRemove);
        panel.add(btnPanel, BorderLayout.SOUTH);

        // Note
        JLabel lblNote = new JLabel("Hệ số tùy chỉnh theo từng ngành + tổ hợp (vd: CNTT+A00: Toán*2, Lý*1, Hóa*1)");
        lblNote.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        lblNote.setForeground(Color.GRAY);
        panel.add(lblNote, BorderLayout.NORTH);

        return panel;
    }

    private void handleAddTohop() {
        String maNganh = currentDto != null ? currentDto.getMaNganh() : txtMaNganh.getText().trim();
        if (maNganh.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Vui lòng nhập mã ngành trước khi thêm tổ hợp!",
                    "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Set<String> existingTohops = new HashSet<>();
        for (MajorTohopDTO dto : tohopList) {
            existingTohops.add(dto.getMaToHop());
        }

        MajorTohopDTO result = MajorTohopFormDialog.showDialog(
                (Frame) SwingUtilities.getWindowAncestor(this),
                subjectGroupService, maNganh, existingTohops);

        if (result != null) {
            tohopList.add(result);
            refreshTohopTable();
        }
    }

    private void handleRemoveTohop(JTable table) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn tổ hợp cần xóa!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this,
                "Xóa tổ hợp " + tohopTableModel.getValueAt(selectedRow, 0) + " khỏi danh sách?",
                "Xác nhận", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            tohopList.remove(selectedRow);
            refreshTohopTable();
        }
    }

    private void refreshTohopTable() {
        tohopTableModel.setRowCount(0);
        for (MajorTohopDTO dto : tohopList) {
            tohopTableModel.addRow(new Object[]{
                    dto.getMaToHop(),
                    "",
                    dto.getThMon1(), dto.getHsMon1(),
                    dto.getThMon2(), dto.getHsMon2(),
                    dto.getThMon3(), dto.getHsMon3()
            });
        }
        // Fill ten TH from service cache
        try {
            Paging<SubjectGroupResponse> paging = subjectGroupService.search(null, 1, 500);
            for (SubjectGroupResponse sg : paging.getData()) {
                for (int i = 0; i < tohopTableModel.getRowCount(); i++) {
                    if (sg.getMatohop().equals(tohopTableModel.getValueAt(i, 0))) {
                        tohopTableModel.setValueAt(sg.getTentohop(), i, 1);
                    }
                }
            }
        } catch (Exception ignored) {
        }
    }

    // ==================== Helper methods ====================

    private JSpinner createQuotaSpinner() {
        JSpinner spinner = new JSpinner(new SpinnerNumberModel(0, 0, 10000, 10));
        spinner.setFont(MAIN_FONT);
        spinner.setPreferredSize(new Dimension(100, 28));
        return spinner;
    }

    private JTextField createModernTextField() {
        JTextField txt = new JTextField(25);
        txt.setFont(MAIN_FONT);
        txt.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(Color.LIGHT_GRAY, 1),
                new EmptyBorder(7, 10, 7, 10)));
        return txt;
    }

    private JTextField createReadonlyField(String text) {
        JTextField txt = new JTextField(text, 10);
        txt.setFont(MAIN_FONT);
        txt.setEditable(false);
        txt.setBackground(new Color(240, 240, 240));
        txt.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(Color.LIGHT_GRAY, 1),
                new EmptyBorder(7, 10, 7, 10)));
        return txt;
    }

    private JLabel createBoldLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(BOLD_FONT);
        return lbl;
    }

    private void wireCheckboxToSpinner(JCheckBox checkBox, JSpinner spinner) {
        checkBox.addItemListener(e -> {
            boolean selected = e.getStateChange() == ItemEvent.SELECTED;
            spinner.setEnabled(selected);
            if (!selected) spinner.setValue(0);
            updateTotalChiTieu();
        });
    }

    private void wireSpinnerToTotal(JSpinner spinner) {
        spinner.addChangeListener((ChangeEvent e) -> updateTotalChiTieu());
    }

    private void updateTotalChiTieu() {
        int total = 0;
        if (chkTuyenThang.isSelected()) total += ((Number) spSlXtt.getValue()).intValue();
        if (chkDGNL.isSelected()) total += ((Number) spSlDgnl.getValue()).intValue();
        if (chkTHPT.isSelected()) total += ((Number) spSlThpt.getValue()).intValue();
        if (chkVSAT.isSelected()) total += ((Number) spSlVsat.getValue()).intValue();
        txtTongChiTieu.setText(String.valueOf(total));
    }

    private int calculateTotalChiTieu() {
        int total = 0;
        if (chkTuyenThang.isSelected()) total += ((Number) spSlXtt.getValue()).intValue();
        if (chkDGNL.isSelected()) total += ((Number) spSlDgnl.getValue()).intValue();
        if (chkTHPT.isSelected()) total += ((Number) spSlThpt.getValue()).intValue();
        if (chkVSAT.isSelected()) total += ((Number) spSlVsat.getValue()).intValue();
        return total;
    }

    // ==================== Load / Save ====================

    private void loadDataForEdit() {
        if (currentDto == null) return;

        txtMaNganh.setText(currentDto.getMaNganh());
        txtTenNganh.setText(currentDto.getTenNganh());

        // Diem san
        spDiemSan.setValue(currentDto.getDiemSan() != null ? currentDto.getDiemSan() : 15.0);

        // Diem chuan
        txtDiemChuan.setText(currentDto.getDiemTrungTuyen() != null
                ? String.valueOf(currentDto.getDiemTrungTuyen()) : "");

        // Phuong thuc
        chkTuyenThang.setSelected(Boolean.TRUE.equals(currentDto.getTuyenThang()));
        chkDGNL.setSelected(Boolean.TRUE.equals(currentDto.getDgnl()));
        chkTHPT.setSelected(Boolean.TRUE.equals(currentDto.getThpt()));
        chkVSAT.setSelected(Boolean.TRUE.equals(currentDto.getVsat()));

        spSlXtt.setValue(currentDto.getSlXtt() != null ? currentDto.getSlXtt() : 0);
        spSlDgnl.setValue(currentDto.getSlDgnl() != null ? currentDto.getSlDgnl() : 0);
        spSlThpt.setValue(currentDto.getSlThpt() != null ? currentDto.getSlThpt() : 0);
        spSlVsat.setValue(currentDto.getSlVsat() != null ? currentDto.getSlVsat() : 0);

        updateTotalChiTieu();

        // Load danh sach to hop & he so
        try {
            List<MajorTohopDTO> existingTohops = controller.loadTohops(currentDto.getMaNganh());
            tohopList.addAll(existingTohops);
            refreshTohopTable();
        } catch (Exception e) {
            System.err.println("Khong the load to hop cho nganh: " + e.getMessage());
        }
    }

    private void saveMajor() {
        String maNganh = txtMaNganh.getText().trim();
        String tenNganh = txtTenNganh.getText().trim();

        if (maNganh.isEmpty() || tenNganh.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Vui lòng nhập đầy đủ Mã ngành và Tên ngành!",
                    "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        boolean anyMethodSelected = chkTuyenThang.isSelected() || chkDGNL.isSelected()
                || chkTHPT.isSelected() || chkVSAT.isSelected();
        if (!anyMethodSelected) {
            JOptionPane.showMessageDialog(this,
                    "Vui lòng chọn ít nhất 1 phương thức xét tuyển!",
                    "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (chkTuyenThang.isSelected() && ((Number) spSlXtt.getValue()).intValue() <= 0) {
            JOptionPane.showMessageDialog(this, "Chỉ tiêu tuyển thẳng phải lớn hơn 0!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (chkDGNL.isSelected() && ((Number) spSlDgnl.getValue()).intValue() <= 0) {
            JOptionPane.showMessageDialog(this, "Chỉ tiêu DGNL phải lớn hơn 0!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (chkTHPT.isSelected() && ((Number) spSlThpt.getValue()).intValue() <= 0) {
            JOptionPane.showMessageDialog(this, "Chỉ tiêu Xét THPT phải lớn hơn 0!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (chkVSAT.isSelected() && ((Number) spSlVsat.getValue()).intValue() <= 0) {
            JOptionPane.showMessageDialog(this, "Chỉ tiêu VSAT phải lớn hơn 0!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int totalChiTieu = calculateTotalChiTieu();
        if (totalChiTieu <= 0) {
            JOptionPane.showMessageDialog(this, "Tổng chỉ tiêu phải lớn hơn 0!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        MajorDTO dto = new MajorDTO();
        dto.setMaNganh(maNganh);
        dto.setTenNganh(tenNganh);

        dto.setSlXtt(chkTuyenThang.isSelected() ? ((Number) spSlXtt.getValue()).intValue() : 0);
        dto.setSlDgnl(chkDGNL.isSelected() ? ((Number) spSlDgnl.getValue()).intValue() : 0);
        dto.setSlThpt(chkTHPT.isSelected() ? ((Number) spSlThpt.getValue()).intValue() : 0);
        dto.setSlVsat(chkVSAT.isSelected() ? ((Number) spSlVsat.getValue()).intValue() : 0);
        dto.setChiTieu(totalChiTieu);
        dto.setDiemSan((Double) spDiemSan.getValue());

        String diemChuanText = txtDiemChuan.getText().trim();
        if (diemChuanText.isEmpty()) {
            dto.setDiemTrungTuyen(null);
        } else {
            try {
                dto.setDiemTrungTuyen(Double.parseDouble(diemChuanText));
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Điểm chuẩn không hợp lệ!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                return;
            }
        }

        dto.setTuyenThang(chkTuyenThang.isSelected());
        dto.setDgnl(chkDGNL.isSelected());
        dto.setThpt(chkTHPT.isSelected());
        dto.setVsat(chkVSAT.isSelected());

        try {
            if (currentDto == null) {
                controller.addMajor(dto);
            } else {
                controller.updateMajor(currentDto.getMaNganh(), dto);
            }

            // Save tohop list (only for new additions — existing ones are already in DB)
            String effectiveMaNganh = currentDto != null ? currentDto.getMaNganh() : maNganh;
            for (MajorTohopDTO tohopDto : tohopList) {
                if (tohopDto.getId() == null) {
                    tohopDto.setMaNganh(effectiveMaNganh);
                    try {
                        controller.addTohop(tohopDto);
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(this,
                                "Lỗi gắn tổ hợp " + tohopDto.getMaToHop() + ": " + ex.getMessage(),
                                "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                    }
                }
            }

            JOptionPane.showMessageDialog(this,
                    currentDto == null ? "Thêm ngành thành công!" : "Cập nhật ngành thành công!",
                    "Thành công", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + e.getMessage(), "Lỗi hệ thống", JOptionPane.ERROR_MESSAGE);
        }
    }

    private JButton styleButton(JButton btn, Color bgColor) {
        btn.setFont(BOLD_FONT);
        btn.setBackground(bgColor);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(140, 35));
        return btn;
    }
}
