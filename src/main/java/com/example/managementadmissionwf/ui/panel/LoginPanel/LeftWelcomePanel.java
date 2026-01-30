package com.example.managementadmissionwf.ui.panel.LoginPanel;

import com.example.managementadmissionwf.ui.util.UIConstants;

import javax.swing.*;
import java.awt.*;

/**
 * LeftWelcomePanel - Left panel of the login screen
 * Features blue gradient background with welcome message and decorative
 * elements
 * Stretches to fill available vertical space
 */
public class LeftWelcomePanel extends JPanel {

    private JLabel titleLabel;
    private JLabel subtitleLabel;
    private JLabel descriptionLabel;
    private JLabel logoLabel;

    public LeftWelcomePanel() {
        initComponents();
        setupLayout();
    }

    /**
     * Initialize components
     */
    private void initComponents() {
        // Logo label
        logoLabel = new JLabel("COMPANY LOGO");
        logoLabel.setFont(UIConstants.FONT_SUBTITLE);
        logoLabel.setForeground(UIConstants.TEXT_WHITE);
        logoLabel.setHorizontalAlignment(SwingConstants.LEFT);

        // Title label
        titleLabel = new JLabel("Welcome to");
        titleLabel.setFont(UIConstants.FONT_TITLE);
        titleLabel.setForeground(UIConstants.TEXT_WHITE);
        titleLabel.setHorizontalAlignment(SwingConstants.LEFT);

        // Subtitle label
        subtitleLabel = new JLabel("Admissions Management");
        subtitleLabel.setFont(UIConstants.FONT_HEADING);
        subtitleLabel.setForeground(UIConstants.TEXT_WHITE);
        subtitleLabel.setHorizontalAlignment(SwingConstants.LEFT);

        // Description label
        descriptionLabel = new JLabel("<html><div style='text-align: left; padding: 10px 0;'>"
                + "Streamline your admissions process with our comprehensive management system. "
                + "Access student records, track applications, and manage enrollment efficiently."
                + "</div></html>");
        descriptionLabel.setFont(UIConstants.FONT_BODY);
        descriptionLabel.setForeground(UIConstants.TEXT_WHITE);
        descriptionLabel.setHorizontalAlignment(SwingConstants.LEFT);
    }

    /**
     * Setup layout and styling
     * Using BorderLayout to stretch to full height
     * No fixed sizes - panel grows with window
     */
    private void setupLayout() {
        // Use BorderLayout to fill entire available space
        // This ensures panel stretches vertically when resized
        setLayout(new BorderLayout());
        setBackground(UIConstants.PRIMARY_BLUE);

        // NO setPreferredSize() - let layout manager determine size dynamically

        // Main content panel with padding
        JPanel contentPanel = new JPanel();
        contentPanel.setPreferredSize(new Dimension(UIConstants.LOGIN_FRAME_WIDTH / 2, UIConstants.LOGIN_FRAME_HEIGHT));
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setOpaque(false);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(
                UIConstants.CONTAINER_PADDING,
                UIConstants.CONTAINER_PADDING,
                UIConstants.CONTAINER_PADDING,
                UIConstants.CONTAINER_PADDING));

        // Add components with spacing
        contentPanel.add(logoLabel);
        contentPanel.add(Box.createVerticalStrut(UIConstants.SPACING_EXTRA_LARGE));
        contentPanel.add(titleLabel);
        contentPanel.add(subtitleLabel);
        contentPanel.add(Box.createVerticalStrut(UIConstants.SPACING_LARGE));
        contentPanel.add(descriptionLabel);
        contentPanel.add(Box.createVerticalGlue());

        // Decorative wave panel at bottom
        JPanel wavePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int width = getWidth();
                int height = getHeight();

                // Draw decorative wave shape - scales with panel size
                g2d.setColor(new Color(255, 255, 255, 30));
                g2d.fillOval(-width / 2, height - 100, width * 2, 200);
                g2d.setColor(new Color(255, 255, 255, 20));
                g2d.fillOval(-width / 2, height - 150, width * 2, 200);

                g2d.dispose();
            }
        };
        wavePanel.setOpaque(false);
        // NO setPreferredSize() - let it scale with parent

        // Add components to main panel
        // BorderLayout.CENTER makes contentPanel fill available vertical space
        add(contentPanel, BorderLayout.CENTER);
        // BorderLayout.SOUTH places wavePanel at bottom, it scales horizontally
        add(wavePanel, BorderLayout.SOUTH);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        // Draw gradient background - scales to current panel size
        // getHeight() returns actual height when resized
        GradientPaint gradient = new GradientPaint(
                0, 0, UIConstants.GRADIENT_START,
                0, getHeight(), UIConstants.GRADIENT_END);
        g2d.setPaint(gradient);
        g2d.fillRect(0, 0, getWidth(), getHeight());

        g2d.dispose();
    }
}