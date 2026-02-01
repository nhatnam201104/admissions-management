package com.example.managementadmissionwf.ui.panel;

import javax.swing.*;
import java.awt.*;

/**
 * AdmissionPanel - Temporary placeholder for Admission Result List
 * Shows demo UI only (large title JLabel)
 */
public class AdmissionPanel extends JPanel {

    public AdmissionPanel() {
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // Title label
        JLabel titleLabel = new JLabel("Screen: Admission Result List", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        titleLabel.setForeground(new Color(44, 62, 80));

        // Subtitle
        JLabel subtitleLabel = new JLabel("<html><center>This panel will contain:<br>• Admission result table<br>• View admission details<br>• Filter by major or year<br>• Export results functionality</center></html>", SwingConstants.CENTER);
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(127, 140, 141));

        // Panel for centering
        JPanel centerPanel = new JPanel(new BorderLayout(0, 20));
        centerPanel.setBackground(Color.BLUE);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));

        centerPanel.add(titleLabel, BorderLayout.NORTH);
        centerPanel.add(subtitleLabel, BorderLayout.CENTER);

        add(centerPanel, BorderLayout.CENTER);
    }
}
