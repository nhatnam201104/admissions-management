package com.example.managementadmissionwf.ui.frame;

import com.example.managementadmissionwf.bus.interfaces.AuthService;
import com.example.managementadmissionwf.ui.panel.LoginPanel.LeftWelcomePanel;
import com.example.managementadmissionwf.ui.panel.LoginPanel.RightLoginPanel;
import com.example.managementadmissionwf.ui.util.UIConstants;

import javax.swing.*;
import java.awt.*;

/**
 * LoginFrame - Main login window
 * Contains left welcome panel and right login panel
 * Fully fills the JFrame with two-column responsive layout
 */
public class LoginFrame extends JFrame {

    private JPanel rootPanel;
    private LeftWelcomePanel leftPanel;
    private RightLoginPanel rightPanel;

    public LoginFrame() {
        initComponents();
        setupFrame();
        setupLayout();
    }

    /**
     * Initialize components
     */
    private void initComponents() {
        leftPanel = new LeftWelcomePanel();
        rightPanel = new RightLoginPanel();

        // Root panel using BorderLayout for full frame coverage
        // This ensures the panel fills the entire JFrame
        rootPanel = new JPanel(new BorderLayout());
        rootPanel.setOpaque(false);
    }

    /**
     * Setup frame properties
     * Using BorderLayout on content pane ensures full frame filling
     */
    private void setupFrame() {
        setTitle("Login - Admissions Management");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


        setLayout(new BorderLayout());

        setResizable(true);

        // Set background color for the frame
        getContentPane().setBackground(UIConstants.BACKGROUND_GRAY);
    }

    /**
     * Setup layout for root panel
     * Both panels stretch to fill available space equally
     */
    private void setupLayout() {

        rootPanel.add(leftPanel, BorderLayout.WEST);

        rootPanel.add(rightPanel, BorderLayout.CENTER);

        add(rootPanel, BorderLayout.CENTER);
        pack();

        // Center on screen
        setLocationRelativeTo(null);
    }

    /**
     * Display the login frame
     */
    public void display() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                setVisible(true);
            }
        });
    }

}