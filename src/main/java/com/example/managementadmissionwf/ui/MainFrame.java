package com.example.managementadmissionwf.ui;

import com.example.managementadmissionwf.bus.interfaces.StudentService;
import com.example.managementadmissionwf.dto.StudentDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;


@Component
@RequiredArgsConstructor
public class MainFrame extends JFrame {
    
    private final StudentService studentService;
    
    // UI Components
    private JTable studentTable;
    private DefaultTableModel tableModel;
    private JTextField txtStudentCode;
    private JTextField txtFirstName;
    private JTextField txtLastName;
    private JTextField txtEmail;
    private JTextField txtPhone;
    private JTextField txtAddress;
    private JComboBox<StudentDTO.StudentStatus> cmbStatus;
    private JButton btnDelete;
    private JButton btnRefresh;
    private JButton btnApprove;
    private JButton btnReject;
    private JButton btnEnroll;
    private JButton btnSearch;
    private JTextField txtSearch;
    private JLabel lblStatus;
    
    public void initialize() {
        // Frame setup
        setTitle("Student Admission Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 700);
        setLocationRelativeTo(null);
        
        // Create main panel with BorderLayout
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Create top panel for statistics
        JPanel topPanel = createStatisticsPanel();
        mainPanel.add(topPanel, BorderLayout.NORTH);
        
        // Create split pane
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(400);
        splitPane.setResizeWeight(0.4);
        
        // Create form panel (left side)
        JPanel formPanel = createFormPanel();
        splitPane.setLeftComponent(formPanel);
        
        // Create table panel (right side)
        JPanel tablePanel = createTablePanel();
        splitPane.setRightComponent(tablePanel);
        
        mainPanel.add(splitPane, BorderLayout.CENTER);
        
        // Add to frame
        add(mainPanel);
        
        // Load initial data
        loadStudents();
        
        // Make visible
        setVisible(true);
    }
    
    private JPanel createStatisticsPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Statistics"));
        
        var statistics = studentService.getStudentStatistics();
        String statsText = "Total Students: " + statistics.values().stream().mapToLong(Long::longValue).sum() + " | " +
                "Pending: " + statistics.getOrDefault(StudentDTO.StudentStatus.PENDING, 0L) + " | " +
                "Approved: " + statistics.getOrDefault(StudentDTO.StudentStatus.APPROVED, 0L) + " | " +
                "Enrolled: " + statistics.getOrDefault(StudentDTO.StudentStatus.ENROLLED, 0L);
        
        JLabel lblStats = new JLabel(statsText);
        lblStats.setFont(new Font("Arial", Font.BOLD, 12));
        panel.add(lblStats);
        
        return panel;
    }
    
    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Student Form"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Student Code
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Student Code:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        txtStudentCode = new JTextField(20);
        panel.add(txtStudentCode, gbc);
        
        // First Name
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        panel.add(new JLabel("First Name:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        txtFirstName = new JTextField(20);
        panel.add(txtFirstName, gbc);
        
        // Last Name
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0;
        panel.add(new JLabel("Last Name:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        txtLastName = new JTextField(20);
        panel.add(txtLastName, gbc);
        
        // Email
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0;
        panel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        txtEmail = new JTextField(20);
        panel.add(txtEmail, gbc);
        
        // Phone
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.weightx = 0;
        panel.add(new JLabel("Phone:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        txtPhone = new JTextField(20);
        panel.add(txtPhone, gbc);
        
        // Address
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.weightx = 0;
        panel.add(new JLabel("Address:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        txtAddress = new JTextField(20);
        panel.add(txtAddress, gbc);
        
        // Status
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.weightx = 0;
        panel.add(new JLabel("Status:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        cmbStatus = new JComboBox<>(StudentDTO.StudentStatus.values());
        panel.add(cmbStatus, gbc);
        
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
        JButton btnAdd = new JButton("Add");
        JButton btnUpdate = new JButton("Update");
        btnDelete = new JButton("Delete");
        btnClear = new JButton("Clear");
        
        buttonPanel.add(btnAdd);
        buttonPanel.add(btnUpdate);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnClear);
        
        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.CENTER;
        panel.add(buttonPanel, gbc);
        
        // Status label
        lblStatus = new JLabel(" ");
        lblStatus.setForeground(Color.BLUE);
        gbc.gridy = 8;
        panel.add(lblStatus, gbc);
        
        // Add action listeners
        btnAdd.addActionListener(this::handleAdd);
        btnUpdate.addActionListener(this::handleUpdate);
        btnDelete.addActionListener(this::handleDelete);
        btnClear.addActionListener(this::handleClear);
        
        return panel;
    }
    
    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Student List"));
        
        // Search panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        searchPanel.add(new JLabel("Search:"));
        txtSearch = new JTextField(30);
        btnSearch = new JButton("Search");
        searchPanel.add(txtSearch);
        searchPanel.add(btnSearch);
        btnRefresh = new JButton("Refresh");
        searchPanel.add(btnRefresh);
        panel.add(searchPanel, BorderLayout.NORTH);
        
        // Table
        tableModel = new DefaultTableModel(
            new Object[]{"ID", "Code", "Name", "Email", "Status"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        studentTable = new JTable(tableModel);
        studentTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        studentTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                handleTableSelection();
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(studentTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Action buttons
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        btnApprove = new JButton("Approve");
        btnReject = new JButton("Reject");
        btnEnroll = new JButton("Enroll");
        actionPanel.add(btnApprove);
        actionPanel.add(btnReject);
        actionPanel.add(btnEnroll);
        panel.add(actionPanel, BorderLayout.SOUTH);
        
        // Add action listeners
        btnSearch.addActionListener(this::handleSearch);
        btnRefresh.addActionListener(e -> loadStudents());
        btnApprove.addActionListener(this::handleApprove);
        btnReject.addActionListener(this::handleReject);
        btnEnroll.addActionListener(this::handleEnroll);
        
        return panel;
    }
    
    private void loadStudents() {
        try {
            List<StudentDTO> students = studentService.getAllStudents();
            populateTable(students);
            updateStatus("Loaded " + students.size() + " students");
        } catch (Exception e) {
            showError("Error loading students: " + e.getMessage());
        }
    }
    
    private void populateTable(List<StudentDTO> students) {
        tableModel.setRowCount(0);
        for (StudentDTO student : students) {
            tableModel.addRow(new Object[]{
                student.getId(),
                student.getStudentCode(),
                student.getFullName(),
                student.getEmail(),
                student.getStatus()
            });
        }
    }
    
    private void handleAdd(ActionEvent e) {
        try {
            StudentDTO studentDTO = createStudentDTO();
            StudentDTO created = studentService.createStudent(studentDTO);
            updateStatus("Student created successfully: " + created.getFullName());
            clearForm();
            loadStudents();
        } catch (Exception ex) {
            showError("Error creating student: " + ex.getMessage());
        }
    }
    
    private void handleUpdate(ActionEvent e) {
        int selectedRow = studentTable.getSelectedRow();
        if (selectedRow == -1) {
            showError("Please select a student to update");
            return;
        }
        
        try {
            Long id = (Long) tableModel.getValueAt(selectedRow, 0);
            StudentDTO studentDTO = createStudentDTO();
            StudentDTO updated = studentService.updateStudent(id, studentDTO);
            updateStatus("Student updated successfully: " + updated.getFullName());
            clearForm();
            loadStudents();
        } catch (Exception ex) {
            showError("Error updating student: " + ex.getMessage());
        }
    }
    
    private void handleDelete(ActionEvent e) {
        int selectedRow = studentTable.getSelectedRow();
        if (selectedRow == -1) {
            showError("Please select a student to delete");
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to delete this student?",
            "Confirm Delete",
            JOptionPane.YES_NO_OPTION
        );
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                Long id = (Long) tableModel.getValueAt(selectedRow, 0);
                studentService.deleteStudent(id);
                updateStatus("Student deleted successfully");
                clearForm();
                loadStudents();
            } catch (Exception ex) {
                showError("Error deleting student: " + ex.getMessage());
            }
        }
    }
    
    private void handleClear(ActionEvent e) {
        clearForm();
    }
    
    private void handleSearch(ActionEvent e) {
        String searchTerm = txtSearch.getText().trim();
        if (searchTerm.isEmpty()) {
            loadStudents();
            return;
        }
        
        try {
            List<StudentDTO> students = studentService.searchStudents(searchTerm);
            populateTable(students);
            updateStatus("Found " + students.size() + " students matching '" + searchTerm + "'");
        } catch (Exception ex) {
            showError("Error searching students: " + ex.getMessage());
        }
    }
    
    private void handleApprove(ActionEvent e) {
        int selectedRow = studentTable.getSelectedRow();
        if (selectedRow == -1) {
            showError("Please select a student to approve");
            return;
        }
        
        try {
            Long id = (Long) tableModel.getValueAt(selectedRow, 0);
            StudentDTO updated = studentService.approveStudent(id);
            updateStatus("Student approved: " + updated.getFullName());
            loadStudents();
        } catch (Exception ex) {
            showError("Error approving student: " + ex.getMessage());
        }
    }
    
    private void handleReject(ActionEvent e) {
        int selectedRow = studentTable.getSelectedRow();
        if (selectedRow == -1) {
            showError("Please select a student to reject");
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to reject this student?",
            "Confirm Reject",
            JOptionPane.YES_NO_OPTION
        );
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                Long id = (Long) tableModel.getValueAt(selectedRow, 0);
                StudentDTO updated = studentService.rejectStudent(id);
                updateStatus("Student rejected: " + updated.getFullName());
                loadStudents();
            } catch (Exception ex) {
                showError("Error rejecting student: " + ex.getMessage());
            }
        }
    }
    
    private void handleEnroll(ActionEvent e) {
        int selectedRow = studentTable.getSelectedRow();
        if (selectedRow == -1) {
            showError("Please select a student to enroll");
            return;
        }
        
        try {
            Long id = (Long) tableModel.getValueAt(selectedRow, 0);
            StudentDTO updated = studentService.enrollStudent(id);
            updateStatus("Student enrolled: " + updated.getFullName());
            loadStudents();
        } catch (Exception ex) {
            showError("Error enrolling student: " + ex.getMessage());
        }
    }
    
    private void handleTableSelection() {
        int selectedRow = studentTable.getSelectedRow();
        if (selectedRow == -1) {
            clearForm();
            return;
        }
        
        try {
            Long id = (Long) tableModel.getValueAt(selectedRow, 0);
            StudentDTO student = studentService.getStudentById(id).orElse(null);
            if (student != null) {
                txtStudentCode.setText(student.getStudentCode());
                txtFirstName.setText(student.getFirstName());
                txtLastName.setText(student.getLastName());
                txtEmail.setText(student.getEmail());
                txtPhone.setText(student.getPhoneNumber());
                txtAddress.setText(student.getAddress());
                cmbStatus.setSelectedItem(student.getStatus());
            }
        } catch (Exception ex) {
            showError("Error loading student details: " + ex.getMessage());
        }
    }
    
    private StudentDTO createStudentDTO() {
        return StudentDTO.builder()
            .studentCode(txtStudentCode.getText().trim())
            .firstName(txtFirstName.getText().trim())
            .lastName(txtLastName.getText().trim())
            .email(txtEmail.getText().trim())
            .phoneNumber(txtPhone.getText().trim())
            .address(txtAddress.getText().trim())
            .status((StudentDTO.StudentStatus) cmbStatus.getSelectedItem())
            .build();
    }
    
    private void clearForm() {
        txtStudentCode.setText("");
        txtFirstName.setText("");
        txtLastName.setText("");
        txtEmail.setText("");
        txtPhone.setText("");
        txtAddress.setText("");
        cmbStatus.setSelectedIndex(0);
        studentTable.clearSelection();
    }
    
    private void updateStatus(String message) {
        lblStatus.setText(message);
    }
    
    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
        updateStatus("Error: " + message);
    }
    
    private JButton btnClear;
}
