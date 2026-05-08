package com.example.managementadmissionwf.ui.component;

import com.example.managementadmissionwf.config.ApplicationContextHolder;
import com.example.managementadmissionwf.ui.frame.MainFrame;
import com.example.managementadmissionwf.ui.panel.candidate.CandidatePanel;
import com.example.managementadmissionwf.ui.panel.major.MajorPanel;
import com.example.managementadmissionwf.ui.panel.score.ScorePanel;
import com.example.managementadmissionwf.ui.panel.subjectgroup.SubjectGroupPanel;
import com.example.managementadmissionwf.ui.panel.admission.AdmissionPanel;
import com.example.managementadmissionwf.ui.panel.StatisticPanel;
import com.example.managementadmissionwf.ui.panel.user.UserManagementPanel;
import com.example.managementadmissionwf.ui.panel.BonusScorePanel;
import com.example.managementadmissionwf.ui.panel.conversion.ConversionTablePanel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * NavigationController - Handles menu click events and manages panel navigation
 * NO UI layout code - pure controller logic
 */
public class Navigation {

    private MainFrame mainFrame;

    // Content panels (lazy loaded from Spring context)
    private CandidatePanel candidatePanel;
    private ScorePanel scorePanel;
    private MajorPanel majorPanel;
    private SubjectGroupPanel subjectGroupPanel;
    private BonusScorePanel bonusScorePanel;
    private ConversionTablePanel conversionTablePanel;
    private UserManagementPanel userManagementPanel;
    private AdmissionPanel admissionPanel;
    private StatisticPanel statisticPanel;

    public Navigation() {
        // Lazy load panels from Spring context
    }

    public void init(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        loadPanelsFromContext();
        setupMenuListeners();

        // Show default panel
        showCandidatePanel();
    }

    /**
     * Load all panels from Spring application context
     */
    private void loadPanelsFromContext() {
        this.candidatePanel = ApplicationContextHolder.getBean(CandidatePanel.class);
        this.scorePanel = ApplicationContextHolder.getBean(ScorePanel.class);
        this.majorPanel = ApplicationContextHolder.getBean(MajorPanel.class);
        this.subjectGroupPanel = ApplicationContextHolder.getBean(SubjectGroupPanel.class);
        this.bonusScorePanel = ApplicationContextHolder.getBean(BonusScorePanel.class);
        this.conversionTablePanel = ApplicationContextHolder.getBean(ConversionTablePanel.class);
        this.userManagementPanel = ApplicationContextHolder.getBean(UserManagementPanel.class);
        this.admissionPanel = ApplicationContextHolder.getBean(AdmissionPanel.class);
        this.statisticPanel = ApplicationContextHolder.getBean(StatisticPanel.class);
    }

    /**
     * Setup menu button click listeners
     */
    private void setupMenuListeners() {
        mainFrame.getMenuCandidate().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showCandidatePanel();
            }
        });

        mainFrame.getMenuScore().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showScorePanel();
            }
        });

        mainFrame.getMenuMajor().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showMajorPanel();
            }
        });

        mainFrame.getMenuSubjectGroup().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showSubjectGroupPanel();
            }
        });

        mainFrame.getMenuBonusScore().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showBonusScorePanel();
            }
        });

        mainFrame.getMenuConversionTable().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showConversionTablePanel();
            }
        });

        mainFrame.getMenuUserManagement().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showUserManagementPanel();
            }
        });

        mainFrame.getMenuAdmission().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showAdmissionPanel();
            }
        });

        mainFrame.getMenuStatistic().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showStatisticPanel();
            }
        });
    }

    /**
     * Show Candidate Management panel
     */
    private void showCandidatePanel() {
        if (candidatePanel != null) {
            mainFrame.setContent(candidatePanel);
        }
        mainFrame.highlightMenuButton(mainFrame.getMenuCandidate());
    }

    /**
     * Show Candidate Score Management panel
     */
    private void showScorePanel() {
        if (scorePanel != null) {
            mainFrame.setContent(scorePanel);
        }
        mainFrame.highlightMenuButton(mainFrame.getMenuScore());
    }

    /**
     * Show Major List Management panel
     */
    private void showMajorPanel() {
        if (majorPanel != null) {
            mainFrame.setContent(majorPanel);
        }
        mainFrame.highlightMenuButton(mainFrame.getMenuMajor());
    }

    /**
     * Show Subject Combination List panel
     */
    private void showSubjectGroupPanel() {
        if (subjectGroupPanel != null) {
            mainFrame.setContent(subjectGroupPanel);
        }
        mainFrame.highlightMenuButton(mainFrame.getMenuSubjectGroup());
    }

    /**
     * Show Admission Threshold Score Management panel
     */

    /**
     * Show Bonus Score Management panel
     */
    private void showBonusScorePanel() {
        if (bonusScorePanel != null) {
            mainFrame.setContent(bonusScorePanel);
        }
        mainFrame.highlightMenuButton(mainFrame.getMenuBonusScore());
    }

    /**
     * Show Conversion Table Management panel
     */
    private void showConversionTablePanel() {
        if (conversionTablePanel != null) {
            mainFrame.setContent(conversionTablePanel);
        }
        mainFrame.highlightMenuButton(mainFrame.getMenuConversionTable());
    }

    /**
     * Show User Management panel
     */
    private void showUserManagementPanel() {
        if (userManagementPanel != null) {
            mainFrame.setContent(userManagementPanel);
        }
        mainFrame.highlightMenuButton(mainFrame.getMenuUserManagement());
    }

    /**
     * Show Admission Result List panel
     */
    private void showAdmissionPanel() {
        if (admissionPanel != null) {
            mainFrame.setContent(admissionPanel);
        }
        mainFrame.highlightMenuButton(mainFrame.getMenuAdmission());
    }

    /**
     * Show Statistics & Reports panel
     */
    private void showStatisticPanel() {
        if (statisticPanel != null) {
            mainFrame.setContent(statisticPanel);
        }
        mainFrame.highlightMenuButton(mainFrame.getMenuStatistic());
    }
}
