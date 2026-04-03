package com.example.managementadmissionwf.ui.panel;

import com.example.managementadmissionwf.ui.util.UIFactory;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ConversionTablePanel extends JPanel {

    private final Color PRIMARY = new Color(33, 150, 243);
    private final Color SUCCESS = new Color(46, 204, 113);
    private final Color DANGER = new Color(231, 76, 60);
    private final Color BG = new Color(248, 250, 252);
    private final Color BORDER = new Color(220, 220, 220);
    private final Font FONT = new Font("Segoe UI", Font.PLAIN, 14);
    private final Font FONT_BOLD = new Font("Segoe UI", Font.BOLD, 14);

    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtSearch;
    private JComboBox<String> cbPhuongThuc, cbToHop;
    private JComboBox<Integer> cbPageSize;
    private JLabel lblPageInfo;
    private JButton btnPrev, btnNext;

    private List<Object[]> allData;
    private List<Object[]> filteredData;
    private int currentPage = 1;
    private int pageSize = 20;

    public ConversionTablePanel() {
        generateMockData();
        initComponents();
        applyFilters();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBackground(BG);
        setBorder(new EmptyBorder(15, 15, 15, 15));

        add(createTopPanel(), BorderLayout.NORTH);
        add(createCenterPanel(), BorderLayout.CENTER);
        add(createBottomPanel(), BorderLayout.SOUTH);
    }

    private JPanel createTopPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BG);

        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.X_AXIS));
        leftPanel.setBackground(BG);

        JPanel searchWrapper = new JPanel(new BorderLayout());
        searchWrapper.setBackground(Color.WHITE);
        searchWrapper.setBorder(new CompoundBorder(
                new LineBorder(BORDER, 1, true),
                new EmptyBorder(6, 10, 6, 10)
        ));
        searchWrapper.setPreferredSize(new Dimension(250, 32));
        searchWrapper.setMaximumSize(new Dimension(250, 32));

        txtSearch = new JTextField() {
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
                    g2.drawString("Tìm kiếm môn học...", 2, y);
                    g2.dispose();
                }
            }
        };
        txtSearch.setBorder(null);
        txtSearch.setFont(FONT);
        searchWrapper.add(txtSearch, BorderLayout.CENTER);

        JPanel comboPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        comboPanel.setBackground(BG);
        
        cbPhuongThuc = createStyledCombo(new String[]{"Tất cả", "THPT", "DGNL", "VSAT"});
        cbToHop = createStyledCombo(new String[]{"Tất cả", "A00", "A01", "B00", "D01", "D07"});
        
        comboPanel.add(new JLabel("Phương thức:"));
        comboPanel.add(cbPhuongThuc);
        comboPanel.add(new JLabel("Tổ hợp:"));
        comboPanel.add(cbToHop);

        JButton btnFilter = styleButton(new JButton("Lọc", UIFactory.loadIcon("filter_list", 16, 16)), PRIMARY);
        btnFilter.addActionListener(e -> applyFilters());
        comboPanel.add(btnFilter);

        leftPanel.add(searchWrapper);
        leftPanel.add(comboPanel);

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightPanel.setBackground(BG);
        
        panel.add(leftPanel, BorderLayout.WEST);

        return panel;
    }

    private JPanel createCenterPanel() {
        JPanel container = new JPanel(new BorderLayout(0, 10));
        container.setBackground(BG);

        JLabel title = new JLabel("Danh sách Bảng quy đổi điểm");
        title.setFont(FONT_BOLD);
        container.add(title, BorderLayout.NORTH);

        String[] columnNames = {
                "STT", "Phương thức", "Tổ hợp", "Môn", 
                "Điểm A (Min)", "Điểm B (Max)", "Điểm C (Quy đổi)", "Điểm D (Tối đa)"
        };
        
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        table = new JTable(tableModel);
        table.setFont(FONT);
        table.setRowHeight(36);
        table.setGridColor(BORDER);
        table.setSelectionBackground(new Color(225, 245, 254));
        table.setSelectionForeground(Color.BLACK);

        JTableHeader header = table.getTableHeader();
        header.setFont(FONT_BOLD);
        header.setBackground(new Color(232, 240, 254));
        header.setPreferredSize(new Dimension(100, 40));
        header.setReorderingAllowed(false);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(new LineBorder(BORDER));
        container.add(scrollPane, BorderLayout.CENTER);

        return container;
    }

    private JPanel createBottomPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        panel.setBackground(BG);

        panel.add(new JLabel("Số dòng:"));
        cbPageSize = new JComboBox<>(new Integer[]{10, 20, 50});
        cbPageSize.setSelectedItem(20);
        cbPageSize.addActionListener(e -> {
            pageSize = (Integer) cbPageSize.getSelectedItem();
            currentPage = 1;
            updateTable();
        });
        panel.add(cbPageSize);

        btnPrev = new JButton("Trước", UIFactory.loadIcon("chevron_left", 16, 16));
        btnNext = new JButton("Sau", UIFactory.loadIcon("chevron_right", 16, 16));
        lblPageInfo = new JLabel("Trang 1 / 1");
        lblPageInfo.setFont(FONT_BOLD);

        btnPrev.addActionListener(e -> { if (currentPage > 1) { currentPage--; updateTable(); } });
        btnNext.addActionListener(e -> {
            int totalPages = (int) Math.ceil((double) filteredData.size() / pageSize);
            if (currentPage < totalPages) { currentPage++; updateTable(); }
        });

        panel.add(btnPrev);
        panel.add(lblPageInfo);
        panel.add(btnNext);

        return panel;
    }

    // === UTILITY METHODS ===

    private JComboBox<String> createStyledCombo(String[] items) {
        JComboBox<String> cb = new JComboBox<>(items);
        cb.setFont(FONT);
        cb.setBackground(Color.WHITE);
        cb.setPreferredSize(new Dimension(110, 32));
        return cb;
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
            @Override
            public void paint(Graphics g, JComponent c) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int w = c.getWidth(), h = c.getHeight();
                g2.setColor(new Color(0, 0, 0, 30));
                g2.fillRoundRect(2, 2, w - 2, h - 2, 10, 10);
                g2.setColor(bgColor);
                g2.fillRoundRect(0, 0, w - 2, h - 2, 10, 10);
                g2.dispose();
                super.paint(g, c);
            }
        });
        return btn;
    }

    private void generateMockData() {
        allData = new ArrayList<>();
        String[] pts = {"THPT", "DGNL", "VSAT"};
        String[] toHops = {"A00", "A01", "B00", "D01"};
        
        String[] tenMonHoc = {"TO", "LY", "N1", "NL1", "etc"};

        for (int i = 1; i <= 100; i++) {
            String mon = tenMonHoc[(i - 1) % tenMonHoc.length];
            
            String pt = pts[i % pts.length];
            String th = toHops[i % toHops.length];

            double diemA = 5.0 + (i % 3);
            double diemB = 10.0;
            double diemC = 8.0 + (i % 2);
            double diemD = 10.0;

            allData.add(new Object[]{
                i,
                pt,     
                th,   
                mon,
                diemA,
                diemB,
                diemC,
                diemD
            });
        }
        filteredData = new ArrayList<>(allData);
    }

    private void applyFilters() {
        String pt = (String) cbPhuongThuc.getSelectedItem();
        String th = (String) cbToHop.getSelectedItem();
        String search = txtSearch.getText().toLowerCase();

        filteredData = allData.stream().filter(row -> {
            boolean mPt = pt.equals("Tất cả") || row[1].equals(pt);
            boolean mTh = th.equals("Tất cả") || row[2].equals(th);
            boolean mSh = search.isEmpty() || row[3].toString().toLowerCase().contains(search);
            return mPt && mTh && mSh;
        }).collect(Collectors.toList());

        currentPage = 1;
        updateTable();
    }

    private void updateTable() {
        tableModel.setRowCount(0);
        int total = filteredData.size();
        int totalPages = Math.max(1, (int) Math.ceil((double) total / pageSize));
        int start = (currentPage - 1) * pageSize;
        int end = Math.min(start + pageSize, total);

        for (int i = start; i < end; i++) tableModel.addRow(filteredData.get(i));

        lblPageInfo.setText(String.format("Trang %d / %d", currentPage, totalPages));
        btnPrev.setEnabled(currentPage > 1);
        btnNext.setEnabled(currentPage < totalPages);
    }
}