package com.example.managementadmissionwf.ui.panel.major;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;

public class MajorListPanel extends JPanel {

    private final Color PRIMARY_COLOR = new Color(33, 150, 243);   // Blue
    private final Color PRIMARY_DARK = new Color(25, 118, 210);   // Dark Blue
    private final Color BG_COLOR = new Color(248, 250, 252);      // Background
    private final Color TEXT_PRIMARY = new Color(33, 33, 33);     // Black
    private final Color TEXT_SECONDARY = new Color(100, 100, 100);// Gray
    private final Color BORDER_COLOR = new Color(220, 220, 220);  // Border

    private final Font MAIN_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    private final Font BOLD_FONT = new Font("Segoe UI", Font.BOLD, 14);

    private JTable masterTable;
    private JTable detailTable;
    private DefaultTableModel masterModel;
    private DefaultTableModel detailModel;

    public MajorListPanel() {
        initComponents();
        loadMockMasterData();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(BG_COLOR);

        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 230)),
                new EmptyBorder(10, 10, 10, 10)
        ));

        String[] masterCols = {"Mã ngành", "Tên ngành", "Chỉ tiêu", "Điểm sàn", "Phương thức"};
        masterModel = new DefaultTableModel(masterCols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        masterTable = createModernTable(masterModel);
        JScrollPane masterScroll = new JScrollPane(masterTable);
        masterScroll.setBorder(createModernTitledBorder("Danh sách Ngành"));

        masterTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && masterTable.getSelectedRow() != -1) {
                int row = masterTable.getSelectedRow();
                String maNganh = masterTable.getValueAt(row, 0).toString();
                updateDetailTable(maNganh);
            }
        });

        String[] detailCols = {"Tổ hợp", "Môn 1 (HS)", "Môn 2 (HS)", "Môn 3 (HS)"};
        detailModel = new DefaultTableModel(detailCols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        detailTable = createModernTable(detailModel);
        JScrollPane detailScroll = new JScrollPane(detailTable);
        detailScroll.setBorder(createModernTitledBorder("Tổ hợp môn xét tuyển"));

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, masterScroll, detailScroll);
        splitPane.setDividerLocation(260);
        splitPane.setDividerSize(8);
        splitPane.setBorder(null);
        splitPane.setContinuousLayout(true);
        splitPane.setBackground(BG_COLOR);

        add(splitPane, BorderLayout.CENTER);
    }

    private JTable createModernTable(DefaultTableModel model) {
        JTable table = new JTable(model);
        table.setFont(MAIN_FONT);
        table.setRowHeight(36);
        table.setShowVerticalLines(false);
        table.setGridColor(BORDER_COLOR);

        table.setForeground(TEXT_PRIMARY);
        table.setBackground(Color.WHITE);

        table.setSelectionBackground(new Color(225, 245, 254));
        table.setSelectionForeground(TEXT_PRIMARY);

        table.setFillsViewportHeight(true);

        JTableHeader header = table.getTableHeader();
        header.setFont(BOLD_FONT);
        header.setBackground(new Color(232, 240, 254));
        header.setForeground(TEXT_PRIMARY);
        header.setPreferredSize(new Dimension(100, 42));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_COLOR));

        header.setReorderingAllowed(false);

        return table;
    }

    private TitledBorder createModernTitledBorder(String title) {
        TitledBorder border = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(BORDER_COLOR), title);

        border.setTitleFont(BOLD_FONT);
        border.setTitleColor(PRIMARY_DARK);

        return border;
    }

    //MOCK DATA 
    private void loadMockMasterData() {
        masterModel.addRow(new Object[]{"7480201", "Công nghệ thông tin", 500, 18.0, "THPT, ĐGNL, Tuyển thẳng"});
        masterModel.addRow(new Object[]{"7340120", "Kinh doanh quốc tế", 200, 20.0, "THPT, Tuyển thẳng"});
        masterModel.addRow(new Object[]{"7140209", "Sư phạm Toán học", 100, 22.0, "THPT"});
        masterModel.addRow(new Object[]{"7480101", "Khoa học máy tính", 400, 19.0, "THPT, ĐGNL"});
        masterModel.addRow(new Object[]{"7480102", "Mạng máy tính", 350, 18.5, "THPT"});
        masterModel.addRow(new Object[]{"7480103", "Kỹ thuật phần mềm", 450, 19.5, "THPT, ĐGNL"});
        masterModel.addRow(new Object[]{"7340115", "Marketing", 300, 21.0, "THPT, Tuyển thẳng"});
        masterModel.addRow(new Object[]{"7340116", "Thương mại điện tử", 250, 20.5, "THPT, ĐGNL"});
        masterModel.addRow(new Object[]{"7340101", "Quản trị kinh doanh", 500, 20.0, "THPT"});
        masterModel.addRow(new Object[]{"7340201", "Tài chính - Ngân hàng", 350, 21.5, "THPT"});
        masterModel.addRow(new Object[]{"7340301", "Kế toán", 300, 20.0, "THPT"});
        masterModel.addRow(new Object[]{"7340302", "Kiểm toán", 200, 21.0, "THPT"});
        masterModel.addRow(new Object[]{"7220201", "Ngôn ngữ Anh", 400, 22.0, "THPT"});
        masterModel.addRow(new Object[]{"7220202", "Ngôn ngữ Trung", 300, 21.0, "THPT"});
        masterModel.addRow(new Object[]{"7220203", "Ngôn ngữ Nhật", 250, 21.5, "THPT"});
        masterModel.addRow(new Object[]{"7220204", "Ngôn ngữ Hàn", 200, 22.0, "THPT"});
        masterModel.addRow(new Object[]{"7140210", "Sư phạm Vật lý", 120, 22.5, "THPT"});
        masterModel.addRow(new Object[]{"7140211", "Sư phạm Hóa học", 100, 22.0, "THPT"});
        masterModel.addRow(new Object[]{"7140212", "Sư phạm Sinh học", 90, 21.5, "THPT"});
        masterModel.addRow(new Object[]{"7510201", "Công nghệ kỹ thuật cơ khí", 300, 18.0, "THPT"});
        masterModel.addRow(new Object[]{"7510301", "Công nghệ kỹ thuật điện", 280, 18.5, "THPT"});
        masterModel.addRow(new Object[]{"7510401", "Công nghệ kỹ thuật điện tử", 260, 19.0, "THPT"});
        masterModel.addRow(new Object[]{"7580201", "Kỹ thuật xây dựng", 200, 17.5, "THPT"});
    }

    private void updateDetailTable(String maNganh) {
        detailModel.setRowCount(0);

        switch (maNganh) {
            case "7480201":
            case "7480101":
            case "7480102":
            case "7480103":
                detailModel.addRow(new Object[]{"A00", "Toán (2.0)", "Vật lý (1.0)", "Hóa học (1.0)"});
                detailModel.addRow(new Object[]{"A01", "Toán (2.0)", "Vật lý (1.0)", "Tiếng Anh (1.0)"});
                detailModel.addRow(new Object[]{"D01", "Toán (1.0)", "Ngữ văn (1.0)", "Tiếng Anh (2.0)"});
                break;

            case "7340120":
            case "7340115":
            case "7340116":
            case "7340101":
            case "7340201":
            case "7340301":
            case "7340302":
                detailModel.addRow(new Object[]{"A01", "Toán (1.0)", "Vật lý (1.0)", "Tiếng Anh (1.0)"});
                detailModel.addRow(new Object[]{"D01", "Toán (1.0)", "Ngữ văn (1.0)", "Tiếng Anh (2.0)"});
                detailModel.addRow(new Object[]{"D07", "Toán (1.0)", "Hóa học (1.0)", "Tiếng Anh (2.0)"});
                break;

            case "7140209":
            case "7140210":
            case "7140211":
            case "7140212":
                detailModel.addRow(new Object[]{"A00", "Toán (2.0)", "Vật lý (1.0)", "Hóa học (1.0)"});
                detailModel.addRow(new Object[]{"B00", "Toán (1.0)", "Hóa học (1.0)", "Sinh học (2.0)"});
                break;

            case "7220201":
            case "7220202":
            case "7220203":
            case "7220204":
                detailModel.addRow(new Object[]{"D01", "Toán (1.0)", "Ngữ văn (1.0)", "Tiếng Anh (2.0)"});
                detailModel.addRow(new Object[]{"D14", "Ngữ văn (2.0)", "Lịch sử (1.0)", "Tiếng Anh (1.0)"});
                break;

            case "7510201":
            case "7510301":
            case "7510401":
            case "7580201":
                detailModel.addRow(new Object[]{"A00", "Toán (2.0)", "Vật lý (1.0)", "Hóa học (1.0)"});
                detailModel.addRow(new Object[]{"A01", "Toán (2.0)", "Vật lý (1.0)", "Tiếng Anh (1.0)"});
                break;

            default:
                // fallback nếu chưa có data
                detailModel.addRow(new Object[]{"N/A", "-", "-", "-"});
                break;
        }
    }
}