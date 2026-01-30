package com.example.managementadmissionwf.mapper;

import com.example.managementadmissionwf.dal.entity.Student;
import com.example.managementadmissionwf.dto.StudentDTO;
import org.mapstruct.*;

import java.util.List;

@Mapper(
    componentModel = "spring", // Use Spring for dependency injection
    unmappedTargetPolicy = ReportingPolicy.IGNORE, // Ignore unmapped fields
    uses = {} // Add other mappers if needed
)
public interface StudentMapper {
    

    @Mapping(source = "status", target = "status", qualifiedByName = "statusToDtoStatus")
    StudentDTO toDto(Student entity);
    

    @Mapping(source = "status", target = "status", qualifiedByName = "dtoStatusToStatus")
    Student toEntity(StudentDTO dto);
    

    List<StudentDTO> toDtoList(List<Student> entities);
    

    List<Student> toEntityList(List<StudentDTO> dtos);

    @Named("statusToDtoStatus")
    default StudentDTO.StudentStatus statusToDtoStatus(Student.StudentStatus status) {
        if (status == null) {
            return null;
        }
        return StudentDTO.StudentStatus.valueOf(status.name());
    }
    

    @Named("dtoStatusToStatus")
    default Student.StudentStatus dtoStatusToStatus(StudentDTO.StudentStatus status) {
        if (status == null) {
            return null;
        }
        return Student.StudentStatus.valueOf(status.name());
    }
    

    @Mapping(source = "status", target = "status", qualifiedByName = "dtoStatusToStatus")
    void updateEntityFromDto(StudentDTO dto, @MappingTarget Student entity);
}
