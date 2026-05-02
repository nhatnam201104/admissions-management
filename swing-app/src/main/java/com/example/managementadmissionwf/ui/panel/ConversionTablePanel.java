package com.example.managementadmissionwf.ui.panel;

import com.example.managementadmissionwf.ui.util.ToolbarAction;
import com.example.managementadmissionwf.ui.util.UIFactory;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ConversionTablePanel extends AbstractFeaturePanel {

    private JTable table;
    private DefaultTableModel tableModel;
    private JComboBox<String> cbPhuongThuc;
    private JComboBox<String> cbToHop;

    private List<Object[]> allData;
    private List<Object[]> filteredData;

    public ConversionTablePanel() {
        generateMockData();
        buildUI();
        applyFilters();
    }

    @Override
    protected void createFilterFields(JPanel filterPanel) {
        filterPanel.add(UIFactory.createFilterLabel("Phương thức:"));
        cbPhuongThuc = UIFactory.createFilterCombo(new String[]{"Tất cả", "THPT", "DGNL", "VSAT"}, 110);
        filterPanel.add(cbPhuongThuc);

        filterPanel.add(UIFactory.createFilterLabel("Tổ hợp:"));
        cbToHop = UIFactory.createFilterCombo(new String[]{"Tất cả", "A00", "A01", "B00", "D01", "D07"}, 110);
        filterPanel.add(cbToHop);
    }

    @Override
    protected void resetFilters() {
        super.resetFilters();
        if (cbPhuongThuc != null) cbPhuongThuc.setSelectedIndex(0);
        if (cbToHop != null) cbToHop.setSelectedIndex(0);
        applyFilters();
    }

    @Override
    protected Set<ToolbarAction> getToolbarActions() {
        return EnumSet.of(ToolbarAction.EXPORT_EXCEL, ToolbarAction.IMPORT_EXCEL);
    }

    @Override
    protected void onToolbarAction(ToolbarAction action) {
        // TODO: Wire export/import when service layer is implemented
    }

    @Override
    protected JComponent createContentPanel() {
        JPanel container = new JPanel(new BorderLayout(0, 10));
        container.setBackground(Color.WHITE);

        String[] columnNames = {
                "STT", "Phương thức", "Tổ hợp", "Môn",
                "Điểm A (Min)", "Điểm B (Max)", "Điểm C (Quy đổi)", "Điểm D (Tối đa)"
        };

        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };

        table = UIFactory.createStandardTable(tableModel);
        JScrollPane scrollPane = UIFactory.createStandardScrollPane(table);
        container.add(scrollPane, BorderLayout.CENTER);
        return container;
    }

    @Override
    protected void loadData() {
        applyFilters();
    }

    @Override
    protected String getItemLabel() {
        return "bản ghi";
    }

    // ========== Client-side filter + pagination ==========

    private void applyFilters() {
        String pt = cbPhuongThuc != null ? (String) cbPhuongThuc.getSelectedItem() : "Tất cả";
        String th = cbToHop != null ? (String) cbToHop.getSelectedItem() : "Tất cả";
        String search = getSearchField().getText().toLowerCase();

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
                    i, pt, th, mon, diemA, diemB, diemC, diemD
            });
        }
        filteredData = new ArrayList<>(allData);
    }
}
