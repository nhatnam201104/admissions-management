package com.example.managementadmissionwf.ui.panel.subjectgroup;

import com.example.managementadmissionwf.dto.request.SubjectGroupRequest;
import com.example.managementadmissionwf.dto.response.SubjectGroupResponse;

import javax.swing.*;
import java.awt.*;

/**
 * Dialog for adding/editing subject groups
 */
public class SubjectGroupFormDialog extends JDialog {

    private JTextField txtGroupCode;
    private JTextField txtGroupName;
    private JTextField txtSubject1;
    private JTextField txtSubject2;
    private JTextField txtSubject3;
    private boolean confirmed;
    private SubjectGroupRequest resultData;

    public SubjectGroupFormDialog(Frame parent, String title) {
        super(parent, title, true);
        initComponents();
        setLocationRelativeTo(parent);
    }

    private void initComponents() {
        setSize(400, 350);
        setLayout(new BorderLayout());

        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Mã tổ hợp
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.EAST;
        contentPanel.add(new JLabel("Mã tổ hợp:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        txtGroupCode = new JTextField(20);
        contentPanel.add(txtGroupCode, gbc);

        // Tên tổ hợp
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.EAST;
        contentPanel.add(new JLabel("Tên tổ hợp:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        txtGroupName = new JTextField(20);
        contentPanel.add(txtGroupName, gbc);

        // Môn 1
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.EAST;
        contentPanel.add(new JLabel("Môn 1:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        txtSubject1 = new JTextField(20);
        contentPanel.add(txtSubject1, gbc);

        // Môn 2
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.EAST;
        contentPanel.add(new JLabel("Môn 2:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        txtSubject2 = new JTextField(20);
        contentPanel.add(txtSubject2, gbc);

        // Môn 3
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.EAST;
        contentPanel.add(new JLabel("Môn 3:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        txtSubject3 = new JTextField(20);
        contentPanel.add(txtSubject3, gbc);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));

        JButton btnSave = new JButton("Lưu");
        btnSave.setBackground(new Color(46, 204, 113));
        btnSave.setForeground(Color.WHITE);
        btnSave.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnSave.setFocusPainted(false);
        btnSave.addActionListener(e -> handleSave());

        JButton btnCancel = new JButton("Hủy");
        btnCancel.setBackground(new Color(149, 165, 166));
        btnCancel.setForeground(Color.WHITE);
        btnCancel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnCancel.setFocusPainted(false);
        btnCancel.addActionListener(e -> dispose());

        buttonPanel.add(btnSave);
        buttonPanel.add(btnCancel);

        add(contentPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void handleSave() {
        // Validate input
        if (txtGroupCode.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập mã tổ hợp!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (txtGroupName.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập tên tổ hợp!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (txtSubject1.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập môn 1!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        SubjectGroupRequest req = new SubjectGroupRequest();
        req.setMatohop(txtGroupCode.getText().trim());
        req.setTentohop(txtGroupName.getText().trim());
        req.setMon1(txtSubject1.getText().trim());
        req.setMon2(txtSubject2.getText().trim());
        req.setMon3(txtSubject3.getText().trim());
        this.resultData = req;

        confirmed = true;
        dispose();
    }

    /**
     * Show dialog for adding new subject group
     */
    public static SubjectGroupRequest showAddDialog(Frame parent) {
        SubjectGroupFormDialog dialog = new SubjectGroupFormDialog(parent, "Thêm tổ hợp môn");
        dialog.setVisible(true);

        if (dialog.confirmed) {
            return dialog.resultData;
        }
        return null;
    }

    /**
     * Show dialog for editing existing subject group
     */
    public static SubjectGroupRequest showEditDialog(Frame parent, SubjectGroupResponse data) {
        SubjectGroupFormDialog dialog = new SubjectGroupFormDialog(parent, "Sửa tổ hợp môn");

        // Fill data
        dialog.txtGroupCode.setText(data.getMatohop());
        dialog.txtGroupName.setText(data.getTentohop());
        dialog.txtSubject1.setText(data.getMon1());
        dialog.txtSubject2.setText(data.getMon2());
        dialog.txtSubject3.setText(data.getMon3());

        dialog.setVisible(true);

        if (dialog.confirmed) {
            return dialog.resultData;
        }
        return null;
    }
}