package com.example.managementadmissionwf.ui.panel;

import javax.swing.*;
import java.awt.*;

/**
 * UserManagementPanel - Temporary placeholder for User Management
 * Shows demo UI only (large title JLabel)
 */
public class UserManagementPanel extends JPanel {
    
    public UserManagementPanel() {
        initComponents();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        
        // Title label
        JLabel titleLabel = new JLabel("Screen: Quản lý người dùng", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        titleLabel.setForeground(new Color(44, 62, 80));
        
        // Subtitle
        JLabel subtitleLabel = new JLabel("<html><center>This panel will contain:<br>• User list table<br>• Add/Edit/Delete user functionality<br>• Change password<br>• Change user role (user ↔ admin)<br>• Enable/disable users</center></html>", SwingConstants.CENTER);
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(127, 140, 141));
        
        // Panel for centering
        JPanel centerPanel = new JPanel(new BorderLayout(0, 20));
        centerPanel.setBackground(Color.WHITE);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));
        
        centerPanel.add(titleLabel, BorderLayout.NORTH);
        centerPanel.add(subtitleLabel, BorderLayout.CENTER);
        
        add(centerPanel, BorderLayout.CENTER);
    }
}