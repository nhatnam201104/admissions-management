package com.example.managementadmissionwf.service;

import com.example.managementadmissionwf.dto.common.ImportResult;
import com.example.managementadmissionwf.dto.common.Paging;
import com.example.managementadmissionwf.dto.request.SubjectGroupRequest;
import com.example.managementadmissionwf.dto.response.SubjectGroupResponse;

import java.io.InputStream;
import java.io.OutputStream;

public interface SubjectGroupService {

    Paging<SubjectGroupResponse> search(String keyword, int page, int size);

    SubjectGroupResponse findById(Integer id);

    SubjectGroupResponse create(SubjectGroupRequest request);

    SubjectGroupResponse update(Integer id, SubjectGroupRequest request);

    void delete(Integer id);

    void exportExcel(OutputStream outputStream, String keyword);

    ImportResult<SubjectGroupResponse> importExcel(InputStream inputStream);
}
