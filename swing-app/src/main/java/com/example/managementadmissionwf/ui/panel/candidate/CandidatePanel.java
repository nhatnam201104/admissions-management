package com.example.managementadmissionwf.ui.panel.candidate;

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
public class CandidatePanel extends AbstractFeaturePanel {

    @Autowired
    private CandidateController controller;

    private CandidateListPanel listPanel;
    private JComboBox<String> cboKhuVuc;
    private JComboBox<String> cboDoiTuong;

    public CandidatePanel() {
        super();
    }

    @PostConstruct
    private void initComponents() {
        listPanel = new CandidateListPanel();
        buildUI();
    }

    @Override
    protected void onInit() {
        controller.setCandidatePanel(this);
    }

    @Override
    protected void createFilterFields(JPanel filterPanel) {
        filterPanel.add(UIFactory.createFilterLabel("Khu vực:"));
        cboKhuVuc = UIFactory.createFilterCombo(new String[]{"Tất cả", "KV1", "KV2", "KV3"}, 100);
        filterPanel.add(cboKhuVuc);

        filterPanel.add(UIFactory.createFilterLabel("Đối tượng:"));
        cboDoiTuong = UIFactory.createFilterCombo(
                new String[]{"Tất cả", "Không", "KV1", "KV2-NT", "KV2", "KV3", "Con thương binh"}, 120);
        filterPanel.add(cboDoiTuong);
    }

    @Override
    protected void resetFilters() {
        super.resetFilters();
        if (cboKhuVuc != null) cboKhuVuc.setSelectedIndex(0);
        if (cboDoiTuong != null) cboDoiTuong.setSelectedIndex(0);
    }

    @Override
    protected Set<ToolbarAction> getToolbarActions() {
        return EnumSet.of(ToolbarAction.ADD, ToolbarAction.EDIT, ToolbarAction.DELETE, ToolbarAction.VIEW_DETAIL,
                ToolbarAction.REFRESH, ToolbarAction.EXPORT_EXCEL, ToolbarAction.IMPORT_EXCEL);
    }

    @Override
    protected void onToolbarAction(ToolbarAction action) {
        switch (action) {
            case ADD -> controller.addCandidate();
            case EDIT -> controller.editCandidate();
            case DELETE -> controller.deleteCandidate();
            case VIEW_DETAIL -> controller.viewCandidateDetail();
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
        String khuVuc = (String) cboKhuVuc.getSelectedItem();
        String doiTuong = (String) cboDoiTuong.getSelectedItem();
        controller.searchCandidates(keyword, khuVuc, doiTuong);
    }

    @Override
    protected String getItemLabel() {
        return "thí sinh";
    }

    public CandidateListPanel getListPanel() {
        return listPanel;
    }
}
