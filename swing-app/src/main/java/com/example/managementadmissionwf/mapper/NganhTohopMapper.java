package com.example.managementadmissionwf.mapper;

import com.example.managementadmissionwf.dal.entity.XtNganhTohop;
import com.example.managementadmissionwf.dto.major.MajorTohopDTO;
import org.mapstruct.*;

import java.util.List;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface NganhTohopMapper {

    @Mapping(source = "id", target = "id")
    @Mapping(source = "manganh", target = "maNganh")
    @Mapping(source = "matohop", target = "maToHop")
    @Mapping(source = "thMon1", target = "thMon1")
    @Mapping(source = "hsmon1", target = "hsMon1")
    @Mapping(source = "thMon2", target = "thMon2")
    @Mapping(source = "hsmon2", target = "hsMon2")
    @Mapping(source = "thMon3", target = "thMon3")
    @Mapping(source = "hsmon3", target = "hsMon3")
    MajorTohopDTO toResponse(XtNganhTohop entity);

    List<MajorTohopDTO> toResponseList(List<XtNganhTohop> entities);

    @Mapping(target = "id", ignore = true)
    @Mapping(source = "maNganh", target = "manganh")
    @Mapping(source = "maToHop", target = "matohop")
    @Mapping(source = "thMon1", target = "thMon1")
    @Mapping(source = "hsMon1", target = "hsmon1")
    @Mapping(source = "thMon2", target = "thMon2")
    @Mapping(source = "hsMon2", target = "hsmon2")
    @Mapping(source = "thMon3", target = "thMon3")
    @Mapping(source = "hsMon3", target = "hsmon3")
    XtNganhTohop toEntity(MajorTohopDTO dto);

    @Mapping(target = "id", ignore = true)
    void updateEntity(@MappingTarget XtNganhTohop entity, MajorTohopDTO dto);
}