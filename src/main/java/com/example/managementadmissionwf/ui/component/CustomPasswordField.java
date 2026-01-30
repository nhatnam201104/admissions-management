package com.example.managementadmissionwf.ui.component;

import com.example.managementadmissionwf.ui.util.UIConstants;

import javax.swing.*;
import javax.swing.border.AbstractBorder;
import java.awt.*;

/**
 * CustomPasswordField - A modern password field with rounded border
 * Reusable component for the application
 */
public class CustomPasswordField extends JPasswordField {
    
    public CustomPasswordField() {
        customizePasswordField();
    }
    
    public CustomPasswordField(String text) {
        super(text);
        customizePasswordField();
    }
    
    public CustomPasswordField(int columns) {
        super(columns);
        customizePasswordField();
    }
    
    /**
     * Configure the password field with modern styling
     */
    private void customizePasswordField() {
        setFont(UIConstants.FONT_BODY);
        setForeground(UIConstants.TEXT_PRIMARY);
        setBackground(UIConstants.BACKGROUND_WHITE);
        setBorder(new RoundedBorder(UIConstants.TEXT_FIELD_BORDER_RADIUS, UIConstants.BORDER_LIGHT));
        setCaretColor(UIConstants.PRIMARY_BLUE);
        setEchoChar('•');
        // NO setPreferredSize - let layout manager determine size dynamically
        setMinimumSize(new Dimension(500, UIConstants.TEXT_FIELD_HEIGHT));
        
        // Add focus listener for border color change
        addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                setBorder(new RoundedBorder(UIConstants.TEXT_FIELD_BORDER_RADIUS, UIConstants.BORDER_FOCUS));
            }
            
            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                setBorder(new RoundedBorder(UIConstants.TEXT_FIELD_BORDER_RADIUS, UIConstants.BORDER_LIGHT));
            }
        });
    }
    
    /**
     * Custom rounded border implementation
     */
    private static class RoundedBorder extends AbstractBorder {
        private final int radius;
        private final Color color;
        
        public RoundedBorder(int radius, Color color) {
            this.radius = radius;
            this.color = color;
        }
        
        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setColor(color);
            g2d.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
            g2d.dispose();
        }
        
        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(10, 15, 10, 15);
        }
        
        @Override
        public Insets getBorderInsets(Component c, Insets insets) {
            insets.left = 15;
            insets.top = 10;
            insets.right = 15;
            insets.bottom = 10;
            return insets;
        }
    }
}