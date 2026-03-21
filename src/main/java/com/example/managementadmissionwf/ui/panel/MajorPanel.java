package com.example.managementadmissionwf.ui.panel;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MajorPanel extends JPanel {

    private final Color PRIMARY = new Color(33, 150, 243);
    private final Color PRIMARY_DARK = new Color(25, 118, 210);
    private final Color SUCCESS = new Color(46, 204, 113);
    private final Color DANGER = new Color(231, 76, 60);
    private final Color BG = new Color(248, 250, 252);
    private final Color TEXT = new Color(33, 33, 33);
    private final Color BORDER = new Color(220, 220, 220);

    private final Font FONT = new Font("Segoe UI", Font.PLAIN, 14);
    private final Font FONT_BOLD = new Font("Segoe UI", Font.BOLD, 14);

    private JTable majorTable;
    private JTable subjectGroupTable;

    public MajorPanel() {
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBackground(BG);
        setBorder(new EmptyBorder(15, 15, 15, 15));

        add(createTopPanel(), BorderLayout.NORTH);
        add(createCenterPanel(), BorderLayout.CENTER);
    }

    private ImageIcon createIcon(String path, int width, int height) {
        java.net.URL imgURL = getClass().getResource(path);
        
        if (imgURL != null) {
            ImageIcon icon = new ImageIcon(imgURL);
            Image img = icon.getImage();
            Image newImg = img.getScaledInstance(width, height, java.awt.Image.SCALE_SMOOTH);
            return new ImageIcon(newImg);
        } else {
            System.err.println("Không tìm thấy file ảnh: " + path);
            return null;
        }
    }



    private JPanel createTopPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BG);

        JPanel left = new JPanel();
        left.setLayout(new BoxLayout(left, BoxLayout.X_AXIS));
        left.setBackground(BG);

        JPanel searchWrapper = new JPanel(new BorderLayout());
        searchWrapper.setBackground(Color.WHITE);
        searchWrapper.setBorder(new CompoundBorder(
                new LineBorder(BORDER, 1, true),
                new EmptyBorder(6, 10, 6, 10)
        ));
        searchWrapper.setPreferredSize(new Dimension(250, 32)); 
        searchWrapper.setMaximumSize(new Dimension(250, 32));

        JLabel icon = new JLabel(createIcon("/icons/search.png", 16, 16));
        icon.setBorder(new EmptyBorder(0, 0, 0, 6));
        JTextField txtSearch = new JTextField() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (getText().isEmpty()) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    
                    g2.setColor(new Color(170, 170, 170)); 
                    g2.setFont(getFont());
                    FontMetrics fm = g2.getFontMetrics();
                    int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
                    
                    g2.drawString("Nhập nội dung tìm kiếm...", 2, y);
                    g2.dispose();
                }
            }
        };
        txtSearch.setBorder(null);
        txtSearch.setFont(FONT);

        searchWrapper.add(icon, BorderLayout.WEST);
        searchWrapper.add(txtSearch, BorderLayout.CENTER);

        JPanel methodWrapper = new JPanel();
        methodWrapper.setLayout(new BoxLayout(methodWrapper, BoxLayout.Y_AXIS));
        methodWrapper.setBackground(BG);
        methodWrapper.setBorder(new EmptyBorder(0, 15, 0, 0));

        JComboBox<String> cbMethod = new JComboBox<>(new String[]{
                "Tất cả", "THPT", "ĐGNL", "Tuyển thẳng" , "VSAT"
        });
        cbMethod.setFont(FONT);
        cbMethod.setBackground(Color.WHITE);

        cbMethod.setPreferredSize(new Dimension(140, 32));
        cbMethod.setMaximumSize(new Dimension(140, 32));
        cbMethod.setAlignmentX(Component.LEFT_ALIGNMENT);

        methodWrapper.add(Box.createVerticalGlue());
        methodWrapper.add(Box.createVerticalStrut(3));
        methodWrapper.add(cbMethod);
        methodWrapper.add(Box.createVerticalGlue());


        left.add(searchWrapper);
        left.add(methodWrapper);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        right.setBackground(BG);
        JButton btnAdd = new JButton("Thêm ngành", createIcon("/icons/add.png", 16, 16));
        JButton btnEdit = new JButton("Sửa", createIcon("/icons/edit.png", 16, 16));
        JButton btnDelete = new JButton("Xóa", createIcon("/icons/delete.png", 16, 16));
        JButton btnAddGroup = new JButton("Thêm tổ hợp", createIcon("/icons/add.png", 16, 16));

        right.add(styleButton(btnAdd, SUCCESS));
        right.add(styleButton(btnEdit, PRIMARY));
        right.add(styleButton(btnDelete, DANGER));
        right.add(styleButton(btnAddGroup, PRIMARY_DARK));

        panel.add(left, BorderLayout.WEST);
        panel.add(right, BorderLayout.EAST);

        return panel;
    }

    private JComponent createCenterPanel() {

        JPanel container = new JPanel(new BorderLayout(10, 10));
        container.setBackground(BG);

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(BG);

        JLabel title = new JLabel("Danh sách ngành");
        title.setFont(FONT_BOLD);

        JButton btnStats = styleButton(new JButton("Thống kê"), PRIMARY);

        header.add(title, BorderLayout.WEST);
        header.add(btnStats, BorderLayout.EAST);

        String[] majorCols = {"Mã Ngành", "Tên Ngành", "Chỉ Tiêu", "Điểm Sàn", "Phương thức"};
        Object[][] majorData = {
                {"7480201", "Công nghệ thông tin", "500", "18.0", "THPT, ĐGNL"},
                {"7480101", "Khoa học máy tính", "400", "19.0", "THPT, ĐGNL"},
                {"7480102", "Mạng máy tính", "350", "18.5", "THPT"},
                {"7480103", "Kỹ thuật phần mềm", "450", "19.5", "THPT, ĐGNL"},
                {"7340120", "Kinh doanh quốc tế", "200", "20.0", "THPT"},
                {"7340115", "Marketing", "300", "21.0", "THPT, Tuyển thẳng"},
                {"7340116", "Thương mại điện tử", "250", "20.5", "THPT, ĐGNL"},
                {"7340101", "Quản trị kinh doanh", "500", "20.0", "THPT"},
                {"7340201", "Tài chính - Ngân hàng", "350", "21.5", "THPT"},
                {"7340301", "Kế toán", "300", "20.0", "THPT"},
                {"7340302", "Kiểm toán", "200", "21.0", "THPT"},
                {"7220201", "Ngôn ngữ Anh", "400", "22.0", "THPT"},
                {"7220202", "Ngôn ngữ Trung", "300", "21.0", "THPT"},
                {"7220203", "Ngôn ngữ Nhật", "250", "21.5", "THPT"},
                {"7220204", "Ngôn ngữ Hàn", "200", "22.0", "THPT"},
                {"7140209", "Sư phạm Toán học", "100", "22.0", "THPT"},
                {"7140210", "Sư phạm Vật lý", "120", "22.5", "THPT"},
                {"7140211", "Sư phạm Hóa học", "100", "22.0", "THPT"},
                {"7140212", "Sư phạm Sinh học", "90", "21.5", "THPT"},
                {"7510201", "Công nghệ kỹ thuật cơ khí", "300", "18.0", "THPT"},
                {"7510301", "Công nghệ kỹ thuật điện", "280", "18.5", "THPT"},
                {"7510401", "Công nghệ kỹ thuật điện tử", "260", "19.0", "THPT"},
                {"7580201", "Kỹ thuật xây dựng", "200", "17.5", "THPT"}
        };

        majorTable = createTable(majorData, majorCols);
        JScrollPane majorScroll = new JScrollPane(majorTable);
        majorScroll.setBorder(new EmptyBorder(5, 0, 5, 0));

        JPanel majorPanel = new JPanel(new BorderLayout());
        majorPanel.setBackground(BG);
        majorPanel.add(header, BorderLayout.NORTH);
        majorPanel.add(majorScroll, BorderLayout.CENTER);

        String[] groupCols = {"Tổ hợp", "Môn 1", "Môn 2", "Môn 3"};
        Object[][] groupData = {
                {"A00", "Toán", "Vật lý", "Hóa học"},
                {"A01", "Toán", "Vật lý", "Tiếng Anh"},
                {"D01", "Toán", "Ngữ văn", "Tiếng Anh"},
                {"D07", "Toán", "Hóa học", "Tiếng Anh"},
                {"B00", "Toán", "Hóa học", "Sinh học"},
                {"C00", "Ngữ văn", "Lịch sử", "Địa lý"},
                {"D14", "Ngữ văn", "Lịch sử", "Tiếng Anh"}
        };

        subjectGroupTable = createTable(groupData, groupCols);
        JScrollPane groupScroll = new JScrollPane(subjectGroupTable);
        groupScroll.setBorder(createBorder("Tổ hợp xét tuyển"));

        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, majorPanel, groupScroll);
        split.setDividerLocation(280);
        split.setDividerSize(6);
        split.setBorder(null);

        return split;
    }

    private JButton styleButton(JButton btn, Color bgColor) {
        btn.setFont(FONT_BOLD);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setContentAreaFilled(false);
        btn.setBorder(new EmptyBorder(8, 16, 8, 16));
        btn.setOpaque(false);

        btn.setUI(new javax.swing.plaf.basic.BasicButtonUI() {
            public void paint(Graphics g, JComponent c) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int w = c.getWidth();
                int h = c.getHeight();

                g2.setColor(new Color(0, 0, 0, 30));
                g2.fillRoundRect(3, 3, w - 3, h - 3, 12, 12);

                g2.setColor(bgColor);
                g2.fillRoundRect(0, 0, w - 3, h - 3, 12, 12);

                g2.dispose();
                super.paint(g, c);
            }
        });

        return btn;
    }

    private JTable createTable(Object[][] data, String[] cols) {
        DefaultTableModel model = new DefaultTableModel(data, cols) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        JTable table = new JTable(model);
        table.setFont(FONT);
        table.setRowHeight(36);
        table.setForeground(Color.BLACK);
        table.setBackground(Color.WHITE);
        table.setGridColor(BORDER);

        table.getTableHeader().setReorderingAllowed(false);

        table.setSelectionBackground(new Color(225, 245, 254));
        table.setSelectionForeground(Color.BLACK);

        JTableHeader header = table.getTableHeader();
        header.setFont(FONT_BOLD);
        header.setBackground(new Color(232, 240, 254));
        header.setPreferredSize(new Dimension(100, 40));

        return table;
    }

    private TitledBorder createBorder(String title) {
        TitledBorder border = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(BORDER), title);
        border.setTitleFont(FONT_BOLD);
        border.setTitleColor(PRIMARY_DARK);
        return border;
    }
}