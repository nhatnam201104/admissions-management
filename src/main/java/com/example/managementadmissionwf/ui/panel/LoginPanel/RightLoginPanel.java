package com.example.managementadmissionwf.ui.panel.LoginPanel;

import com.example.managementadmissionwf.bus.interfaces.AuthService;
import com.example.managementadmissionwf.config.ApplicationContextHolder;
import com.example.managementadmissionwf.dto.Auth.LoginDTO;
import com.example.managementadmissionwf.dto.Auth.LoginResponseDTO;
import com.example.managementadmissionwf.ui.component.CustomButton;
import com.example.managementadmissionwf.ui.component.CustomPasswordField;
import com.example.managementadmissionwf.ui.component.CustomTextField;
import com.example.managementadmissionwf.ui.component.Navigation;
import com.example.managementadmissionwf.ui.frame.MainFrame;
import com.example.managementadmissionwf.ui.util.UIConstants;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)

public class RightLoginPanel extends JPanel {

    JLabel titleLabel;
    JLabel subtitleLabel;
    CustomTextField usernameField;
    CustomPasswordField passwordField;
    JCheckBox rememberMeCheckBox;
    CustomButton loginButton;
    JLabel signupLabel;
    JLabel forgotPasswordLabel;
    AuthService authService;

    // final LoginController controller;

    public RightLoginPanel() {
        // this.controller = controller;
        initComponents();
        setupLayout();
        authService = ApplicationContextHolder.getBean(AuthService.class);
    }

    /**
     * Initialize components
     */
    private void initComponents() {
        // Title label
        titleLabel = new JLabel("Login");
        titleLabel.setFont(UIConstants.FONT_TITLE);
        titleLabel.setForeground(UIConstants.TEXT_PRIMARY);
        titleLabel.setHorizontalAlignment(SwingConstants.LEFT);

        // Subtitle label
        subtitleLabel = new JLabel("Please login to continue");
        subtitleLabel.setFont(UIConstants.FONT_BODY);
        subtitleLabel.setForeground(UIConstants.TEXT_SECONDARY);
        subtitleLabel.setHorizontalAlignment(SwingConstants.LEFT);

        // Username field - NO setPreferredSize, let it scale with container
        usernameField = new CustomTextField();
        usernameField.setMinimumSize(new Dimension(200, UIConstants.TEXT_FIELD_HEIGHT));

        // Password field - NO setPreferredSize, let it scale with container
        passwordField = new CustomPasswordField();
        passwordField.setMinimumSize(new Dimension(200, UIConstants.TEXT_FIELD_HEIGHT));

        // Remember me checkbox
        rememberMeCheckBox = new JCheckBox("Remember me");
        rememberMeCheckBox.setFont(UIConstants.FONT_SMALL);
        rememberMeCheckBox.setForeground(UIConstants.TEXT_SECONDARY);
        rememberMeCheckBox.setOpaque(false);
        rememberMeCheckBox.setBorderPainted(false);
        rememberMeCheckBox.setFocusPainted(false);
        rememberMeCheckBox.setIcon(new CustomCheckBoxIcon());
        rememberMeCheckBox.setSelectedIcon(new CustomCheckBoxIcon(true));

        // Login button
        loginButton = new CustomButton("LOGIN");
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleLogin();
            }
        });

        // Signup label (hyperlink style)
        signupLabel = new JLabel("<html><u>Don't have an account? Signup</u></html>");
        signupLabel.setFont(UIConstants.FONT_LINK);
        signupLabel.setForeground(UIConstants.PRIMARY_BLUE);
        signupLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        signupLabel.setHorizontalAlignment(SwingConstants.CENTER);
        signupLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                handleSignup();
            }
        });

        // Forgot password label (hyperlink style)
        forgotPasswordLabel = new JLabel("<html><u>Forgot your password?</u></html>");
        forgotPasswordLabel.setFont(UIConstants.FONT_LINK);
        forgotPasswordLabel.setForeground(UIConstants.PRIMARY_BLUE);
        forgotPasswordLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        forgotPasswordLabel.setHorizontalAlignment(SwingConstants.CENTER);
        forgotPasswordLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                handleForgotPassword();
            }
        });
    }

    /**
     * Setup layout and styling
     * Using BorderLayout to stretch to full height
     * GridBagLayout with weightx/weighty for responsive form
     */
    private void setupLayout() {
        // Use BorderLayout to fill entire available space
        // This ensures panel stretches vertically when resized
        setLayout(new BorderLayout());
        setBackground(UIConstants.BACKGROUND_WHITE);
        setPreferredSize(new Dimension(UIConstants.LOGIN_FRAME_WIDTH / 2, UIConstants.LOGIN_FRAME_HEIGHT));
        // NO setPreferredSize() - let layout manager determine size dynamically

        // Main content panel with padding
        // GridBagLayout allows precise control over component sizing and positioning
        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setOpaque(false);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(
                UIConstants.CONTAINER_PADDING,
                UIConstants.CONTAINER_PADDING,
                UIConstants.CONTAINER_PADDING,
                UIConstants.CONTAINER_PADDING));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 0, UIConstants.SPACING_MEDIUM, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.gridwidth = GridBagConstraints.REMAINDER;

        // Add title
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        contentPanel.add(titleLabel, gbc);

        // Add subtitle
        gbc.gridy = 1;
        contentPanel.add(subtitleLabel, gbc);

        // Add spacer
        gbc.gridy = 2;
        gbc.weighty = 0.1;
        contentPanel.add(Box.createVerticalStrut(UIConstants.SPACING_LARGE), gbc);
        gbc.weighty = 0;

        // Username label
        JLabel usernameLabel = new JLabel("Username");
        usernameLabel.setFont(UIConstants.FONT_SMALL);
        usernameLabel.setForeground(UIConstants.TEXT_SECONDARY);
        usernameLabel.setHorizontalAlignment(SwingConstants.LEFT);
        gbc.gridy = 3;
        gbc.insets = new Insets(0, 0, UIConstants.SPACING_SMALL, 0);
        contentPanel.add(usernameLabel, gbc);

        // Username field - weightx=1 makes it stretch horizontally
        gbc.gridy = 4;
        gbc.insets = new Insets(0, 0, UIConstants.SPACING_MEDIUM, 0);
        gbc.weightx = 1.0;
        contentPanel.add(usernameField, gbc);

        // Password label
        JLabel passwordLabel = new JLabel("Password");
        passwordLabel.setFont(UIConstants.FONT_SMALL);
        passwordLabel.setForeground(UIConstants.TEXT_SECONDARY);
        passwordLabel.setHorizontalAlignment(SwingConstants.LEFT);
        gbc.gridy = 5;
        gbc.insets = new Insets(0, 0, UIConstants.SPACING_SMALL, 0);
        contentPanel.add(passwordLabel, gbc);

        // Password field - weightx=1 makes it stretch horizontally
        gbc.gridy = 6;
        gbc.insets = new Insets(0, 0, UIConstants.SPACING_MEDIUM, 0);
        gbc.weightx = 1.0;
        contentPanel.add(passwordField, gbc);

        // Remember me checkbox and forgot password in same row


        // Login button - weightx=1 makes it stretch horizontally
        gbc.gridy = 7;
        gbc.insets = new Insets(0, 0, UIConstants.SPACING_LARGE, 0);
        gbc.weightx = 1.0;
        contentPanel.add(loginButton, gbc);

        // Spacer with weighty to push form to top
        gbc.gridy = 8;
        gbc.weighty = 0.3;
        contentPanel.add(Box.createVerticalGlue(), gbc);
        gbc.weighty = 0;

        // Signup link
//        gbc.gridy = 10;
//        gbc.insets = new Insets(0, 0, UIConstants.SPACING_SMALL, 0);
//        gbc.anchor = GridBagConstraints.CENTER;
//        contentPanel.add(signupLabel, gbc);
//
//        // Forgot password link
//        gbc.gridy = 11;
//        gbc.insets = new Insets(0, 0, 0, 0);
//        contentPanel.add(forgotPasswordLabel, gbc);

        // Add content panel to CENTER - stretches both horizontally and vertically
        // BorderLayout.CENTER fills all available space (fill=BOTH)
        add(contentPanel, BorderLayout.CENTER);
    }

    /**
     * Handle login button click
     * Delegates authentication to controller and handles result
     */
    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        // UI validation - only for basic empty check before network call
        // Detailed validation is in DTO layer
        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please fill out all of fields!",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }


        // Create DTO
        LoginDTO loginDTO = new LoginDTO(username, password);

        // Call authentication
        LoginResponseDTO response = authService.login(loginDTO);

        if (response.isSuccess() && response.getUser() != null) {
            // Get MainFrame from Spring context and set user
            MainFrame mainFrame = ApplicationContextHolder.getBean(MainFrame.class);
            mainFrame.setUser(response.getUser());

            // Initialize MainFrame components
            mainFrame.initComponents();
            mainFrame.setupFrame();

            Navigation navController = new Navigation();
            navController.init(mainFrame);
            mainFrame.setVisible(true);

            // Close login window
            Window window = SwingUtilities.getWindowAncestor(this);
            if (window != null) {
                window.dispose();
            }
        } else {
            // Should not happen if exceptions are properly handled
            JOptionPane.showMessageDialog(this,
                    "Login failed. Please try again.",
                    "Login Error",
                    JOptionPane.ERROR_MESSAGE);
        }

    }

    private void handleSignup() {
        // TODO: Implement signup navigation
        System.out.println("Signup clicked");
    }

    /**
     * Handle forgot password link click - Placeholder for business logic
     */
    private void handleForgotPassword() {
        // TODO: Implement forgot password logic
        System.out.println("Forgot password clicked");
    }

    /**
     * Custom checkbox icon
     */
    private static class CustomCheckBoxIcon implements Icon {
        private final boolean selected;

        public CustomCheckBoxIcon() {
            this(false);
        }

        public CustomCheckBoxIcon(boolean selected) {
            this.selected = selected;
        }

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Draw box
            g2d.setColor(UIConstants.BORDER_LIGHT);
            g2d.drawRoundRect(x, y, getIconWidth() - 1, getIconHeight() - 1, 3, 3);

            if (selected) {
                g2d.setColor(UIConstants.PRIMARY_BLUE);
                g2d.fillRoundRect(x, y, getIconWidth() - 1, getIconHeight() - 1, 3, 3);

                // Draw checkmark
                g2d.setColor(Color.WHITE);
                g2d.setStroke(new BasicStroke(2));
                int[] xPoints = {x + 4, x + 7, x + 11};
                int[] yPoints = {y + 8, y + 11, y + 4};
                g2d.drawPolyline(xPoints, yPoints, 3);
            }

            g2d.dispose();
        }

        @Override
        public int getIconWidth() {
            return 18;
        }

        @Override
        public int getIconHeight() {
            return 18;
        }
    }
}