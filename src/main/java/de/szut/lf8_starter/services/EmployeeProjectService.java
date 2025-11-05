package de.szut.lf8_starter.services;

import de.szut.lf8_starter.client.EmployeeServiceClient;
import de.szut.lf8_starter.dtos.get.EmployeeProjectsDto;
import de.szut.lf8_starter.dtos.get.GetEmployeeDto;
import de.szut.lf8_starter.dtos.get.GetEmployeeDto.SkillSetDto;
import de.szut.lf8_starter.entities.ProjectEmployee;
import de.szut.lf8_starter.exceptionHandling.ResourceNotFoundException;
import de.szut.lf8_starter.repositories.ProjectEmployeeRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EmployeeProjectService {

  private final ProjectEmployeeRepository projectEmployeeRepository;
  private final EmployeeServiceClient employeeServiceClient;

  public EmployeeProjectService(ProjectEmployeeRepository projectEmployeeRepository,
      EmployeeServiceClient employeeServiceClient) {
    this.projectEmployeeRepository = projectEmployeeRepository;
    this.employeeServiceClient = employeeServiceClient;
  }

  public EmployeeProjectsDto getEmployeeProjects(Long employeeId) {
    GetEmployeeDto employee = employeeServiceClient.getEmployeeById(employeeId);

    if (employee == null) {
      log.error("Could not find employee with id {}", employeeId);
      throw new ResourceNotFoundException("Could not find employee with id: " + employeeId);
    }
    List<ProjectEmployee> employeeProjects = projectEmployeeRepository.findByEmployeeId(employeeId);
    if (employeeProjects.isEmpty()) {
      log.error("Could not find projects for employee with id {}", employeeId);
      throw new ResourceNotFoundException(
          "Could not find projects for employee with id: " + employeeId);
    }

    var skillMap = employee.getSkillSet().stream()
        .collect(Collectors.toMap(SkillSetDto::getId, SkillSetDto::getName));

    EmployeeProjectsDto dto = new EmployeeProjectsDto();
    dto.setEmployeeId(employeeId);

    List<EmployeeProjectsDto.ProjectAssignmentDto> projectList = employeeProjects.stream()
        .collect(Collectors.groupingBy(pe -> pe.getProject().getId()))
        .entrySet().stream()
        .map(entry -> {
          ProjectEmployee temp = entry.getValue().get(0);
          EmployeeProjectsDto.ProjectAssignmentDto projectDto = new EmployeeProjectsDto.ProjectAssignmentDto();
          projectDto.setProjectId(temp.getProject().getId());
          projectDto.setProjectDescription(temp.getProject().getBezeichnung());
          projectDto.setStartDate(temp.getStartDate());
          projectDto.setEndDate(temp.getEndDate());

          List<String> matchingSkills = temp.getProject().getRequiredSkillIds().stream()
              .filter(skillMap::containsKey)
              .map(skillMap::get)
              .collect(Collectors.toList());

          projectDto.setQualifications(matchingSkills);

          return projectDto;
        })
        .collect(Collectors.toList());

    dto.setProjects(projectList);

    log.info("Projekte für Mitarbeiter {} abgerufen", employeeId);
    return dto;
  }
}
