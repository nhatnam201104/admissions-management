package com.example.managementadmissionwf.ui.panel.user;

import com.example.managementadmissionwf.dto.User.CreateUserRequest;
import com.example.managementadmissionwf.dto.User.GetUserResponse;
import com.example.managementadmissionwf.dto.User.UpdateUserRequest;

import javax.swing.*;
import java.awt.*;
import java.util.regex.Pattern;

/**
 * Form Dialog for Add/Edit User
 */
public class UserFormDialog extends JDialog {
    private boolean createMode;
    private GetUserResponse editingUser;
    private boolean saved = false;

    // Form fields
    private JTextField txtFullname;
    private JTextField txtEmail;
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JComboBox<String> cboRole;
    private JLabel lblPassword;

    private JButton btnSave;
    private JButton btnCancel;

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");

    public UserFormDialog(Frame parent, String title, boolean createMode) {
        super(parent, title, true);
        this.createMode = createMode;
        this.editingUser = null;
        initComponents();
        setLocationRelativeTo(parent);
    }

    public UserFormDialog(Frame parent, String title, GetUserResponse user) {
        super(parent, title, true);
        this.createMode = false;
        this.editingUser = user;
        initComponents();
        loadUserData();
        setLocationRelativeTo(parent);
    }

    private void initComponents() {
        setSize(450, 350);
        setLayout(new BorderLayout());
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel formPanel = createFormPanel();
        add(formPanel, BorderLayout.CENTER);

        JPanel buttonPanel = createButtonPanel();
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 5, 8, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        // Fullname
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(createLabel("Họ tên *:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        txtFullname = createTextField();
        panel.add(txtFullname, gbc);
        gbc.weightx = 0;

        // Email
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(createLabel("Email *:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        txtEmail = createTextField();
        panel.add(txtEmail, gbc);
        gbc.weightx = 0;

        // Username
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(createLabel("Username *:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        txtUsername = createTextField();
        panel.add(txtUsername, gbc);
        gbc.weightx = 0;

        // Password (only for create mode)
        gbc.gridx = 0; gbc.gridy = 3;
        lblPassword = createLabel("Mật khẩu *:");
        panel.add(lblPassword, gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        txtPassword = new JPasswordField();
        txtPassword.setPreferredSize(new Dimension(200, 30));
        txtPassword.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        panel.add(txtPassword, gbc);
        gbc.weightx = 0;

        if (!createMode) {
            lblPassword.setVisible(false);
            txtPassword.setVisible(false);
            txtUsername.setEnabled(false);
        }

        // Role — only STUDENT for create, disable if editing ADMIN
        gbc.gridx = 0; gbc.gridy = 4;
        panel.add(createLabel("Vai trò *:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        cboRole = new JComboBox<>(new String[]{"STUDENT", "ADMIN"});
        cboRole.setPreferredSize(new Dimension(200, 30));
        cboRole.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        if (createMode) {
            // In create mode, default to STUDENT and hide ADMIN
            cboRole.setSelectedItem("STUDENT");
            cboRole.setEnabled(false);
        }
        panel.add(cboRole, gbc);
        gbc.weightx = 0;

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

    private void loadUserData() {
        if (editingUser != null) {
            txtFullname.setText(editingUser.getFullname());
            txtEmail.setText(editingUser.getEmail());
            txtUsername.setText(editingUser.getUsername());
            if (editingUser.getRole() != null) {
                cboRole.setSelectedItem(editingUser.getRole().name());
            }
            // If editing an ADMIN user, disable role change
            if ("ADMIN".equals(editingUser.getRole().name())) {
                cboRole.setEnabled(false);
            }
        }
    }

    private void saveData() {
        if (!validateForm()) {
            return; // Do NOT dispose — let user fix errors
        }
        saved = true;
        dispose();
    }

    private boolean validateForm() {
        if (txtFullname.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Họ tên không được để trống", "Lỗi", JOptionPane.ERROR_MESSAGE);
            txtFullname.requestFocus();
            return false;
        }
        if (txtEmail.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Email không được để trống", "Lỗi", JOptionPane.ERROR_MESSAGE);
            txtEmail.requestFocus();
            return false;
        }
        if (!EMAIL_PATTERN.matcher(txtEmail.getText().trim()).matches()) {
            JOptionPane.showMessageDialog(this, "Email không đúng định dạng", "Lỗi", JOptionPane.ERROR_MESSAGE);
            txtEmail.requestFocus();
            return false;
        }
        if (txtUsername.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Username không được để trống", "Lỗi", JOptionPane.ERROR_MESSAGE);
            txtUsername.requestFocus();
            return false;
        }
        if (createMode) {
            String password = new String(txtPassword.getPassword());
            if (password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Mật khẩu không được để trống", "Lỗi", JOptionPane.ERROR_MESSAGE);
                txtPassword.requestFocus();
                return false;
            }
            if (password.length() < 6) {
                JOptionPane.showMessageDialog(this, "Mật khẩu phải có ít nhất 6 ký tự", "Lỗi", JOptionPane.ERROR_MESSAGE);
                txtPassword.requestFocus();
                return false;
            }
        }
        return true;
    }

    public boolean isSaved() {
        return saved;
    }

    public CreateUserRequest getCreateRequest() {
        return CreateUserRequest.builder()
                .fullname(txtFullname.getText().trim())
                .email(txtEmail.getText().trim())
                .username(txtUsername.getText().trim())
                .password(new String(txtPassword.getPassword()))
                .role((String) cboRole.getSelectedItem())
                .build();
    }

    public UpdateUserRequest getUpdateRequest() {
        return UpdateUserRequest.builder()
                .id(editingUser.getId())
                .fullname(txtFullname.getText().trim())
                .email(txtEmail.getText().trim())
                .username(txtUsername.getText().trim())
                .role((String) cboRole.getSelectedItem())
                .build();
    }

    // Helper methods
    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        return label;
    }

    private JTextField createTextField() {
        JTextField field = new JTextField();
        field.setPreferredSize(new Dimension(200, 30));
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        return field;
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
