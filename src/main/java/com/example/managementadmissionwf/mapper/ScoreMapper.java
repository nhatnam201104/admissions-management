package com.example.managementadmissionwf.mapper;

import com.example.managementadmissionwf.dal.entity.XtDiemthixettuyen;
import com.example.managementadmissionwf.dto.score.ScoreDTO;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ScoreMapper {
    @Mapping(source = "to", target = "toan")
    @Mapping(source = "li", target = "ly")
    @Mapping(source = "ho", target = "hoa")
    @Mapping(source = "va", target = "van")
    @Mapping(source = "si", target = "sinh")
    @Mapping(source = "di", target = "dia")
    @Mapping(source = "dPhuongthuc", target = "phuongThuc")
    ScoreDTO toDto(XtDiemthixettuyen entity);

    @InheritInverseConfiguration
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "thisinh", ignore = true)
    @Mapping(target = "isDeleted", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    XtDiemthixettuyen toEntity(ScoreDTO dto);

    @InheritInverseConfiguration
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "thisinh", ignore = true)
    @Mapping(target = "cccd", ignore = true)
    @Mapping(target = "isDeleted", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntityFromDto(ScoreDTO dto, @MappingTarget XtDiemthixettuyen entity);
}