package com.example.managementadmissionwf.ui.panel.candidate;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.example.managementadmissionwf.ui.util.UIFactory;

import javax.swing.*;
import java.awt.*;

/**
 * Main Panel for Candidate Management
 * Integrates filter, action bar, and list panel
 */
@Component
public class CandidatePanel extends JPanel {
    
    @Autowired
    private CandidateController controller;
    
    private CandidateListPanel listPanel;
    
    // Filter components
    private JTextField txtSearch;
    private JComboBox<String> cboKhuVuc;
    private JComboBox<String> cboDoiTuong;
    private JButton btnSearch;
    private JButton btnReset;
    
    // Action buttons
    private JButton btnAdd;
    private JButton btnEdit;
    private JButton btnDelete;
    private JButton btnRefresh;
    
    public CandidatePanel() {
    }
    
    @PostConstruct
    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        
        // Create panels
        JPanel filterPanel = createFilterPanel();
        JPanel actionPanel = createActionPanel();
        listPanel = new CandidateListPanel();
        
        // Connect controller
        controller.setCandidatePanel(this);
        
        // Add panels
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(filterPanel, BorderLayout.NORTH);
        topPanel.add(actionPanel, BorderLayout.SOUTH);
        
        add(topPanel, BorderLayout.NORTH);
        add(listPanel, BorderLayout.CENTER);
        
        // Load initial data
        controller.loadAllCandidates();
    }
    
    private JPanel createFilterPanel() {
        JPanel panel = UIFactory.createFilterPanel();
        
        // Search field
        JLabel lblSearch = new JLabel("Tìm kiếm:");
        lblSearch.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        panel.add(lblSearch);
        
        txtSearch = new JTextField(20);
        txtSearch.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtSearch.setToolTipText("Tìm theo CCCD, SBD, hoặc Họ tên");
        panel.add(txtSearch);
        
        // Khu vực filter
        JLabel lblKhuVuc = new JLabel("Khu vực:");
        lblKhuVuc.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        panel.add(lblKhuVuc);
        
        String[] khuVucItems = {"Tất cả", "KV1", "KV2", "KV3"};
        cboKhuVuc = new JComboBox<>(khuVucItems);
        cboKhuVuc.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cboKhuVuc.setPreferredSize(new Dimension(100, 30));
        panel.add(cboKhuVuc);
        
        // Đối tượng filter
        JLabel lblDoiTuong = new JLabel("Đối tượng:");
        lblDoiTuong.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        panel.add(lblDoiTuong);
        
        String[] doiTuongItems = {"Tất cả", "Không", "KV1", "KV2-NT", "KV2", "KV3", "Con thương binh"};
        cboDoiTuong = new JComboBox<>(doiTuongItems);
        cboDoiTuong.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cboDoiTuong.setPreferredSize(new Dimension(120, 30));
        panel.add(cboDoiTuong);
        
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
        btnAdd.addActionListener(e -> controller.addCandidate());
        panel.add(btnAdd);
        
        // Edit button
        btnEdit = UIFactory.createActionButton("Sửa", "edit", new Color(52, 152, 219), 100);
        btnEdit.addActionListener(e -> controller.editCandidate());
        panel.add(btnEdit);
        
        // Delete button
        btnDelete = UIFactory.createActionButton("Xóa", "delete", new Color(231, 76, 60), 100);
        btnDelete.addActionListener(e -> controller.deleteCandidate());
        panel.add(btnDelete);
        
        // Refresh button
        btnRefresh = UIFactory.createActionButton("Làm mới", "refresh", new Color(149, 165, 166), 120);
        btnRefresh.addActionListener(e -> controller.refreshData());
        panel.add(btnRefresh);
        
        return panel;
    }
    
    private void handleSearch() {
        String keyword = txtSearch.getText().trim();
        String khuVuc = (String) cboKhuVuc.getSelectedItem();
        String doiTuong = (String) cboDoiTuong.getSelectedItem();
        controller.searchCandidates(keyword, khuVuc, doiTuong);
    }
    
    private void handleReset() {
        txtSearch.setText("");
        cboKhuVuc.setSelectedIndex(0);
        cboDoiTuong.setSelectedIndex(0);
        controller.loadAllCandidates();
    }
    
    public CandidateListPanel getListPanel() {
        return listPanel;
    }
}