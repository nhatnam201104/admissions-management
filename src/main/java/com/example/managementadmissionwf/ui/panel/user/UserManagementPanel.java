package com.example.managementadmissionwf.ui.panel.user;

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
public class UserManagementPanel extends AbstractFeaturePanel {

    @Autowired
    private UserController controller;

    private UserListPanel listPanel;
    private JComboBox<String> cboRole;

    public UserManagementPanel() {
        super();
    }

    @PostConstruct
    private void initComponents() {
        listPanel = new UserListPanel();
        buildUI();
    }

    @Override
    protected void onInit() {
        controller.setManagementPanel(this);
    }

    @Override
    protected void createFilterFields(JPanel filterPanel) {
        filterPanel.add(UIFactory.createFilterLabel("Vai trò:"));
        cboRole = UIFactory.createFilterCombo(new String[]{"Tất cả", "ADMIN", "STUDENT"}, 120);
        filterPanel.add(cboRole);
    }

    @Override
    protected void resetFilters() {
        super.resetFilters();
        if (cboRole != null) cboRole.setSelectedIndex(0);
    }

    @Override
    protected Set<ToolbarAction> getToolbarActions() {
        return EnumSet.of(ToolbarAction.ADD, ToolbarAction.EDIT, ToolbarAction.DELETE,
                ToolbarAction.REFRESH, ToolbarAction.EXPORT_EXCEL, ToolbarAction.IMPORT_EXCEL);
    }

    @Override
    protected void onToolbarAction(ToolbarAction action) {
        switch (action) {
            case ADD -> controller.addUser();
            case EDIT -> controller.editUser();
            case DELETE -> controller.deleteUser();
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
        String role = (String) cboRole.getSelectedItem();
        controller.loadUsers(keyword, role, getCurrentPage(), getPageSize());
    }

    @Override
    protected String getItemLabel() {
        return "người dùng";
    }

    public UserListPanel getListPanel() {
        return listPanel;
    }
}
