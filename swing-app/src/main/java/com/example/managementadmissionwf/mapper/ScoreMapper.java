package com.example.managementadmissionwf.mapper;

import com.example.managementadmissionwf.dal.entity.XtDiemthixettuyen;
import com.example.managementadmissionwf.dto.score.ScoreDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ScoreMapper {

    // ===== ENTITY -> DTO =====
    @Mapping(source = "to", target = "toan")
    @Mapping(source = "li", target = "ly")
    @Mapping(source = "ho", target = "hoa")
    @Mapping(source = "va", target = "van")
    @Mapping(source = "si", target = "sinh")
    @Mapping(source = "di", target = "dia")
    @Mapping(source = "DPhuongthuc", target = "phuongThuc")
    ScoreDTO toDto(XtDiemthixettuyen entity);

    // ===== DTO -> ENTITY (CREATE) =====
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "thisinh", ignore = true)
    @Mapping(target = "isDeleted", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(source = "toan", target = "to")
    @Mapping(source = "ly", target = "li")
    @Mapping(source = "hoa", target = "ho")
    @Mapping(source = "van", target = "va")
    @Mapping(source = "sinh", target = "si")
    @Mapping(source = "dia", target = "di")
    @Mapping(source = "phuongThuc", target = "dPhuongthuc")
    XtDiemthixettuyen toEntity(ScoreDTO dto);

    // ===== UPDATE (QUAN TRỌNG NHẤT) =====
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "thisinh", ignore = true)
    @Mapping(target = "cccd", ignore = true) 
    @Mapping(target = "isDeleted", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)

    @Mapping(source = "toan", target = "to")
    @Mapping(source = "ly", target = "li")
    @Mapping(source = "hoa", target = "ho")
    @Mapping(source = "van", target = "va")
    @Mapping(source = "sinh", target = "si")
    @Mapping(source = "dia", target = "di")
    @Mapping(source = "phuongThuc", target = "DPhuongthuc")

    void updateEntityFromDto(ScoreDTO dto, @MappingTarget XtDiemthixettuyen entity);
}
