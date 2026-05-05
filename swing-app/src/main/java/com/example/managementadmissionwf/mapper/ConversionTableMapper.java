package com.example.managementadmissionwf.mapper;

import com.example.managementadmissionwf.dal.entity.XtBangquydoi;
import com.example.managementadmissionwf.dto.ConversionTableDTO;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, builder = @Builder(disableBuilder = true))
public interface ConversionTableMapper {

    @Mapping(source = "DPhuongthuc", target = "phuongThuc")
    @Mapping(source = "DTohop", target = "toHop")
    @Mapping(source = "DMon", target = "mon")
    @Mapping(source = "DDiema", target = "diemA")
    @Mapping(source = "DDiemb", target = "diemB")
    @Mapping(source = "DDiemc", target = "diemC")
    @Mapping(source = "DDiemd", target = "diemD")
    @Mapping(source = "DMaquydoi", target = "maQuyDoi")
    @Mapping(source = "DPhanvi", target = "phanVi")
    ConversionTableDTO toResponse(XtBangquydoi entity);

    List<ConversionTableDTO> toResponseList(List<XtBangquydoi> entities);

    @Mapping(target = "id", ignore = true)
    @Mapping(source = "phuongThuc", target = "DPhuongthuc")
    @Mapping(source = "toHop", target = "DTohop")
    @Mapping(source = "mon", target = "DMon")
    @Mapping(source = "diemA", target = "DDiema")
    @Mapping(source = "diemB", target = "DDiemb")
    @Mapping(source = "diemC", target = "DDiemc")
    @Mapping(source = "diemD", target = "DDiemd")
    @Mapping(source = "maQuyDoi", target = "DMaquydoi")
    @Mapping(source = "phanVi", target = "DPhanvi")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    XtBangquydoi toEntity(ConversionTableDTO dto);

    @Mapping(source = "phuongThuc", target = "DPhuongthuc")
    @Mapping(source = "toHop", target = "DTohop")
    @Mapping(source = "mon", target = "DMon")
    @Mapping(source = "diemA", target = "DDiema")
    @Mapping(source = "diemB", target = "DDiemb")
    @Mapping(source = "diemC", target = "DDiemc")
    @Mapping(source = "diemD", target = "DDiemd")
    @Mapping(source = "maQuyDoi", target = "DMaquydoi")
    @Mapping(source = "phanVi", target = "DPhanvi")
    void updateEntity(@MappingTarget XtBangquydoi entity, ConversionTableDTO dto);
}