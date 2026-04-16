package com.example.managementadmissionwf.ui.panel.score;

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
import java.awt.*;
import java.util.EnumSet;
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
        // Không gọi loadData() trực tiếp ở đây để tránh HeadlessException trong Test
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
                // Fix lỗi: Reset toàn bộ state về mặc định
                getSearchField().setText(""); 
                if (cboPhuongThuc != null) {
                    cboPhuongThuc.setSelectedIndex(0); 
                }
                super.currentPage = 1; 
                loadData();
            }
            case EXPORT_EXCEL -> showInfo("Chức năng xuất Excel đang thực hiện...");
            case IMPORT_EXCEL -> handleImportExcel();
            case BONUS_SCORE -> handleBonusScore();
            default -> {}
        }
    }

    @Override
    protected void createFilterFields(JPanel filterPanel) {
        filterPanel.add(UIFactory.createFilterLabel("Phương thức:"));
        cboPhuongThuc = UIFactory.createFilterCombo(new String[]{"Tất cả", "THPT", "DGNL", "VSAT"}, 100);
        // Lắng nghe sự kiện đổi combo để tự reload
        cboPhuongThuc.addActionListener(e -> {
            super.currentPage = 1;
            loadData();
        });
        filterPanel.add(cboPhuongThuc);
    }

    @Override
    protected void loadData() {
        // Kiểm tra headless để tránh lỗi khi chạy Test context
        if (GraphicsEnvironment.isHeadless()) return;

        try {
            String keyword = getSearchField().getText().trim();
            String phuongThuc = (String) cboPhuongThuc.getSelectedItem();
            
            // Xử lý trang cho API (0-based)
            int apiPage = Math.max(0, super.currentPage - 1); 
            
            Page<ScoreDTO> page = controller.getScores(keyword, phuongThuc, apiPage, PAGE_SIZE);
            
            if (page != null) {
                listPanel.loadData(page.getContent());
                // Cập nhật UI phân trang (1-based)
                super.updatePaginationDirect(apiPage + 1, page.getTotalPages(), page.getTotalElements());
            }
        } catch (Exception e) {
            e.printStackTrace();
            showError("Lỗi nạp dữ liệu: " + e.getMessage());
        }
    }

    private void handleImportExcel() {
        showInfo("Chức năng Import Excel đang được phát triển...");
    }

    private void handleBonusScore() {
        ScoreDTO selected = listPanel.getSelectedScore();
        if (selected == null) {
            showWarning("Vui lòng chọn thí sinh!");
            return;
        }

        BonusScoreDTO bonus = new BonusScoreDTO();
        bonus.setCccd(selected.getCccd()); 
        
        BonusScoreFormDialog dialog = new BonusScoreFormDialog(
            (Frame) SwingUtilities.getWindowAncestor(this), 
            "Quản lý Điểm Cộng", 
            bonus 
        );
        dialog.setVisible(true);

        if (dialog.isSaved()) {
            loadData();
            showInfo("Cập nhật điểm cộng thành công!");
        }
    }

    private void handleAdd() {
        String cccd = inputCCCD();
        if (cccd == null || cccd.trim().isEmpty()) return;

        ScoreDTO score = new ScoreDTO();
        score.setCccd(cccd.trim());

        ScoreFormDialog dialog = new ScoreFormDialog((Frame) SwingUtilities.getWindowAncestor(this), "Thêm Điểm Thi", score);
        dialog.setVisible(true);

        if (dialog.isSaved()) {
            try {
                controller.saveScore(dialog.getScore(), false);
                loadData();
                showInfo("Thêm thành công!");
            } catch (Exception e) {
                showError(e.getMessage());
            }
        }
    }

    private void handleEdit() {
        ScoreDTO selected = listPanel.getSelectedScore();
        if (selected == null) {
            showWarning("Vui lòng chọn dòng cần sửa!");
            return;
        }

        ScoreFormDialog dialog = new ScoreFormDialog((Frame) SwingUtilities.getWindowAncestor(this), "Sửa Điểm Thi", selected);
        dialog.setVisible(true);

        if (dialog.isSaved()) {
            try {
                controller.saveScore(dialog.getScore(), true);
                loadData();
                showInfo("Cập nhật thành công!");
            } catch (Exception e) {
                showError(e.getMessage());
            }
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
                controller.deleteScore(selected.getCccd());
                loadData();
                showInfo("Xóa thành công!");
            } catch (Exception e) {
                showError(e.getMessage());
            }
        }
    }

    // --- Utils ---
    private String inputCCCD() { return JOptionPane.showInputDialog(this, "Nhập CCCD (12 số):"); }
    
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