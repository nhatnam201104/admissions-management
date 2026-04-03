package com.example.managementadmissionwf.ui.panel;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import org.springframework.stereotype.Component;
import com.example.managementadmissionwf.ui.util.UIFactory;

import jakarta.annotation.PostConstruct;

import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;
import java.util.ArrayList;

@Component
public class BonusScorePanel extends JPanel {
	
	//Filter
	private JTextField txtSearch;
	private JButton btnSearch, btnReset;
	
	//Table components
	private JTable table;
	private DefaultTableModel tableModel;
	
	// Pagination components
	private JLabel lblTotalItems;
    private JButton btnPrev;
    private JButton btnFirst;
    private JLabel lblPageInfo;
    private JButton btnNext;
    private JButton btnLast;
    private JComboBox<Integer> cboPageSize;
    
    //State variables
    private int currentPage = 1;
    private int totalPages = 1;
	
    //Mock Data 
    private List<BonusScoreMock> allData = new ArrayList<>();
    private List<BonusScoreMock> currentFilteredData = new ArrayList<>();
    
    public BonusScorePanel() {
    	
    }
    
    @PostConstruct
    private void init() {
        generateMockData();
        initComponents();
        handleSearch();
    }
    
    private void initComponents() {
    	setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        
        JPanel filterPanel = createFilterPanel();
        JPanel tablePanel = createTablePanel();
        JPanel paginationPanel = createPaginationPanel();
        
        add(filterPanel, BorderLayout.NORTH);
        add(tablePanel, BorderLayout.CENTER);
        add(paginationPanel, BorderLayout.SOUTH);
        
    }
    
    private JPanel createFilterPanel() {
    	JPanel panel = UIFactory.createFilterPanel();
    	
    	JLabel lblSearch = new JLabel("Tìm kiếm:");
        lblSearch.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        panel.add(lblSearch);

        txtSearch = new JTextField(25);
        txtSearch.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtSearch.setToolTipText("Nhập CCCD hoặc Họ tên thí sinh...");
        panel.add(txtSearch);

        btnSearch = UIFactory.createActionButton("Tìm kiếm", "search", new Color(52, 152, 219), 120);
        btnSearch.addActionListener(e -> handleSearch());
        panel.add(btnSearch);

        btnReset = UIFactory.createActionButton("Reset", "reset", new Color(149, 165, 166), 110);
        btnReset.addActionListener(e -> handleReset());
        panel.add(btnReset);
    	
        return panel;
    }
    
    private JPanel createTablePanel() {
    	JPanel panel = new JPanel(new BorderLayout());
    	panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
//    	panel.setBackground(Color.WHITE);
    	panel.setBackground(new Color(240, 240, 240));


    	String[] columnNames = {"STT", "CCCD", "Họ tên thí sinh", "Điểm chứng chỉ", "Điểm ƯTXT", "Tổng điểm"};
    	tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        table = new JTable(tableModel);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.getTableHeader().setBackground(new Color(44, 62, 80));
        table.getTableHeader().setForeground(Color.WHITE);
        table.setRowHeight(30);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
        
        table.getColumnModel().getColumn(0).setPreferredWidth(50);
        table.getColumnModel().getColumn(1).setPreferredWidth(120);
        table.getColumnModel().getColumn(2).setPreferredWidth(200);
        table.getColumnModel().getColumn(3).setPreferredWidth(120);
        table.getColumnModel().getColumn(4).setPreferredWidth(100);
        table.getColumnModel().getColumn(5).setPreferredWidth(100);
        
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }
    
    private JPanel createPaginationPanel() {
    	JPanel panel = UIFactory.createPaginationPanel();
        
        lblTotalItems = new JLabel("Tổng: 0 thí sinh");
        lblTotalItems.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblTotalItems.setForeground(new Color(100, 100, 100));
        panel.add(lblTotalItems);
        
        panel.add(Box.createHorizontalStrut(30));
    	
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
        
        JLabel lblSize = new JLabel("Hiển thị:");
        lblSize.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblSize.setForeground(new Color(100, 100, 100));
        panel.add(lblSize);
        
        cboPageSize = new JComboBox<>(new Integer[]{10, 20, 50});
        cboPageSize.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cboPageSize.setPreferredSize(new Dimension(65, 28));
        cboPageSize.setSelectedItem(20);
        cboPageSize.addActionListener(e -> handleSearch());
        panel.add(cboPageSize);
        
        return panel;
    }
    
    //MOCK
    
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
    
    //Logic
    private void handleSearch() {
    	String keyword = txtSearch.getText().trim().toLowerCase();
    	
    	currentFilteredData = allData.stream()
    			.filter(m -> keyword.isEmpty()
    					||  m.cccd.contains(keyword)
    					|| m.hoTen.toLowerCase().contains(keyword))
    			.collect(Collectors.toList());
    	
    	currentPage = 1;
    	updateTableAndPagination();
    }
    
    private void handleReset() {
        txtSearch.setText("");
        cboPageSize.setSelectedItem(20);
        handleSearch();
    }
    
    private void goToPage(int page) {
        if (page < 1 || page > totalPages) return;
        currentPage = page;
        updateTableAndPagination();
    }
    
    private void updateTableAndPagination() {
    	int pageSize = (Integer) cboPageSize.getSelectedItem();
    	int totalItems = currentFilteredData.size();
    	totalPages = (int) Math.ceil((double) totalItems / pageSize);
    	if (totalPages == 0) totalPages = 1;
    	
    	//slice data
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
    	
    	lblTotalItems.setText("Tổng: " + totalItems + " thí sinh");
        lblPageInfo.setText("Trang " + currentPage + " / " + totalPages);
        
        boolean isFirst = currentPage <= 1;
        boolean isLast = currentPage >= totalPages;
        btnFirst.setEnabled(!isFirst);
        btnPrev.setEnabled(!isFirst);
        btnNext.setEnabled(!isLast);
        btnLast.setEnabled(!isLast);
    }
    
    //Inner Class 
    private static class BonusScoreMock {
        String cccd;
        String hoTen;
        double diemCC;
        double diemUTXT;

        public BonusScoreMock(String cccd, String hoTen, double diemCC, double diemUTXT) {
            this.cccd = cccd;
            this.hoTen = hoTen;
            this.diemCC = diemCC;
            this.diemUTXT = diemUTXT;
        }

        public double getTongDiem() {
            return diemCC + diemUTXT;
        }
    }
}