package com.example.managementadmissionwf.mapper;

import com.example.managementadmissionwf.dal.entity.XtNganh;
import com.example.managementadmissionwf.dto.major.MajorDTO;
import org.mapstruct.*;

import java.util.List;

@Mapper(
        componentModel = "spring",
        builder = @Builder(disableBuilder = true),
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface MajorMapper {

    // ==================== Entity → DTO ====================
    @Mapping(source = "idnganh", target = "idNganh")
    @Mapping(source = "manganh", target = "maNganh")
    @Mapping(source = "tennganh", target = "tenNganh")
    // Phải viết hoa chữ N đầu tiên của Entity vì Lombok tạo getNTohopgoc()
    @Mapping(source = "NTohopgoc",       target = "tohopGoc")
    @Mapping(source = "NChitieu",        target = "chiTieu")
    @Mapping(source = "NDiemsan",        target = "diemSan")
    @Mapping(source = "NDiemtrungtuyen", target = "diemTrungTuyen")
    @Mapping(source = "NTuyenthang",     target = "tuyenThang")
    @Mapping(source = "NDgnl",           target = "dgnl")
    @Mapping(source = "NThpt",           target = "thpt")
    @Mapping(source = "NVsat",           target = "vsat")
    // Các trường sl... viết thường chữ s vì Getter là getSlXtt() -> property là slXtt
    @Mapping(source = "slXtt",           target = "slXtt")
    @Mapping(source = "slDgnl",          target = "slDgnl")
    @Mapping(source = "slVsat",          target = "slVsat")
    @Mapping(source = "slThpt",          target = "slThpt")
    MajorDTO toResponse(XtNganh entity);

    List<MajorDTO> toResponseList(List<XtNganh> entities);

    // ==================== DTO → Entity ====================
    @Mapping(target = "idnganh",         source = "idNganh")
    @Mapping(target = "manganh",         source = "maNganh")
    @Mapping(target = "tennganh",        source = "tenNganh")
    @Mapping(target = "NTohopgoc",       source = "tohopGoc")
    @Mapping(target = "NChitieu",        source = "chiTieu")
    @Mapping(target = "NDiemsan",        source = "diemSan")
    @Mapping(target = "NDiemtrungtuyen", source = "diemTrungTuyen")
    @Mapping(target = "NTuyenthang",     source = "tuyenThang")
    @Mapping(target = "NDgnl",           source = "dgnl")
    @Mapping(target = "NThpt",           source = "thpt")
    @Mapping(target = "NVsat",           source = "vsat")
    @Mapping(target = "isDeleted",       ignore = true)
    @Mapping(target = "createdAt",       ignore = true)
    @Mapping(target = "updatedAt",       ignore = true)
    XtNganh toEntity(MajorDTO dto);

    @InheritConfiguration(name = "toEntity")
    @Mapping(target = "idnganh", ignore = true)
    @Mapping(target = "manganh", ignore = true)
    void updateEntity(@MappingTarget XtNganh entity, MajorDTO dto);
}