package com.example.managementadmissionwf.ui.util;

import java.awt.Color;

public enum ToolbarAction {
    ADD("Thêm", "add", new Color(46, 204, 113), 100),
    EDIT("Sửa", "edit", new Color(52, 152, 219), 100),
    DELETE("Xóa", "delete", new Color(231, 76, 60), 100),
    REFRESH("Làm mới", "refresh", new Color(149, 165, 166), 120),
    EXPORT_EXCEL("Xuất Excel", "export", new Color(39, 174, 96), 130),
    IMPORT_EXCEL("Nhập Excel", "import", new Color(243, 156, 18), 130),
    EXPORT_PDF("Xuất PDF", "picture_as_pdf", new Color(231, 76, 60), 120),
    PRINT("In danh sách", "print", new Color(52, 152, 219), 135),
    UPDATE("Cập nhật KQ", "refresh", new Color(243, 156, 18), 145), 
    BONUS_SCORE("Điểm cộng", "stars", new Color(155, 89, 182), 120);

    private final String text;
    private final String iconName;
    private final Color bgColor;
    private final int width;

    ToolbarAction(String text, String iconName, Color bgColor, int width) {
        this.text = text;
        this.iconName = iconName;
        this.bgColor = bgColor;
        this.width = width;
    }

    public String getText() { return text; }
    public String getIconName() { return iconName; }
    public Color getBgColor() { return bgColor; }
    public int getWidth() { return width; }
}
