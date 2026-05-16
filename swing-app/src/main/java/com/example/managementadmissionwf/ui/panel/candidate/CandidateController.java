package com.example.managementadmissionwf.ui.panel.candidate;

import com.example.managementadmissionwf.bus.interfaces.CandidateService;
import com.example.managementadmissionwf.dal.entity.XtNganh;
import com.example.managementadmissionwf.dal.repository.BonusScoreRepository;
import com.example.managementadmissionwf.dal.repository.MajorRepository;
import com.example.managementadmissionwf.dal.repository.NguyenVongRepository;
import com.example.managementadmissionwf.dal.repository.ScoreRepository;
import com.example.managementadmissionwf.dto.candidate.CandidateDTO;
import com.example.managementadmissionwf.dto.common.ImportResult;
import com.example.managementadmissionwf.dto.common.Paging;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;

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
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Controller for Candidate Management
 * Handles business logic for CandidatePanel
 */
@Component
public class CandidateController {
    
    @Autowired
    private CandidateService candidateService;

    @Autowired
    private ScoreRepository scoreRepository;

    @Autowired
    private BonusScoreRepository bonusScoreRepository;

    @Autowired
    private NguyenVongRepository nguyenVongRepository;

    @Autowired
    private MajorRepository majorRepository;
    
    private CandidatePanel candidatePanel;
    private CandidateListPanel listPanel;
    
    public void setCandidatePanel(CandidatePanel candidatePanel) {
        this.candidatePanel = candidatePanel;
        this.listPanel = candidatePanel.getListPanel();
    }

    /**
     * Search candidates and update UI pagination
     */
    public void searchCandidates(String keyword, String khuVuc, String doiTuong) {
        int page = candidatePanel.getCurrentPage();
        int size = candidatePanel.getPageSize();

        Paging<CandidateDTO> paging = candidateService.searchCandidates(keyword, khuVuc, doiTuong, page, size);
        
        listPanel.loadData(paging.getData());
        
        candidatePanel.updatePagination(paging);
    }
    
    /**
     * Add new candidate
     */
    public void addCandidate() {
        CandidateFormDialog dialog = new CandidateFormDialog(
            (Frame) SwingUtilities.getWindowAncestor(candidatePanel),
            "Thêm Thí Sinh Mới",
            null
        );
        
        while (true) {
            dialog.setVisible(true); 
            
            if (!dialog.isSaved()) {
                dialog.dispose();
                break; 
            }
            
            try {
                CandidateDTO newCandidate = dialog.getCandidate();
                
                if (candidateService.existsByCccdIncludingDeleted(newCandidate.getCccd())) {
                    JOptionPane.showMessageDialog(candidatePanel, 
                        "CCCD này đã tồn tại trong hệ thống (có thể đã bị xóa trước đó).\nVui lòng sử dụng CCCD khác!", 
                        "Cảnh báo trùng lặp", JOptionPane.WARNING_MESSAGE);
                    dialog.setSaved(false);
                    continue;
                }
                
                candidateService.createCandidate(newCandidate); 
                
                dialog.dispose(); 
                candidatePanel.refreshData();
                JOptionPane.showMessageDialog(candidatePanel, "Thêm thí sinh thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                break; 
                
            } catch (ConstraintViolationException cve) {
                StringBuilder errorMsg = new StringBuilder("Dữ liệu không hợp lệ:\n");
                for (ConstraintViolation<?> violation : cve.getConstraintViolations()) {
                    errorMsg.append("- ").append(violation.getMessage()).append("\n");
                }
                JOptionPane.showMessageDialog(candidatePanel, errorMsg.toString(), "Lỗi nhập liệu", JOptionPane.WARNING_MESSAGE);
                dialog.setSaved(false); 
                
            } catch (RuntimeException re) {
                JOptionPane.showMessageDialog(candidatePanel, re.getMessage(), "Cảnh báo trùng lặp", JOptionPane.WARNING_MESSAGE);
                dialog.setSaved(false);
                
            } catch (Exception e) {
                JOptionPane.showMessageDialog(candidatePanel, "Lỗi: " + e.getMessage(), "Lỗi hệ thống", JOptionPane.ERROR_MESSAGE);
                dialog.setSaved(false);
            }
        }
    }
    
    /**
     * Edit selected candidate
     */
    public void editCandidate() {
        CandidateDTO selected = listPanel.getSelectedCandidate();
        if (selected == null) {
            JOptionPane.showMessageDialog(candidatePanel, "Vui lòng chọn thí sinh cần sửa!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        CandidateFormDialog dialog = new CandidateFormDialog(
            (Frame) SwingUtilities.getWindowAncestor(candidatePanel),
            "Sửa Thông Tin Thí Sinh",
            selected
        );
        
        while (true) {
            dialog.setVisible(true);
            
            if (!dialog.isSaved()) {
                dialog.dispose(); 
                break;
            }
            
            try {
                CandidateDTO updatedCandidate = dialog.getCandidate();      
                candidateService.updateCandidate(updatedCandidate);
                
                dialog.dispose();
                candidatePanel.refreshData();
                JOptionPane.showMessageDialog(candidatePanel, "Cập nhật thí sinh thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                break; 
                
            } catch (ConstraintViolationException cve) {
                StringBuilder errorMsg = new StringBuilder("Dữ liệu không hợp lệ:\n");
                for (ConstraintViolation<?> violation : cve.getConstraintViolations()) {
                    errorMsg.append("- ").append(violation.getMessage()).append("\n");
                }
                JOptionPane.showMessageDialog(candidatePanel, errorMsg.toString(), "Lỗi nhập liệu", JOptionPane.WARNING_MESSAGE);
                dialog.setSaved(false); 
                
            } catch (RuntimeException re) {
                JOptionPane.showMessageDialog(candidatePanel, re.getMessage(), "Cảnh báo trùng lặp", JOptionPane.WARNING_MESSAGE);
                dialog.setSaved(false);
                
            } catch (Exception e) {
                JOptionPane.showMessageDialog(candidatePanel, "Lỗi: " + e.getMessage(), "Lỗi hệ thống", JOptionPane.ERROR_MESSAGE);
                dialog.setSaved(false);
            }
        }
    }
    
    /**
     * Delete selected candidate
     */
    public void deleteCandidate() {
        CandidateDTO selected = listPanel.getSelectedCandidate();
        if (selected == null) {
            JOptionPane.showMessageDialog(candidatePanel, "Vui lòng chọn thí sinh cần xóa!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(
            candidatePanel,
            "Bạn có chắc chắn muốn xóa thí sinh:\n" + selected.getHoTen() + "\nCCCD: " + selected.getCccd() + "?",
            "Xác nhận xóa",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                candidateService.deleteCandidate(selected.getCccd());
                candidatePanel.refreshData();
                JOptionPane.showMessageDialog(candidatePanel, "Xóa thí sinh thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(candidatePanel, "Lỗi: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public void viewCandidateDetail() {
        CandidateDTO selected = listPanel.getSelectedCandidate();
        if (selected == null) {
            JOptionPane.showMessageDialog(candidatePanel, "Vui lòng chọn thí sinh cần xem chi tiết!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String cccd = selected.getCccd();
        Map<String, String> majorNames = majorRepository.findByIsDeletedFalse().stream()
                .collect(Collectors.toMap(XtNganh::getManganh, XtNganh::getTennganh, (a, b) -> a));

        CandidateDetailDialog dialog = new CandidateDetailDialog(
                (Frame) SwingUtilities.getWindowAncestor(candidatePanel),
                selected,
                scoreRepository.findAllByCccd(cccd),
                bonusScoreRepository.findByCccd(cccd).orElse(null),
                nguyenVongRepository.findByNnCccd(cccd),
                majorNames
        );
        dialog.setVisible(true);
    }

    public void exportExcel(String keyword) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Chọn vị trí lưu file Excel");
        fileChooser.setFileFilter(new FileNameExtensionFilter("Excel Files (*.xlsx)", "xlsx"));
        fileChooser.setSelectedFile(new File("Danh_sach_thisinh.xlsx"));

        int userSelection = fileChooser.showSaveDialog(candidatePanel);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            String filePath = fileToSave.getAbsolutePath();
            if (!filePath.toLowerCase().endsWith(".xlsx")) {
                filePath += ".xlsx";
            }
            try (OutputStream os = new FileOutputStream(filePath)) {
                candidateService.exportExcel(os, keyword);
                JOptionPane.showMessageDialog(candidatePanel,
                        "Đã xuất dữ liệu ra file Excel thành công!\n" + filePath,
                        "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(candidatePanel,
                        "Lỗi khi xuất file Excel: " + e.getMessage(),
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public void importExcel() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Chọn file Excel để nhập dữ liệu");
        fileChooser.setFileFilter(new FileNameExtensionFilter("Excel Files (*.xlsx)", "xlsx"));

        int userSelection = fileChooser.showOpenDialog(candidatePanel);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToOpen = fileChooser.getSelectedFile();
            try (InputStream is = new FileInputStream(fileToOpen)) {
                ImportResult<CandidateDTO> result = candidateService.importExcel(is);
                
                String msg = String.format("Kết quả xử lý:\n- Tổng số dòng: %d\n- Thành công: %d\n- Lỗi: %d",
                        result.getTotalRows(), result.getSuccessCount(), result.getErrorCount());
                
                if (result.getErrorCount() > 0) {
                    msg += "\n\nChi tiết lỗi:\n" + String.join("\n", result.getErrors().subList(0, Math.min(5, result.getErrors().size())));
                    if (result.getErrors().size() > 5) msg += "\n...";
                    JOptionPane.showMessageDialog(candidatePanel, msg, "Nhập dữ liệu (Có lỗi)", JOptionPane.WARNING_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(candidatePanel, msg, "Nhập dữ liệu thành công", JOptionPane.INFORMATION_MESSAGE);
                }
                candidatePanel.refreshData();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(candidatePanel,
                        "Lỗi khi đọc file Excel: " + e.getMessage(),
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
