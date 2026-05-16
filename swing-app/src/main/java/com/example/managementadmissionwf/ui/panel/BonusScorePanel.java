package com.example.managementadmissionwf.ui.panel;

import com.example.managementadmissionwf.bus.interfaces.BonusScoreService;
import com.example.managementadmissionwf.dto.common.ImportResult;
import com.example.managementadmissionwf.dto.score.BonusScoreDTO;
import com.example.managementadmissionwf.dto.score.BonusScoreViewDTO;
import com.example.managementadmissionwf.ui.panel.score.BonusScoreFormDialog;
import com.example.managementadmissionwf.ui.util.ToolbarAction;
import com.example.managementadmissionwf.ui.util.UIFactory;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class BonusScorePanel extends AbstractFeaturePanel {

    private final BonusScoreService bonusScoreService;

    private JTable table;
    private DefaultTableModel tableModel;
    private List<BonusScoreViewDTO> currentRows = List.of();

    @PostConstruct
    private void init() {
        buildUI();
    }

    @Override
    protected JComponent createContentPanel() {
        String[] columnNames = {
                "STT", "CCCD", "Họ tên thí sinh", "NV", "Mã ngành", "Tên ngành",
                "Phương thức", "Tổ hợp", "Điểm CC", "Điểm UTXT", "Tổng điểm"
        };
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = UIFactory.createStandardTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        return UIFactory.createStandardScrollPane(table);
    }

    @Override
    protected Set<ToolbarAction> getToolbarActions() {
        return EnumSet.of(ToolbarAction.ADD, ToolbarAction.EDIT, ToolbarAction.DELETE,
                ToolbarAction.REFRESH, ToolbarAction.IMPORT_EXCEL, ToolbarAction.UPDATE);
    }

    @Override
    protected void onToolbarAction(ToolbarAction action) {
        switch (action) {
            case ADD -> handleAdd();
            case EDIT -> handleEdit();
            case DELETE -> handleDelete();
            case REFRESH -> refreshData();
            case IMPORT_EXCEL -> handleImportExcel();
            case UPDATE -> handleRecomputePriority();
            default -> {
            }
        }
    }

    private void handleImportExcel() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Chọn file Excel điểm cộng");
        if (chooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }
        try (var is = new java.io.FileInputStream(chooser.getSelectedFile())) {
            ImportResult<BonusScoreDTO> result = bonusScoreService.importExcel(is);
            String msg = String.format(
                    "Tổng dòng: %d\nThành công: %d\nLỗi: %d%s",
                    result.getTotalRows(),
                    result.getSuccessCount(),
                    result.getErrorCount(),
                    result.getErrorCount() > 0
                            ? "\n\nChi tiết lỗi (5 đầu):\n- "
                              + String.join("\n- ",
                                  result.getErrors().subList(0, Math.min(5, result.getErrors().size())))
                            : "");
            JOptionPane.showMessageDialog(this, msg, "Kết quả import",
                    result.getErrorCount() == 0
                            ? JOptionPane.INFORMATION_MESSAGE
                            : JOptionPane.WARNING_MESSAGE);
            refreshData();
        } catch (Exception ex) {
            showError("Lỗi import: " + ex.getMessage());
        }
    }

    private void handleRecomputePriority() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Tính lại điểm ưu tiên (KV + ĐT) cho toàn bộ thí sinh theo Quy chế tuyển sinh?",
                "Xác nhận",
                JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }
        try {
            int updated = bonusScoreService.recomputeAllPriorityPoints();
            showInfo("Đã cập nhật " + updated + " bản ghi điểm ưu tiên.");
            refreshData();
        } catch (Exception ex) {
            showError("Lỗi: " + ex.getMessage());
        }
    }


    @Override
    protected void loadData() {
        try {
            String keyword = getSearchField().getText().trim();
            Pageable pageable = PageRequest.of(Math.max(0, currentPage - 1), getPageSize());
            Page<BonusScoreViewDTO> page = bonusScoreService.searchBonusScoreViews(keyword, pageable);

            currentRows = page.getContent();
            populateTable(currentRows, page.getNumber() * page.getSize());
            updatePaginationDirect(page.getNumber() + 1, page.getTotalPages(), page.getTotalElements());
        } catch (Exception e) {
            showError("Lỗi tải dữ liệu điểm cộng: " + e.getMessage());
        }
    }

    @Override
    protected String getItemLabel() {
        return "dòng điểm cộng";
    }

    private void populateTable(List<BonusScoreViewDTO> rows, int startIndex) {
        tableModel.setRowCount(0);
        int stt = startIndex + 1;
        for (BonusScoreViewDTO row : rows) {
            tableModel.addRow(new Object[]{
                    stt++,
                    valueOrDash(row.getCccd()),
                    valueOrDash(row.getHoTen()),
                    row.getNvTt() != null ? row.getNvTt() : "-",
                    valueOrDash(row.getMaNganh()),
                    valueOrDash(row.getTenNganh()),
                    valueOrDash(row.getPhuongThuc()),
                    valueOrDash(row.getToHop()),
                    formatScore(row.getDiemCc()),
                    formatScore(row.getDiemUtxt()),
                    formatScore(row.getDiemTong())
            });
        }
    }

    private BonusScoreViewDTO getSelectedRow() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) {
            return null;
        }
        int modelRow = table.convertRowIndexToModel(selectedRow);
        return modelRow >= 0 && modelRow < currentRows.size() ? currentRows.get(modelRow) : null;
    }

    private void handleAdd() {
        String cccd = JOptionPane.showInputDialog(this, "Nhập CCCD thí sinh:");
        if (cccd == null) {
            return;
        }
        cccd = cccd.trim();
        if (!cccd.matches("\\d{12}")) {
            showWarning("CCCD phải gồm đúng 12 chữ số.");
            return;
        }

        BonusScoreDTO dto = BonusScoreDTO.builder().cccd(cccd).build();
        showForm(dto, false);
    }

    private void handleEdit() {
        BonusScoreViewDTO selected = getSelectedRow();
        if (selected == null) {
            showWarning("Vui lòng chọn một dòng điểm cộng.");
            return;
        }

        BonusScoreDTO dto = bonusScoreService.getBonusScoreByCccd(selected.getCccd());
        showForm(dto, true);
    }

    private void handleDelete() {
        BonusScoreViewDTO selected = getSelectedRow();
        if (selected == null) {
            showWarning("Vui lòng chọn một dòng điểm cộng.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Xóa điểm cộng của CCCD: " + selected.getCccd() + "?",
                "Xác nhận xóa",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            bonusScoreService.deleteBonusScore(selected.getCccd());
            refreshData();
            showInfo("Xóa điểm cộng thành công.");
        } catch (Exception e) {
            showError("Không thể xóa điểm cộng: " + e.getMessage());
        }
    }

    private void showForm(BonusScoreDTO dto, boolean isUpdate) {
        BonusScoreFormDialog dialog = new BonusScoreFormDialog(
                (Frame) SwingUtilities.getWindowAncestor(this),
                isUpdate ? "Cập nhật điểm cộng" : "Thêm điểm cộng",
                dto
        );
        dialog.setSaveHandler(result -> {
            result.setCccd(result.getCccd().trim());
            if (isUpdate) {
                bonusScoreService.updateBonusScore(result);
            } else {
                bonusScoreService.createBonusScore(result);
            }
        });
        dialog.setVisible(true);

        if (dialog.isSaved()) {
            refreshData();
            showInfo(isUpdate ? "Cập nhật điểm cộng thành công." : "Thêm điểm cộng thành công.");
        }
    }

    private String valueOrDash(String value) {
        return value == null || value.isBlank() ? "-" : value.trim();
    }

    private String formatScore(Double value) {
        return value != null ? String.format("%.2f", value) : "-";
    }

    private void showInfo(String message) {
        JOptionPane.showMessageDialog(this, message, "Thông báo", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showWarning(String message) {
        JOptionPane.showMessageDialog(this, message, "Cảnh báo", JOptionPane.WARNING_MESSAGE);
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Lỗi", JOptionPane.ERROR_MESSAGE);
    }
}
