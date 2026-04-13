package com.example.managementadmissionwf.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.example.managementadmissionwf.dal.entity.XtThisinhxettuyen25;
import com.example.managementadmissionwf.dto.candidate.CandidateDTO;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface CandidateMapper {
	CandidateDTO toDTO(XtThisinhxettuyen25 entity);
	
	List<CandidateDTO> toDTOList(List<XtThisinhxettuyen25> entities);
	
	@Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "isDeleted", ignore = true)
    @Mapping(target = "hoVaTen", ignore = true)
    XtThisinhxettuyen25 toEntity(CandidateDTO dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "cccd", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "isDeleted", ignore = true)
    @Mapping(target = "hoVaTen", ignore = true)
    void updateEntity(@MappingTarget XtThisinhxettuyen25 entity, CandidateDTO dto);

    default LocalDate map(Date date) {
        if (date == null) return null;
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    default Date map(LocalDate localDate) {
        if (localDate == null) return null;
        return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }
}
