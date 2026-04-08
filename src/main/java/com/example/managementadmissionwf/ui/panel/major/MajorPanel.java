package com.example.managementadmissionwf.ui.panel.major;

import com.example.managementadmissionwf.bus.interfaces.MajorService;
import com.example.managementadmissionwf.bus.interfaces.SubjectGroupService;
import com.example.managementadmissionwf.dto.common.ImportResult;
import com.example.managementadmissionwf.dto.major.MajorDTO;
import com.example.managementadmissionwf.ui.panel.AbstractFeaturePanel;
import com.example.managementadmissionwf.ui.util.ToolbarAction;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.EnumSet;
import java.util.Set;

@Component
public class MajorPanel extends AbstractFeaturePanel {

    private final MajorService majorService;
    private final SubjectGroupService subjectGroupService;
    private MajorController controller;
    private MajorListPanel listPanel;

    @Autowired
    public MajorPanel(MajorService majorService, SubjectGroupService subjectGroupService) {
        this.majorService = majorService;
        this.subjectGroupService = subjectGroupService;
    }

    @PostConstruct
    private void init() {
        this.controller = new MajorController(majorService);
        this.listPanel = new MajorListPanel(controller);
        buildUI();
    }

    @Override
    protected JComponent createContentPanel() {
        return listPanel;
    }

    @Override
    protected void loadData() {
        if (controller == null || listPanel == null || txtSearch == null) return;
        String keyword = txtSearch.getText().trim();
        int page = currentPage;
        int size = getPageSize();
        controller.loadMajorsWithPaging(keyword, listPanel.getMasterModel(), page, size);
    }

    @Override
    protected String getItemLabel() {
        return "ngành";
    }

    @Override
    protected Set<ToolbarAction> getToolbarActions() {
        return EnumSet.of(ToolbarAction.ADD, ToolbarAction.EDIT, ToolbarAction.DELETE, ToolbarAction.REFRESH, ToolbarAction.EXPORT_EXCEL, ToolbarAction.IMPORT_EXCEL);
    }

    @Override
    protected void onToolbarAction(ToolbarAction action) {
        switch (action) {
            case ADD -> showAddDialog();
            case EDIT -> showEditDialog();
            case DELETE -> deleteSelectedMajor();
            case REFRESH -> refreshData();
            case EXPORT_EXCEL -> exportToExcel();
            case IMPORT_EXCEL -> importFromExcel();
        }
    }

    private void showAddDialog() {
        new MajorFormDialog((JFrame) SwingUtilities.getWindowAncestor(this), controller, subjectGroupService, null).setVisible(true);
    }

    private void showEditDialog() {
        String maNganh = listPanel.getSelectedMaNganh();
        if (maNganh == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một ngành!");
            return;
        }
        try {
            MajorDTO dto = majorService.getByMaNganh(maNganh);
            new MajorFormDialog((JFrame) SwingUtilities.getWindowAncestor(this), controller, subjectGroupService, dto).setVisible(true);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + e.getMessage());
        }
    }

    private void deleteSelectedMajor() {
        String maNganh = listPanel.getSelectedMaNganh();
        if (maNganh != null) {
            controller.deleteMajor(maNganh, listPanel.getDetailModel());
        }
    }

    private void exportToExcel() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setSelectedFile(new java.io.File("DanhSachNganh_" + java.time.LocalDate.now() + ".xlsx"));
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try (OutputStream os = new java.io.FileOutputStream(fileChooser.getSelectedFile())) {
                majorService.exportExcel(os, txtSearch.getText().trim());
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
                ImportResult<MajorDTO> result = majorService.importExcel(is);
                String msg = String.format("Kết quả: Thành công %d, Lỗi %d", result.getSuccessCount(), result.getErrorCount());
                JOptionPane.showMessageDialog(this, msg);
                refreshData();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Lỗi Import: " + e.getMessage());
            }
        }
    }
}