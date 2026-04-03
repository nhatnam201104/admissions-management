package com.example.managementadmissionwf.ui.panel.score;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.example.managementadmissionwf.ui.util.UIFactory;


import javax.swing.*;
import java.awt.*;

/**
 * Main Panel for Score Management
 * Integrates filter, action bar, and list panel
 */
@Component
public class ScorePanel extends JPanel {
    
    @Autowired
    private ScoreController controller;
    
    private ScoreListPanel listPanel;
    
    // Filter components
    private JTextField txtSearch;
    private JComboBox<String> cboPhuongThuc;
    private JButton btnSearch;
    private JButton btnReset;
    
    // Action buttons
    private JButton btnAdd;
    private JButton btnEdit;
    private JButton btnDelete;
    private JButton btnRefresh;
    
    public ScorePanel() {
    }
    
    @PostConstruct
    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        
        // Create panels
        JPanel filterPanel = createFilterPanel();
        JPanel actionPanel = createActionPanel();
        listPanel = new ScoreListPanel();
        
        // Connect controller
        controller.setScorePanel(this);
        
        // Add panels
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(filterPanel, BorderLayout.NORTH);
        topPanel.add(actionPanel, BorderLayout.SOUTH);
        
        add(topPanel, BorderLayout.NORTH);
        add(listPanel, BorderLayout.CENTER);
        
        // Load initial data
        controller.loadAllScores();
    }
    
    private JPanel createFilterPanel() {
        JPanel panel = UIFactory.createFilterPanel();
        
        // Search field
        JLabel lblSearch = new JLabel("Tìm kiếm:");
        lblSearch.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        panel.add(lblSearch);
        
        txtSearch = new JTextField(25);
        txtSearch.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtSearch.setToolTipText("Tìm theo CCCD, SBD, hoặc Tên thí sinh");
        panel.add(txtSearch);
        
        // Phương thức filter
        JLabel lblPhuongThuc = new JLabel("Phương thức:");
        lblPhuongThuc.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        panel.add(lblPhuongThuc);
        
        String[] phuongThucItems = {"Tất cả", "THPT", "DGNL", "VSAT"};
        cboPhuongThuc = new JComboBox<>(phuongThucItems);
        cboPhuongThuc.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cboPhuongThuc.setPreferredSize(new Dimension(100, 30));
        panel.add(cboPhuongThuc);
        
        // Search button
        btnSearch = UIFactory.createActionButton("Tìm kiếm", "search", new Color(52, 152, 219), 120);
        btnSearch.addActionListener(e -> handleSearch());
        panel.add(btnSearch);
        
        // Reset button
        btnReset = UIFactory.createActionButton("Reset", "reset", new Color(149, 165, 166), 110);
        btnReset.addActionListener(e -> handleReset());
        panel.add(btnReset);
        
        return panel;
    }
    
    private JPanel createActionPanel() {
        JPanel panel = UIFactory.createActionPanel();
        
        // Add button
        btnAdd = UIFactory.createActionButton("Thêm", "add", new Color(46, 204, 113), 100);
        btnAdd.addActionListener(e -> controller.addScore());
        panel.add(btnAdd);
        
        // Edit button
        btnEdit = UIFactory.createActionButton("Sửa", "edit", new Color(52, 152, 219), 100);
        btnEdit.addActionListener(e -> controller.editScore());
        panel.add(btnEdit);
        
        // Delete button
        btnDelete = UIFactory.createActionButton("Xóa", "delete", new Color(231, 76, 60), 100);
        btnDelete.addActionListener(e -> controller.deleteScore());
        panel.add(btnDelete);
        
        // Refresh button
        btnRefresh = UIFactory.createActionButton("Làm mới", "refresh", new Color(149, 165, 166), 120);
        btnRefresh.addActionListener(e -> controller.refreshData());
        panel.add(btnRefresh);
        
        return panel;
    }
    
    private void handleSearch() {
        String keyword = txtSearch.getText().trim();
        String phuongThuc = (String) cboPhuongThuc.getSelectedItem();
        controller.searchScores(keyword, phuongThuc);
    }
    
    private void handleReset() {
        txtSearch.setText("");
        cboPhuongThuc.setSelectedIndex(0);
        controller.loadAllScores();
    }
    
    public ScoreListPanel getListPanel() {
        return listPanel;
    }
}