package com.example.managementadmissionwf.ui.component;

import com.example.managementadmissionwf.ui.frame.MainFrame;
import com.example.managementadmissionwf.ui.panel.*;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * NavigationController - Handles menu click events and manages panel navigation
 * NO UI layout code - pure controller logic
 */
@NoArgsConstructor(force = true)
@RequiredArgsConstructor
public class Navigation {

    private MainFrame mainFrame;

    // Content panels
    private final CandidatePanel candidatePanel;
    private final ScorePanel scorePanel;
    private final WishPanel wishPanel;
    private final MajorPanel majorPanel;
    private final ThresholdPanel thresholdPanel;
    private final SubjectGroupPanel subjectGroupPanel;
    private final AdmissionPanel admissionPanel;
    private final StatisticPanel statisticPanel;

    public void init(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setupMenuListeners();

        // Show default panel
        showCandidatePanel();
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
     * Show Wish / Preference Management panel
     */
    private void showWishPanel() {
        if (wishPanel != null) {
            mainFrame.setContent(wishPanel);
        }
        mainFrame.highlightMenuButton(mainFrame.getMenuWish());
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
     * Show Admission Threshold Score Management panel
     */
    private void showThresholdPanel() {
        if (thresholdPanel != null) {
            mainFrame.setContent(thresholdPanel);
        }
        mainFrame.highlightMenuButton(mainFrame.getMenuThreshold());
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
