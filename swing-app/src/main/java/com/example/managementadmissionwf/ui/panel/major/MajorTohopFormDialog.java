package com.example.managementadmissionwf.ui.panel.major;

import com.example.managementadmissionwf.bus.interfaces.SubjectGroupService;
import com.example.managementadmissionwf.dto.common.Paging;
import com.example.managementadmissionwf.dto.major.MajorTohopDTO;
import com.example.managementadmissionwf.dto.response.SubjectGroupResponse;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;
import java.util.Set;

public class MajorTohopFormDialog extends JDialog {

    private final Color PRIMARY_COLOR = new Color(44, 62, 80);
    private final Color SUCCESS_COLOR = new Color(39, 174, 96);
    private final Color BG_COLOR = Color.WHITE;
    private final Font MAIN_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    private final Font BOLD_FONT = new Font("Segoe UI", Font.BOLD, 14);

    private final SubjectGroupService subjectGroupService;
    private final String maNganh;
    private final Set<String> existingTohops;

    private JComboBox<String> cbToHop;
    private JLabel lblMon1, lblMon2, lblMon3;
    private JSpinner spHsMon1, spHsMon2, spHsMon3;

    private boolean confirmed;
    private MajorTohopDTO resultData;

    private MajorTohopFormDialog(Frame parent, SubjectGroupService subjectGroupService,
                                  String maNganh, Set<String> existingTohops) {
        super(parent, "Gắn tổ hợp cho ngành", true);
        this.subjectGroupService = subjectGroupService;
        this.maNganh = maNganh;
        this.existingTohops = existingTohops;

        initComponents();
        setSize(480, 340);
        setLocationRelativeTo(parent);
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        getContentPane().setBackground(BG_COLOR);

        JLabel lblTitle = new JLabel(" GẮN TỔ HỢP & HỆ SỐ");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTitle.setForeground(PRIMARY_COLOR);
        lblTitle.setBorder(new EmptyBorder(12, 15, 12, 15));
        add(lblTitle, BorderLayout.NORTH);

        JPanel content = new JPanel(new GridBagLayout());
        content.setBackground(BG_COLOR);
        content.setBorder(new EmptyBorder(10, 20, 10, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;

        // Chon to hop
        gbc.gridx = 0; gbc.gridy = 0;
        content.add(new JLabel("Tổ hợp (*):"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 2;
        cbToHop = new JComboBox<>();
        cbToHop.setFont(MAIN_FONT);
        loadToHopComboBox();
        cbToHop.addActionListener(e -> onToHopSelected());
        content.add(cbToHop, gbc);

        // Mon 1 + He so 1
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 1;
        content.add(new JLabel("Môn 1:"), gbc);
        gbc.gridx = 1;
        lblMon1 = new JLabel("-");
        lblMon1.setFont(MAIN_FONT);
        content.add(lblMon1, gbc);
        gbc.gridx = 2;
        content.add(new JLabel("HS:"), gbc);
        gbc.gridx = 3;
        spHsMon1 = createHsSpinner();
        content.add(spHsMon1, gbc);

        // Mon 2 + He so 2
        gbc.gridx = 0; gbc.gridy = 2;
        content.add(new JLabel("Môn 2:"), gbc);
        gbc.gridx = 1;
        lblMon2 = new JLabel("-");
        lblMon2.setFont(MAIN_FONT);
        content.add(lblMon2, gbc);
        gbc.gridx = 2;
        content.add(new JLabel("HS:"), gbc);
        gbc.gridx = 3;
        spHsMon2 = createHsSpinner();
        content.add(spHsMon2, gbc);

        // Mon 3 + He so 3
        gbc.gridx = 0; gbc.gridy = 3;
        content.add(new JLabel("Môn 3:"), gbc);
        gbc.gridx = 1;
        lblMon3 = new JLabel("-");
        lblMon3.setFont(MAIN_FONT);
        content.add(lblMon3, gbc);
        gbc.gridx = 2;
        content.add(new JLabel("HS:"), gbc);
        gbc.gridx = 3;
        spHsMon3 = createHsSpinner();
        content.add(spHsMon3, gbc);

        add(content, BorderLayout.CENTER);

        // Buttons
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        bottom.setBackground(BG_COLOR);
        JButton btnCancel = styleButton(new JButton("Hủy"), new Color(149, 165, 166));
        JButton btnSave = styleButton(new JButton("Thêm"), SUCCESS_COLOR);
        btnCancel.addActionListener(e -> dispose());
        btnSave.addActionListener(e -> handleSave());
        bottom.add(btnCancel);
        bottom.add(btnSave);
        add(bottom, BorderLayout.SOUTH);
    }

    private JSpinner createHsSpinner() {
        JSpinner spinner = new JSpinner(new SpinnerNumberModel(1.0, 0.25, 4.0, 0.25));
        spinner.setFont(MAIN_FONT);
        spinner.setPreferredSize(new Dimension(60, 28));
        spinner.setEditor(new JSpinner.NumberEditor(spinner, "#.##"));
        return spinner;
    }

    private void loadToHopComboBox() {
        cbToHop.removeAllItems();
        cbToHop.addItem("");
        try {
            Paging<SubjectGroupResponse> paging = subjectGroupService.search(null, 1, 500);
            for (SubjectGroupResponse sg : paging.getData()) {
                if (!existingTohops.contains(sg.getMatohop())) {
                    cbToHop.addItem(sg.getMatohop() + " - " + sg.getTentohop());
                }
            }
        } catch (Exception e) {
            System.err.println("Không thể tải danh sách tổ hợp: " + e.getMessage());
        }
    }

    private void onToHopSelected() {
        String selected = (String) cbToHop.getSelectedItem();
        if (selected == null || selected.trim().isEmpty()) {
            lblMon1.setText("-");
            lblMon2.setText("-");
            lblMon3.setText("-");
            return;
        }
        String maToHop = selected.split(" - ")[0].trim();
        try {
            List<SubjectGroupResponse> all = subjectGroupService.search(null, 1, 500).getData();
            for (SubjectGroupResponse sg : all) {
                if (sg.getMatohop().equals(maToHop)) {
                    lblMon1.setText(sg.getMon1());
                    lblMon2.setText(sg.getMon2());
                    lblMon3.setText(sg.getMon3());
                    return;
                }
            }
        } catch (Exception e) {
            System.err.println("Không thể tải chi tiết tổ hợp: " + e.getMessage());
        }
    }

    private void handleSave() {
        String selected = (String) cbToHop.getSelectedItem();
        if (selected == null || selected.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn tổ hợp!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String maToHop = selected.split(" - ")[0].trim();

        double hs1 = ((Number) spHsMon1.getValue()).doubleValue();
        double hs2 = ((Number) spHsMon2.getValue()).doubleValue();
        double hs3 = ((Number) spHsMon3.getValue()).doubleValue();

        if (hs1 <= 0 || hs2 <= 0 || hs3 <= 0) {
            JOptionPane.showMessageDialog(this, "Hệ số phải lớn hơn 0!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        MajorTohopDTO dto = new MajorTohopDTO();
        dto.setMaNganh(maNganh);
        dto.setMaToHop(maToHop);
        dto.setThMon1(lblMon1.getText());
        dto.setThMon2(lblMon2.getText());
        dto.setThMon3(lblMon3.getText());
        dto.setHsMon1(hs1);
        dto.setHsMon2(hs2);
        dto.setHsMon3(hs3);

        this.resultData = dto;
        this.confirmed = true;
        dispose();
    }

    public static MajorTohopDTO showDialog(Frame parent, SubjectGroupService subjectGroupService,
                                           String maNganh, Set<String> existingTohops) {
        MajorTohopFormDialog dialog = new MajorTohopFormDialog(parent, subjectGroupService, maNganh, existingTohops);
        dialog.setVisible(true);
        return dialog.confirmed ? dialog.resultData : null;
    }

    private JButton styleButton(JButton btn, Color bgColor) {
        btn.setFont(BOLD_FONT);
        btn.setBackground(bgColor);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(120, 35));
        return btn;
    }
}
