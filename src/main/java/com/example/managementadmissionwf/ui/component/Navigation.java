package com.example.managementadmissionwf.ui.component;

import com.example.managementadmissionwf.ui.frame.MainFrame;
import com.example.managementadmissionwf.ui.panel.*;
import com.example.managementadmissionwf.ui.panel.wish.WishPanel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * NavigationController - Handles menu click events and manages panel navigation
 * NO UI layout code - pure controller logic
 */
public class Navigation {

    private final MainFrame mainFrame;

    // Content panels
    private CandidatePanel candidatePanel;
    private ScorePanel scorePanel;
    private WishPanel wishPanel;
    private MajorPanel majorPanel;
    private ThresholdPanel thresholdPanel;
    private SubjectGroupPanel subjectGroupPanel;
    private AdmissionPanel admissionPanel;
    private StatisticPanel statisticPanel;

    public Navigation(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        initializePanels();
        setupMenuListeners();

        // Show default panel
        showCandidatePanel();
    }

    /**
     * Initialize all content panels (lazy loading pattern)
     */
    private void initializePanels() {
        // Panels are created on first use
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

        mainFrame.getMenuWish().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showWishPanel();
            }
        });

        mainFrame.getMenuMajor().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showMajorPanel();
            }
        });

        mainFrame.getMenuThreshold().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showThresholdPanel();
            }
        });

        mainFrame.getMenuSubjectGroup().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showSubjectGroupPanel();
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
        if (candidatePanel == null) {
            candidatePanel = new CandidatePanel();
        }
        mainFrame.setContent(candidatePanel);
        mainFrame.highlightMenuButton(mainFrame.getMenuCandidate());
    }

    /**
     * Show Candidate Score Management panel
     */
    private void showScorePanel() {
        if (scorePanel == null) {
            scorePanel = new ScorePanel();
        }
        mainFrame.setContent(scorePanel);
        mainFrame.highlightMenuButton(mainFrame.getMenuScore());
    }

    /**
     * Show Wish / Preference Management panel
     */
    private void showWishPanel() {
        if (wishPanel == null) {
            wishPanel = new WishPanel();
        }
        mainFrame.setContent(wishPanel);
        mainFrame.highlightMenuButton(mainFrame.getMenuWish());
    }

    /**
     * Show Major List Management panel
     */
    private void showMajorPanel() {
        if (majorPanel == null) {
            majorPanel = new MajorPanel();
        }
        mainFrame.setContent(majorPanel);
        mainFrame.highlightMenuButton(mainFrame.getMenuMajor());
    }

    /**
     * Show Admission Threshold Score Management panel
     */
    private void showThresholdPanel() {
        if (thresholdPanel == null) {
            thresholdPanel = new ThresholdPanel();
        }
        mainFrame.setContent(thresholdPanel);
        mainFrame.highlightMenuButton(mainFrame.getMenuThreshold());
    }

    /**
     * Show Subject Combination List panel
     */
    private void showSubjectGroupPanel() {
        if (subjectGroupPanel == null) {
            subjectGroupPanel = new SubjectGroupPanel();
        }
        mainFrame.setContent(subjectGroupPanel);
        mainFrame.highlightMenuButton(mainFrame.getMenuSubjectGroup());
    }

    /**
     * Show Admission Result List panel
     */
    private void showAdmissionPanel() {
        if (admissionPanel == null) {
            admissionPanel = new AdmissionPanel();
        }
        mainFrame.setContent(admissionPanel);
        mainFrame.highlightMenuButton(mainFrame.getMenuAdmission());
    }

    /**
     * Show Statistics & Reports panel
     */
    private void showStatisticPanel() {
        if (statisticPanel == null) {
            statisticPanel = new StatisticPanel();
        }
        mainFrame.setContent(statisticPanel);
        mainFrame.highlightMenuButton(mainFrame.getMenuStatistic());
    }
}
