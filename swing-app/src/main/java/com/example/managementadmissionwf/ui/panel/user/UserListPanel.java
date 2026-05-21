package com.example.managementadmissionwf.ui.panel.user;

import com.example.managementadmissionwf.dto.User.GetUserResponse;
import com.example.managementadmissionwf.ui.util.UIFactory;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * List Panel for User Management
 * Displays JTable with user list
 */
public class UserListPanel extends JPanel {
    private JTable table;
    private DefaultTableModel tableModel;
    private List<GetUserResponse> users;
    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public UserListPanel() {
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        createTable();

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));

        add(scrollPane, BorderLayout.CENTER);
    }

    private void createTable() {
        String[] columnNames = {"STT", "ID", "Họ tên", "Email", "Username", "Role", "Ngày tạo"};

        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(tableModel);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        UIFactory.applyStandardHeaderStyle(table);
        table.setRowHeight(30);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        table.getColumnModel().getColumn(0).setPreferredWidth(50);
        table.getColumnModel().getColumn(1).setPreferredWidth(50);
        table.getColumnModel().getColumn(2).setPreferredWidth(150);
        table.getColumnModel().getColumn(3).setPreferredWidth(200);
        table.getColumnModel().getColumn(4).setPreferredWidth(120);
        table.getColumnModel().getColumn(5).setPreferredWidth(80);
        table.getColumnModel().getColumn(6).setPreferredWidth(100);
    }

    public void loadData(List<GetUserResponse> users) {
        this.users = users != null ? users : List.of();
        tableModel.setRowCount(0);

        int stt = 1;
        for (GetUserResponse user : users) {
            Object[] rowData = {
                    stt++,
                    user.getId(),
                    user.getFullname(),
                    user.getEmail(),
                    user.getUsername(),
                    user.getRole() != null ? user.getRole().name() : "",
                    user.getCreatedAt() != null ? user.getCreatedAt().format(dtf) : ""
            };
            tableModel.addRow(rowData);
        }
    }

    public GetUserResponse getSelectedUser() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow >= 0 && users != null && selectedRow < users.size()) {
            return users.get(selectedRow);
        }
        return null;
    }

    public void clearSelection() {
        table.clearSelection();
    }

    public JTable getTable() {
        return table;
    }
}
