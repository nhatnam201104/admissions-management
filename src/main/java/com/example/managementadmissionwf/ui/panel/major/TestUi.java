package com.example.managementadmissionwf.ui.panel.major;

import javax.swing.*;

import com.example.managementadmissionwf.ui.panel.ConversionTablePanel;


public class TestUi {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }

            ConversionTablePanel panel = new ConversionTablePanel();
            // JFrame testFrame = new JFrame("Test Nhanh Giao Diện Ngành Học");
            JFrame testFrame = new JFrame("Kiểm tra Giao diện Bảng quy đổi");
            testFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            testFrame.setSize(1000, 650);
            testFrame.setLocationRelativeTo(null);
            testFrame.setVisible(true);

            testFrame.add(panel);

            // Test giao diện dialog
            // MajorFormDialog dialog = new MajorFormDialog(testFrame, null);
            // dialog.setVisible(true);
        });
    }
}
            
