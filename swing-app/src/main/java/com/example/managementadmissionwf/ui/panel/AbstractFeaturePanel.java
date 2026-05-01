package com.example.managementadmissionwf.ui.panel;

import com.example.managementadmissionwf.dto.common.Paging;
import com.example.managementadmissionwf.ui.util.ToolbarAction;
import com.example.managementadmissionwf.ui.util.UIFactory;

import javax.swing.*;
import java.awt.*;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

public abstract class AbstractFeaturePanel extends JPanel {

    // ========== Pagination state ==========
    protected int currentPage = 1;
    protected int totalPages = 1;
    protected long totalItems = 0;

    // ========== UI components managed by base class ==========
    protected JTextField txtSearch;
    private JLabel lblTotalItems;
    private JLabel lblPageInfo;
    private JButton btnFirst;
    private JButton btnPrev;
    private JButton btnNext;
    private JButton btnLast;
    private JComboBox<Integer> cboPageSize;
    private Map<ToolbarAction, JButton> actionButtons = new EnumMap<>(ToolbarAction.class);

    // ========== Abstract methods — subclass implements ==========

    /** Subclass adds domain-specific filter fields to this panel */
    protected void createFilterFields(JPanel filterPanel) {
        // Default: no extra filters. Subclass overrides to add combos etc.
    }

    /** Subclass returns the main content component (typically a ListPanel) */
    protected abstract JComponent createContentPanel();

    /** Subclass specifies which toolbar buttons to show */
    protected Set<ToolbarAction> getToolbarActions() {
        return EnumSet.of(ToolbarAction.ADD, ToolbarAction.EDIT, ToolbarAction.DELETE,
                ToolbarAction.REFRESH, ToolbarAction.EXPORT_EXCEL, ToolbarAction.IMPORT_EXCEL);
    }

    /** Subclass handles toolbar button clicks */
    protected void onToolbarAction(ToolbarAction action) {
        // Default: no-op. Subclass overrides.
    }

    /** Subclass loads data for the current page + filters */
    protected abstract void loadData();

    /** Label for "Tổng: X {itemLabel}" */
    protected abstract String getItemLabel();

    // ========== Optional overrides ==========

    /** Override to reset all domain-specific filter fields */
    protected void resetFilters() {
        txtSearch.setText("");
        currentPage = 1;
    }

    /** Called after base class builds UI. Override for post-init setup (e.g., wire controller). */
    protected void onInit() {
        // Default: no-op.
    }

    // ========== Initialization ==========

    protected AbstractFeaturePanel() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
    }

    /**
     * Call this from subclass @PostConstruct to build the standard UI.
     */
    protected final void buildUI() {
        // 1. Top section: filter + action
        JPanel topPanel = UIFactory.createTopSection();
        topPanel.add(buildFilterPanel(), BorderLayout.NORTH);
        topPanel.add(buildActionPanel(), BorderLayout.SOUTH);

        // 2. Content
        JComponent content = createContentPanel();

        // 3. Pagination
        JPanel paginationPanel = buildPaginationPanel();

        add(topPanel, BorderLayout.NORTH);
        add(content, BorderLayout.CENTER);
        add(paginationPanel, BorderLayout.SOUTH);

        // 4. Post-init hook
        onInit();

        // 5. Load initial data
        refreshData();
    }

    // ========== UI Building (private — not overridable) ==========

    private JPanel buildFilterPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(240, 240, 240));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Row 1: search field + search/clear buttons
        JPanel searchRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        searchRow.setBackground(new Color(240, 240, 240));

        searchRow.add(UIFactory.createFilterLabel("Tìm kiếm:"));
        txtSearch = UIFactory.createFilterTextField(20, "Nhập từ khóa tìm kiếm...");
        searchRow.add(txtSearch);

        JButton btnSearch = UIFactory.createActionButton("Tìm kiếm", "search",
                new Color(52, 152, 219), 120);
        btnSearch.addActionListener(e -> handleSearch());
        searchRow.add(btnSearch);

        JButton btnReset = UIFactory.createActionButton("Clear", "reset",
                new Color(149, 165, 166), 110);
        btnReset.addActionListener(e -> handleReset());
        searchRow.add(btnReset);

        // Row 2: domain-specific filters (subclass adds combos etc.)
        JPanel filterRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        filterRow.setBackground(new Color(240, 240, 240));
        createFilterFields(filterRow);

        panel.add(searchRow, BorderLayout.NORTH);
        panel.add(filterRow, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel buildActionPanel() {
        JPanel panel = UIFactory.createActionPanel();

        for (ToolbarAction action : getToolbarActions()) {
            JButton btn = UIFactory.createActionButton(
                    action.getText(), action.getIconName(), action.getBgColor(), action.getWidth());
            btn.addActionListener(e -> onToolbarAction(action));
            actionButtons.put(action, btn);
            panel.add(btn);
        }

        return panel;
    }

    private JPanel buildPaginationPanel() {
        JPanel panel = UIFactory.createPaginationPanel();

        // Total items label
        lblTotalItems = new JLabel("Tổng: 0 " + getItemLabel());
        lblTotalItems.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblTotalItems.setForeground(new Color(100, 100, 100));
        panel.add(lblTotalItems);

        panel.add(Box.createHorizontalStrut(30));

        // Navigation buttons
        btnFirst = UIFactory.createPaginationButton("first_page", new Color(149, 165, 166), "Trang đầu");
        btnFirst.addActionListener(e -> goToPage(1));
        panel.add(btnFirst);

        btnPrev = UIFactory.createPaginationButton("chevron_left", new Color(52, 152, 219), "Trang trước");
        btnPrev.addActionListener(e -> goToPage(currentPage - 1));
        panel.add(btnPrev);

        lblPageInfo = new JLabel("Trang 1 / 1");
        lblPageInfo.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblPageInfo.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
        panel.add(lblPageInfo);

        btnNext = UIFactory.createPaginationButton("chevron_right", new Color(52, 152, 219), "Trang sau");
        btnNext.addActionListener(e -> goToPage(currentPage + 1));
        panel.add(btnNext);

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
            loadData();
        });
        panel.add(cboPageSize);

        return panel;
    }

    // ========== Common behavior ==========

    private void handleSearch() {
        currentPage = 1;
        loadData();
    }

    private void handleReset() {
        resetFilters();
        loadData();
    }

    private void goToPage(int page) {
        if (page < 1 || page > totalPages) return;
        currentPage = page;
        loadData();
    }

    public void refreshData() {
        currentPage = 1;
        loadData();
    }

    public void updatePagination(Paging<?> paging) {
        this.currentPage = paging.getPage();
        this.totalPages = paging.getTotalPages() == 0 ? 1 : paging.getTotalPages();
        this.totalItems = paging.getTotalItems();

        lblTotalItems.setText("Tổng: " + paging.getTotalItems() + " " + getItemLabel());
        lblPageInfo.setText("Trang " + this.currentPage + " / " + this.totalPages);

        boolean isFirst = this.currentPage <= 1;
        boolean isLast = !paging.isHasNext();
        btnFirst.setEnabled(!isFirst);
        btnPrev.setEnabled(!isFirst);
        btnNext.setEnabled(!isLast);
        btnLast.setEnabled(!isLast);
    }

    /**
     * For panels using client-side pagination (mock data).
     * Handles pagination state internally without Paging DTO.
     */
    public void updatePaginationDirect(int page, int totalPages, long totalItems) {
        this.currentPage = page;
        this.totalPages = totalPages == 0 ? 1 : totalPages;
        this.totalItems = totalItems;

        lblTotalItems.setText("Tổng: " + totalItems + " " + getItemLabel());
        lblPageInfo.setText("Trang " + page + " / " + this.totalPages);

        boolean isFirst = page <= 1;
        boolean isLast = page >= totalPages;
        btnFirst.setEnabled(!isFirst);
        btnPrev.setEnabled(!isFirst);
        btnNext.setEnabled(!isLast);
        btnLast.setEnabled(!isLast);
    }

    // ========== Accessors ==========

    public int getCurrentPage() { return currentPage; }
    public int getPageSize() { return (Integer) cboPageSize.getSelectedItem(); }
    public JTextField getSearchField() { return txtSearch; }
    public JButton getActionButton(ToolbarAction action) { return actionButtons.get(action); }
}
