package com.example.managementadmissionwf.bus.interfaces;

import com.example.managementadmissionwf.dto.ConversionTableDTO;
import com.example.managementadmissionwf.dto.common.ImportResult;
import com.example.managementadmissionwf.dto.common.Paging;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

public interface ConversionTableService {

    Paging<ConversionTableDTO> search(String phuongThuc, String toHop, String mon, String keyword, int page, int size);

    ConversionTableDTO getById(Integer id);

    ConversionTableDTO create(ConversionTableDTO dto);

    ConversionTableDTO update(Integer id, ConversionTableDTO dto);

    void delete(Integer id);

    List<String> getAllPhuongThuc();

    List<String> getAllToHop();

    List<String> getAllMon();

    void exportExcel(OutputStream outputStream, String phuongThuc, String toHop, String mon, String keyword);

    ImportResult<ConversionTableDTO> importExcel(InputStream inputStream);
}