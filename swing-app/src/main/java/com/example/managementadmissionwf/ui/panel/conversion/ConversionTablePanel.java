package com.example.managementadmissionwf.ui.panel.conversion;

import com.example.managementadmissionwf.bus.interfaces.ConversionTableService;
import com.example.managementadmissionwf.dto.ConversionTableDTO;
import com.example.managementadmissionwf.dto.common.ImportResult;
import com.example.managementadmissionwf.ui.panel.AbstractFeaturePanel;
import com.example.managementadmissionwf.ui.util.ToolbarAction;
import com.example.managementadmissionwf.ui.util.UIFactory;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.Set;

@Component
public class ConversionTablePanel extends AbstractFeaturePanel {

    private final ConversionTableService conversionTableService;
    private ConversionTableController controller;
    private ConversionTableListPanel listPanel;

    // Filter components
    private JComboBox<String> cbPhuongThuc;
    private JComboBox<String> cbToHop;
    private JComboBox<String> cbMon;

    private static final String ALL = "Tất cả";
    private static final String[] PHUONG_THUC_OPTIONS = { "VSAT", "THPT", "DGNL", "IELTS", "TOEIC" };
    private static final String[] TO_HOP_OPTIONS = { "A00", "A01", "A02", "B00", "B01", "C00", "D01", "D07" };
    private static final String[] MON_OPTIONS = { "TO", "LY", "HH", "SH", "LS", "DL", "AN", "NV", "IELTS", "TOEIC" };

    public ConversionTablePanel(ConversionTableService conversionTableService) {
        this.conversionTableService = conversionTableService;
    }

    @PostConstruct
    private void init() {
        this.controller = new ConversionTableController(conversionTableService);
        this.listPanel = new ConversionTableListPanel(controller);
        buildUI();
    }

    @Override
    protected void createFilterFields(JPanel filterPanel) {
        filterPanel.add(UIFactory.createFilterLabel("Phương thức:"));
        cbPhuongThuc = UIFactory.createFilterCombo(new String[] { ALL }, 110);
        filterPanel.add(cbPhuongThuc);

        filterPanel.add(UIFactory.createFilterLabel("Tổ hợp:"));
        cbToHop = UIFactory.createFilterCombo(new String[] { ALL }, 110);
        filterPanel.add(cbToHop);

        filterPanel.add(UIFactory.createFilterLabel("Môn:"));
        cbMon = UIFactory.createFilterCombo(new String[] { ALL }, 110);
        filterPanel.add(cbMon);

        // Load filter options from service
        loadFilterOptions();
    }

    private void loadFilterOptions() {
        try {
            // Load Phương thức
            if (cbPhuongThuc.getItemCount() <= 1) {
                for (String pt : PHUONG_THUC_OPTIONS) {
                    cbPhuongThuc.addItem(pt);
                }
            }

            // Load Tổ hợp
            if (cbToHop.getItemCount() <= 1) {
                for (String th : TO_HOP_OPTIONS) {
                    cbToHop.addItem(th);
                }
            }

            // Load Môn
            if (cbMon.getItemCount() <= 1) {
                for (String mon : MON_OPTIONS) {
                    cbMon.addItem(mon);
                }
            }
        } catch (Exception e) {
            System.err.println("Error loading filter options: " + e.getMessage());
        }
    }

    @Override
    protected void resetFilters() {
        super.resetFilters();
        if (cbPhuongThuc != null)
            cbPhuongThuc.setSelectedIndex(0);
        if (cbToHop != null)
            cbToHop.setSelectedIndex(0);
        if (cbMon != null)
            cbMon.setSelectedIndex(0);
        applyFilters();
    }

    @Override
    protected JComponent createContentPanel() {
        return listPanel;
    }

    @Override
    protected Set<ToolbarAction> getToolbarActions() {
        return EnumSet.of(ToolbarAction.ADD, ToolbarAction.EDIT, ToolbarAction.DELETE,
                ToolbarAction.REFRESH, ToolbarAction.EXPORT_EXCEL, ToolbarAction.IMPORT_EXCEL);
    }

    @Override
    protected void onToolbarAction(ToolbarAction action) {
        switch (action) {
            case ADD -> showAddDialog();
            case EDIT -> showEditDialog();
            case DELETE -> deleteSelectedConversion();
            case REFRESH -> refreshData();
            case EXPORT_EXCEL -> exportToExcel();
            case IMPORT_EXCEL -> importFromExcel();
            default -> throw new IllegalArgumentException("Unexpected value: " + action);
        }
    }

    @Override
    protected void loadData() {
        if (controller == null || listPanel == null || txtSearch == null)
            return;

        String phuongThuc = getSelectedFilterValue(cbPhuongThuc);
        String toHop = getSelectedFilterValue(cbToHop);
        String mon = getSelectedFilterValue(cbMon);
        String keyword = txtSearch.getText().trim();
        int page = currentPage;
        int size = getPageSize();

        controller.loadConversionTables(phuongThuc, toHop, mon, keyword,
                listPanel.getMasterModel(), page, size);
    }

    private String getSelectedFilterValue(JComboBox<String> combo) {
        if (combo == null)
            return null;
        String selected = (String) combo.getSelectedItem();
        return (selected == null || ALL.equals(selected)) ? null : selected;
    }

    private void applyFilters() {
        loadData();
    }

    @Override
    protected String getItemLabel() {
        return "bản ghi";
    }

    private void showAddDialog() {
        new ConversionTableFormDialog((JFrame) SwingUtilities.getWindowAncestor(this), controller, null)
                .setVisible(true);
    }

    private void showEditDialog() {
        Integer id = listPanel.getSelectedId();
        if (id == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một bản ghi!");
            return;
        }
        try {
            ConversionTableDTO dto = conversionTableService.getById(id);
            new ConversionTableFormDialog((JFrame) SwingUtilities.getWindowAncestor(this), controller, dto)
                    .setVisible(true);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + e.getMessage());
        }
    }

    private void deleteSelectedConversion() {
        Integer id = listPanel.getSelectedId();
        if (id != null) {
            controller.deleteConversionTable(id);
        }
    }

    private void exportToExcel() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setSelectedFile(new java.io.File("BangQuyDoiDiem_" + java.time.LocalDate.now() + ".xlsx"));
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try (OutputStream os = new java.io.FileOutputStream(fileChooser.getSelectedFile())) {
                String phuongThuc = getSelectedFilterValue(cbPhuongThuc);
                String toHop = getSelectedFilterValue(cbToHop);
                String mon = getSelectedFilterValue(cbMon);
                String keyword = txtSearch.getText().trim();
                controller.exportToExcel(os, phuongThuc, toHop, mon, keyword);
                JOptionPane.showMessageDialog(this, "Xuất file Excel thành công!");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Lỗi: " + e.getMessage());
            }
        }
    }

    private void importFromExcel() {
        JFileChooser fileChooser = new JFileChooser();
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try (InputStream is = new java.io.FileInputStream(fileChooser.getSelectedFile())) {
                ImportResult<ConversionTableDTO> result = controller.importFromExcel(is);
                String msg = String.format("Kết quả: Thành công %d, Lỗi %d", result.getSuccessCount(),
                        result.getErrorCount());
                JOptionPane.showMessageDialog(this, msg);
                refreshData();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Lỗi Import: " + e.getMessage());
            }
        }
    }
}