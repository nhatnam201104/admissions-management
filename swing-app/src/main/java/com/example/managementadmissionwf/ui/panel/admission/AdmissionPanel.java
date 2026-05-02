package com.example.managementadmissionwf.ui.panel.admission;

import com.example.managementadmissionwf.ui.util.ToolbarAction;
import com.example.managementadmissionwf.ui.util.UIFactory;
import com.example.managementadmissionwf.ui.panel.AbstractFeaturePanel;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;
import java.util.EnumSet;
import java.util.Set;

@Component
public class AdmissionPanel extends AbstractFeaturePanel {

    @Autowired
    private AdmissionResultController controller;

    private ResultTable resultTable;
    private JComboBox<String> cboKetQua;
    private JComboBox<String> cboNganh;
    private JComboBox<String> cboPhuongThuc;

    // Statistics components
    private JLabel lblTotalAdmitted;
    private JLabel lblAdmissionRate;

    public AdmissionPanel() {
        super();
    }

    @PostConstruct
    private void initComponents() {
        resultTable = new ResultTable();
        buildUI();
    }

    @Override
    protected void onInit() {
        controller.setAdmissionPanel(this, resultTable);
        // Wire search/reset after buildUI creates the fields
        // Search and reset are handled by base class via loadData()
    }

    @Override
    protected void createFilterFields(JPanel filterPanel) {
        filterPanel.add(UIFactory.createFilterLabel("Kết quả:"));
        cboKetQua = UIFactory.createFilterCombo(new String[]{"Tất cả", "TRUNG_TUYEN", "TRUOT", "CHO_XET"}, 120);
        filterPanel.add(cboKetQua);

        filterPanel.add(UIFactory.createFilterLabel("Ngành:"));
        cboNganh = UIFactory.createFilterCombo(
                new String[]{"Tất cả", "Công nghệ thông tin", "Quản trị kinh doanh", "Kỹ thuật phần mềm"}, 160);
        filterPanel.add(cboNganh);

        filterPanel.add(UIFactory.createFilterLabel("Phương thức:"));
        cboPhuongThuc = UIFactory.createFilterCombo(new String[]{"Tất cả", "THPT", "DGNL", "Học bạ"}, 100);
        filterPanel.add(cboPhuongThuc);
    }

    @Override
    protected void resetFilters() {
        super.resetFilters();
        if (cboKetQua != null) cboKetQua.setSelectedIndex(0);
        if (cboNganh != null) cboNganh.setSelectedIndex(0);
        if (cboPhuongThuc != null) cboPhuongThuc.setSelectedIndex(0);
    }

    @Override
    protected Set<ToolbarAction> getToolbarActions() {
        return EnumSet.of(ToolbarAction.UPDATE, ToolbarAction.EXPORT_EXCEL, ToolbarAction.EXPORT_PDF, ToolbarAction.PRINT);
    }

    @Override
    protected void onToolbarAction(ToolbarAction action) {
        switch (action) {
            case UPDATE -> controller.updateResult();
            case EXPORT_EXCEL -> controller.handleExportExcel();
            case EXPORT_PDF -> controller.handleExportPDF();
            default -> {}
        }
    }

    @Override
    protected JComponent createContentPanel() {
        // Composite: ResultTable + statistic bar at bottom
        JPanel content = new JPanel(new BorderLayout());
        content.setBackground(Color.WHITE);
        content.add(resultTable, BorderLayout.CENTER);
        content.add(createStatisticPanel(), BorderLayout.SOUTH);
        return content;
    }

    @Override
    protected void loadData() {
        String keyword = getSearchField().getText().trim();
        String ketQua = (String) cboKetQua.getSelectedItem();
        String nganh = (String) cboNganh.getSelectedItem();
        controller.search(keyword, ketQua, nganh);
    }

    @Override
    protected String getItemLabel() {
        return "bản ghi";
    }

    private JPanel createStatisticPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(200, 200, 200)));

        lblTotalAdmitted = new JLabel("Tổng số trúng tuyển: 0");
        lblTotalAdmitted.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTotalAdmitted.setForeground(new Color(46, 204, 113));

        lblAdmissionRate = new JLabel("Tỷ lệ trúng tuyển: 0.0%");
        lblAdmissionRate.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblAdmissionRate.setForeground(new Color(52, 152, 219));

        panel.add(lblTotalAdmitted);
        panel.add(lblAdmissionRate);
        return panel;
    }

    public void updateStatistics(long totalAdmitted, double rate) {
        lblTotalAdmitted.setText("Tổng số trúng tuyển: " + totalAdmitted);
        lblAdmissionRate.setText(String.format("Tỷ lệ trúng tuyển: %.1f%%", rate));
    }
}
