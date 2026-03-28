package com.example.managementadmissionwf.ui.panel;

import com.example.managementadmissionwf.dto.User.GetUserResponse;
import com.example.managementadmissionwf.dto.common.Paging;
import com.example.managementadmissionwf.ui.panel.user.UserController;
import com.example.managementadmissionwf.ui.panel.user.UserListPanel;
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

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(filterPanel, BorderLayout.NORTH);
        topPanel.add(actionPanel, BorderLayout.SOUTH);

        add(topPanel, BorderLayout.NORTH);
        add(listPanel, BorderLayout.CENTER);
        add(paginationPanel, BorderLayout.SOUTH);

        // Load initial data
        refreshData();
    }

    private JPanel createFilterPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        panel.setBackground(new Color(240, 240, 240));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

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

        btnSearch = createButton("Tìm kiếm", new Color(52, 152, 219));
        btnSearch.addActionListener(e -> handleSearch());
        panel.add(btnSearch);

        btnReset = createButton("Reset", new Color(149, 165, 166));
        btnReset.addActionListener(e -> handleReset());
        panel.add(btnReset);

        return panel;
    }

    private JPanel createActionPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        panel.setBackground(new Color(240, 240, 240));
        panel.setBorder(BorderFactory.createEmptyBorder(5, 10, 10, 10));

        btnThem = createButton("➕ Thêm", new Color(46, 204, 113));
        btnThem.addActionListener(e -> controller.addUser());
        panel.add(btnThem);

        btnSua = createButton("✏️ Sửa", new Color(52, 152, 219));
        btnSua.addActionListener(e -> controller.editUser());
        panel.add(btnSua);

        btnXoa = createButton("🗑️ Xóa", new Color(231, 76, 60));
        btnXoa.addActionListener(e -> controller.deleteUser());
        panel.add(btnXoa);

        btnLamMoi = createButton("🔄 Làm mới", new Color(149, 165, 166));
        btnLamMoi.addActionListener(e -> refreshData());
        panel.add(btnLamMoi);

        return panel;
    }

    private JPanel createPaginationPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 10));
        panel.setBackground(new Color(240, 240, 240));
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEmptyBorder(5, 10, 5, 10), "", 0, 0));

        // Total label
        lblTotalUsers = new JLabel("Tổng: 0 người dùng");
        lblTotalUsers.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblTotalUsers.setForeground(new Color(100, 100, 100));
        panel.add(lblTotalUsers);

        panel.add(Box.createHorizontalStrut(30));

        // First page button
        btnFirst = createSmallButton("⏮", new Color(149, 165, 166));
        btnFirst.setToolTipText("Trang đầu");
        btnFirst.addActionListener(e -> goToPage(1));
        panel.add(btnFirst);

        // Previous button
        btnPrev = createSmallButton("◀", new Color(52, 152, 219));
        btnPrev.setToolTipText("Trang trước");
        btnPrev.addActionListener(e -> goToPage(currentPage - 1));
        panel.add(btnPrev);

        // Page info label
        lblPageInfo = new JLabel("Trang 1 / 1");
        lblPageInfo.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblPageInfo.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
        panel.add(lblPageInfo);

        // Next button
        btnNext = createSmallButton("▶", new Color(52, 152, 219));
        btnNext.setToolTipText("Trang sau");
        btnNext.addActionListener(e -> goToPage(currentPage + 1));
        panel.add(btnNext);

        // Last page button
        btnLast = createSmallButton("⏭", new Color(149, 165, 166));
        btnLast.setToolTipText("Trang cuối");
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

    private JButton createButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(120, 35));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    private JButton createSmallButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(35, 30));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }
}
