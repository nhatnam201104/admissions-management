package com.example.managementadmissionwf.bus.interfaces;

import com.example.managementadmissionwf.dto.common.ImportResult;
import com.example.managementadmissionwf.dto.common.Paging;
import com.example.managementadmissionwf.dto.major.MajorDTO;
import com.example.managementadmissionwf.dto.major.MajorTohopDTO;

import java.io.InputStream;
import java.io.OutputStream;

public interface MajorService {

    Paging<MajorDTO> search(String keyword, int page, int size);

    MajorDTO getByMaNganh(String maNganh);

    MajorDTO create(MajorDTO dto);

    MajorDTO update(String maNganh, MajorDTO dto);

    void delete(String maNganh);

    Paging<MajorTohopDTO> getTohopByMaNganh(String maNganh, int page, int size);

    MajorTohopDTO addTohop(MajorTohopDTO tohopDTO);

    void removeTohop(Integer tohopId);

    void exportExcel(OutputStream outputStream, String keyword);

    ImportResult<MajorDTO> importExcel(InputStream inputStream);

    void refreshAllStatistics();
}