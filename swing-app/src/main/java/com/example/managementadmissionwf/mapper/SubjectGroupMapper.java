package com.example.managementadmissionwf.mapper;

import com.example.managementadmissionwf.dal.entity.XtTohopMonthi;
import com.example.managementadmissionwf.dto.request.SubjectGroupRequest;
import com.example.managementadmissionwf.dto.response.SubjectGroupResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface SubjectGroupMapper {

    SubjectGroupResponse toResponse(XtTohopMonthi entity);

    List<SubjectGroupResponse> toResponseList(List<XtTohopMonthi> entities);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "isDeleted", ignore = true)
    XtTohopMonthi toEntity(SubjectGroupRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "isDeleted", ignore = true)
    void updateEntity(@MappingTarget XtTohopMonthi entity, SubjectGroupRequest request);

    // For import
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "isDeleted", ignore = true)
    @Mapping(target = "matohop", source = "matohop")
    @Mapping(target = "mon1", source = "mon1")
    @Mapping(target = "mon2", source = "mon2")
    @Mapping(target = "mon3", source = "mon3")
    @Mapping(target = "tentohop", source = "tentohop")
    
    XtTohopMonthi toEntityFromResponse(SubjectGroupResponse response);
}
