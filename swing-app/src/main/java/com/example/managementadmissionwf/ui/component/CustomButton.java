package com.example.managementadmissionwf.ui.component;

import com.example.managementadmissionwf.ui.util.UIConstants;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * CustomButton - A modern, rounded button with flat design
 * Reusable component for the application
 */
public class CustomButton extends JButton {
    
    private boolean isHovered = false;
    private boolean isPressed = false;
    
    public CustomButton() {
        this("");
    }
    
    public CustomButton(String text) {
        super(text);
        customizeButton();
    }
    
    /**
     * Configure the button with modern styling
     */
    private void customizeButton() {
        setFont(UIConstants.FONT_BUTTON);
        setForeground(Color.WHITE);
        setBackground(UIConstants.PRIMARY_BLUE);
        setBorderPainted(false);
        setFocusPainted(false);
        setContentAreaFilled(false);
        setOpaque(false);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        // NO setPreferredSize - let layout manager determine size dynamically
        setMinimumSize(new Dimension(200, UIConstants.BUTTON_HEIGHT));
        
        // Add mouse listeners for hover effects
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                isHovered = true;
                repaint();
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                isHovered = false;
                isPressed = false;
                repaint();
            }
            
            @Override
            public void mousePressed(MouseEvent e) {
                isPressed = true;
                repaint();
            }
            
            @Override
            public void mouseReleased(MouseEvent e) {
                isPressed = false;
                repaint();
            }
        });
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Determine background color based on state
        Color backgroundColor = UIConstants.PRIMARY_BLUE;
        if (isPressed) {
            backgroundColor = UIConstants.PRIMARY_BLUE_DARK;
        } else if (isHovered) {
            backgroundColor = UIConstants.PRIMARY_BLUE_LIGHT;
        }
        
        // Draw rounded rectangle
        int width = getWidth();
        int height = getHeight();
        int radius = UIConstants.BUTTON_BORDER_RADIUS;
        
        g2d.setColor(backgroundColor);
        g2d.fillRoundRect(0, 0, width - 1, height - 1, radius, radius);
        
        g2d.dispose();
        
        // Paint text and other components
        super.paintComponent(g);
    }
}