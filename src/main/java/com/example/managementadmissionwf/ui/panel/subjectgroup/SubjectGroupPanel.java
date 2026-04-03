package com.example.managementadmissionwf.ui.panel.subjectgroup;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.example.managementadmissionwf.ui.util.UIFactory;

import javax.swing.*;
import java.awt.*;

/**
 * Main Panel for Subject Group Management
 * Integrates filter, action bar, and list panel
 */
@Component
public class SubjectGroupPanel extends JPanel {

    @Autowired
    private SubjectGroupController controller;

    private SubjectGroupListPanel listPanel;

    // Filter components
    private JTextField txtSearch;
    private JButton btnSearch;
    private JButton btnReset;

    // Action buttons
    private JButton btnAdd;
    private JButton btnEdit;
    private JButton btnDelete;
    private JButton btnRefresh;
    private JButton btnExport;
    private JButton btnImport;

    // Pagination components
    private JLabel lblTotalItems;
    private JButton btnPrev;
    private JButton btnFirst;
    private JLabel lblPageInfo;
    private JButton btnNext;
    private JButton btnLast;
    private JComboBox<Integer> cboPageSize;

    private int currentPage = 1;
    private int totalPages = 1;
    private long totalItems = 0;

    public SubjectGroupPanel() {
    }

    @PostConstruct
    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // Create panels
        JPanel filterPanel = createFilterPanel();
        JPanel actionPanel = createActionPanel();
        listPanel = new SubjectGroupListPanel();
        JPanel paginationPanel = createPaginationPanel();

        // Connect controller
        controller.setSubjectGroupPanel(this);

        // Add panels
        JPanel topPanel = UIFactory.createTopSection();
        topPanel.add(filterPanel, BorderLayout.NORTH);
        topPanel.add(actionPanel, BorderLayout.SOUTH);

        add(topPanel, BorderLayout.NORTH);
        add(listPanel, BorderLayout.CENTER);
        add(paginationPanel, BorderLayout.SOUTH);

        // Load initial data
        refreshData();
    }

    private JPanel createFilterPanel() {
        JPanel panel = UIFactory.createFilterPanel();

        // Search field
        JLabel lblSearch = new JLabel("Tìm kiếm:");
        lblSearch.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        panel.add(lblSearch);

        txtSearch = new JTextField(20);
        txtSearch.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtSearch.setToolTipText("Tìm theo mã tổ hợp hoặc tên môn");
        panel.add(txtSearch);

        // Search button
        btnSearch = UIFactory.createActionButton("Tìm kiếm", "search", new Color(52, 152, 219), 120);
        btnSearch.addActionListener(e -> handleSearch());
        panel.add(btnSearch);

        // Reset button
        btnReset = UIFactory.createActionButton("Reset", "reset", new Color(149, 165, 166), 110);
        btnReset.addActionListener(e -> handleReset());
        panel.add(btnReset);

        return panel;
    }

    private JPanel createActionPanel() {
        JPanel panel = UIFactory.createActionPanel();

        // Add button
        btnAdd = UIFactory.createActionButton("Thêm", "add", new Color(46, 204, 113), 100);
        btnAdd.addActionListener(e -> controller.addSubjectGroup());
        panel.add(btnAdd);

        // Edit button
        btnEdit = UIFactory.createActionButton("Sửa", "edit", new Color(52, 152, 219), 100);
        btnEdit.addActionListener(e -> controller.editSubjectGroup());
        panel.add(btnEdit);

        // Delete button
        btnDelete = UIFactory.createActionButton("Xóa", "delete", new Color(231, 76, 60), 100);
        btnDelete.addActionListener(e -> controller.deleteSubjectGroup());
        panel.add(btnDelete);

        // Refresh button
        btnRefresh = UIFactory.createActionButton("Làm mới", "refresh", new Color(149, 165, 166), 120);
        btnRefresh.addActionListener(e -> refreshData());
        panel.add(btnRefresh);

        // Export button
        btnExport = UIFactory.createActionButton("Xuất Excel", "export", new Color(39, 174, 96), 130);
        btnExport.addActionListener(e -> controller.exportExcel(txtSearch.getText().trim()));
        panel.add(btnExport);

        // Import button
        btnImport = UIFactory.createActionButton("Nhập Excel", "import", new Color(243, 156, 18), 130);
        btnImport.addActionListener(e -> controller.importExcel());
        panel.add(btnImport);

        return panel;
    }

    private JPanel createPaginationPanel() {
        JPanel panel = UIFactory.createPaginationPanel();

        // Total label
        lblTotalItems = new JLabel("Tổng: 0 bản ghi");
        lblTotalItems.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblTotalItems.setForeground(new Color(100, 100, 100));
        panel.add(lblTotalItems);

        panel.add(Box.createHorizontalStrut(30));

        // First page button
        btnFirst = UIFactory.createPaginationButton("first_page", new Color(149, 165, 166), "Trang đầu");
        btnFirst.addActionListener(e -> goToPage(1));
        panel.add(btnFirst);

        // Previous button
        btnPrev = UIFactory.createPaginationButton("chevron_left", new Color(52, 152, 219), "Trang trước");
        btnPrev.addActionListener(e -> goToPage(currentPage - 1));
        panel.add(btnPrev);

        // Page info label
        lblPageInfo = new JLabel("Trang 1 / 1");
        lblPageInfo.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblPageInfo.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
        panel.add(lblPageInfo);

        // Next button
        btnNext = UIFactory.createPaginationButton("chevron_right", new Color(52, 152, 219), "Trang sau");
        btnNext.addActionListener(e -> goToPage(currentPage + 1));
        panel.add(btnNext);

        // Last page button
        btnLast = UIFactory.createPaginationButton("last_page", new Color(149, 165, 166), "Trang cuối");
        btnLast.addActionListener(e -> goToPage(totalPages));
        panel.add(btnLast);

        panel.add(Box.createHorizontalStrut(30));

        // Page size combo
        JLabel lblSize = new JLabel("Hiển thị:");
        lblSize.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblSize.setForeground(new Color(100, 100, 100));
        panel.add(lblSize);

        cboPageSize = new JComboBox<>(new Integer[] { 10, 20, 50 });
        cboPageSize.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cboPageSize.setPreferredSize(new Dimension(65, 28));
        cboPageSize.addActionListener(e -> {
            currentPage = 1;
            loadCurrentPage();
        });
        panel.add(cboPageSize);

        return panel;
    }

    private void handleSearch() {
        currentPage = 1;
        loadCurrentPage();
    }

    private void handleReset() {
        txtSearch.setText("");
        currentPage = 1;
        loadCurrentPage();
    }

    private void goToPage(int page) {
        if (page < 1 || page > totalPages)
            return;
        currentPage = page;
        loadCurrentPage();
    }

    private void loadCurrentPage() {
        String keyword = txtSearch.getText().trim();
        int pageSize = (Integer) cboPageSize.getSelectedItem();
        controller.loadSubjectGroups(keyword, currentPage - 1, pageSize); // Assuming Service uses 0-based page index
    }

    public void refreshData() {
        currentPage = 1;
        loadCurrentPage();
    }

    public void updatePagination(com.example.managementadmissionwf.dto.common.Paging<?> paging) {
        // Assume API returns 0-based index but UI is 1-based
        this.currentPage = paging.getPage() + 1;
        this.totalPages = paging.getTotalPages() == 0 ? 1 : paging.getTotalPages();
        this.totalItems = paging.getTotalItems();

        lblTotalItems.setText("Tổng: " + paging.getTotalItems() + " bản ghi");
        lblPageInfo.setText("Trang " + this.currentPage + " / " + this.totalPages);

        boolean isFirst = this.currentPage <= 1;
        boolean isLast = !paging.isHasNext();
        btnFirst.setEnabled(!isFirst);
        btnPrev.setEnabled(!isFirst);
        btnNext.setEnabled(!isLast);
        btnLast.setEnabled(!isLast);
    }

    public SubjectGroupListPanel getListPanel() {
        return listPanel;
    }
}