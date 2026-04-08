package com.example.managementadmissionwf.ui.panel;

import com.example.managementadmissionwf.ui.util.ToolbarAction;
import com.example.managementadmissionwf.ui.util.UIFactory;
import com.example.managementadmissionwf.ui.panel.AbstractFeaturePanel;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class BonusScorePanel extends AbstractFeaturePanel {

    private JTable table;
    private DefaultTableModel tableModel;

    // Mock data
    private List<BonusScoreMock> allData = new ArrayList<>();
    private List<BonusScoreMock> currentFilteredData = new ArrayList<>();

    public BonusScorePanel() {
        super();
    }

    @PostConstruct
    private void init() {
        generateMockData();
        buildUI();
        handleSearchInternal();
    }

    @Override
    protected JComponent createContentPanel() {
        String[] columnNames = {"STT", "CCCD", "Họ tên thí sinh", "Điểm chứng chỉ", "Điểm ƯTXT", "Tổng điểm"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = UIFactory.createStandardTable(tableModel);
        return UIFactory.createStandardScrollPane(table);
    }

    @Override
    protected Set<ToolbarAction> getToolbarActions() {
        return EnumSet.of(ToolbarAction.ADD, ToolbarAction.EDIT, ToolbarAction.DELETE,
                ToolbarAction.REFRESH, ToolbarAction.EXPORT_EXCEL, ToolbarAction.IMPORT_EXCEL);
    }

    @Override
    protected void onToolbarAction(ToolbarAction action) {
        // TODO: Wire to BonusScoreController when service layer is implemented
    }

    @Override
    protected void loadData() {
        handleSearchInternal();
    }

    @Override
    protected String getItemLabel() {
        return "thí sinh";
    }

    // ========== Client-side pagination logic ==========

    private void handleSearchInternal() {
        String keyword = getSearchField().getText().trim().toLowerCase();
        currentFilteredData = allData.stream()
                .filter(m -> keyword.isEmpty()
                        || m.cccd.contains(keyword)
                        || m.hoTen.toLowerCase().contains(keyword))
                .collect(Collectors.toList());

        currentPage = 1;
        updateTableAndPagination();
    }

    @Override
    protected void resetFilters() {
        super.resetFilters();
        handleSearchInternal();
    }

    private void updateTableAndPagination() {
        int pageSize = getPageSize();
        int totalItems = currentFilteredData.size();
        int totalPages = (int) Math.ceil((double) totalItems / pageSize);
        if (totalPages == 0) totalPages = 1;

        int startIndex = (currentPage - 1) * pageSize;
        int endIndex = Math.min(startIndex + pageSize, totalItems);
        List<BonusScoreMock> pageData = currentFilteredData.subList(startIndex, endIndex);

        tableModel.setRowCount(0);
        int stt = startIndex + 1;
        for (BonusScoreMock m : pageData) {
            tableModel.addRow(new Object[]{
                    stt++, m.cccd, m.hoTen, m.diemCC, m.diemUTXT, m.getTongDiem()
            });
        }

        updatePaginationDirect(currentPage, totalPages, totalItems);
    }

    // ========== Mock data ==========

    private void generateMockData() {
        allData.add(new BonusScoreMock("079204000001", "Nguyễn Văn An", 10.0, 0.5));
        allData.add(new BonusScoreMock("079204000002", "Trần Thị Bình", 9.5, 0.0));
        allData.add(new BonusScoreMock("079204000003", "Lê Hoàng Cường", 0.0, 1.0));
        allData.add(new BonusScoreMock("079204000004", "Phạm Đăng Dương", 8.0, 0.5));
        allData.add(new BonusScoreMock("079204000005", "Hoàng Ngọc Em", 10.0, 2.0));
        allData.add(new BonusScoreMock("079204000006", "Vũ Minh Phương", 0.0, 0.0));
        allData.add(new BonusScoreMock("079204000007", "Đặng Quang Huy", 9.0, 0.5));
        allData.add(new BonusScoreMock("079204000008", "Bùi Thanh Tùng", 7.5, 1.0));
        allData.add(new BonusScoreMock("079204000009", "Đỗ Quỳnh Như", 10.0, 0.0));
        allData.add(new BonusScoreMock("079204000010", "Hồ Việt Dũng", 0.0, 2.5));
        allData.add(new BonusScoreMock("079204000011", "Ngô Khắc Tiệp", 8.5, 0.5));
        allData.add(new BonusScoreMock("079204000012", "Dương Yến Ngọc", 9.5, 1.0));
        allData.add(new BonusScoreMock("079204000013", "Lý Hải Anh", 0.0, 0.0));
        allData.add(new BonusScoreMock("079204000014", "Đoàn Thiên Tôn", 10.0, 2.0));
        allData.add(new BonusScoreMock("079204000015", "Trương Triết Hạn", 9.0, 0.5));
    }

    private static class BonusScoreMock {
        String cccd;
        String hoTen;
        double diemCC;
        double diemUTXT;

        BonusScoreMock(String cccd, String hoTen, double diemCC, double diemUTXT) {
            this.cccd = cccd;
            this.hoTen = hoTen;
            this.diemCC = diemCC;
            this.diemUTXT = diemUTXT;
        }

        double getTongDiem() {
            return diemCC + diemUTXT;
        }
    }
}
