package com.example.managementadmissionwf.ui.util;

import java.awt.*;

/**
 * UIConstants - Centralized UI constants for the application
 * Contains colors, fonts, and other UI-related constants
 */
public class UIConstants {

    // ========================================
    // COLORS
    // ========================================

    // Primary colors
    public static final Color PRIMARY_BLUE = new Color(66, 133, 244);
    public static final Color PRIMARY_BLUE_DARK = new Color(51, 103, 214);
    public static final Color PRIMARY_BLUE_LIGHT = new Color(102, 161, 255);

    // Gradient colors for left panel
    public static final Color GRADIENT_START = new Color(66, 133, 244);
    public static final Color GRADIENT_END = new Color(36, 73, 147);

    // Background colors
    public static final Color BACKGROUND_WHITE = new Color(255, 255, 255);
    public static final Color BACKGROUND_LIGHT_GRAY = new Color(245, 245, 245);
    public static final Color BACKGROUND_GRAY = new Color(240, 240, 240);

    // Text colors
    public static final Color TEXT_PRIMARY = new Color(33, 33, 33);
    public static final Color TEXT_SECONDARY = new Color(102, 102, 102);
    public static final Color TEXT_TERTIARY = new Color(153, 153, 153);
    public static final Color TEXT_WHITE = new Color(255, 255, 255);

    // Border colors
    public static final Color BORDER_LIGHT = new Color(200, 200, 200);
    public static final Color BORDER_FOCUS = new Color(66, 133, 244);

    // ========================================
    // FONTS
    // ========================================

    public static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 28);
    public static final Font FONT_SUBTITLE = new Font("Segoe UI", Font.BOLD, 20);
    public static final Font FONT_HEADING = new Font("Segoe UI", Font.BOLD, 24);
    public static final Font FONT_BODY = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font FONT_SMALL = new Font("Segoe UI", Font.PLAIN, 12);
    public static final Font FONT_BUTTON = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font FONT_LINK = new Font("Segoe UI", Font.PLAIN, 12);

    // ========================================
    // DIMENSIONS
    // ========================================

    public static final int FRAME_WIDTH = 1400;
    public static final int FRAME_HEIGHT = 800;
    public static final int LOGIN_FRAME_WIDTH = 1100;
    public static final int LOGIN_FRAME_HEIGHT = 700;

    public static final int BUTTON_HEIGHT = 45;
    public static final int TEXT_FIELD_HEIGHT = 45;
    public static final int TEXT_FIELD_BORDER_RADIUS = 8;
    public static final int BUTTON_BORDER_RADIUS = 8;
    public static final int CONTAINER_PADDING = 40;

    // ========================================
    // SPACING
    // ========================================

    public static final int SPACING_SMALL = 10;
    public static final int SPACING_MEDIUM = 15;
    public static final int SPACING_LARGE = 20;
    public static final int SPACING_EXTRA_LARGE = 30;
}