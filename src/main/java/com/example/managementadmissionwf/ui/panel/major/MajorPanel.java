package com.example.managementadmissionwf.ui.panel.major;

import com.example.managementadmissionwf.ui.util.ToolbarAction;
import com.example.managementadmissionwf.ui.util.UIFactory;
import com.example.managementadmissionwf.ui.panel.AbstractFeaturePanel;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.EnumSet;
import java.util.stream.Collectors;

@Component
public class MajorPanel extends AbstractFeaturePanel {

    private JTable majorTable;
    private DefaultTableModel tableModel;
    private JComboBox<String> cboPhuongThuc;

    // Mock data
    private List<Object[]> allData = new ArrayList<>();
    private List<Object[]> filteredData = new ArrayList<>();

    public MajorPanel() {
        super();
    }

    @PostConstruct
    private void initComponents() {
        generateMockData();
        buildUI();
        applyFilters();
    }

    @Override
    protected void createFilterFields(JPanel filterPanel) {
        filterPanel.add(UIFactory.createFilterLabel("Phương thức:"));
        cboPhuongThuc = UIFactory.createFilterCombo(
                new String[]{"Tất cả", "THPT", "ĐGNL", "Tuyển thẳng", "VSAT"}, 140);
        filterPanel.add(cboPhuongThuc);
    }

    @Override
    protected void resetFilters() {
        super.resetFilters();
        if (cboPhuongThuc != null) cboPhuongThuc.setSelectedIndex(0);
        applyFilters();
    }

    @Override
    protected Set<ToolbarAction> getToolbarActions() {
        return EnumSet.of(ToolbarAction.ADD, ToolbarAction.EDIT, ToolbarAction.DELETE,
                ToolbarAction.REFRESH, ToolbarAction.EXPORT_EXCEL, ToolbarAction.IMPORT_EXCEL);
    }

    @Override
    protected void onToolbarAction(ToolbarAction action) {
        // TODO: Wire to MajorController when service layer is fully implemented
    }

    @Override
    protected JComponent createContentPanel() {
        String[] columnNames = {"Mã Ngành", "Tên Ngành", "Chỉ Tiêu", "Điểm Sàn", "Phương thức"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };

        majorTable = UIFactory.createStandardTable(tableModel);
        return UIFactory.createStandardScrollPane(majorTable);
    }

    @Override
    protected void loadData() {
        applyFilters();
    }

    @Override
    protected String getItemLabel() {
        return "ngành";
    }

    // ========== Client-side filter logic ==========

    private void applyFilters() {
        String keyword = getSearchField().getText().trim().toLowerCase();
        String phuongThuc = cboPhuongThuc != null ? (String) cboPhuongThuc.getSelectedItem() : "Tất cả";

        filteredData = allData.stream().filter(row -> {
            boolean mKw = keyword.isEmpty()
                    || row[0].toString().toLowerCase().contains(keyword)
                    || row[1].toString().toLowerCase().contains(keyword);
            boolean mPt = phuongThuc.equals("Tất cả") || row[4].toString().contains(phuongThuc);
            return mKw && mPt;
        }).collect(Collectors.toList());

        currentPage = 1;
        updateTable();
    }

    private void updateTable() {
        tableModel.setRowCount(0);
        int pageSize = getPageSize();
        int total = filteredData.size();
        int totalPages = Math.max(1, (int) Math.ceil((double) total / pageSize));
        int start = (currentPage - 1) * pageSize;
        int end = Math.min(start + pageSize, total);

        for (int i = start; i < end; i++) {
            tableModel.addRow(filteredData.get(i));
        }

        updatePaginationDirect(currentPage, totalPages, total);
    }

    // ========== Mock data ==========

    private void generateMockData() {
        String[][] rawData = {
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

        allData = new ArrayList<>();
        for (String[] row : rawData) {
            allData.add(row);
        }
        filteredData = new ArrayList<>(allData);
    }
}
