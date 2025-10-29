package de.szut.lf8_starter.dtos.get;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class EmployeeProjectsDto {
    private Long employeeId;
    private List<ProjectAssignmentDto> projects;

    @Data
    public static class ProjectAssignmentDto {
        private Long projectId;
        private String projectDescription;
        private LocalDate startDate;
        private LocalDate endDate;
        private List<String> qualifications;
    }
}
