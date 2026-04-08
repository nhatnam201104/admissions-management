package com.example.managementadmissionwf.ui.panel.score;

import com.example.managementadmissionwf.ui.util.ToolbarAction;
import com.example.managementadmissionwf.ui.util.UIFactory;
import com.example.managementadmissionwf.ui.panel.AbstractFeaturePanel;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.util.EnumSet;
import java.util.Set;

@Component
public class ScorePanel extends AbstractFeaturePanel {

    @Autowired
    private ScoreController controller;

    private ScoreListPanel listPanel;
    private JComboBox<String> cboPhuongThuc;

    public ScorePanel() {
        super();
    }

    @PostConstruct
    private void initComponents() {
        listPanel = new ScoreListPanel();
        buildUI();
    }

    @Override
    protected void onInit() {
        controller.setScorePanel(this);
    }

    @Override
    protected void createFilterFields(JPanel filterPanel) {
        filterPanel.add(UIFactory.createFilterLabel("Phương thức:"));
        cboPhuongThuc = UIFactory.createFilterCombo(new String[]{"Tất cả", "THPT", "DGNL", "VSAT"}, 100);
        filterPanel.add(cboPhuongThuc);
    }

    @Override
    protected void resetFilters() {
        super.resetFilters();
        if (cboPhuongThuc != null) cboPhuongThuc.setSelectedIndex(0);
    }

    @Override
    protected Set<ToolbarAction> getToolbarActions() {
        return EnumSet.of(ToolbarAction.ADD, ToolbarAction.EDIT, ToolbarAction.DELETE,
                ToolbarAction.REFRESH, ToolbarAction.EXPORT_EXCEL, ToolbarAction.IMPORT_EXCEL);
    }

    @Override
    protected void onToolbarAction(ToolbarAction action) {
        switch (action) {
            case ADD -> controller.addScore();
            case EDIT -> controller.editScore();
            case DELETE -> controller.deleteScore();
            case REFRESH -> refreshData();
            case EXPORT_EXCEL -> controller.exportExcel(getSearchField().getText().trim());
            case IMPORT_EXCEL -> controller.importExcel();
            default -> {}
        }
    }

    @Override
    protected JComponent createContentPanel() {
        return listPanel;
    }

    @Override
    protected void loadData() {
        String keyword = getSearchField().getText().trim();
        String phuongThuc = (String) cboPhuongThuc.getSelectedItem();
        controller.searchScores(keyword, phuongThuc);
    }

    @Override
    protected String getItemLabel() {
        return "bản ghi";
    }

    public ScoreListPanel getListPanel() {
        return listPanel;
    }
}
