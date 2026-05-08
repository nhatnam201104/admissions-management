package com.example.managementadmissionwf.ui.panel.score;

import com.example.managementadmissionwf.dal.entity.XtThisinhxettuyen25;
import com.example.managementadmissionwf.dto.common.ImportResult;
import com.example.managementadmissionwf.dto.score.BonusScoreDTO;
import com.example.managementadmissionwf.dto.score.ScoreDTO;
import com.example.managementadmissionwf.ui.util.ToolbarAction;
import com.example.managementadmissionwf.ui.util.UIFactory;
import com.example.managementadmissionwf.ui.panel.AbstractFeaturePanel;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Component
public class ScorePanel extends AbstractFeaturePanel {

    @Autowired
    private ScoreController controller;
    
    private ScoreListPanel listPanel;
    private JComboBox<String> cboPhuongThuc;
    private static final int PAGE_SIZE = 15; 

    public ScorePanel() {
        super();
    }

    @PostConstruct
    private void initComponents() {
        listPanel = new ScoreListPanel();
        buildUI();
    }

    @Override
    protected Set<ToolbarAction> getToolbarActions() {
        return EnumSet.of(
            ToolbarAction.ADD, 
            ToolbarAction.EDIT, 
            ToolbarAction.DELETE,
            ToolbarAction.REFRESH, 
            ToolbarAction.EXPORT_EXCEL, 
            ToolbarAction.IMPORT_EXCEL,
            ToolbarAction.BONUS_SCORE
        );
    }

    @Override
    protected void onToolbarAction(ToolbarAction action) {
        switch (action) {
            case ADD -> handleAdd();
            case EDIT -> handleEdit();
            case DELETE -> handleDelete();
            case REFRESH -> {
                getSearchField().setText(""); 
                if (cboPhuongThuc != null) {
                    cboPhuongThuc.setSelectedIndex(0); 
                }
                super.currentPage = 1; 
                loadData();
            }
            case EXPORT_EXCEL -> handleExportExcel();
            case IMPORT_EXCEL -> handleImportExcel();
            case BONUS_SCORE -> handleBonusScore();
            default -> {}
        }
    }

    @Override
    protected void createFilterFields(JPanel filterPanel) {
        filterPanel.add(UIFactory.createFilterLabel("Phương thức:"));
        cboPhuongThuc = UIFactory.createFilterCombo(new String[]{"Tất cả", "THPT", "DGNL", "VSAT"}, 100);
        cboPhuongThuc.addActionListener(e -> {
            super.currentPage = 1;
            loadData();
        });
        filterPanel.add(cboPhuongThuc);
    }

    @Override
    protected void loadData() {
        if (GraphicsEnvironment.isHeadless()) return;

        try {
            String keyword = getSearchField().getText().trim();
            String phuongThuc = (String) cboPhuongThuc.getSelectedItem();

            int apiPage = Math.max(0, super.currentPage - 1);

            Page<ScoreDTO> page = controller.getScores(keyword, phuongThuc, apiPage, PAGE_SIZE);

            if (page != null) {

                Map<String, BonusScoreDTO> bonusMap = new HashMap<>();

                Page<BonusScoreDTO> bonusPage = controller.getBonusScores(0, 1000); 

                for (BonusScoreDTO b : bonusPage.getContent()) {
                    bonusMap.put(b.getCccd().trim(), b);
                }
                listPanel.loadData(page.getContent(), bonusMap);

                super.updatePaginationDirect(
                    apiPage + 1,
                    page.getTotalPages(),
                    page.getTotalElements()
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
            showError("Lỗi nạp dữ liệu: " + e.getMessage());
        }
    }

    private void handleExportExcel() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Chọn vị trí lưu file Excel");
        fileChooser.setFileFilter(new FileNameExtensionFilter("Excel Files (*.xlsx)", "xlsx"));
        fileChooser.setSelectedFile(new File("Danh_sach_diem_thi.xlsx"));

        int userSelection = fileChooser.showSaveDialog(this);
        if (userSelection != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File fileToSave = fileChooser.getSelectedFile();
        String filePath = fileToSave.getAbsolutePath();
        if (!filePath.toLowerCase().endsWith(".xlsx")) {
            filePath += ".xlsx";
        }

        try (OutputStream os = new FileOutputStream(filePath)) {
            String keyword = getSearchField().getText().trim();
            String phuongThuc = (String) cboPhuongThuc.getSelectedItem();
            controller.exportScores(os, keyword, phuongThuc);
            showInfo("Đã xuất dữ liệu ra file Excel thành công!\n" + filePath);
        } catch (Exception e) {
            showError("Lỗi khi xuất file Excel: " + e.getMessage());
        }
    }

    private void handleImportExcel() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Chọn file Excel để nhập điểm thi");
        fileChooser.setFileFilter(new FileNameExtensionFilter("Excel Files (*.xlsx)", "xlsx"));

        int userSelection = fileChooser.showOpenDialog(this);
        if (userSelection != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File fileToOpen = fileChooser.getSelectedFile();

        try (InputStream is = new FileInputStream(fileToOpen)) {
            ImportResult<ScoreDTO> result = controller.importScores(is);

            StringBuilder msg = new StringBuilder();
            msg.append("Kết quả nhập dữ liệu:\n");
            msg.append("- Tổng số dòng: ").append(result.getTotalRows()).append("\n");
            msg.append("- Thành công: ").append(result.getSuccessCount()).append("\n");
            msg.append("- Thất bại: ").append(result.getErrorCount()).append("\n");

            if (result.getErrorCount() > 0 && result.getErrors() != null && !result.getErrors().isEmpty()) {
                msg.append("\nChi tiết lỗi (5 lỗi đầu tiên):\n");
                int count = 0;
                for (String error : result.getErrors()) {
                    msg.append("- ").append(error).append("\n");
                    if (++count >= 5) {
                        break;
                    }
                }
            }

            JOptionPane.showMessageDialog(
                    this,
                    msg.toString(),
                    "Kết quả nhập Excel",
                    result.getErrorCount() > 0 ? JOptionPane.WARNING_MESSAGE : JOptionPane.INFORMATION_MESSAGE);

            if (result.getSuccessCount() > 0) {
                super.currentPage = 1;
                loadData();
            }
        } catch (Exception e) {
            showError("Lỗi khi đọc file Excel: " + e.getMessage());
        }
    }

    private void handleBonusScore() {
        ScoreDTO selected = listPanel.getSelectedScore();
        if (selected == null) {
            showWarning("Vui lòng chọn thí sinh!");
            return;
        }

        String cccd = selected.getCccd() != null ? selected.getCccd().trim() : null;

        if (cccd == null || cccd.isEmpty()) {
            showError("CCCD không hợp lệ!");
            return;
        }

        BonusScoreDTO bonus;
        boolean isUpdate = true;

        try {
            bonus = controller.getBonusScoreByCccd(cccd);
        } catch (Exception e) {
            bonus = new BonusScoreDTO();
            bonus.setCccd(cccd);
            isUpdate = false;
        }

        BonusScoreFormDialog dialog = new BonusScoreFormDialog(
            (Frame) SwingUtilities.getWindowAncestor(this),
            "Quản lý Điểm Cộng",
            bonus
        );
        boolean finalIsUpdate = isUpdate;
        dialog.setSaveHandler(result -> {
            result.setCccd(result.getCccd().trim());
            controller.saveBonusScore(result, finalIsUpdate);
        });

        dialog.setVisible(true);

        if (dialog.isSaved()) {
            loadData();
            showInfo(isUpdate 
                ? "Cập nhật điểm cộng thành công!" 
                : "Thêm điểm cộng thành công!");
        }
    }

    private void handleAdd() {
        String cccd = inputCCCD();
        if (cccd == null || cccd.trim().isEmpty()) return;

        cccd = cccd.trim();

        if (!cccd.matches("\\d{12}")) {
            showError("CCCD phải gồm đúng 12 chữ số!");
            return;
        }

        try {
            if (!controller.existsCandidateByCccd(cccd)) {
                showError("CCCD không tồn tại trong hệ thống thí sinh!");
                return;
            }

            

        } catch (Exception e) {
            showError("Lỗi kiểm tra CCCD: " + e.getMessage());
            return;
        }
        
        XtThisinhxettuyen25 candidate = controller.getCandidateByCccd(cccd);

        if (candidate == null) {
            showError("Không tìm thấy thí sinh!");
            return;
        }

        ScoreDTO score = new ScoreDTO();
        score.setCccd(cccd);

        score.setSobaodanh(candidate.getSobaodanh());

        ScoreFormDialog dialog = new ScoreFormDialog(
            (Frame) SwingUtilities.getWindowAncestor(this),
            "Thêm Điểm Thi",
            score
        );
        dialog.setSaveHandler(dto -> controller.saveScore(dto, false));
        dialog.setVisible(true);

        if (dialog.isSaved()) {
            loadData();
            showInfo("Thêm thành công!");
        }
    }

    private void handleEdit() {
        ScoreDTO selected = listPanel.getSelectedScore();
        if (selected == null) {
            showWarning("Vui lòng chọn dòng cần sửa!");
            return;
        }

        ScoreFormDialog dialog = new ScoreFormDialog((Frame) SwingUtilities.getWindowAncestor(this), "Sửa Điểm Thi", selected);
        dialog.setSaveHandler(dto -> controller.saveScore(dto, true));
        dialog.setVisible(true);

        if (dialog.isSaved()) {
            loadData();
            showInfo("Cập nhật thành công!");
        }
    }

    private void handleDelete() {
        ScoreDTO selected = listPanel.getSelectedScore();
        if (selected == null) return;

        int confirm = JOptionPane.showConfirmDialog(this, 
            "Xác nhận xóa điểm của CCCD: " + selected.getCccd() + "?", 
            "Xác nhận", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                controller.deleteScore(selected.getCccd(), selected.getPhuongThuc());
                loadData();
                showInfo("Xóa thành công!");
            } catch (Exception e) {
                showError(e.getMessage());
            }
        }
    }

    // --- Utils ---
    private String inputCCCD() {
        while (true) {
            String cccd = JOptionPane.showInputDialog(this, "Nhập CCCD (12 số):");

            if (cccd == null) return null; // cancel

            cccd = cccd.trim();

            if (cccd.matches("\\d{12}")) {
                return cccd;
            }

            showError("CCCD phải gồm đúng 12 chữ số!");
        }
    }
    
    private void showInfo(String msg) { 
        if (!GraphicsEnvironment.isHeadless())
            JOptionPane.showMessageDialog(this, msg, "Thông báo", JOptionPane.INFORMATION_MESSAGE); 
    }
    
    private void showError(String msg) { 
        if (!GraphicsEnvironment.isHeadless())
            JOptionPane.showMessageDialog(this, msg, "Lỗi", JOptionPane.ERROR_MESSAGE); 
    }
    
    private void showWarning(String msg) { 
        if (!GraphicsEnvironment.isHeadless())
            JOptionPane.showMessageDialog(this, msg, "Cảnh báo", JOptionPane.WARNING_MESSAGE); 
    }

    @Override protected JComponent createContentPanel() { return listPanel; }
    @Override protected String getItemLabel() { return "bản ghi"; }
}
