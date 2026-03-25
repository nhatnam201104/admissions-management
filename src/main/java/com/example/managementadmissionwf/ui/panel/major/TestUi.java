package com.example.managementadmissionwf.ui.panel.major;

import javax.swing.*;


public class TestUi {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }

            JFrame testFrame = new JFrame("Test Nhanh Giao Diện Ngành Học");
            testFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            testFrame.setSize(1000, 650);
            testFrame.setLocationRelativeTo(null);
            testFrame.setVisible(true);

            testFrame.add(new MajorPanel());

            // Test giao diện dialog
            // MajorFormDialog dialog = new MajorFormDialog(testFrame, null);
            // dialog.setVisible(true);
        });
    }
}