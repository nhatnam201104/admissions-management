package com.example.managementadmissionwf.ui.panel.user;

import com.example.managementadmissionwf.bus.interfaces.AuthService;
import com.example.managementadmissionwf.bus.interfaces.UserService;
import com.example.managementadmissionwf.dto.User.*;
import com.example.managementadmissionwf.dto.common.ApiResponse;
import com.example.managementadmissionwf.dto.common.Paging;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Controller for User Management
 */
@Component
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private AuthService authService;

    private UserManagementPanel managementPanel;
    private UserListPanel listPanel;

    public void setManagementPanel(UserManagementPanel panel) {
        this.managementPanel = panel;
        this.listPanel = panel.getListPanel();
    }

    public void loadUsers(String keyword, String role, int page, int limit) {
        GetUserRequest request = GetUserRequest.builder()
                .keyword(keyword)
                .role(role)
                .page(page)
                .limit(limit)
                .currentUserId(authService.getCurrentUser() != null ? authService.getCurrentUser().getId() : null)
                .build();

        ApiResponse<Paging<GetUserResponse>> response = userService.getUsers(request);
        if (response.getCode() == 200 && response.getData() != null) {
            Paging<GetUserResponse> paging = response.getData();
            listPanel.loadData(paging.getData());
            managementPanel.updatePagination(paging);
        } else {
            listPanel.loadData(null);
            JOptionPane.showMessageDialog(managementPanel,
                    response.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void addUser() {
        UserFormDialog dialog = new UserFormDialog(
                (Frame) SwingUtilities.getWindowAncestor(managementPanel),
                "Thêm Người Dùng Mới",
                true);
        dialog.setVisible(true);

        if (dialog.isSaved()) {
            try {
                CreateUserRequest request = dialog.getCreateRequest();
                ApiResponse<CreateUserResponse> response = userService.createUser(request);
                if (response.getCode() == 201) {
                    JOptionPane.showMessageDialog(managementPanel,
                            "Thêm người dùng thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                    managementPanel.refreshData();
                } else {
                    JOptionPane.showMessageDialog(managementPanel,
                            response.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(managementPanel,
                        "Lỗi: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public void editUser() {
        GetUserResponse selected = listPanel.getSelectedUser();
        if (selected == null) {
            JOptionPane.showMessageDialog(managementPanel,
                    "Vui lòng chọn người dùng cần sửa!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Warn if user is ADMIN
        if (selected.getRole() != null && "ADMIN".equals(selected.getRole().name())) {
            JOptionPane.showMessageDialog(managementPanel,
                    "Tài khoản ADMIN không thể chỉnh sửa vai trò.",
                    "Cảnh báo", JOptionPane.WARNING_MESSAGE);
        }

        UserFormDialog dialog = new UserFormDialog(
                (Frame) SwingUtilities.getWindowAncestor(managementPanel),
                "Sửa Thông Tin Người Dùng",
                selected);
        dialog.setVisible(true);

        if (dialog.isSaved()) {
            try {
                UpdateUserRequest request = dialog.getUpdateRequest();
                ApiResponse<UpdateUserResponse> response = userService.updateUser(request);
                if (response.getCode() == 200) {
                    JOptionPane.showMessageDialog(managementPanel,
                            "Cập nhật người dùng thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                    managementPanel.refreshData();
                } else {
                    JOptionPane.showMessageDialog(managementPanel,
                            response.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(managementPanel,
                        "Lỗi: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public void deleteUser() {
        GetUserResponse selected = listPanel.getSelectedUser();
        if (selected == null) {
            JOptionPane.showMessageDialog(managementPanel,
                    "Vui lòng chọn người dùng cần xóa!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                managementPanel,
                "Bạn có chắc chắn muốn xóa người dùng:\n" + selected.getFullname() + "\nUsername: "
                        + selected.getUsername() + "?",
                "Xác nhận xóa",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                ApiResponse<Void> response = userService.deleteUser(selected.getId());
                if (response.getCode() == 200) {
                    JOptionPane.showMessageDialog(managementPanel,
                            "Xóa người dùng thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                    managementPanel.refreshData();
                } else {
                    JOptionPane.showMessageDialog(managementPanel,
                            response.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(managementPanel,
                        "Lỗi: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public void exportExcel(String keyword) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Chọn vị trí lưu file Excel");
        fileChooser.setFileFilter(new FileNameExtensionFilter("Excel Files (*.xlsx)", "xlsx"));
        fileChooser.setSelectedFile(new File("Danh_sach_nguoi_dung.xlsx"));

        int userSelection = fileChooser.showSaveDialog(managementPanel);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            String filePath = fileToSave.getAbsolutePath();
            if (!filePath.toLowerCase().endsWith(".xlsx")) {
                filePath += ".xlsx";
            }
            try (OutputStream os = new FileOutputStream(filePath)) {

                JOptionPane.showMessageDialog(managementPanel,
                        "Đã xuất dữ liệu ra file Excel thành công!\n" + filePath,
                        "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(managementPanel,
                        "Lỗi khi xuất file Excel: " + e.getMessage(),
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public void importExcel() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Chọn file Excel để nhập dữ liệu");
        fileChooser.setFileFilter(new FileNameExtensionFilter("Excel Files (*.xlsx)", "xlsx"));

        int userSelection = fileChooser.showOpenDialog(managementPanel);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToOpen = fileChooser.getSelectedFile();
            try (InputStream is = new FileInputStream(fileToOpen)) {

                JOptionPane.showMessageDialog(managementPanel,
                        "Nhập dữ liệu từ Excel thành công!",
                        "Kết quả nhập Excel", JOptionPane.INFORMATION_MESSAGE);
                managementPanel.refreshData();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(managementPanel,
                        "Lỗi khi đọc file Excel: " + e.getMessage(),
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
