package com.example.managementadmissionwf.ui.panel;

import javax.swing.*;
import java.awt.*;

/**
 * CandidatePanel - Temporary placeholder for Candidate Management
 * Shows demo UI only (large title JLabel)
 */
public class CandidatePanel extends JPanel {
    
    public CandidatePanel() {
        initComponents();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        
        // Title label
        JLabel titleLabel = new JLabel("Screen: Candidate Management", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        titleLabel.setForeground(new Color(44, 62, 80));
        
        // Subtitle
        JLabel subtitleLabel = new JLabel("<html><center>This panel will contain:<br>• Candidate list table<br>• Add/Edit/Delete candidate functionality<br>• Search and filter options<br>• Candidate details form</center></html>", SwingConstants.CENTER);
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
