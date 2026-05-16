package com.example.managementadmissionwf.ui.panel.subjectgroup;

import com.example.managementadmissionwf.ui.util.ToolbarAction;
import com.example.managementadmissionwf.ui.panel.AbstractFeaturePanel;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.util.EnumSet;
import java.util.Set;

@Component
public class SubjectGroupPanel extends AbstractFeaturePanel {

    @Autowired
    private SubjectGroupController controller;

    private SubjectGroupListPanel listPanel;

    public SubjectGroupPanel() {
        super();
    }

    @PostConstruct
    private void initComponents() {
        listPanel = new SubjectGroupListPanel();
        buildUI();
    }

    @Override
    protected void onInit() {
        controller.setSubjectGroupPanel(this);
    }

    @Override
    protected Set<ToolbarAction> getToolbarActions() {
        return EnumSet.of(ToolbarAction.ADD, ToolbarAction.EDIT, ToolbarAction.DELETE,
                ToolbarAction.VIEW_DETAIL, ToolbarAction.REFRESH,
                ToolbarAction.EXPORT_EXCEL, ToolbarAction.IMPORT_EXCEL);
    }

    @Override
    protected void onToolbarAction(ToolbarAction action) {
        switch (action) {
            case ADD -> controller.addSubjectGroup();
            case EDIT -> controller.editSubjectGroup();
            case DELETE -> controller.deleteSubjectGroup();
            case VIEW_DETAIL -> controller.viewSubjectGroupDetail();
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
        controller.loadSubjectGroups(keyword, getCurrentPage(), getPageSize());
    }

    @Override
    protected String getItemLabel() {
        return "bản ghi";
    }

    public SubjectGroupListPanel getListPanel() {
        return listPanel;
    }
}
