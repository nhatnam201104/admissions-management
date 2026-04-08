package com.example.managementadmissionwf.ui.util;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.net.URL;

public final class UIFactory {
    private UIFactory() {
    }

    public static JPanel createTopSection() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(240, 240, 240));
        return panel;
    }

    public static JPanel createFilterPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        panel.setBackground(new Color(240, 240, 240));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        return panel;
    }

    public static JPanel createActionPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        panel.setBackground(new Color(240, 240, 240));
        panel.setBorder(BorderFactory.createEmptyBorder(5, 10, 10, 10));
        return panel;
    }

    public static JPanel createPaginationPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 10));
        panel.setBackground(new Color(240, 240, 240));
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEmptyBorder(5, 10, 5, 10), "", 0, 0));
        return panel;
    }

    public static JButton createActionButton(String text, String iconName, Color bgColor, int width) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(width, 35));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        ImageIcon icon = loadIcon(iconName, 16, 16);
        if (icon != null) {
            button.setIcon(icon);
            button.setIconTextGap(8);
        }
        return button;
    }

    public static JButton createPaginationButton(String iconName, Color bgColor, String tooltip) {
        JButton button = new JButton();
        button.setPreferredSize(new Dimension(35, 30));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setToolTipText(tooltip);

        ImageIcon icon = loadIcon(iconName, 16, 16);
        if (icon != null) {
            button.setIcon(icon);
        }
        return button;
    }

    // ========== Filter helpers ==========

    public static JLabel createFilterLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        return label;
    }

    public static JTextField createFilterTextField(int columns, String tooltip) {
        JTextField field = new JTextField(columns);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setToolTipText(tooltip);
        return field;
    }

    public static JComboBox<String> createFilterCombo(String[] items, int width) {
        JComboBox<String> combo = new JComboBox<>(items);
        combo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        combo.setPreferredSize(new Dimension(width, 30));
        return combo;
    }

    // ========== Table helpers ==========

    public static JTable createStandardTable(DefaultTableModel model) {
        JTable table = new JTable(model);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setRowHeight(36);
        table.setForeground(Color.BLACK);
        table.setBackground(Color.WHITE);
        table.setGridColor(new Color(220, 220, 220));
        table.setSelectionBackground(new Color(225, 245, 254));
        table.setSelectionForeground(Color.BLACK);
        table.getTableHeader().setReorderingAllowed(false);

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBackground(new Color(232, 240, 254));
        header.setPreferredSize(new Dimension(100, 40));

        return table;
    }

    public static JScrollPane createStandardScrollPane(JTable table) {
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        return scrollPane;
    }

    // ========== Icon loading ==========

    public static ImageIcon loadIcon(String iconName, int width, int height) {
        if (iconName == null || iconName.isBlank()) {
            return null;
        }
        try {
            URL iconUrl = UIFactory.class.getResource("/icons/" + iconName + ".png");
            if (iconUrl == null) {
                return null;
            }
            ImageIcon icon = new ImageIcon(iconUrl);
            Image scaled = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
            return new ImageIcon(scaled);
        } catch (Exception ignored) {
            return null;
        }
    }
}
