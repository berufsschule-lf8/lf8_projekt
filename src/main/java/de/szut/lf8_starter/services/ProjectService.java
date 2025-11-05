package de.szut.lf8_starter.services;

import de.szut.lf8_starter.client.EmployeeServiceClient;
import de.szut.lf8_starter.dtos.create.CreateProjectDto;
import de.szut.lf8_starter.dtos.get.GetEmployeeDto;
import de.szut.lf8_starter.dtos.get.GetProjectDto;
import de.szut.lf8_starter.dtos.get.GetProjectEmployeesDto;
import de.szut.lf8_starter.entities.Project;
import de.szut.lf8_starter.entities.ProjectEmployee;
import de.szut.lf8_starter.exceptionHandling.ProjectAssignmentConflictException;
import de.szut.lf8_starter.exceptionHandling.ResourceNotFoundException;
import de.szut.lf8_starter.exceptionHandling.SkillsNotMatchingException;
import de.szut.lf8_starter.repositories.ProjectEmployeeRepository;
import de.szut.lf8_starter.repositories.ProjectRepository;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ProjectService {

  private final ProjectRepository projectRepository;
  private final ProjectEmployeeRepository projectEmployeeRepository;
  private final EmployeeServiceClient employeeServiceClient;

  public ProjectService(ProjectRepository projectRepository,
      ProjectEmployeeRepository projectEmployeeRepository,
      EmployeeServiceClient employeeServiceClient) {
    this.projectRepository = projectRepository;
    this.projectEmployeeRepository = projectEmployeeRepository;
    this.employeeServiceClient = employeeServiceClient;
  }

  public GetProjectDto createProject(CreateProjectDto createProjectDto) {
    Project project = mapToEntity(createProjectDto);
    Project savedProject = projectRepository.save(project);
    log.info("Created project {}", savedProject.getBezeichnung());
    return mapToDto(savedProject);
  }

  public GetProjectDto addEmployeeToProject(Long projectId, Long employeeId) {
    Optional<Project> projectOptional = projectRepository.findById(projectId);
    if (projectOptional.isEmpty()) {
      log.error("Could not find project with id {}", projectId);
      throw new ResourceNotFoundException("Could not find project with id " + projectId);
    }
    GetEmployeeDto employee = employeeServiceClient.getEmployeeById(employeeId);
    if (employee == null) {
      log.error("Could not find employee with id {}", employeeId);
      throw new ResourceNotFoundException("Could not find employee with id " + employeeId);
    }

    Project project = projectOptional.get();

    if (!project.getRequiredSkillIds().isEmpty()) {
      List<Long> employeeSkillIds = employee.getSkillSet().stream()
          .map(GetEmployeeDto.SkillSetDto::getId)
          .collect(Collectors.toList());

      boolean hasRequiredSkills = new HashSet<>(employeeSkillIds).containsAll(
          project.getRequiredSkillIds());
      if (!hasRequiredSkills) {
        log.error("Employee {} does not have required Skills", employeeId);
        throw new SkillsNotMatchingException(
            "Employee " + employeeId + " does not have required Skills");
      }
    }

    List<ProjectEmployee> overlapping = projectEmployeeRepository.findOverlappingAssignments(
        employeeId, project.getStartdatum(), project.getGeplantesEnddatum());
    if (!overlapping.isEmpty()) {
      log.error("Employee {} is already assigned to another project during this period",
          employeeId);
      throw new ProjectAssignmentConflictException(
          "Employee is already assigned to another project");
    }

    ProjectEmployee projectEmployee = new ProjectEmployee();
    projectEmployee.setProject(project);
    projectEmployee.setEmployeeId(employeeId);
    projectEmployee.setStartDate(project.getStartdatum());
    projectEmployee.setEndDate(project.getGeplantesEnddatum());

    projectEmployeeRepository.save(projectEmployee);

    log.info("Added employee {} to project {}", employeeId, projectId);
    return mapToDto(project);
  }

  public void delete(long id) {
    Optional<Project> optionalProject = projectRepository.findById(id);

    if (optionalProject.isEmpty()) {
      throw new ResourceNotFoundException("Project by id = " + id + " was not found.");
    }
    projectRepository.deleteById(id);
  }

  public List<GetProjectDto> getAllProjects() {
    return projectRepository.findAll().stream().map(this::mapToDto).collect(Collectors.toList());
  }

  public GetProjectDto getProjectById(Long id) {
    Optional<Project> project = projectRepository.findById(id);
    if (project.isPresent()) {
      log.info("Found project {}", project.get().getBezeichnung());
      return mapToDto(project.get());
    } else {
      log.error("Project not found with id {}", id);
      throw new ResourceNotFoundException("There is no project with id: " + id);
    }
  }

  public GetProjectEmployeesDto getAllEmployeesInProject(Long projectId) {
    Optional<Project> optionalProject = projectRepository.findById(projectId);
    if (optionalProject.isEmpty()) {
      log.error("Could not find project with id {}", projectId);
      throw new ResourceNotFoundException("Project with id " + projectId + " was not found.");
    }

    Project project = optionalProject.get();
    List<ProjectEmployee> projectEmployees = projectEmployeeRepository.findByProjectId(projectId);

    GetProjectEmployeesDto response = new GetProjectEmployeesDto();
    response.setProjectId(project.getId());
    response.setProjectDescription(project.getBezeichnung());

    List<GetProjectEmployeesDto.EmployeeWithSkillsDto> employeeDtos = projectEmployees.stream()
        .map(pe -> {
          GetEmployeeDto employee = employeeServiceClient.getEmployeeById(pe.getEmployeeId());
          if (employee == null) {
            return null;
          }

          GetProjectEmployeesDto.EmployeeWithSkillsDto empDto = new GetProjectEmployeesDto.EmployeeWithSkillsDto();
          empDto.setEmployeeId(employee.getId());

          List<GetProjectEmployeesDto.SkillDto> skills = employee.getSkillSet().stream()
              .map(skill -> {
                GetProjectEmployeesDto.SkillDto skillDto = new GetProjectEmployeesDto.SkillDto();
                skillDto.setId(skill.getId());
                skillDto.setName(skill.getName());
                return skillDto;
              })
              .collect(Collectors.toList());

          empDto.setSkills(skills);
          return empDto;
        })
        .filter(Objects::nonNull)
        .collect(Collectors.toList());

    response.setEmployees(employeeDtos);
    return response;
  }

  public GetProjectDto update(Long id, CreateProjectDto updateProject) {

    Project project = projectRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException(
            "Project by id = " + id + " was not found and therefore could not be updated."
        ));

    project.setBezeichnung(updateProject.getBezeichnung());
    project.setVerantwortlicherMitarbeiterId(updateProject.getVerantwortlicherMitarbeiterId());
    project.setKundenId(updateProject.getKundenId());
    project.setKundenansprechpartner(updateProject.getKundenansprechpartner());
    project.setKommentar(updateProject.getKommentar());
    project.setStartdatum(updateProject.getStartdatum());
    project.setGeplantesEnddatum(updateProject.getGeplantesEnddatum());

    projectRepository.save(project);
    log.info("Project was updated in repository");
    return mapToDto(project);
  }

  private Project mapToEntity(CreateProjectDto dto) {
    Project project = new Project();
    project.setBezeichnung(dto.getBezeichnung());
    project.setVerantwortlicherMitarbeiterId(dto.getVerantwortlicherMitarbeiterId());
    project.setKundenId(dto.getKundenId());
    project.setKundenansprechpartner(dto.getKundenansprechpartner());
    project.setKommentar(dto.getKommentar());
    project.setStartdatum(dto.getStartdatum());
    project.setGeplantesEnddatum(dto.getGeplantesEnddatum());
    project.setRequiredSkillIds(dto.getRequiredSkillIds());
    return project;
  }

  private GetProjectDto mapToDto(Project project) {
    GetProjectDto dto = new GetProjectDto();
    dto.setId(project.getId());
    dto.setBezeichnung(project.getBezeichnung());
    dto.setVerantwortlicherMitarbeiterId(project.getVerantwortlicherMitarbeiterId());
    dto.setKundenId(project.getKundenId());
    dto.setKundenansprechpartner(project.getKundenansprechpartner());
    dto.setKommentar(project.getKommentar());
    dto.setStartdatum(project.getStartdatum());
    dto.setGeplantesEnddatum(project.getGeplantesEnddatum());
    dto.setTatsaechlichesEnddatum(project.getTatsaechlichesEnddatum());
    dto.setRequiredSkillIds(project.getRequiredSkillIds());
    return dto;
  }

  public void deleteEmployee(long projectId, long employeeId) {
    Optional<ProjectEmployee> projectEmployee =
        projectEmployeeRepository.findByProjectIdAndEmployeeId(projectId, employeeId);
    Optional<Project> project = projectRepository.findById(projectId);

    if (project.isEmpty()) {
      throw new ResourceNotFoundException("Project for ID: " + projectId + " does not exist");
    }
    if (projectEmployee.isEmpty()) {
      throw new ResourceNotFoundException("Employee with id " + employeeId
          + " was not assigned to project " + projectId);
    }
    projectEmployeeRepository.delete(projectEmployee.get());
  }
}
