package de.szut.lf8_starter.dtos.get;

import java.util.List;
import lombok.Data;

@Data
public class GetProjectEmployeesDto {
  private Long projectId;
  private String projectDescription;
  private List<EmployeeWithSkillsDto> employees;

  @Data
  public static class EmployeeWithSkillsDto {
    private Long employeeId;
    private List<SkillDto> skills;
  }

  @Data
  public static class SkillDto {
    private Long id;
    private String name;
  }
}
