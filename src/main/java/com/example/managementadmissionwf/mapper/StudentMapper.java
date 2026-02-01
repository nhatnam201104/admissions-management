package com.example.managementadmissionwf.mapper;

import com.example.managementadmissionwf.dal.entity.Students;
import com.example.managementadmissionwf.dto.StudentDTO;
import org.mapstruct.*;

import java.util.List;

@Mapper(
    componentModel = "spring", // Use Spring for dependency injection
    unmappedTargetPolicy = ReportingPolicy.IGNORE, // Ignore unmapped fields
    uses = {} // Add other mappers if needed
)
public interface StudentMapper {
    List<StudentDTO> toDtoList(List<Students> entities);

    List<Students> toEntityList(List<StudentDTO> dtos);
}
