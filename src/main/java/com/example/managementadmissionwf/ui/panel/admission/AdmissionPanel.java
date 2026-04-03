package com.example.managementadmissionwf.ui.panel.admission;

import javax.swing.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.example.managementadmissionwf.ui.util.UIFactory;

import jakarta.annotation.PostConstruct;

import java.awt.*;

@Component
public class AdmissionPanel extends JPanel {
	private ResultTable resultTable;
	
	@Autowired
    private AdmissionResultController controller;
	
	 // Filter components
	private JTextField txtSearch;
    private JComboBox<String> cboKetQua;
    private JComboBox<String> cboNganh;
    private JComboBox<String> cboPhuongThuc;
    private JButton btnSearch;
    private JButton btnReset;
    private JButton btnUpdate;
    
    // Action buttons
    private JButton btnExportExcel;
    private JButton btnExportPDF;
    private JButton btnPrint;
    
    //Statistics components
    private JLabel lblTotalAdmitted;
    private JLabel lblAdmissionRate;
    
    public AdmissionPanel() {
  
    }
    
    @PostConstruct
    private void initComponents() {
    	setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JPanel topPanel = createTopPanel();
        resultTable = new ResultTable();
        JPanel bottomPanel = createStatisticPanel();
        
        controller.setAdmissionPanel(this, resultTable);
        btnExportExcel.addActionListener(e -> controller.handleExportExcel()); 
        btnExportPDF.addActionListener(e -> controller.handleExportPDF());    
        btnUpdate.addActionListener(e -> controller.updateResult());
        btnReset.addActionListener(e -> handleReset());
        controller.loadResults();
        
        add(topPanel, BorderLayout.NORTH);
        add(resultTable, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }
    
    private JPanel createTopPanel() {
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(Color.WHITE);

        JPanel filterPanel = createFilterPanel();
        JPanel actionPanel = createActionPanel();

        topPanel.add(filterPanel, BorderLayout.NORTH);
        topPanel.add(actionPanel, BorderLayout.SOUTH);

        return topPanel;
    }
    
    private JPanel createFilterPanel() {
    	JPanel panel = UIFactory.createFilterPanel();

        // Ô Tìm kiếm
        JLabel lblSearch = new JLabel("Từ khóa:");
        lblSearch.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        panel.add(lblSearch);
        
        txtSearch = new JTextField(10);
        txtSearch.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtSearch.setToolTipText("Nhập CCCD, SBD hoặc Họ tên...");//hover chuột sẽ hiện gợi ý
        panel.add(txtSearch);

        // Filter Kết quả
        JLabel lblKetQua = new JLabel("Kết quả:");
        lblKetQua.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        panel.add(lblKetQua);
        
        String[] ketQuaItems = {"Tất cả", "TRUNG_TUYEN", "TRUOT", "CHO_XET"};
        cboKetQua = new JComboBox<>(ketQuaItems);
        cboKetQua.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        panel.add(cboKetQua);

        // Filter Ngành
        JLabel lblNganh = new JLabel("Ngành:");
        lblNganh.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        panel.add(lblNganh);
        
        String[] nganhItems = {"Tất cả", "Công nghệ thông tin", "Quản trị kinh doanh", "Kỹ thuật phần mềm"};
        cboNganh = new JComboBox<>(nganhItems);
        cboNganh.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        panel.add(cboNganh);

        // Filter Phương thức
        JLabel lblPhuongThuc = new JLabel("Phương thức:");
        lblPhuongThuc.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        panel.add(lblPhuongThuc);
        
        String[] phuongThucItems = {"Tất cả", "THPT", "DGNL", "Học bạ"};
        cboPhuongThuc = new JComboBox<>(phuongThucItems);
        cboPhuongThuc.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        panel.add(cboPhuongThuc);

        // Nút Tìm kiếm & Reset
        btnSearch = UIFactory.createActionButton("Tìm kiếm", "search", new Color(52, 152, 219), 120);
        btnReset = UIFactory.createActionButton("Reset", "reset", new Color(149, 165, 166), 110);
        btnSearch.addActionListener(e -> handleSearch());

        panel.add(btnSearch);
        panel.add(btnReset);

        return panel;
    }
    
    private JPanel createActionPanel() {
        JPanel panel = UIFactory.createActionPanel();
        
        btnUpdate = UIFactory.createActionButton("Cập nhật KQ", "refresh", new Color(243, 156, 18), 145);
        btnExportExcel = UIFactory.createActionButton("Xuất Excel", "export", new Color(46, 204, 113), 130);
        btnExportPDF = UIFactory.createActionButton("Xuất PDF", "picture_as_pdf", new Color(231, 76, 60), 120);
        btnPrint = UIFactory.createActionButton("In danh sách", "print", new Color(52, 152, 219), 135);

        panel.add(btnUpdate);
        panel.add(btnExportExcel);
        panel.add(btnExportPDF);
        panel.add(btnPrint);

        return panel;
    }
    
    private JPanel createStatisticPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(200, 200, 200)));

        lblTotalAdmitted = new JLabel("Tổng số trúng tuyển: 2");
        lblTotalAdmitted.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTotalAdmitted.setForeground(new Color(46, 204, 113));

        lblAdmissionRate = new JLabel("Tỷ lệ trúng tuyển: 40.0%");
        lblAdmissionRate.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblAdmissionRate.setForeground(new Color(52, 152, 219));

        panel.add(lblTotalAdmitted);
        panel.add(lblAdmissionRate);

        return panel;
    }
    
    private void handleSearch() {
    	String keyword = txtSearch.getText().trim();
        String ketQua = (String) cboKetQua.getSelectedItem();
        String nganh = (String) cboNganh.getSelectedItem();
        controller.search(keyword, ketQua, nganh);
    }

    private void handleReset() {
    	txtSearch.setText(""); 
        cboKetQua.setSelectedIndex(0);
        cboNganh.setSelectedIndex(0);
        cboPhuongThuc.setSelectedIndex(0); 
        controller.loadResults();
    }
    
}
