package com.example.managementadmissionwf.ui.panel.major;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class MajorFormDialog extends JDialog {

    private final Color PRIMARY_COLOR = new Color(44, 62, 80);
    private final Color ACCENT_COLOR = new Color(52, 152, 219);
    private final Color SUCCESS_COLOR = new Color(39, 174, 96);
    private final Color DANGER_COLOR = new Color(231, 76, 60);
    private final Color BG_COLOR = Color.WHITE;
    private final Font MAIN_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    private final Font BOLD_FONT = new Font("Segoe UI", Font.BOLD, 14);

    private JTextField txtMaNganh, txtTenNganh, txtToHopGoc;
    private JSpinner spChiTieu, spDiemSan, spDiemChuan;
    private JCheckBox chkTuyenThang, chkDGNL, chkTHPT, chkVSAT;

    private JComboBox<String> cbTohop;
    private JTable groupTable;
    private DefaultTableModel groupTableModel;

    private Map<String, String[]> mockTohopData;

    public MajorFormDialog(Frame parent, String title) {
        super(parent, title, true); 
        initMockData();
        initComponents();
        setSize(700, 550);
        setLocationRelativeTo(parent);
    }

    private void initMockData() {
        mockTohopData = new HashMap<>();
        mockTohopData.put("A00", new String[]{"Toán (2.0)", "Vật lý (1.0)", "Hóa học (1.0)"});
        mockTohopData.put("A01", new String[]{"Toán (2.0)", "Vật lý (1.0)", "Tiếng Anh (1.0)"});
        mockTohopData.put("D01", new String[]{"Toán (1.0)", "Ngữ văn (1.0)", "Tiếng Anh (2.0)"});
        mockTohopData.put("B00", new String[]{"Toán (1.0)", "Hóa học (1.0)", "Sinh học (2.0)"});
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        getContentPane().setBackground(BG_COLOR);

        JLabel lblTitle = new JLabel("  THÔNG TIN NGÀNH HỌC");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setForeground(PRIMARY_COLOR);
        lblTitle.setBorder(new EmptyBorder(15, 10, 15, 10));
        add(lblTitle, BorderLayout.NORTH);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(BOLD_FONT);
        tabbedPane.setBackground(new Color(240, 240, 240));

        tabbedPane.addTab("1. Thông tin chung", createTab1InfoPanel());
        tabbedPane.addTab("2. Tổ hợp xét tuyển", createTab2GroupPanel());

        add(tabbedPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));
        bottomPanel.setBackground(BG_COLOR);

        JButton btnSave = styleButton(new JButton("Lưu thay đổi"), SUCCESS_COLOR);
        JButton btnCancel = styleButton(new JButton("Hủy bỏ"), DANGER_COLOR);

        btnCancel.addActionListener(e -> dispose());

        btnSave.addActionListener(e -> handleSaveAction());

        bottomPanel.add(btnCancel);
        bottomPanel.add(btnSave);
        add(bottomPanel, BorderLayout.SOUTH);
    }


    private JPanel createTab1InfoPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(BG_COLOR);
        panel.setBorder(new EmptyBorder(20, 30, 20, 30));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10); 

        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.3;
        panel.add(new JLabel("Mã ngành (*):"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 0.7;
        txtMaNganh = createModernTextField();
        panel.add(txtMaNganh, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Tên ngành (*):"), gbc);
        gbc.gridx = 1; gbc.gridy = 1;
        txtTenNganh = createModernTextField();
        panel.add(txtTenNganh, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Tổ hợp gốc:"), gbc);
        gbc.gridx = 1; gbc.gridy = 2;
        txtToHopGoc = createModernTextField();
        panel.add(txtToHopGoc, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("Chỉ tiêu (*):"), gbc);
        gbc.gridx = 1; gbc.gridy = 3;
        spChiTieu = new JSpinner(new SpinnerNumberModel(100, 1, 5000, 10));
        spChiTieu.setFont(MAIN_FONT);
        panel.add(spChiTieu, gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        panel.add(new JLabel("Điểm sàn / Điểm chuẩn:"), gbc);
        gbc.gridx = 1; gbc.gridy = 4;
        
        JPanel scorePanel = new JPanel(new GridLayout(1, 2, 10, 0));
        scorePanel.setBackground(BG_COLOR);
        spDiemSan = new JSpinner(new SpinnerNumberModel(15.0, 0.0, 30.0, 0.5));
        spDiemChuan = new JSpinner(new SpinnerNumberModel(15.0, 0.0, 30.0, 0.5));
        spDiemSan.setFont(MAIN_FONT); spDiemChuan.setFont(MAIN_FONT);
        scorePanel.add(spDiemSan);
        scorePanel.add(spDiemChuan);
        panel.add(scorePanel, gbc);

        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2;
        JPanel methodPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        methodPanel.setBackground(BG_COLOR);
        methodPanel.setBorder(BorderFactory.createTitledBorder("Phương thức xét tuyển"));
        
        chkTuyenThang = new JCheckBox("Tuyển thẳng"); chkTuyenThang.setBackground(BG_COLOR);
        chkDGNL = new JCheckBox("ĐGNL"); chkDGNL.setBackground(BG_COLOR);
        chkTHPT = new JCheckBox("Xét THPT"); chkTHPT.setBackground(BG_COLOR);
        chkVSAT = new JCheckBox("Kỳ thi VSAT"); chkVSAT.setBackground(BG_COLOR);

        methodPanel.add(chkTuyenThang); methodPanel.add(chkDGNL);
        methodPanel.add(chkTHPT); methodPanel.add(chkVSAT);
        panel.add(methodPanel, gbc);

        return panel;
    }
    
    private JPanel createTab2GroupPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(BG_COLOR);
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        JPanel addPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        addPanel.setBackground(BG_COLOR);
        
        cbTohop = new JComboBox<>(mockTohopData.keySet().toArray(new String[0]));
        cbTohop.setFont(MAIN_FONT);
        
        JButton btnAddGroup = styleButton(new JButton("+ Thêm vào danh sách"), ACCENT_COLOR);
        JButton btnRemoveGroup = styleButton(new JButton("- Gỡ bỏ chọn"), DANGER_COLOR);

        addPanel.add(new JLabel("Chọn tổ hợp mẫu: "));
        addPanel.add(cbTohop);
        addPanel.add(btnAddGroup);
        addPanel.add(btnRemoveGroup);
        panel.add(addPanel, BorderLayout.NORTH);

        String[] cols = {"Mã Tổ Hợp", "Môn 1 (Hệ số)", "Môn 2 (Hệ số)", "Môn 3 (Hệ số)"};
        groupTableModel = new DefaultTableModel(cols, 0);
        groupTable = new JTable(groupTableModel);
        groupTable.setRowHeight(30);
        groupTable.setFont(MAIN_FONT);
        
        JTableHeader header = groupTable.getTableHeader();
        header.setFont(BOLD_FONT);
        header.setBackground(PRIMARY_COLOR);
        header.setForeground(Color.WHITE);

        panel.add(new JScrollPane(groupTable), BorderLayout.CENTER);

        btnAddGroup.addActionListener(e -> {
            String selectedCode = (String) cbTohop.getSelectedItem();
            if (selectedCode != null) {
                for (int i = 0; i < groupTableModel.getRowCount(); i++) {
                    if (groupTableModel.getValueAt(i, 0).equals(selectedCode)) {
                        JOptionPane.showMessageDialog(this, "Tổ hợp " + selectedCode + " đã tồn tại trong danh sách!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                }
                
                // Lấy data auto-fill từ mock data
                String[] subjects = mockTohopData.get(selectedCode);
                groupTableModel.addRow(new Object[]{selectedCode, subjects[0], subjects[1], subjects[2]});
            }
        });

        // Xử lý sự kiện: Xóa tổ hợp khỏi bảng
        btnRemoveGroup.addActionListener(e -> {
            int selectedRow = groupTable.getSelectedRow();
            if (selectedRow != -1) {
                groupTableModel.removeRow(selectedRow);
            } else {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn một tổ hợp trong bảng để xóa.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        return panel;
    }

    // LOGIC: VALIDATION VÀ LƯU DỮ LIỆU
    private void handleSaveAction() {
        String maNganh = txtMaNganh.getText().trim();
        String tenNganh = txtTenNganh.getText().trim();
        int chiTieu = (Integer) spChiTieu.getValue();

        // 1. Validate: Không được để trống
        if (maNganh.isEmpty() || tenNganh.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Mã ngành và Tên ngành không được để trống!", "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // 2. Validate: Chỉ tiêu phải lớn hơn 0
        if (chiTieu <= 0) {
            JOptionPane.showMessageDialog(this, "Chỉ tiêu tuyển sinh phải lớn hơn 0!", "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // 3. Validate: Unique Mã Ngành (gọi majorService.checkExists(maNganh) ở đây)
        // Giả lập check lỗi trùng mã ngành 7480201
        if (maNganh.equals("7480201")) {
            JOptionPane.showMessageDialog(this, "Mã ngành " + maNganh + " đã tồn tại trong hệ thống!", "Lỗi trùng lặp", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // 4. Nếu pass qua hết Validation -> Gom data thành DTO và gửi cho Service lưu lại
        JOptionPane.showMessageDialog(this, "Lưu thành công ngành: " + tenNganh, "Thành công", JOptionPane.INFORMATION_MESSAGE);
        dispose(); // Đóng popup
    }

    // UTILITIES CHO UI
    private JTextField createModernTextField() {
        JTextField txt = new JTextField(20);
        txt.setFont(MAIN_FONT);
        txt.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(Color.LIGHT_GRAY),
                new EmptyBorder(5, 8, 5, 8))); 
        return txt;
    }

    private JButton styleButton(JButton btn, Color bgColor) {
        btn.setFont(BOLD_FONT);
        btn.setBackground(bgColor);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(150, 35));
        return btn;
    }
}