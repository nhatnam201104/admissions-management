package com.example.managementadmissionwf.ui.panel.user;

import com.example.managementadmissionwf.dto.common.Paging;
import com.example.managementadmissionwf.ui.util.UIFactory;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;

/**
 * Main Panel for User Management
 * Integrates filter, action bar, list panel, and pagination
 */
@Component
public class UserManagementPanel extends JPanel {

    @Autowired
    private UserController controller;

    private UserListPanel listPanel;

    // Filter components
    private JTextField txtSearch;
    private JComboBox<String> cboRole;
    private JButton btnSearch;
    private JButton btnReset;

    // Action buttons
    private JButton btnThem;
    private JButton btnSua;
    private JButton btnXoa;
    private JButton btnLamMoi;

    // Pagination components
    private JLabel lblTotalUsers;
    private JButton btnPrev;
    private JButton btnFirst;
    private JLabel lblPageInfo;
    private JButton btnNext;
    private JButton btnLast;
    private JComboBox<Integer> cboPageSize;

    private int currentPage = 1;
    private int totalPages = 1;
    private long totalItems = 0;

    public UserManagementPanel() {
    }

    @PostConstruct
    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        JPanel filterPanel = createFilterPanel();
        JPanel actionPanel = createActionPanel();
        listPanel = new UserListPanel();
        JPanel paginationPanel = createPaginationPanel();

        controller.setManagementPanel(this);

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

        JLabel lblSearch = new JLabel("Tìm kiếm:");
        lblSearch.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        panel.add(lblSearch);

        txtSearch = new JTextField(20);
        txtSearch.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtSearch.setToolTipText("Tìm theo họ tên, username, hoặc email");
        panel.add(txtSearch);

        JLabel lblRole = new JLabel("Vai trò:");
        lblRole.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        panel.add(lblRole);

        cboRole = new JComboBox<>(new String[]{"Tất cả", "ADMIN", "STUDENT"});
        cboRole.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cboRole.setPreferredSize(new Dimension(120, 30));
        panel.add(cboRole);

        btnSearch = UIFactory.createActionButton("Tìm kiếm", "search", new Color(52, 152, 219), 120);
        btnSearch.addActionListener(e -> handleSearch());
        panel.add(btnSearch);

        btnReset = UIFactory.createActionButton("Reset", "reset", new Color(149, 165, 166), 110);
        btnReset.addActionListener(e -> handleReset());
        panel.add(btnReset);

        return panel;
    }

    private JPanel createActionPanel() {
        JPanel panel = UIFactory.createActionPanel();

        btnThem = UIFactory.createActionButton("Thêm", "add", new Color(46, 204, 113), 110);
        btnThem.addActionListener(e -> controller.addUser());
        panel.add(btnThem);

        btnSua = UIFactory.createActionButton("Sửa", "edit", new Color(52, 152, 219), 110);
        btnSua.addActionListener(e -> controller.editUser());
        panel.add(btnSua);

        btnXoa = UIFactory.createActionButton("Xóa", "delete", new Color(231, 76, 60), 110);
        btnXoa.addActionListener(e -> controller.deleteUser());
        panel.add(btnXoa);

        btnLamMoi = UIFactory.createActionButton("Làm mới", "refresh", new Color(149, 165, 166), 130);
        btnLamMoi.addActionListener(e -> refreshData());
        panel.add(btnLamMoi);

        return panel;
    }

    private JPanel createPaginationPanel() {
        JPanel panel = UIFactory.createPaginationPanel();

        // Total label
        lblTotalUsers = new JLabel("Tổng: 0 người dùng");
        lblTotalUsers.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblTotalUsers.setForeground(new Color(100, 100, 100));
        panel.add(lblTotalUsers);

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

        cboPageSize = new JComboBox<>(new Integer[]{10, 20, 50});
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
        cboRole.setSelectedIndex(0);
        currentPage = 1;
        loadCurrentPage();
    }

    private void goToPage(int page) {
        if (page < 1 || page > totalPages) return;
        currentPage = page;
        loadCurrentPage();
    }

    private void loadCurrentPage() {
        String keyword = txtSearch.getText().trim();
        String role = (String) cboRole.getSelectedItem();
        int pageSize = (Integer) cboPageSize.getSelectedItem();
        controller.loadUsers(keyword, role, currentPage, pageSize);
    }

    public void refreshData() {
        currentPage = 1;
        loadCurrentPage();
    }

    public void updatePagination(Paging<?> paging) {
        this.currentPage = paging.getPage();
        this.totalPages = paging.getTotalPages() == 0 ? 1 : paging.getTotalPages();
        this.totalItems = paging.getTotalItems();

        int from = (paging.getPage() - 1) * paging.getLimit() + 1;
        int to = (int) Math.min(paging.getPage() * paging.getLimit(), paging.getTotalItems());
        if (paging.getTotalItems() == 0) {
            from = 0;
            to = 0;
        }

        lblTotalUsers.setText("Tổng: " + paging.getTotalItems() + " người dùng");
        lblPageInfo.setText("Trang " + paging.getPage() + " / " + this.totalPages);

        boolean isFirst = paging.getPage() <= 1;
        boolean isLast = !paging.isHasNext();
        btnFirst.setEnabled(!isFirst);
        btnPrev.setEnabled(!isFirst);
        btnNext.setEnabled(!isLast);
        btnLast.setEnabled(!isLast);
    }

    public UserListPanel getListPanel() {
        return listPanel;
    }

}
