package com.example.managementadmissionwf.ui.panel;

import javax.swing.*;
import java.awt.*;

/**
 * MajorPanel - Temporary placeholder for Major List Management
 * Shows demo UI only (large title JLabel)
 */
public class MajorPanel extends JPanel {
    
    public MajorPanel() {
        initComponents();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        
        // Title label
        JLabel titleLabel = new JLabel("Screen: Major List Management", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        titleLabel.setForeground(new Color(44, 62, 80));
        
        // Subtitle
        JLabel subtitleLabel = new JLabel("<html><center>This panel will contain:<br>• Major list table<br>• Add/Edit/Delete major functionality<br>• Major code and name<br>• Quota management</center></html>", SwingConstants.CENTER);
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
