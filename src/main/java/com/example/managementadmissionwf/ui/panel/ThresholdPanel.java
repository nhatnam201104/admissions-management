package com.example.managementadmissionwf.ui.panel;

import javax.swing.*;
import java.awt.*;

/**
 * ThresholdPanel - Temporary placeholder for Admission Threshold Score Management
 * Shows demo UI only (large title JLabel)
 */
public class ThresholdPanel extends JPanel {
    
    public ThresholdPanel() {
        initComponents();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        
        // Title label
        JLabel titleLabel = new JLabel("Screen: Admission Threshold Score Management", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        titleLabel.setForeground(new Color(44, 62, 80));
        
        // Subtitle
        JLabel subtitleLabel = new JLabel("<html><center>This panel will contain:<br>• Threshold score list table<br>• Add/Edit/Delete threshold functionality<br>• Major-wise threshold scores<br>• Year-based threshold management</center></html>", SwingConstants.CENTER);
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
