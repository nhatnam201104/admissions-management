package com.example.managementadmissionwf.ui.panel.subjectgroup;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;

/**
 * Main Panel for Subject Group Management
 * Integrates filter, action bar, and list panel
 */
@Component
public class SubjectGroupPanel extends JPanel {
    
    @Autowired
    private SubjectGroupController controller;
    
    private SubjectGroupListPanel listPanel;
    
    // Filter components
    private JTextField txtSearch;
    private JButton btnSearch;
    private JButton btnReset;
    
    // Action buttons
    private JButton btnAdd;
    private JButton btnEdit;
    private JButton btnDelete;
    private JButton btnRefresh;
    
    public SubjectGroupPanel() {
    }
    
    @PostConstruct
    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        
        // Create panels
        JPanel filterPanel = createFilterPanel();
        JPanel actionPanel = createActionPanel();
        listPanel = new SubjectGroupListPanel();
        
        // Connect controller
        controller.setSubjectGroupPanel(this);
        
        // Add panels
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(filterPanel, BorderLayout.NORTH);
        topPanel.add(actionPanel, BorderLayout.SOUTH);
        
        add(topPanel, BorderLayout.NORTH);
        add(listPanel, BorderLayout.CENTER);
        
        // Load initial data
        controller.loadAllSubjectGroups();
    }
    
    private JPanel createFilterPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        panel.setBackground(new Color(240, 240, 240));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Search field
        JLabel lblSearch = new JLabel("Tìm kiếm:");
        lblSearch.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        panel.add(lblSearch);
        
        txtSearch = new JTextField(20);
        txtSearch.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtSearch.setToolTipText("Tìm theo mã tổ hợp hoặc tên môn");
        panel.add(txtSearch);
        
        // Search button
        btnSearch = createButton("Tìm kiếm", new Color(52, 152, 219));
        btnSearch.addActionListener(e -> handleSearch());
        panel.add(btnSearch);
        
        // Reset button
        btnReset = createButton("Reset", new Color(149, 165, 166));
        btnReset.addActionListener(e -> handleReset());
        panel.add(btnReset);
        
        return panel;
    }
    
    private JPanel createActionPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        panel.setBackground(new Color(240, 240, 240));
        panel.setBorder(BorderFactory.createEmptyBorder(5, 10, 10, 10));
        
        // Add button
        btnAdd = createButton("Thêm", new Color(46, 204, 113));
        btnAdd.addActionListener(e -> controller.addSubjectGroup());
        panel.add(btnAdd);
        
        // Edit button
        btnEdit = createButton("Sửa", new Color(52, 152, 219));
        btnEdit.addActionListener(e -> controller.editSubjectGroup());
        panel.add(btnEdit);
        
        // Delete button
        btnDelete = createButton("Xóa", new Color(231, 76, 60));
        btnDelete.addActionListener(e -> controller.deleteSubjectGroup());
        panel.add(btnDelete);
        
        // Refresh button
        btnRefresh = createButton("Làm mới", new Color(149, 165, 166));
        btnRefresh.addActionListener(e -> controller.refreshData());
        panel.add(btnRefresh);
        
        return panel;
    }
    
    private void handleSearch() {
        String keyword = txtSearch.getText().trim();
        controller.searchSubjectGroups(keyword);
    }
    
    private void handleReset() {
        txtSearch.setText("");
        controller.loadAllSubjectGroups();
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
    
    public SubjectGroupListPanel getListPanel() {
        return listPanel;
    }
}