package com.example.managementadmissionwf.mapper;

import com.example.managementadmissionwf.dal.entity.XtDiemcongxettuyen;
import com.example.managementadmissionwf.dto.score.BonusScoreDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface BonusScoreMapper {

    BonusScoreDTO toDto(XtDiemcongxettuyen entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "thisinh", ignore = true)
    @Mapping(target = "isDeleted", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    XtDiemcongxettuyen toEntity(BonusScoreDTO dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "thisinh", ignore = true)
    @Mapping(target = "cccd", ignore = true)
    @Mapping(target = "isDeleted", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntityFromDto(BonusScoreDTO dto, @MappingTarget XtDiemcongxettuyen entity);
}