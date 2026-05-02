package com.example.managementadmissionwf.ui.frame;

import com.example.managementadmissionwf.dto.User.UserDTO;
import com.example.managementadmissionwf.ui.panel.candidate.CandidatePanel;
import com.example.managementadmissionwf.ui.panel.major.MajorPanel;
import com.example.managementadmissionwf.ui.panel.score.ScorePanel;
import com.example.managementadmissionwf.ui.panel.subjectgroup.SubjectGroupPanel;
import com.example.managementadmissionwf.ui.panel.user.UserManagementPanel;
import com.example.managementadmissionwf.ui.panel.BonusScorePanel;
import com.example.managementadmissionwf.ui.panel.ConversionTablePanel;
import com.example.managementadmissionwf.ui.panel.StatisticPanel;
import com.example.managementadmissionwf.ui.util.UIConstants;
import com.example.managementadmissionwf.ui.util.UIFactory;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

/**
 * MainFrame - Overall layout with sidebar and content panel
 * NetBeans GUI Builder compatible (.form file required)
 */
@Slf4j
@Getter
@Setter
public class MainFrame extends JFrame {

    private JPanel rootPanel;
    private JPanel sidebarPanel;
    private JPanel contentPanel;

    // User section components
    private JLabel userIconLabel;
    private JLabel usernameLabel;

    // Menu components
    private JPanel menuPanel;
    private JButton menuCandidate;
    private JButton menuScore;
    private JButton menuMajor;
    private JButton menuSubjectGroup;

    private JButton menuBonusScore;
    private JButton menuConversionTable;
    private JButton menuUserManagement;
    private JButton menuAdmission;
    private JButton menuStatistic;
    private UserDTO user;

    // Injected panels
    @Autowired(required = false)
    private CandidatePanel candidatePanel;

    @Autowired(required = false)
    private ScorePanel scorePanel;

    @Autowired(required = false)
    private MajorPanel majorPanel;

    @Autowired(required = false)
    private SubjectGroupPanel subjectGroupPanel;

    @Autowired(required = false)
    private UserManagementPanel userManagementPanel;

    @Autowired(required = false)
    private BonusScorePanel bonusScorePanel;

    @Autowired(required = false)
    private ConversionTablePanel conversionTablePanel;

    @Autowired(required = false)
    private com.example.managementadmissionwf.ui.panel.admission.AdmissionPanel admissionPanel;


    @Autowired(required = false)
    private StatisticPanel statisticPanel;


    /**
     * Default constructor for Spring Bean
     */
    public MainFrame() {
    }

    /**
     * Constructor with user parameter
     */
    public MainFrame(UserDTO user) {
        this.user = user;
        initComponents();
        setupFrame();
    }

    /**
     * Initialize components - NetBeans GUI Builder will generate this
     */
    public void initComponents() {
        // Root panel
        rootPanel = new JPanel();
        rootPanel.setLayout(new BorderLayout(0, 0));

        // Sidebar panel - Use BoxLayout Y-axis for vertical stacking
        sidebarPanel = new JPanel();
        sidebarPanel.setLayout(new BoxLayout(sidebarPanel, BoxLayout.Y_AXIS));
        sidebarPanel.setPreferredSize(new Dimension(300, 600));
        sidebarPanel.setBackground(new Color(44, 62, 80));

        // User section - Use FlowLayout for horizontal layout of user info
        JPanel userSection = new JPanel();
        userSection.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 15));
        userSection.setBackground(new Color(44, 62, 80));
        userSection.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        userSection.setAlignmentX(Component.LEFT_ALIGNMENT);

        userIconLabel = new JLabel();
        userIconLabel.setIcon(Objects.requireNonNullElseGet(
                UIFactory.loadIcon("users", 24, 24),
                () -> new ImageIcon(Objects.requireNonNull(getClass().getResource("/icons/users.png")))));
        userIconLabel.setPreferredSize(new Dimension(50, 50));

        usernameLabel = new JLabel(user.getUsername());
        usernameLabel.setForeground(Color.WHITE);
        usernameLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));

        userSection.add(userIconLabel);
        userSection.add(usernameLabel);
        userSection.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));

        // Menu panel - Use BoxLayout Y-axis for vertical stacking
        menuPanel = new JPanel();
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
        menuPanel.setBackground(new Color(44, 62, 80));
        menuPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));
        menuPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Create menu buttons
        menuCandidate = createMenuButton("Quản lý thí sinh", "users");
        menuScore = createMenuButton("Quản lý điểm", "score");
        menuMajor = createMenuButton("Danh sách ngành", "book");
        menuSubjectGroup = createMenuButton("Tổ hợp môn", "layers");
        menuBonusScore = createMenuButton("Điểm cộng", "trendingUp");
        menuConversionTable = createMenuButton("Bảng quy đổi", "layers");
        menuUserManagement = createMenuButton("Quản lý người dùng", "users");
        menuAdmission = createMenuButton("Quản lý nguyện vọng và xét tuyển ", "checkCircle");
        menuStatistic = createMenuButton("Thống kê", "barchart");

        // Add buttons to menu panel
        menuPanel.add(menuCandidate);
        menuPanel.add(Box.createVerticalStrut(5));
        menuPanel.add(menuScore);
        menuPanel.add(Box.createVerticalStrut(5));
        menuPanel.add(menuMajor);
        menuPanel.add(Box.createVerticalStrut(5));
        menuPanel.add(menuSubjectGroup);
        menuPanel.add(Box.createVerticalStrut(5));
        menuPanel.add(menuBonusScore);
        menuPanel.add(Box.createVerticalStrut(5));
        menuPanel.add(menuConversionTable);
        menuPanel.add(Box.createVerticalStrut(5));
        menuPanel.add(menuUserManagement);
        menuPanel.add(Box.createVerticalStrut(5));
        if (user.getRole().toString().equalsIgnoreCase("admin")) {
            menuPanel.add(menuAdmission);
            menuPanel.add(Box.createVerticalStrut(5));
            menuPanel.add(menuStatistic);
        }

        // Add menu button action listeners
        setupMenuListeners();

        // Add components to sidebar
        sidebarPanel.add(userSection);
        sidebarPanel.add(menuPanel);

        // Content panel
        contentPanel = new JPanel();
        contentPanel.setLayout(new BorderLayout());
        contentPanel.setBackground(Color.WHITE);

        // Add panels to root
        rootPanel.add(sidebarPanel, BorderLayout.WEST);
        rootPanel.add(contentPanel, BorderLayout.CENTER);

        // Set content pane
        setContentPane(rootPanel);
    }

    /**
     * Create a menu button with styling
     */
    private JButton createMenuButton(String text, String iconName) {
        JButton button = new JButton(text);
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setBackground(new Color(44, 62, 80));
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Add icon
        try {
            ImageIcon icon = UIFactory.loadIcon(iconName, 18, 18);
            if (icon != null) {
                button.setIcon(icon);
                button.setIconTextGap(15);
            }
        } catch (Exception e) {
            // Icon not found, continue without icon
        }

        // Hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(52, 152, 219));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                if (!button.isSelected()) {
                    button.setBackground(new Color(44, 62, 80));
                }
            }
        });

        return button;
    }

    /**
     * Setup menu button action listeners
     */
    private void setupMenuListeners() {
        menuCandidate.addActionListener(e -> {
            if (candidatePanel != null) {
                setContent(candidatePanel);
                highlightMenuButton(menuCandidate);
            }
        });

        menuScore.addActionListener(e -> {
            if (scorePanel != null) {
                setContent(scorePanel);
                highlightMenuButton(menuScore);
            }
        });

        menuMajor.addActionListener(e -> {
            if (majorPanel != null) {
                setContent(majorPanel);
                highlightMenuButton(menuMajor);
            }
        });

        menuSubjectGroup.addActionListener(e -> {
            if (subjectGroupPanel != null) {
                setContent(subjectGroupPanel);
                highlightMenuButton(menuSubjectGroup);
            }
        });



        menuBonusScore.addActionListener(e -> {
            if (bonusScorePanel != null) {
                setContent(bonusScorePanel);
                highlightMenuButton(menuBonusScore);
            }
        });

        menuConversionTable.addActionListener(e -> {
            if (conversionTablePanel != null) {
                setContent(conversionTablePanel);
                highlightMenuButton(menuConversionTable);
            }
        });

        menuUserManagement.addActionListener(e -> {
            if (userManagementPanel != null) {
                setContent(userManagementPanel);
                highlightMenuButton(menuUserManagement);
            }
        });


        if (user.getRole().toString().equalsIgnoreCase("admin")) {
            menuAdmission.addActionListener(e -> {
                if (admissionPanel != null) {
                    setContent(admissionPanel);
                    highlightMenuButton(menuAdmission);
                }
            });

            menuStatistic.addActionListener(e -> {
                if (statisticPanel != null) {
                    setContent(statisticPanel);
                    highlightMenuButton(menuStatistic);
                }
            });
        }

    }

    /**
     * Show placeholder panel for unimplemented features
     */
    private void showPlaceholderPanel(String featureName) {
        JPanel placeholder = new JPanel(new BorderLayout());
        placeholder.setBackground(Color.WHITE);

        JLabel message = new JLabel(
                "<html><div style='text-align: center;'>" +
                        "<h2>" + featureName + "</h2>" +
                        "<p>Tính năng này đang được phát triển.</p>" +
                        "<p>Vui lòng quay lại sau.</p>" +
                        "</div></html>",
                SwingConstants.CENTER);
        message.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        message.setForeground(new Color(127, 140, 141));

        placeholder.add(message, BorderLayout.CENTER);
        setContent(placeholder);
    }

    /**
     * Setup frame properties
     */
    public void setupFrame() {
        setTitle("Admission Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(UIConstants.FRAME_WIDTH, UIConstants.FRAME_HEIGHT);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(1000, 700));

        // Initialize with default panel
        if (candidatePanel != null) {
            setContent(candidatePanel);
            highlightMenuButton(menuCandidate);
        }
    }

    /**
     * Set the content panel on the right side
     *
     * @param panel The panel to display
     */
    public void setContent(JPanel panel) {
        // Remove all existing content
        contentPanel.removeAll();

        // Add new panel with BorderLayout.CENTER constraint
        contentPanel.add(panel, BorderLayout.CENTER);

        // Revalidate and repaint to update the layout
        contentPanel.revalidate();
        contentPanel.repaint();

        // Also revalidate/repaint root panel to ensure full refresh
        rootPanel.revalidate();
        rootPanel.repaint();
    }

    /**
     * Highlight a specific menu item
     *
     * @param selectedButton The button to highlight
     */
    public void highlightMenuButton(JButton selectedButton) {
        // Reset all buttons
        JButton[] buttons = {menuCandidate, menuScore, menuMajor, menuSubjectGroup,
               menuBonusScore, menuConversionTable, menuUserManagement,
                menuAdmission, menuStatistic};

        for (JButton button : buttons) {
            button.setSelected(false);
            button.setBackground(new Color(44, 62, 80));
            button.setFont(button.getFont().deriveFont(Font.PLAIN));
        }

        // Highlight selected button
        selectedButton.setSelected(true);
        selectedButton.setBackground(new Color(41, 128, 185));
        selectedButton.setFont(selectedButton.getFont().deriveFont(Font.BOLD));
    }
}