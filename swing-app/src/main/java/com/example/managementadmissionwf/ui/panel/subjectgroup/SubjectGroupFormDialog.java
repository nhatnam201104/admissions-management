package com.example.managementadmissionwf.ui.panel.subjectgroup;

import com.example.managementadmissionwf.dto.request.SubjectGroupRequest;
import com.example.managementadmissionwf.dto.response.SubjectGroupResponse;

import javax.swing.*;
import java.awt.*;

/**
 * Dialog for adding/editing subject groups.
 * Subjects are selected via editable combo boxes with a standard list.
 * Users can add custom subjects at runtime via the "+" button.
 */
public class SubjectGroupFormDialog extends JDialog {

    private static final String[] DEFAULT_SUBJECTS = {
            "TO", "LI", "HO", "SI", "SU", "DI", "VA",
            "N1", "NL1", "NK1", "NK2",
            "HAT", "VE", "MUA", "TIENG_DUC", "NHAC", "KICH", "TDTT", "DAN"
    };

    private final DefaultComboBoxModel<String> comboBoxModel1 = new DefaultComboBoxModel<>(DEFAULT_SUBJECTS);
    private final DefaultComboBoxModel<String> comboBoxModel2 = new DefaultComboBoxModel<>(DEFAULT_SUBJECTS);
    private final DefaultComboBoxModel<String> comboBoxModel3 = new DefaultComboBoxModel<>(DEFAULT_SUBJECTS);

    private JTextField txtGroupCode;
    private JTextField txtGroupName;
    private JComboBox<String> cboSubject1;
    private JComboBox<String> cboSubject2;
    private JComboBox<String> cboSubject3;
    private boolean confirmed;
    private SubjectGroupRequest resultData;

    public SubjectGroupFormDialog(Frame parent, String title) {
        super(parent, title, true);
        initComponents();
        setLocationRelativeTo(parent);
    }

    private void initComponents() {
        setSize(450, 350);
        setLayout(new BorderLayout());

        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Ma to hop
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.EAST;
        contentPanel.add(new JLabel("Mã tổ hợp:"), gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        txtGroupCode = new JTextField(20);
        contentPanel.add(txtGroupCode, gbc);

        // Ten to hop
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.EAST;
        contentPanel.add(new JLabel("Tên tổ hợp:"), gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        txtGroupName = new JTextField(20);
        contentPanel.add(txtGroupName, gbc);

        // Mon 1
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.EAST;
        contentPanel.add(new JLabel("Môn 1:"), gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 1.0;
        cboSubject1 = createSubjectComboBox(comboBoxModel1);
        contentPanel.add(cboSubject1, gbc);

        gbc.gridx = 2;
        gbc.gridwidth = 1;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        contentPanel.add(createAddSubjectButton(comboBoxModel1, comboBoxModel2, comboBoxModel3), gbc);

        // Mon 2
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        contentPanel.add(new JLabel("Môn 2:"), gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 1.0;
        cboSubject2 = createSubjectComboBox(comboBoxModel2);
        contentPanel.add(cboSubject2, gbc);

        gbc.gridx = 2;
        gbc.gridwidth = 1;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        contentPanel.add(createAddSubjectButton(comboBoxModel1, comboBoxModel2, comboBoxModel3), gbc);

        // Mon 3
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        contentPanel.add(new JLabel("Môn 3:"), gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 1.0;
        cboSubject3 = createSubjectComboBox(comboBoxModel3);
        contentPanel.add(cboSubject3, gbc);

        gbc.gridx = 2;
        gbc.gridwidth = 1;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        contentPanel.add(createAddSubjectButton(comboBoxModel1, comboBoxModel2, comboBoxModel3), gbc);

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

    private JComboBox<String> createSubjectComboBox(DefaultComboBoxModel<String> model) {
        JComboBox<String> comboBox = new JComboBox<>(model);
        comboBox.setEditable(true);
        comboBox.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        return comboBox;
    }

    private JButton createAddSubjectButton(DefaultComboBoxModel<String>... models) {
        JButton btnAdd = new JButton("+");
        btnAdd.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnAdd.setFocusPainted(false);
        btnAdd.setToolTipText("Thêm môn tự chọn");
        btnAdd.addActionListener(e -> showCustomSubjectDialog(models));
        return btnAdd;
    }

    private void showCustomSubjectDialog(DefaultComboBoxModel<String>[] models) {
        JTextField txtCustomSubject = new JTextField(15);
        txtCustomSubject.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.add(new JLabel("Tên môn:"), BorderLayout.NORTH);
        panel.add(txtCustomSubject, BorderLayout.CENTER);

        int result = JOptionPane.showConfirmDialog(
                this,
                panel,
                "Thêm môn tự chọn",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (result == JOptionPane.OK_OPTION) {
            String subjectName = txtCustomSubject.getText().trim();
            if (subjectName.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Vui lòng nhập tên môn!",
                        "Lỗi",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            for (DefaultComboBoxModel<String> model : models) {
                boolean exists = false;
                for (int i = 0; i < model.getSize(); i++) {
                    if (model.getElementAt(i).trim().equalsIgnoreCase(subjectName)) {
                        exists = true;
                        break;
                    }
                }
                if (!exists) {
                    model.addElement(subjectName);
                }
            }
        }
    }

    private void handleSave() {
        if (txtGroupCode.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập mã tổ hợp!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (txtGroupName.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập tên tổ hợp!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Object selected1 = cboSubject1.getSelectedItem();
        Object selected2 = cboSubject2.getSelectedItem();
        Object selected3 = cboSubject3.getSelectedItem();

        String mon1 = (selected1 != null) ? selected1.toString().trim() : "";
        String mon2 = (selected2 != null) ? selected2.toString().trim() : "";
        String mon3 = (selected3 != null) ? selected3.toString().trim() : "";

        if (mon1.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn môn 1!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (mon2.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn môn 2!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (mon3.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn môn 3!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (mon1.equalsIgnoreCase(mon2)
                || mon1.equalsIgnoreCase(mon3)
                || mon2.equalsIgnoreCase(mon3)) {
            JOptionPane.showMessageDialog(this,
                    "Không được chọn trùng môn trong cùng một tổ hợp!",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        SubjectGroupRequest req = new SubjectGroupRequest();
        req.setMatohop(txtGroupCode.getText().trim());
        req.setTentohop(txtGroupName.getText().trim());
        req.setMon1(mon1);
        req.setMon2(mon2);
        req.setMon3(mon3);
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

        // Ensure existing subject values are present in combo box models
        ensureSubjectExists(dialog.comboBoxModel1, data.getMon1());
        ensureSubjectExists(dialog.comboBoxModel2, data.getMon2());
        ensureSubjectExists(dialog.comboBoxModel3, data.getMon3());

        dialog.txtGroupCode.setText(data.getMatohop());
        dialog.txtGroupName.setText(data.getTentohop());
        dialog.cboSubject1.setSelectedItem(data.getMon1());
        dialog.cboSubject2.setSelectedItem(data.getMon2());
        dialog.cboSubject3.setSelectedItem(data.getMon3());

        dialog.setVisible(true);

        if (dialog.confirmed) {
            return dialog.resultData;
        }
        return null;
    }

    private static void ensureSubjectExists(DefaultComboBoxModel<String> model, String subject) {
        if (subject == null || subject.trim().isEmpty()) {
            return;
        }
        for (int i = 0; i < model.getSize(); i++) {
            if (model.getElementAt(i).trim().equalsIgnoreCase(subject.trim())) {
                return;
            }
        }
        model.addElement(subject.trim());
    }
}
