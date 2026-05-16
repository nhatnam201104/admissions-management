package com.example.managementadmissionwf.ui.panel.admission;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class ResultExportDialog extends JDialog {
    private boolean confirmed = false;
    private String formatType;

    private JCheckBox chkStt, chkCccd, chkHoTen, chkSbd, chkNguyenVong, chkNganh, chkDiem,
            chkKetQua, chkPhuongThuc, chkToHop, chkDiemChuan, chkNgayXet;
    
    private JRadioButton rbAll, rbSelected, rbFiltered;

    public ResultExportDialog(Frame parent, String formatType) {
        super(parent, "Cấu hình xuất dữ liệu", true);
        this.formatType = formatType;
        initComponents();
        setLocationRelativeTo(parent);
    }

    private void initComponents() {
        setSize(400, 500);
        setLayout(new BorderLayout(15, 15));
        ((JPanel)getContentPane()).setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        //CHỌN CỘT (Checkbox List)
        JPanel columnPanel = new JPanel(new GridLayout(0, 2, 5, 5));
        columnPanel.setBorder(BorderFactory.createTitledBorder("1. Chọn cột (Columns)"));
        chkStt = new JCheckBox("STT", true);
        chkCccd = new JCheckBox("CCCD", true);
        chkHoTen = new JCheckBox("Họ tên", true);
        chkSbd = new JCheckBox("SBD", true);
        chkNguyenVong = new JCheckBox("Nguyện vọng", true);
        chkNganh = new JCheckBox("Ngành", true);
        chkDiem = new JCheckBox("Điểm XT", true);
        chkKetQua = new JCheckBox("Kết quả", true);
        chkPhuongThuc = new JCheckBox("Phương thức", true);
        chkToHop = new JCheckBox("Tổ hợp", true);
        chkDiemChuan = new JCheckBox("Điểm chuẩn", true);
        chkNgayXet = new JCheckBox("Ngày xét", true);
        
        columnPanel.add(chkStt); columnPanel.add(chkCccd);
        columnPanel.add(chkHoTen); columnPanel.add(chkSbd);
        columnPanel.add(chkNguyenVong); columnPanel.add(chkNganh);
        columnPanel.add(chkPhuongThuc); columnPanel.add(chkToHop);
        columnPanel.add(chkDiem); columnPanel.add(chkDiemChuan);
        columnPanel.add(chkKetQua); columnPanel.add(chkNgayXet);

        //PHẠM VI (Radio Buttons)
        JPanel scopePanel = new JPanel(new GridLayout(1, 3));
        scopePanel.setBorder(BorderFactory.createTitledBorder("2. Chọn phạm vi (Scope)"));
        rbAll = new JRadioButton("Tất cả", true);
        rbSelected = new JRadioButton("Dòng chọn");
        rbFiltered = new JRadioButton("Theo filter");
        
        ButtonGroup group = new ButtonGroup();
        group.add(rbAll); group.add(rbSelected); group.add(rbFiltered);
        
        scopePanel.add(rbAll); scopePanel.add(rbSelected); scopePanel.add(rbFiltered);

        //FORMAT
        JPanel formatPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        formatPanel.setBorder(BorderFactory.createTitledBorder("3. Định dạng file (Format)"));
        JTextField txtFormat = new JTextField("Format: " + formatType);
        txtFormat.setEditable(false);
        txtFormat.setPreferredSize(new Dimension(340, 30));
        formatPanel.add(txtFormat);


        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.add(columnPanel);
        centerPanel.add(Box.createVerticalStrut(10));
        centerPanel.add(scopePanel);
        centerPanel.add(Box.createVerticalStrut(10));
        centerPanel.add(formatPanel);

        // Action
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnExport = new JButton("Export");
        btnExport.setBackground(new Color(46, 204, 113));
        btnExport.setForeground(Color.WHITE);
        JButton btnCancel = new JButton("Cancel");
        
        btnExport.addActionListener(e -> { confirmed = true; dispose(); });
        btnCancel.addActionListener(e -> dispose());
        
        actionPanel.add(btnExport);
        actionPanel.add(btnCancel);

        add(centerPanel, BorderLayout.CENTER);
        add(actionPanel, BorderLayout.SOUTH);
    }

    public boolean isConfirmed() { return confirmed; }
    
    public Map<String, Boolean> getSelectedColumns() {
        Map<String, Boolean> cols = new HashMap<>();
        cols.put("STT", chkStt.isSelected());
        cols.put("CCCD", chkCccd.isSelected());
        cols.put("HoTen", chkHoTen.isSelected());
        cols.put("SBD", chkSbd.isSelected());
        cols.put("NguyenVong", chkNguyenVong.isSelected());
        cols.put("Nganh", chkNganh.isSelected());
        cols.put("Diem", chkDiem.isSelected());
        cols.put("KetQua", chkKetQua.isSelected());
        cols.put("PhuongThuc", chkPhuongThuc.isSelected());
        cols.put("ToHop", chkToHop.isSelected());
        cols.put("DiemChuan", chkDiemChuan.isSelected());
        cols.put("NgayXet", chkNgayXet.isSelected());
        return cols;
    }

    // Lấy phạm vi
    public String getSelectedScope() {
        if (rbSelected.isSelected()) return "SELECTED";
        if (rbFiltered.isSelected()) return "FILTERED";
        return "ALL";
    }
}
